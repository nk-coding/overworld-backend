package de.unistuttgart.overworldbackend.service;

import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.mapper.PlayerMapper;
import de.unistuttgart.overworldbackend.repositories.AchievementRepository;
import de.unistuttgart.overworldbackend.repositories.PlayerRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PlayerMapper playerMapper;

    @Autowired
    private AchievementRepository achievementRepository;

    public List<PlayerDTO> getPlayers()
    {
        return playerMapper.playersToPlayerDTOs(playerRepository.findAll());
    }

    public PlayerDTO getPlayer(final String playerId)
    {
        Optional<Player> player = playerRepository.findById(playerId);
        if(player.isPresent())
        {
            return playerMapper.playerToPlayerDTO(player.get());
        }
        else
        {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    String.format(
                            "There is no player with playerId %s", playerId
                    )
            );
        }
    }

    public PlayerDTO createPlayer(final PlayerInitialData playerInitialData)
    {
        final Optional<Player> existingPlayer = playerRepository.findById(
                playerInitialData.getUserId()
        );
        if (existingPlayer.isPresent()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                String.format("There is already a playerstatistic for userId %s", playerInitialData.getUserId())
            );
        }
        final Player newPlayer = new Player(playerInitialData.getUserId(), playerInitialData.getUsername());
        for (final Achievement achievement : achievementRepository.findAll()) {
            newPlayer.getAchievementStatistics().add(new AchievementStatistic(newPlayer, achievement));
        }
        final Player savedPlayer = playerRepository.save(newPlayer);
        return playerMapper.playerToPlayerDTO(savedPlayer);
    }
}
