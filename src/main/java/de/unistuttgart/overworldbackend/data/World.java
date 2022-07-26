package de.unistuttgart.overworldbackend.data;

import java.util.List;
import java.util.Set;
import javax.persistence.*;
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
public class World extends Area {

  @OneToMany(cascade = CascadeType.ALL)
  List<Dungeon> dungeons;

  public World(
    String staticName,
    String topicName,
    boolean active,
    Set<MinigameTask> minigameTasks,
    List<NPC> npcs,
    List<Dungeon> dungeons
  ) {
    super(staticName, topicName, active, minigameTasks, npcs);
    this.dungeons = dungeons;
  }
}
