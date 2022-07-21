package de.unistuttgart.overworldbackend.data;

import java.util.Set;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class Area {

  @Id
  @GeneratedValue(generator = "uuid")
  UUID id;

  @NotNull
  String staticName;

  String topicName;

  boolean active;

  @OneToMany
  Set<MinigameTask> minigameTasks;

  @OneToMany
  Set<NPCTask> npcTasks;

  public Area(
    String staticName,
    String topicName,
    boolean active,
    Set<MinigameTask> minigameTasks,
    Set<NPCTask> npcTasks
  ) {
    this.staticName = staticName;
    this.topicName = topicName;
    this.active = active;
    this.minigameTasks = minigameTasks;
    this.npcTasks = npcTasks;
  }
}
