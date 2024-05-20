package flo.no.kanji.business.service.impl;

import flo.no.kanji.ai.VertexAiAgent;
import flo.no.kanji.business.exception.ItemNotFoundException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
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

    /**
     * @{inheritDoc}
     */
    @Override

    public ChatSession createSession(VertexAiAgent agent) {
        var session = ChatSessionEntity.builder()
                .agent(agent)
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

        // Init response
        final ChatMessageEntity responseMessage = chatMessageRepository.save(initAppMessage(session));

        // Send back user message
        if (!userMessage.getIsCommand()) {
            emitMessage(chatMessageMapper.toBusinessObject(userMessage), false, emitter);
        }

        // Send response
        generateResponse(responseMessage, emitter);

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

    private void emitMessage(final ChatMessage message, final boolean complete, SseEmitter emitter) {
        cachedThreadPool.execute(() -> {
            try {
                emitter.send(SseEmitter.event().
                        id(String.valueOf(System.currentTimeMillis()))
                        .data(message));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (complete) {
                emitter.complete();
            }
        });
    }

    private ChatMessageEntity initAppMessage(final ChatSessionEntity session) {
        return ChatMessageEntity.builder()
                .chatSession(session)
                .isAppMessage(true)
                .isCommand(false)
                .isGenerating(true)
                .build();
    }

    private void generateResponse(final ChatMessageEntity message, final SseEmitter emitter) {
        cachedThreadPool.execute(() -> {
            // Simulate API call
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            message.setMessage("This is the AI response test");
            message.setIsGenerating(false);
            message.setIsCommand(false);
            chatMessageRepository.save(message);
            emitMessage(chatMessageMapper.toBusinessObject(message), true, emitter);
        });
    }
}
