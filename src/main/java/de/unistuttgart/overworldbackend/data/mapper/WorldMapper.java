package de.unistuttgart.overworldbackend.data.mapper;

import de.unistuttgart.overworldbackend.data.Dungeon;
import de.unistuttgart.overworldbackend.data.DungeonDTO;
import de.unistuttgart.overworldbackend.data.World;
import de.unistuttgart.overworldbackend.data.WorldDTO;
import java.util.Set;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WorldMapper {
  WorldDTO worldToWorldDTO(final World world);

  World worldDTOToWorld(final WorldDTO worldDTO);

  Set<WorldDTO> worldsToWorldDTOs(final Set<World> worlds);
}
