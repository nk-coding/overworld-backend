package de.unistuttgart.overworldbackend.data;

import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LectureInitialData {

  @NotNull
  String lectureName;

  String description;
}
