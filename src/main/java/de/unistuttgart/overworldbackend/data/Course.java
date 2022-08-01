package de.unistuttgart.overworldbackend.data;

import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "courseName", "semester" }) })
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Course {

  static final String SEMESTER_PATTERN = "^(WS|SS)-[0-9][0-9]+$";

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  int id;

  @NotNull
  String courseName;

  @Pattern(regexp = SEMESTER_PATTERN)
  String semester;

  String description;
  boolean active;

  @OneToMany(cascade = CascadeType.ALL)
  List<World> worlds;

  public Course(
    final String courseName,
    final String semester,
    final String description,
    final boolean active,
    final List<World> worlds
  ) {
    this.courseName = courseName;
    this.semester = semester;
    this.description = description;
    this.active = active;
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
