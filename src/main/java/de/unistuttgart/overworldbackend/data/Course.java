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
public class Course {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  int id;

  @NotNull
  String courseName;

  String description;

  @OneToMany(cascade = CascadeType.ALL)
  List<World> worlds;

  public Course(final String courseName, final String description, final List<World> worlds) {
    this.courseName = courseName;
    this.description = description;
    this.worlds = worlds;
  }

  @PrePersist
  private void updateCourseIds() {
    worlds.forEach(world -> {
      world.setCourse(this);
      world
        .getMinigameTasks()
        .forEach(minigameTask -> {
          minigameTask.setCourse(this);
          minigameTask.setArea(world);
        });
      world
        .getNpcs()
        .forEach(npc -> {
          npc.setCourse(this);
          npc.setArea(world);
        });
      for (final Dungeon dungeon : world.getDungeons()) {
        dungeon.setWorld(world);
        dungeon.setCourse(this);
        dungeon
          .getMinigameTasks()
          .forEach(minigameTask -> {
            minigameTask.setCourse(this);
            minigameTask.setArea(dungeon);
          });
        dungeon
          .getNpcs()
          .forEach(npc -> {
            npc.setCourse(this);
            npc.setArea(dungeon);
          });
      }
    });
  }
}
