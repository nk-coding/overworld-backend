package de.unistuttgart.overworldbackend.service;

import de.unistuttgart.overworldbackend.data.AchievementStatistic;
import de.unistuttgart.overworldbackend.data.enums.AchievementTitle;
import de.unistuttgart.overworldbackend.repositories.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class AchievementStatisticService {

    @Autowired
    private PlayerRepository playerRepository;

    /**
     * Returns all achievement statistics for a given player.
     * @param playerId the id of the player
     * @throws ResponseStatusException (404) if the player does not exist
     * @return a list of achievement statistics for the given player
     */
    public List<AchievementStatistic> getAchievementStatisticsFromPlayer(final String playerId) {
        return playerRepository.findById(playerId).orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            String.format("There is no player with id %s", playerId)
        )).getAchievementStatistics();
    }

    /**
     * Returns the achievement statistic for a given player and achievement.
     * @param playerId the id of the player
     * @param achievementTitle the title of the achievement
     * @throws ResponseStatusException (404) if the player or the achievement does not exist
     * @return the achievement statistic for the given player and achievement
     */
    public AchievementStatistic getAchievementStatisticFromPlayer(final String playerId, final AchievementTitle achievementTitle) {
        return getAchievementStatisticsFromPlayer(playerId).stream()
            .filter(achievementStatistic -> achievementStatistic.getAchievement().getAchievementTitle().equals(achievementTitle))
            .findFirst().orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                String.format("There is no achievement statistic for achievement %s", achievementTitle)
            ));
    }
}
