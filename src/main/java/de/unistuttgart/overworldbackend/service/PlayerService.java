package de.unistuttgart.overworldbackend.service;

import de.unistuttgart.overworldbackend.data.Player;
import de.unistuttgart.overworldbackend.data.PlayerDTO;
import de.unistuttgart.overworldbackend.data.PlayerInitialData;
import de.unistuttgart.overworldbackend.data.mapper.PlayerMapper;
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

    @Autowired
    private PlayerMapper playerMapper;


    public PlayerDTO createPlayer(final PlayerInitialData playerInitialData)
    {
        final Optional<Player> existingPlayer = playerRepository.findById(
                playerInitialData.getUserId()
        );
        if (existingPlayer.isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    String.format(
                            "There is already a playerstatistic for userId %s",
                            playerInitialData.getUserId()
                    )
            );
        }
        final Player newPlayer = new Player(playerInitialData.getUserId(), playerInitialData.getUsername());
        final Player savedPlayer = playerRepository.save(newPlayer);
        return playerMapper.playerToPlayerDTO(savedPlayer);
    }
}
