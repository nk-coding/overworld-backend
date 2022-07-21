package de.unistuttgart.overworldbackend.data.mapper;

import de.unistuttgart.overworldbackend.data.NPCTask;
import de.unistuttgart.overworldbackend.data.NPCTaskDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NPCTaskMapper {
  NPCTaskDTO NPCTaskToNPCTaskDTO(final NPCTask npcTask);

  NPCTask NPCTaskDTOToNPCTask(final NPCTaskDTO npcTaskDTO);
}
