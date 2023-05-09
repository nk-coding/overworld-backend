package de.unistuttgart.overworldbackend.data;

import de.unistuttgart.overworldbackend.data.enums.Minigame;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * An action log to log a player's minigame run.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerTaskActionLog {

    @Id
    @GeneratedValue(generator = "uuid")
    UUID id;

    @ManyToOne
    PlayerTaskStatistic playerTaskStatistic;

    @ManyToOne
    Course course;

    LocalDateTime date;

    @PrePersist
    void onCreate() {
        date = LocalDateTime.now();
    }

    long score;

    long currentHighscore;

    long gainedKnowledge;

    UUID configurationId;

    @Enumerated(EnumType.STRING)
    Minigame game;
}
