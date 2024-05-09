package flo.no.kanji.business.model.conversation;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Chat message model object representation
 *
 * @author Florian
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ChatMessage {

    private Long id;

    private UUID conversationId;

    private Boolean isAppMessage;

    @NotBlank
    private String message;

    private List<ChatMessageMistake> mistakes;

    private LocalDateTime createdAt;
}
