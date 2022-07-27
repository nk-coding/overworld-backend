package de.unistuttgart.overworldbackend.data;

import java.util.List;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
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
public class PlayerStatistic {

  @Id
  @GeneratedValue(generator = "uuid")
  UUID id;

  @ManyToMany(cascade = CascadeType.ALL)
  List<Area> unlockedAreas;

  @ManyToMany(cascade = CascadeType.ALL)
  List<Area> completedDungeons;

  @ManyToOne(cascade = CascadeType.ALL)
  Area currentArea;

  @ManyToOne
  Lecture lecture;

  @NotNull
  String userId;

  @NotNull
  String username;

  long knowledge = 0;

  public void addKnowledge(long gainedKnowledge) {
    knowledge += gainedKnowledge;
  }
}
