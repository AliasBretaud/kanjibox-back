package flo.no.kanji.integration.entity.conversation;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "chat_message")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageEntity {

    @OneToMany(mappedBy = "message", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    List<ChatMessageMistakeEntity> mistakes;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private ChatSessionEntity chatSession;

    @Column(name = "is_app_message")
    private Boolean isAppMessage;

    @Column(name = "is_command")
    private Boolean isCommand;

    private String message;

    @Column(name = "is_generating")
    private Boolean isGenerating;

    @Column(name = "is_error")
    private Boolean isError;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
