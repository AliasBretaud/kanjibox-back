package flo.no.kanji.integration.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * User entity persistent database object
 *
 * @author Florian
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
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
