package de.unistuttgart.overworldbackend.data.mapper;

import de.unistuttgart.overworldbackend.data.MinigameTask;
import de.unistuttgart.overworldbackend.data.MinigameTaskDTO;
import java.util.Set;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MinigameTaskMapper {
  MinigameTaskDTO minigameTaskToMinigameTaskDTO(final MinigameTask minigameTask);

  MinigameTask minigameTaskDTOToMinigameTask(final MinigameTaskDTO minigameTaskDTO);

  Set<MinigameTaskDTO> minigameTasksToMinigameTaskDTOs(final Set<MinigameTask> minigameTasks);
}
