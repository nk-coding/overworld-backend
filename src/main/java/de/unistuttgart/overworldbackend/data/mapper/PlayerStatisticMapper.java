package de.unistuttgart.overworldbackend.data.mapper;

import de.unistuttgart.overworldbackend.data.PlayerStatistic;
import de.unistuttgart.overworldbackend.data.PlayerStatisticDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { AreaLocationMapper.class, AreaLocationMapper.class })
public interface PlayerStatisticMapper {
    PlayerStatisticDTO playerStatisticToPlayerstatisticDTO(final PlayerStatistic playerstatistic);
}
