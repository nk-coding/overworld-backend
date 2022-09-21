package de.unistuttgart.overworldbackend.data;

import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.Nullable;

/**
 * Data Transfer Object for Course.
 *
 * @see Course
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseDTO {

  @Nullable
  int id;

  @NotNull
  String courseName;

  @Pattern(regexp = Course.SEMESTER_PATTERN)
  String semester;

  String description;
  boolean active;
  List<WorldDTO> worlds;

  public CourseDTO(
    final String courseName,
    final String semester,
    final String description,
    final boolean active,
    final List<WorldDTO> worlds
  ) {
    this.courseName = courseName;
    this.semester = semester;
    this.description = description;
    this.active = active;
    this.worlds = worlds;
  }
}
