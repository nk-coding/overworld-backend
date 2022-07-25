package de.unistuttgart.overworldbackend.data;

import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Dungeon extends Area {

  @ManyToOne
  World world;

  public Dungeon(
    final String staticName,
    final String topicName,
    final boolean active,
    final Set<MinigameTask> minigameTasks,
    final Set<NPC> npcs,
    final int index
  ) {
    super(staticName, topicName, active, minigameTasks, npcs, index);
  }
}
