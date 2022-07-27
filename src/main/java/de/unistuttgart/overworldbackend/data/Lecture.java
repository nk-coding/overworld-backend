package de.unistuttgart.overworldbackend.data;

import java.util.List;
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
  List<World> worlds;

  public Lecture(final String lectureName, final String description, final List<World> worlds) {
    this.lectureName = lectureName;
    this.description = description;
    this.worlds = worlds;
  }

  @PrePersist
  private void updateLectureIds() {
    worlds.forEach(world -> {
      world.setLecture(this);
      world
        .getMinigameTasks()
        .forEach(minigameTask -> {
          minigameTask.setLecture(this);
          minigameTask.setArea(world);
          minigameTask.setAreaLocation(new AreaLocation(world, null));
        });
      world
        .getNpcs()
        .forEach(npc -> {
          npc.setLecture(this);
          npc.setArea(world);
          npc.setAreaLocation(new AreaLocation(world, null));
        });
      for (final Dungeon dungeon : world.getDungeons()) {
        dungeon.setWorld(world);
        dungeon.setLecture(this);
        dungeon
          .getMinigameTasks()
          .forEach(minigameTask -> {
            minigameTask.setLecture(this);
            minigameTask.setArea(dungeon);
            minigameTask.setAreaLocation(new AreaLocation(world, dungeon));
          });
        dungeon
          .getNpcs()
          .forEach(npc -> {
            npc.setLecture(this);
            npc.setArea(dungeon);
            npc.setAreaLocation(new AreaLocation(world, dungeon));
          });
      }
    });
  }
}
