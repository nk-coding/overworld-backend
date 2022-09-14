package de.unistuttgart.overworldbackend.data;

import java.util.Objects;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    Dungeon dungeon = (Dungeon) o;
    return Objects.equals(world, dungeon.world);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode());
  }
}
