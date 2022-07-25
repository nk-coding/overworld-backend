package de.unistuttgart.overworldbackend.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

@Entity
@Inheritance
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class Area {

  @Id
  @GeneratedValue(generator = "uuid")
  UUID id;

  int index;

  @NotNull
  String staticName;

  String topicName;

  boolean active;

  @OneToMany(cascade = CascadeType.ALL)
  Set<MinigameTask> minigameTasks;

  @OneToMany(cascade = CascadeType.ALL)
  Set<NPC> npcs;

  @ManyToOne
  Lecture lecture;

  public Area(
    final String staticName,
    final String topicName,
    final boolean active,
    final Set<MinigameTask> minigameTasks,
    final Set<NPC> npcs,
    final int index
  ) {
    this.staticName = staticName;
    this.topicName = topicName;
    this.active = active;
    this.minigameTasks = minigameTasks;
    this.npcs = npcs;
    this.index = index;
  }
}
