package flo.no.kanji.integration.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Kanji entity persistent database object
 *
 * @author Florian
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class UserEntity {

    /** Database technical identifier **/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String sub;

    private LocalDateTime lastConnected;

    public UserEntity(final String sub) {
        this.sub = sub;
    }
}
