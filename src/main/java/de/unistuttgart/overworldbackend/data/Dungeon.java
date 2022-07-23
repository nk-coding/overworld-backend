package de.unistuttgart.overworldbackend.data;

import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.builder.EqualsExclude;
import org.apache.commons.lang3.builder.HashCodeExclude;
import org.apache.commons.lang3.builder.ToStringExclude;

@Entity
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Dungeon extends Area {

  @ManyToOne
  World world;

  public Dungeon(String staticName, String topicName, boolean active, Set<MinigameTask> minigameTasks, Set<NPC> npcs) {
    super(staticName, topicName, active, minigameTasks, npcs);
  }
}
