package flo.no.kanji.business.model.conversation;

import com.fasterxml.jackson.annotation.JsonInclude;
import flo.no.kanji.ai.Agent;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Chat session model object representation
 *
 * @author Florian
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ChatSession {

    private UUID id;

    @NotBlank
    private Agent agent;

    private LocalDateTime lastUpdate;

    private List<ChatMessage> messages;

    private Boolean isGenerating;

    private String status;
}
