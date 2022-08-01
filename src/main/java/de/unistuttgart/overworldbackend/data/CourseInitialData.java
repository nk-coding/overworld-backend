package de.unistuttgart.overworldbackend.data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseInitialData {

  @NotNull
  String courseName;

  @Pattern(regexp = "^(WS|SS)-[0-9][0-9]+$")
  String semester;

  String description;
}
