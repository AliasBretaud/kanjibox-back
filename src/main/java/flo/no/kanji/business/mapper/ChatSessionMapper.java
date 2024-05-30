package flo.no.kanji.business.mapper;


import flo.no.kanji.business.model.conversation.ChatSession;
import flo.no.kanji.integration.entity.conversation.ChatSessionEntity;
import org.springframework.stereotype.Component;

/**
 * Chat session object bidirectional mapper between Model objects and Entities
 *
 * @author Florian
 */
@Component
public class ChatSessionMapper {

    /**
     * Transforms a Chat session entity to business object
     *
     * @param sessionEntity ChatSessionEntity Input entity
     * @return Transformed business chat session object
     */
    public ChatSession toBusinessObject(ChatSessionEntity sessionEntity) {
        if (sessionEntity == null) {
            return null;
        }
        return ChatSession.builder()
                .id(sessionEntity.getId())
                .agent(sessionEntity.getAgent())
                .lastUpdate(sessionEntity.getLastAccess())
                .build();
    }
}
