package de.unistuttgart.overworldbackend.data;

import com.fasterxml.jackson.annotation.JsonBackReference;
import de.unistuttgart.overworldbackend.data.enums.Minigame;
import java.util.UUID;
import javax.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.Nullable;

/**
 * A minigame tasks stores the information about a task which game type is played and with which configuration.
 *
 * A minigame task is located in an area and a player can walk in such a minigame spot to start the minigame.
 */
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "index", "area_id", "course_id" }) })
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MinigameTask {

  @Id
  @GeneratedValue(generator = "uuid")
  UUID id;

  int index;

  @Enumerated(EnumType.STRING)
  Minigame game;

  UUID configurationId;

  @Nullable
  String description;

  @JsonBackReference(value = "course-minigames")
  @ManyToOne
  Course course;

  @JsonBackReference(value = "area-minigames")
  @ManyToOne
  Area area;

  public MinigameTask(final Minigame game, final UUID configurationId, final int index) {
    this.game = game;
    this.configurationId = configurationId;
    this.index = index;
  }

  public MinigameTask(final Minigame game, String description, final UUID configurationId, final int index) {
    this.game = game;
    this.configurationId = configurationId;
    this.index = index;
    this.description = description;
  }
}
