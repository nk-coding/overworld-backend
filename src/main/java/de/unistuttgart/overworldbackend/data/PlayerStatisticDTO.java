package de.unistuttgart.overworldbackend.data;

import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.Nullable;

/**
 * Data Transfer Object for PlayerStatistic.
 *
 * @see PlayerStatistic
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerStatisticDTO {

  @Nullable
  UUID id;

  List<AreaLocationDTO> unlockedAreas;

  List<AreaLocationDTO> completedDungeons;

  AreaLocationDTO currentArea;

  String userId;
  String username;

  long knowledge;
}
