package de.unistuttgart.overworldbackend.data.mapper;

import de.unistuttgart.overworldbackend.data.*;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { NPCMapper.class })
public interface PlayerNPCStatisticMapper {
  PlayerNPCStatistic playerNPCStatisticDTOToPlayerNPCStatistic(final PlayerNPCStatisticDTO playerNPCStatisticDTO);

  PlayerNPCStatisticDTO playerNPCStatisticToPlayerNPCStatisticDTO(final PlayerNPCStatistic playerNPCStatistic);

  List<PlayerNPCStatisticDTO> playerNPCStatisticsToPlayerNPCStatisticDTO(
    final List<PlayerNPCStatistic> playerNPCStatistics
  );
}
