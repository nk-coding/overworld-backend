package de.unistuttgart.overworldbackend.data.mapper;

import de.unistuttgart.overworldbackend.data.*;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.ObjectFactory;

@Mapper(componentModel = "spring", uses = { AreaLocationMapper.class })
public interface PlayerstatisticMapper {
  PlayerstatisticDTO playerstatisticToPlayerstatisticDTO(final Playerstatistic playerstatistic);

  Playerstatistic playerstatisticDTOToPlayerStatistic(final PlayerstatisticDTO playerstatisticDTO);
}
