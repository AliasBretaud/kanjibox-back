package flo.no.kanji.integration.entity.conversation;

import flo.no.kanji.ai.Agent;
import flo.no.kanji.integration.entity.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Chat session entity persistent database object
 *
 * @author Florian
 */
@Entity
@Table(name = "chat_session")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ChatSessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Agent agent;

    @NotNull
    @Column(name = "remote_session_id")
    private String remoteSessionId;

    @OneToMany(mappedBy = "chatSession")
    private List<ChatMessageEntity> messages;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "last_access")
    private LocalDateTime lastAccess;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        lastAccess = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        lastAccess = LocalDateTime.now();
    }
}
