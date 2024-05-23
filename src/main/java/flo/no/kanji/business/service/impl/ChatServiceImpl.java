package flo.no.kanji.business.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import flo.no.kanji.ai.Agent;
import flo.no.kanji.ai.openai.model.run.MessageDelta;
import flo.no.kanji.ai.openai.model.run.MessageType;
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
import flo.no.kanji.util.CharacterUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.concurrent.atomic.AtomicBoolean;

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

    @Autowired
    private ObjectMapper objectMapper;

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
            try {
                emitMessage(emitter, SseEmitter.event()
                        .id(userMessage.getId().toString())
                        .name("USER_MESSAGE")
                        .data(objectMapper.writeValueAsString(chatMessageMapper.toBusinessObject(userMessage)))
                        .build());
            } catch (JsonProcessingException e) {
                emitter.completeWithError(e);
            }
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
                .isGenerating(false)
                .build();
    }

    private void generateResponse(final ChatMessageEntity originMessage,
                                  final SseEmitter emitter) {
        cachedThreadPool.execute(() -> {
            var session = originMessage.getChatSession();
            var remoteSessionId = session.getRemoteSessionId();

            // Create message on assistant
            openAIService.createMessage(remoteSessionId, originMessage.getMessage());
            final AtomicBoolean streamData = new AtomicBoolean(false);
            try {
                openAIService.runTest(remoteSessionId, Agent.RESTAURANT, new EventSourceListener() {
                    @Override
                    public void onEvent(@NotNull EventSource eventSource, String id, String type, @NotNull String data) {
                        try {
                            if (MessageType.THREAD_MESSAGE.equals(type)) {
                                var threadMessage = objectMapper.readValue(data, ThreadMessage.class);
                                var response = saveResponse(originMessage, threadMessage);
                                emitMessage(emitter,
                                        SseEmitter.event()
                                                .id(id)
                                                .data(objectMapper.writeValueAsString(response))
                                                .name("ASSISTANT_MESSAGE")
                                                .build());
                            } else if (MessageType.THREAD_MESSAGE_DELTA.equals(type)) {
                                var messageDelta = objectMapper.readValue(data, MessageDelta.class);
                                var token = messageDelta.getDelta().getContent().getFirst().getText().getValue();
                                if (token.equals("message")) {
                                    streamData.set(true);
                                } else if (token.contains(",")) {
                                    streamData.set(false);
                                }
                                if (streamData.get() && CharacterUtils.isJapanese(token)) {
                                    emitMessage(emitter, SseEmitter.event()
                                            .id(id)
                                            .data(token)
                                            .name("ASSISTANT_MESSAGE_DELTA")
                                            .build());
                                }
                            }
                        } catch (Exception e) {
                            log.error(e.getMessage());
                            emitter.completeWithError(e);
                        }

                    }

                    @Override
                    public void onClosed(@NotNull EventSource eventSource) {
                        completeStream(emitter);
                    }

                    @Override
                    public void onFailure(@NotNull EventSource eventSource, Throwable t, Response response) {
                        log.error(response.message());
                        emitter.completeWithError(t);
                    }
                });
            } catch (Exception e) {
                log.error(e.getMessage());
                emitter.completeWithError(e);
            }
        });
    }

    private ChatMessage saveResponse(ChatMessageEntity originMessage,
                                     ThreadMessage message) throws JsonProcessingException {
        var response = initAppMessage(originMessage.getChatSession());
        var messageValue = objectMapper.readValue(
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

        return chatMessageMapper.toBusinessObject(chatMessageRepository.save(response));
    }
}
