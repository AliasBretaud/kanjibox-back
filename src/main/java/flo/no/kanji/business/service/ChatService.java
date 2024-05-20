package flo.no.kanji.business.service;

import flo.no.kanji.ai.VertexAiAgent;
import flo.no.kanji.business.model.conversation.ChatMessage;
import flo.no.kanji.business.model.conversation.ChatSession;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.UUID;

/**
 * Chat operations business service
 *
 * @author Florian
 */
public interface ChatService {

    /**
     * Create a new chat session
     *
     * @param agent Vertex AI agent to use
     * @return Created session
     */
    ChatSession createSession(final VertexAiAgent agent);

    /**
     * Get all the user's chat sessions
     *
     * @return Chat sessions list
     */
    List<ChatSession> getSessionsList();

    /**
     * Send a new message to the conversation
     *
     * @param sessionId Session UUID
     * @param message   Message body
     * @return Response message from AI agent
     */
    SseEmitter sendMessage(final UUID sessionId, final ChatMessage message);

    /**
     * Delete a chat session
     *
     * @param sessionId Session UUID
     */
    void deleteSession(final UUID sessionId);

    /**
     * Get session messages history
     *
     * @param id Session ID
     * @return List of messages
     */
    List<ChatMessage> getSessionMessages(final UUID id);

    /**
     * Get a single message
     *
     * @param id Message id
     * @return Message
     */
    ChatMessage getMessage(final Long id);
}
