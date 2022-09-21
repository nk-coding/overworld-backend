package de.unistuttgart.overworldbackend.data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Data Transfer Object which contains the basic data to create a course.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseInitialData {

  @NotNull
  String courseName;

  @Pattern(regexp = Course.SEMESTER_PATTERN)
  String semester;

  String description;
}
