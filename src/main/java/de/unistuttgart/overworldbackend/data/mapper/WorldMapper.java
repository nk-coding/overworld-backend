package de.unistuttgart.overworldbackend.data.mapper;

import de.unistuttgart.overworldbackend.data.Dungeon;
import de.unistuttgart.overworldbackend.data.DungeonDTO;
import de.unistuttgart.overworldbackend.data.World;
import de.unistuttgart.overworldbackend.data.WorldDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WorldMapper {
  WorldDTO worldToWorldDTO(final World world);

  World worldDTOToWorld(final WorldDTO worldDTO);
}
