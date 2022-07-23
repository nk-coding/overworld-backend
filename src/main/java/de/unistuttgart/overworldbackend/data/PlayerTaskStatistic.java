package de.unistuttgart.overworldbackend.data;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
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
public class PlayerTaskStatistic {

  @Id
  @GeneratedValue(generator = "uuid")
  UUID id;

  @ManyToOne
  Playerstatistic playerstatistic;

  @ManyToOne
  MinigameTask minigameTask;

  @ManyToOne
  Lecture lecture;

  long highscore;

  boolean completed;
}
