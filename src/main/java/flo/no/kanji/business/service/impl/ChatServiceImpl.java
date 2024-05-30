package flo.no.kanji.business.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import flo.no.kanji.ai.AIService;
import flo.no.kanji.ai.Agent;
import flo.no.kanji.business.exception.ItemNotFoundException;
import flo.no.kanji.business.listeners.MessageListener;
import flo.no.kanji.business.mapper.ChatMessageMapper;
import flo.no.kanji.business.mapper.ChatSessionMapper;
import flo.no.kanji.business.model.conversation.ChatMessage;
import flo.no.kanji.business.model.conversation.ChatSession;
import flo.no.kanji.business.service.ChatService;
import flo.no.kanji.business.service.UserService;
import flo.no.kanji.integration.entity.conversation.ChatMessageEntity;
import flo.no.kanji.integration.entity.conversation.ChatSessionEntity;
import flo.no.kanji.integration.repository.ChatMessageRepository;
import flo.no.kanji.integration.repository.ChatSessionRepository;
import flo.no.kanji.util.AuthUtils;
import lombok.extern.slf4j.Slf4j;
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
    private AIService aiService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageListener messageListener;

    /**
     * @{inheritDoc}
     */
    @Override
    public ChatSession createSession(Agent agent) {
        var threadId = aiService.createSession(agent);
        var session = ChatSessionEntity.builder()
                .agent(agent)
                .remoteSessionId(threadId)
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

        // Update session
        chatSessionRepository.save(session);

        // Init response
        var responseMessage = chatMessageRepository.save(initAppMessage(session));

        try {
            // Save user message
            var userMessage = chatMessageMapper.toEntity(message);
            userMessage.setIsAppMessage(false);
            userMessage.setChatSession(session);
            userMessage = chatMessageRepository.save(userMessage);

            // Send back user message
            if (!userMessage.getIsCommand()) {
                emitMessage(emitter, responseMessage, SseEmitter.event()
                        .id(userMessage.getId().toString())
                        .name("USER_MESSAGE")
                        .data(objectMapper.writeValueAsString(chatMessageMapper.toBusinessObject(userMessage)))
                        .build());

            }

            // Run and send response
            sendMessageToAI(userMessage, responseMessage, emitter);
        } catch (Exception e) {
            completeStream(responseMessage, emitter, e);
        }

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

    private void emitMessage(SseEmitter emitter, ChatMessageEntity message,
                             Set<ResponseBodyEmitter.DataWithMediaType> event) {
        try {
            if (event != null) {
                emitter.send(event);
            }
        } catch (IOException e) {
            completeStream(message, emitter, e);
        }
    }

    private void sendMessageToAI(final ChatMessageEntity originMessage,
                                 final ChatMessageEntity responseMessage,
                                 final SseEmitter emitter) {
        cachedThreadPool.execute(() -> {
            var session = originMessage.getChatSession();
            var message = originMessage.getMessage();

            // Create message on assistant
            try {
                final AtomicBoolean streamData = new AtomicBoolean(false);
                messageListener.setEmitter(emitter);
                messageListener.setResponse(responseMessage);
                messageListener.setOriginMessage(originMessage);
                messageListener.setStreamData(streamData);
                aiService.sendMessage(session, message)
                        .doOnNext(messageListener::onNext)
                        .doOnError(messageListener::onError)
                        .subscribe();
            } catch (Exception e) {
                completeStream(responseMessage, emitter, e);
            }
        });
    }

    private ChatMessageEntity initAppMessage(final ChatSessionEntity session) {
        return ChatMessageEntity.builder()
                .chatSession(session)
                .isAppMessage(true)
                .isGenerating(true)
                .isCommand(false)
                .build();
    }

    private void completeStream(final ChatMessageEntity message, final SseEmitter emitter,
                                final Throwable e) {
        if (e != null) {
            emitter.completeWithError(e);
            message.setIsError(true);
        } else {
            emitter.complete();
        }
        message.setIsGenerating(false);
        chatMessageRepository.save(message);
    }
}
