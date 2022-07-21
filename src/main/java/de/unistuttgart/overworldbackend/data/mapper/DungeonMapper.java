package de.unistuttgart.overworldbackend.data.mapper;

import de.unistuttgart.overworldbackend.data.Dungeon;
import de.unistuttgart.overworldbackend.data.DungeonDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DungeonMapper {
  DungeonDTO dungeonToDungeonDTO(final Dungeon dungeon);

  Dungeon dungeonDTOToDungeon(final DungeonDTO dungeonDTO);
}
