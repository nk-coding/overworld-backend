package de.unistuttgart.overworldbackend.service;

import de.unistuttgart.overworldbackend.data.Keybinding;
import de.unistuttgart.overworldbackend.data.KeybindingDTO;
import de.unistuttgart.overworldbackend.data.Player;
import de.unistuttgart.overworldbackend.data.enums.Binding;
import de.unistuttgart.overworldbackend.repositories.KeybindingRepository;
import de.unistuttgart.overworldbackend.repositories.PlayerRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class KeybindingService {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private KeybindingRepository keybindingRepository;

    /**
     * Checks for all players the current keybindings, adds newly created keybindings to the player, and removes none existing keybindings.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void updatePlayerKeybindings() {
        final Binding[] bindings = Binding.values();
        for (final Player player : playerRepository.findAll()) {
            for (final Binding binding : bindings) {
                if (player.getKeybindings().stream().noneMatch(keybinding -> keybinding.getBinding().equals(binding))) {
                    player.getKeybindings().add(new Keybinding(player, binding, ""));
                }
            }
            playerRepository.save(player);
        }
    }

    /**
     * Returns all keybinding statistics for a given player.
     * @param playerId the id of the player
     * @throws ResponseStatusException (404) if the player does not exist
     * @return a list of keybinding statistics for the given player
     */
    public List<Keybinding> getKeybindingStatisticsFromPlayer(final String playerId) {
        return playerRepository
            .findById(playerId)
            .orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Player with id " + playerId + " does not exist")
            )
            .getKeybindings();
    }

    /**
     * Returns the keybinding statistic for a given player and binding.
     * @param playerId the id of the player
     * @param binding the binding of the keybinding
     * @throws ResponseStatusException (404) if the player or the keybinding does not exist
     * @return the keybinding statistic for the given player and binding
     */
    public Keybinding getKeybindingStatisticFromPlayer(final String playerId, final Binding binding) {
        return getKeybindingStatisticsFromPlayer(playerId)
            .stream()
            .filter(keybindingStatistic -> keybindingStatistic.getBinding().equals(binding))
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
     * @param keybindingDTO the updated parameters
     * @throws ResponseStatusException (400) if path doesn't match the provided binding
     * @throws ResponseStatusException (404) if the player or the keybinding does not exist
     * @return the updated keybinding statistic
     */
    public Keybinding updateKeybindingStatistic(
        final String playerId,
        final Binding binding,
        final KeybindingDTO keybindingDTO
    ) {
        final Keybinding keybinding = getKeybindingStatisticFromPlayer(playerId, binding);
        if (!keybindingDTO.getBinding().equals(binding)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                String.format("The path binding doesn't match the given binding")
            );
        }
        keybinding.setKey(keybindingDTO.getKey());
        final Keybinding updatedKeybinding = keybindingRepository.save(keybinding);
        return updatedKeybinding;
    }
}
