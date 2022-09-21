package de.unistuttgart.overworldbackend.data;

import de.unistuttgart.overworldbackend.data.enums.Minigame;
import java.util.Date;
import java.util.UUID;
import javax.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

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

  @CreationTimestamp
  Date date;

  long score;

  long currentHighscore;

  long gainedKnowledge;

  UUID configurationId;

  @Enumerated(EnumType.STRING)
  Minigame game;
}
