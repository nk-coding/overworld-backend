package de.unistuttgart.overworldbackend.data.mapper;

import de.unistuttgart.overworldbackend.data.KeybindingStatistic;
import de.unistuttgart.overworldbackend.data.KeybindingStatisticDTO;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface KeybindingStatisticMapper {
    KeybindingStatisticDTO keybindingStatisticToKeybindingDTO(final KeybindingStatistic keybindingStatistic);

    KeybindingStatistic keybindingStatisticDTOToKeybindingStatistic(
        final KeybindingStatisticDTO keybindingStatisticDTO
    );

    List<KeybindingStatisticDTO> keybindingStatisticsToKeybindingDTOs(
        final List<KeybindingStatistic> keybindingStatistics
    );
}
