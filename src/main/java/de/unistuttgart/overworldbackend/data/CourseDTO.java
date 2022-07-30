package de.unistuttgart.overworldbackend.data;

import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.Nullable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseDTO {

  @Nullable
  int id;

  @NotNull
  String courseName;

  String description;

  List<WorldDTO> worlds;

  public CourseDTO(final String courseName, final String description, final List<WorldDTO> worlds) {
    this.courseName = courseName;
    this.description = description;
    this.worlds = worlds;
  }
}
