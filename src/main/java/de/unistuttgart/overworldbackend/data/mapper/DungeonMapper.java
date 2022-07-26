package de.unistuttgart.overworldbackend.data.mapper;

import de.unistuttgart.overworldbackend.data.Dungeon;
import de.unistuttgart.overworldbackend.data.DungeonDTO;
import java.util.Set;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DungeonMapper {
  DungeonDTO dungeonToDungeonDTO(final Dungeon dungeon);

  Dungeon dungeonDTOToDungeon(final DungeonDTO dungeonDTO);

  Set<DungeonDTO> dungeonsToDungeonDTOs(final Set<Dungeon> dungeons);
}
