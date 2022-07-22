package de.unistuttgart.overworldbackend.data;

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
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Lecture {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  int id;

  @NotNull
  String lectureName;

  String description;

  @OneToMany(cascade = CascadeType.ALL)
  Set<World> worlds;

  public Lecture(String lectureName, String description, Set<World> worlds) {
    this.lectureName = lectureName;
    this.description = description;
    this.worlds = worlds;
    updateLectureIds();
  }

  private void updateLectureIds() {
    worlds.forEach(world -> {
      world.setLecture(this);
      world.getMinigameTasks().forEach(minigameTask -> minigameTask.setLecture(this));
      world.getNpcs().forEach(npc -> npc.setLecture(this));
      world
        .getDungeons()
        .forEach(dungeon -> {
          dungeon.setLecture(this);
          dungeon.getMinigameTasks().forEach(minigameTask -> minigameTask.setLecture(this));
          dungeon.getNpcs().forEach(npc -> npc.setLecture(this));
        });
    });
  }
}
