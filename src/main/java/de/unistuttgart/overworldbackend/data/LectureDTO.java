package de.unistuttgart.overworldbackend.data;

import java.util.Set;
import java.util.UUID;
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
public class LectureDTO {

  @Nullable
  int id;

  @NotNull
  String lectureName;

  String description;

  Set<WorldDTO> worlds;
}
