package flo.no.kanji.integration.entity.conversation;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chat_message_mistake")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageMistakeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "message_id")
    private ChatMessageEntity message;

    private String input;

    private String reason;

    private String correction;
}
