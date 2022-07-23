package de.unistuttgart.overworldbackend.data;

import java.util.List;
import java.util.Set;
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

  List<WorldDTO> worlds;

  public LectureDTO(String lectureName, String description, List<WorldDTO> worlds) {
    this.lectureName = lectureName;
    this.description = description;
    this.worlds = worlds;
  }
}
