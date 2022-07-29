package de.unistuttgart.overworldbackend.data;

import java.util.Set;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Inheritance
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Area {

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
  Course course;

  protected Area(
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
