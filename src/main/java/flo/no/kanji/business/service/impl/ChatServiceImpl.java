package flo.no.kanji.business.service.impl;

import flo.no.kanji.ai.VertexAiAgent;
import flo.no.kanji.business.exception.ItemNotFoundException;
import flo.no.kanji.business.mapper.ChatMessageMapper;
import flo.no.kanji.business.mapper.ChatSessionMapper;
import flo.no.kanji.business.model.conversation.ChatMessage;
import flo.no.kanji.business.model.conversation.ChatSession;
import flo.no.kanji.business.service.ChatService;
import flo.no.kanji.integration.entity.conversation.ChatMessageEntity;
import flo.no.kanji.integration.entity.conversation.ChatSessionEntity;
import flo.no.kanji.integration.repository.ChatSessionRepository;
import flo.no.kanji.util.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Chat business service implementation
 *
 * @author Florian
 * @see ChatService
 */
@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Autowired
    private ChatSessionMapper chatSessionMapper;

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    /**
     * @{inheritDoc}
     */
    @Override
    public ChatSession createSession(VertexAiAgent agent) {
        var session = ChatSessionEntity.builder().agent(agent).build();
        return chatSessionMapper.toBusinessObject(chatSessionRepository.save(session));
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public ChatSession getSession(UUID sessionId) {
        return chatSessionMapper.toBusinessObject(getSessionById(sessionId));
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
    public ChatMessage sendMessage(UUID sessionId, ChatMessage message) {
        var session = getSessionById(sessionId);
        var messages = new ArrayList<>(session.getMessages());
        var userMessage = chatMessageMapper.toEntity(message);
        userMessage.setIsAppMessage(false);

        var agentMessage = ChatMessageEntity
                .builder()
                .isAppMessage(true)
                .build();
        messages.addAll(List.of(userMessage, agentMessage));
        session.setMessages(messages);
        chatSessionRepository.save(session);
        return chatMessageMapper.toBusinessObject(agentMessage);
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public void deleteSession(UUID sessionId) {
        chatSessionRepository.deleteById(sessionId);
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public List<ChatMessage> getSessionMessages(UUID id) {
        var session = getSessionById(id);
        return Optional.ofNullable(session)
                .map(ChatSessionEntity::getMessages)
                .map(messages -> messages.stream().map(chatMessageMapper::toBusinessObject).toList())
                .orElse(null);
    }

    private ChatSessionEntity getSessionById(final UUID id) {
        return chatSessionRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Couldn't find session " + id));
    }
}
