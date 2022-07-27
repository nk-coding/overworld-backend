package de.unistuttgart.overworldbackend.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AreaLocationDTO {

  int worldIndex;

  @Nullable
  Integer dungeonIndex;
}
