package de.unistuttgart.overworldbackend.data;

import java.util.UUID;
import javax.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MinigameTask {

  @Id
  @GeneratedValue(generator = "uuid")
  UUID id;

  String location;
  String game;
  UUID configurationId;

  @ManyToOne
  Lecture lecture;

  public MinigameTask(String location, String game, UUID configurationId) {
    this.location = location;
    this.game = game;
    this.configurationId = configurationId;
  }
}
