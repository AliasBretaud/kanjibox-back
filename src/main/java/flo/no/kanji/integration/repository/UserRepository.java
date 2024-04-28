package flo.no.kanji.integration.repository;

import flo.no.kanji.integration.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findBySub(String sub);
}
