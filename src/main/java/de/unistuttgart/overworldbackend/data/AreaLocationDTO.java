package de.unistuttgart.overworldbackend.data;

import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AreaLocationDTO {

  Integer worldIndex;

  @Nullable
  Integer dungeonIndex;
}
