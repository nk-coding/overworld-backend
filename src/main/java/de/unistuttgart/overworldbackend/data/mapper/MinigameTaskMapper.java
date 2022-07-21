package de.unistuttgart.overworldbackend.data.mapper;

import de.unistuttgart.overworldbackend.data.MinigameTask;
import de.unistuttgart.overworldbackend.data.MinigameTaskDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MinigameTaskMapper {
  MinigameTaskDTO MinigameTaskToMinigameTaskDTO(final MinigameTask minigameTask);

  MinigameTask MinigameTaskDTOToMinigameTask(final MinigameTaskDTO minigameTaskDTO);
}
