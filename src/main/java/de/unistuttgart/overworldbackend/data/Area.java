package de.unistuttgart.overworldbackend.data;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Inheritance
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Area {

  @Id
  @GeneratedValue(generator = "uuid")
  UUID id;

  @NotNull
  String staticName;

  String topicName;

  boolean active;

  @OneToMany(cascade = CascadeType.ALL)
  Set<MinigameTask> minigameTasks;

  @OneToMany(cascade = CascadeType.ALL)
  List<NPC> npcs;

  @ManyToOne
  Lecture lecture;

  public Area(String staticName, String topicName, boolean active, Set<MinigameTask> minigameTasks, List<NPC> npcs) {
    this.staticName = staticName;
    this.topicName = topicName;
    this.active = active;
    this.minigameTasks = minigameTasks;
    this.npcs = npcs;
  }
}
