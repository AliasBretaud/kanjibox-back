package flo.no.kanji.business.mapper;


import flo.no.kanji.business.model.conversation.ChatMessage;
import flo.no.kanji.business.model.conversation.ChatMessageMistake;
import flo.no.kanji.integration.entity.conversation.ChatMessageEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Chat message object bidirectional mapper between Model objects and Entities
 *
 * @author Florian
 */
@Component
public class ChatMessageMapper {

    /**
     * Transforms a Chat message entity to business object
     *
     * @param messageEntity ChatMessageEntity Input entity
     * @return Transformed business chat message object
     */
    public ChatMessage toBusinessObject(ChatMessageEntity messageEntity) {
        if (messageEntity == null) {
            return null;
        }
        var mistakes = Optional.ofNullable(messageEntity.getMistakes())
                .map(mistakesList -> mistakesList.stream()
                        .map(m -> ChatMessageMistake.builder()
                                .input(m.getInput())
                                .reason(m.getReason())
                                .correction(m.getCorrection())
                                .build())
                        .toList())
                .orElse(null);
        return ChatMessage.builder()
                .id(messageEntity.getId())
                .conversationId(messageEntity.getChatSession().getId())
                .isAppMessage(messageEntity.getIsAppMessage())
                .message(messageEntity.getMessage())
                .mistakes(mistakes)
                .createdAt(messageEntity.getCreatedAt())
                .build();
    }

    /**
     * Transforms a ChatMessage business object to entity (before performing save in database)
     *
     * @param chatMessage message Chat message business object
     * @return ChatMessageEntity converted object
     */
    public ChatMessageEntity toEntity(ChatMessage chatMessage) {
        if (chatMessage == null) {
            return null;
        }
        return ChatMessageEntity.builder()
                .isAppMessage(chatMessage.getIsAppMessage())
                .message(chatMessage.getMessage())
                .build();
    }
}
