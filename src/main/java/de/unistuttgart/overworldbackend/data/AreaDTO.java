package de.unistuttgart.overworldbackend.data;

import java.util.Set;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.Nullable;

@MappedSuperclass
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AreaDTO {

  @Nullable
  UUID id;

  @NotNull
  String staticName;

  String topicName;

  boolean active;

  Set<MinigameTaskDTO> minigameTasks;
  Set<NPCTaskDTO> npcTasks;
}
