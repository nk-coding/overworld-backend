package de.unistuttgart.overworldbackend.data.mapper;

import de.unistuttgart.overworldbackend.data.Keybinding;
import de.unistuttgart.overworldbackend.data.KeybindingDTO;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface KeybindingMapper {
    KeybindingDTO keybindingStatisticToKeybindingDTO(final Keybinding keybinding);

    Keybinding keybindingStatisticDTOToKeybindingStatistic(
        final KeybindingDTO keybindingDTO
    );

    List<KeybindingDTO> keybindingStatisticsToKeybindingDTOs(
        final List<Keybinding> keybindings
    );
}
