package de.unistuttgart.overworldbackend.data;

import java.util.UUID;
import javax.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "index", "area_id", "lecture_id" }) })
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MinigameTask {

  @Id
  @GeneratedValue(generator = "uuid")
  UUID id;

  int index;
  String game;
  UUID configurationId;

  @ManyToOne
  Lecture lecture;

  @ManyToOne
  Area area;

  @ManyToOne(cascade = CascadeType.ALL)
  AreaLocation areaLocation;

  public MinigameTask(final String game, final UUID configurationId, final int index) {
    this.game = game;
    this.configurationId = configurationId;
    this.index = index;
  }
}
