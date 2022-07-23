package de.unistuttgart.overworldbackend.data;

import java.util.List;
import java.util.UUID;
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
public class PlayerstatisticDTO {

  @Nullable
  UUID id;

  List<AreaDTO> unlockedAreas;

  List<DungeonDTO> completedDungeons;

  AreaDTO currentArea;

  LectureDTO lecture;

  String userId;
  String username;

  long knowledge;
}
