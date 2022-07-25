package de.unistuttgart.overworldbackend.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.util.List;

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

  public LectureDTO(final String lectureName, final String description, final List<WorldDTO> worlds) {
    this.lectureName = lectureName;
    this.description = description;
    this.worlds = worlds;
  }
}
