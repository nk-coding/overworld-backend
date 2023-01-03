package de.unistuttgart.overworldbackend.data.mapper;

import de.unistuttgart.overworldbackend.data.MinigameTask;
import de.unistuttgart.overworldbackend.data.MinigameTaskDTO;
import de.unistuttgart.overworldbackend.data.Teleporter;
import de.unistuttgart.overworldbackend.data.TeleporterDTO;
import java.util.Set;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { AreaLocationMapper.class })
public interface TeleporterMapper {
    TeleporterDTO teleporterToTeleporterDTO(final Teleporter teleporter);

    Teleporter teleporterDTOToTeleporter(final TeleporterDTO teleporterDTO);

    Set<TeleporterDTO> teleportersToTeleporterDTOs(final Set<Teleporter> teleporters);
}
