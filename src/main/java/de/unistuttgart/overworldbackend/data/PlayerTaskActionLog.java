package de.unistuttgart.overworldbackend.data;

import java.util.Date;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

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

  String game;
}
