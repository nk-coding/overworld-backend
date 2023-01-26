package de.unistuttgart.overworldbackend.service;

import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.enums.Binding;
import de.unistuttgart.overworldbackend.data.mapper.PlayerMapper;
import de.unistuttgart.overworldbackend.repositories.AchievementRepository;
import de.unistuttgart.overworldbackend.repositories.PlayerRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PlayerMapper playerMapper;

    @Autowired
    private AchievementRepository achievementRepository;

    /**
     * get all players
     *
     * @return a list containing all players
     */
    public List<PlayerDTO> getPlayers() {
        return playerMapper.playersToPlayerDTOs(playerRepository.findAll());
    }

    /**
     * get a player by id
     *
     * @param playerId the playerId of the player searching for
     * @return the found player
     * @throws ResponseStatusException (400) when no player with the playerId is found
     */
    public PlayerDTO getPlayer(final String playerId) {
        Optional<Player> player = playerRepository.findById(playerId);
        if (player.isPresent()) {
            return playerMapper.playerToPlayerDTO(player.get());
        } else {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                String.format("There is no player with playerId %s", playerId)
            );
        }
    }

    /**
     * create a player with initial data
     *
     * @param playerRegistrationDTO the player with its userId and username
     * @return the created player as DTO
     * @throws ResponseStatusException (400) when a player with the playerId already exists
     */
    public PlayerDTO createPlayer(final PlayerRegistrationDTO playerRegistrationDTO) {
        final Optional<Player> existingPlayer = playerRepository.findById(playerRegistrationDTO.getUserId());
        if (existingPlayer.isPresent()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                String.format("There is already a playerstatistic for userId %s", playerRegistrationDTO.getUserId())
            );
        }
        final Player newPlayer = new Player(playerRegistrationDTO.getUserId(), playerRegistrationDTO.getUsername());
        for (final Achievement achievement : achievementRepository.findAll()) {
            newPlayer.getAchievementStatistics().add(new AchievementStatistic(newPlayer, achievement));
        }
        Binding[] bindings = Binding.values();
        for (Binding binding : bindings) {
            newPlayer.getKeybindings().add(new Keybinding(newPlayer, binding, ""));
        }
        final Player savedPlayer = playerRepository.save(newPlayer);
        return playerMapper.playerToPlayerDTO(savedPlayer);
    }
}
