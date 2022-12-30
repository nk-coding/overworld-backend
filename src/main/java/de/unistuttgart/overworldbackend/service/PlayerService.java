package de.unistuttgart.overworldbackend.service;

import de.unistuttgart.overworldbackend.data.Player;
import de.unistuttgart.overworldbackend.data.PlayerDTO;
import de.unistuttgart.overworldbackend.data.PlayerStatistic;
import de.unistuttgart.overworldbackend.repositories.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@Transactional
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    public PlayerDTO createPlayer(final PlayerDTO playerDTO)
    {
        final Optional<Player> existingPlayer = playerRepository.findById(
                playerDTO.getUserId()
        );
        if (existingPlayer.isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    String.format(
                            "There is already a playerstatistic for userId %s",
                            playerDTO.getUserId()
                    )
            );
        }
        final Player player = new Player(playerDTO.getUserId(), playerDTO.getUsername());
        
    }
}
