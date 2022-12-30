package de.unistuttgart.overworldbackend.data.mapper;

import de.unistuttgart.overworldbackend.data.Player;
import de.unistuttgart.overworldbackend.data.PlayerDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PlayerMapper {
    PlayerDTO playerToPlayerDTO(Player player);

    List<PlayerDTO> playersToPlayerDTOs(List<Player> players);

    Player playerDTOToPlayer(PlayerDTO playerDTO);
}
