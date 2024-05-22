package flo.no.kanji.business.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import flo.no.kanji.ai.Agent;
import flo.no.kanji.ai.openai.model.run.MessageDelta;
import flo.no.kanji.ai.openai.model.run.ThreadMessage;
import flo.no.kanji.ai.openai.service.OpenAIService;
import flo.no.kanji.business.exception.ItemNotFoundException;
import flo.no.kanji.business.mapper.ChatMessageMapper;
import flo.no.kanji.business.mapper.ChatSessionMapper;
import flo.no.kanji.business.model.conversation.ChatMessage;
import flo.no.kanji.business.model.conversation.ChatSession;
import flo.no.kanji.business.service.ChatService;
import flo.no.kanji.business.service.UserService;
import flo.no.kanji.integration.entity.conversation.ChatMessageEntity;
import flo.no.kanji.integration.entity.conversation.ChatMessageMistakeEntity;
import flo.no.kanji.integration.entity.conversation.ChatSessionEntity;
import flo.no.kanji.integration.repository.ChatMessageRepository;
import flo.no.kanji.integration.repository.ChatSessionRepository;
import flo.no.kanji.util.AuthUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Chat business service implementation
 *
 * @author Florian
 * @see ChatService
 */
@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ChatSessionMapper chatSessionMapper;

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Autowired
    private OpenAIService openAIService;

    /**
     * @{inheritDoc}
     */
    @Override

    public ChatSession createSession(Agent agent) {
        var thread = openAIService.createThread();
        var session = ChatSessionEntity.builder()
                .agent(agent)
                .remoteSessionId(thread.getId())
                .user(userService.getCurrentUser())
                .build();

        return chatSessionMapper.toBusinessObject(chatSessionRepository.save(session));
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public List<ChatSession> getSessionsList() {
        var sessions = chatSessionRepository.findByUserSub(AuthUtils.getUserSub());
        return Optional.ofNullable(sessions)
                .map(s -> s.stream().map(chatSessionMapper::toBusinessObject).toList())
                .orElse(null);
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public SseEmitter sendMessage(UUID sessionId, ChatMessage message) {
        var session = getSessionById(sessionId);
        var emitter = new SseEmitter();

        // Save user message
        var userMessage = chatMessageMapper.toEntity(message);
        userMessage.setIsAppMessage(false);
        userMessage.setChatSession(session);
        userMessage = chatMessageRepository.save(userMessage);

        // Send back user message
        if (!userMessage.getIsCommand()) {
            emitMessage(emitter, SseEmitter.event()
                    .id(userMessage.getId().toString())
                    .name("USER_MESSAGE")
                    .data(userMessage, MediaType.APPLICATION_JSON)
                    .build());
        }

        // Run and send response
        generateResponse(userMessage, emitter);

        return emitter;
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public void deleteSession(UUID sessionId) {
        var session = getSessionById(sessionId);
        chatSessionRepository.delete(session);
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public List<ChatMessage> getSessionMessages(UUID id) {
        var session = getSessionById(id);
        return Optional.ofNullable(session)
                .map(ChatSessionEntity::getMessages)
                .map(messages -> messages.stream()
                        .filter(m -> !m.getIsCommand())
                        .map(chatMessageMapper::toBusinessObject).toList())
                .orElse(null);
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public ChatMessage getMessage(Long id) {
        var message = chatMessageRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Couldn't find message " + id));
        return chatMessageMapper.toBusinessObject(message);
    }

    private ChatSessionEntity getSessionById(final UUID id) {
        return chatSessionRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Couldn't find session " + id));
    }

    public void completeStream(SseEmitter emitter) {
        emitMessage(emitter, null, true);
    }

    private void emitMessage(SseEmitter emitter, Set<ResponseBodyEmitter.DataWithMediaType> event) {
        emitMessage(emitter, event, false);
    }

    private void emitMessage(SseEmitter emitter, Set<ResponseBodyEmitter.DataWithMediaType> event, boolean complete) {
        try {
            if (event != null) {
                emitter.send(event);
            }
            if (complete) {
                emitter.complete();
            }
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
    }

    private ChatMessageEntity initAppMessage(final ChatSessionEntity session) {
        return ChatMessageEntity.builder()
                .chatSession(session)
                .isAppMessage(true)
                .isCommand(false)
                .isGenerating(true)
                .build();
    }

    private void generateResponse(final ChatMessageEntity originMessage,
                                  final SseEmitter emitter) {
        cachedThreadPool.execute(() -> {
            var session = originMessage.getChatSession();
            var remoteSessionId = session.getRemoteSessionId();

            // Create message on assistant
            openAIService.createMessage(remoteSessionId, originMessage.getMessage());

            var flux = openAIService.run(remoteSessionId, Agent.RESTAURANT);
            flux.subscribe(
                    (m) -> {
                        switch (m) {
                            case ThreadMessage tm -> {
                                try {
                                    var response = saveResponse(originMessage, tm);
                                    emitMessage(emitter,
                                            SseEmitter.event()
                                                    .id(tm.getId())
                                                    .data(response, MediaType.APPLICATION_JSON)
                                                    .name("ASSISTANT_MESSAGE")
                                                    .build());
                                } catch (JsonProcessingException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            case MessageDelta md -> emitMessage(emitter,
                                    SseEmitter.event()
                                            .id(md.getId())
                                            .data(md.getDelta().getContent().getFirst().getText().getValue())
                                            .name("ASSISTANT_MESSAGE_DELTA")
                                            .build());
                            default -> throw new IllegalStateException("Unexpected value: " + m);
                        }
                    },
                    (e) -> {
                        log.error(e.getMessage());
                        emitter.completeWithError(e);
                    },
                    () -> completeStream(emitter));
        });
    }

    private ChatMessageEntity saveResponse(ChatMessageEntity originMessage,
                                           ThreadMessage message) throws JsonProcessingException {
        var response = initAppMessage(originMessage.getChatSession());
        var messageValue = new ObjectMapper().readValue(
                message.getContent().getFirst().getText().getValue(), ChatMessage.class);
        response.setMessage(messageValue.getMessage());
        response.setIsCommand(false);
        if (messageValue.getMistakes() != null) {
            originMessage.setMistakes(messageValue.getMistakes().stream()
                    .map(m -> ChatMessageMistakeEntity.builder()
                            .input(m.getInput())
                            .reason(m.getReason())
                            .correction(m.getCorrection())
                            .build())
                    .toList());
        }

        return chatMessageRepository.save(response);
    }
}
