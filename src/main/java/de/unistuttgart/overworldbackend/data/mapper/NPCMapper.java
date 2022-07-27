package de.unistuttgart.overworldbackend.data.mapper;

import de.unistuttgart.overworldbackend.data.NPC;
import de.unistuttgart.overworldbackend.data.NPCDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { AreaLocationMapper.class })
public interface NPCMapper {
  NPCDTO npcToNPCDTO(final NPC npc);

  NPC npcDTOToNPC(final NPCDTO npcDTO);
}
