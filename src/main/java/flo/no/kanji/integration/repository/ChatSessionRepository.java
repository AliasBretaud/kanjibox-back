package flo.no.kanji.integration.repository;

import flo.no.kanji.integration.entity.conversation.ChatSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * JPA repository for CHAT_SESSION table
 *
 * @author Florian
 */
@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSessionEntity, UUID> {
    List<ChatSessionEntity> findByUserSub(String userSub);
}
