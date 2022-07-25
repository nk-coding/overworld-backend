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
public class AreaDTO {

  @Nullable
  UUID id;

  int index;
  @NotNull
  String staticName;

  String topicName;

  boolean active;

  Set<MinigameTaskDTO> minigameTasks;
  Set<NPCDTO> npcs;
}
