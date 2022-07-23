package de.unistuttgart.overworldbackend.data;

import java.util.UUID;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerTaskStatisticData {

  @NotNull
  String game;

  @NotNull
  UUID configurationId;

  @Min(0)
  @Max(100)
  long score;

  @NotNull
  String userId;
}
