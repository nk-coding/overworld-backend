package de.unistuttgart.overworldbackend.data;

import java.util.Set;
import javax.persistence.Entity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Dungeon extends Area {

  public Dungeon(String staticName, String topicName, boolean active, Set<MinigameTask> minigameTasks, Set<NPC> npcs) {
    super(staticName, topicName, active, minigameTasks, npcs);
  }
}
