package de.unistuttgart.overworldbackend.data.mapper;

import de.unistuttgart.overworldbackend.data.NPC;
import de.unistuttgart.overworldbackend.data.NPCDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NPCTaskMapper {
  NPCDTO NPCTaskToNPCTaskDTO(final NPC npc);

  NPC NPCTaskDTOToNPCTask(final NPCDTO npcDTO);
}
