package de.unistuttgart.overworldbackend.data.mapper;

import de.unistuttgart.overworldbackend.data.PlayerTaskStatistic;
import de.unistuttgart.overworldbackend.data.PlayerTaskStatisticDTO;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { MinigameTaskMapper.class })
public interface PlayerTaskStatisticMapper {
    PlayerTaskStatistic playerTaskStatisticDTOToPlayerTaskStatistic(
        final PlayerTaskStatisticDTO playerTaskStatisticDTO
    );

    PlayerTaskStatisticDTO playerTaskStatisticToPlayerTaskStatisticDTO(final PlayerTaskStatistic playerTaskStatistic);

    List<PlayerTaskStatisticDTO> playerTaskStatisticsToPlayerTaskStatisticDTO(
        final List<PlayerTaskStatistic> playerTaskStatistics
    );
}
