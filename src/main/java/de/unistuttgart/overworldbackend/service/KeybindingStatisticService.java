package de.unistuttgart.overworldbackend.service;

import de.unistuttgart.overworldbackend.data.KeybindingStatistic;
import de.unistuttgart.overworldbackend.data.KeybindingStatisticDTO;
import de.unistuttgart.overworldbackend.data.enums.Keybinding;
import de.unistuttgart.overworldbackend.repositories.KeybindingStatisticRepository;
import de.unistuttgart.overworldbackend.repositories.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class KeybindingStatisticService {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private KeybindingStatisticRepository keybindingStatisticRepository;

    /**
     * Returns all keybinding statistics for a given player.
     * @param playerId the id of the player
     * @throws ResponseStatusException (404) if the player does not exist
     * @return a list of keybinding statistics for the given player
     */
    public List<KeybindingStatistic> getKeybindingStatisticsFromPlayer(final String playerId) {
        return playerRepository
                .findById(playerId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Player with id " + playerId + " does not exist")
                )
                .getKeybindingStatistics();
    }

    /**
     * Returns the keybinding statistic for a given player and binding.
     * @param playerId the id of the player
     * @param binding the binding of the keybinding
     * @throws ResponseStatusException (404) if the player or the keybinding does not exist
     * @return the keybinding statistic for the given player and binding
     */
    public KeybindingStatistic getKeybindingStatisticFromPlayer(
            final String playerId,
            final Keybinding binding
    ) {
        return getKeybindingStatisticsFromPlayer(playerId)
                .stream()
                .filter(keybindingStatistic ->
                        keybindingStatistic.getKeybinding().equals(binding)
                )
                .findFirst()
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                String.format("There is no keybinding statistic for binding %s", binding)
                        )
                );
    }

    /**
     * Updates the key of the given keybinding statistic
     * @param playerId the if of the player
     * @param binding the binding of the keybinding
     * @param keybindingStatisticDTO the updated parameters
     * @throws ResponseStatusException (400) if path doesn't match the provided binding
     * @throws ResponseStatusException (404) if the player or the keybinding does not exist
     * @return the updated keybinding statistic
     */
    public KeybindingStatistic updateKeybindingStatistic(
            final String playerId,
            final Keybinding binding,
            final KeybindingStatisticDTO keybindingStatisticDTO
    ) {
        final KeybindingStatistic keybindingStatistic = getKeybindingStatisticFromPlayer(playerId, binding);
        if(!keybindingStatistic.getKeybinding().equals(binding))
        {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    String.format("The path binding doesn't match the given binding")
            );
        }
        keybindingStatistic.setKey(keybindingStatisticDTO.getKey());
        final KeybindingStatistic updatedKeybindingStatistic = keybindingStatisticRepository.save(keybindingStatistic);
        return updatedKeybindingStatistic;
    }
}
