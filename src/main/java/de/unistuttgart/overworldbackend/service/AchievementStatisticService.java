package de.unistuttgart.overworldbackend.service;

import de.unistuttgart.overworldbackend.data.AchievementStatistic;
import de.unistuttgart.overworldbackend.data.AchievementStatisticDTO;
import de.unistuttgart.overworldbackend.data.enums.AchievementTitle;
import de.unistuttgart.overworldbackend.repositories.AchievementRepository;
import de.unistuttgart.overworldbackend.repositories.AchievementStatisticRepository;
import de.unistuttgart.overworldbackend.repositories.PlayerRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class AchievementStatisticService {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private AchievementStatisticRepository achievementStatisticRepository;

    /**
     * Returns all achievement statistics for a given player.
     * @param playerId the id of the player
     * @throws ResponseStatusException (404) if the player does not exist
     * @return a list of achievement statistics for the given player
     */
    public List<AchievementStatistic> getAchievementStatisticsFromPlayer(final String playerId) {
        return playerRepository
            .findById(playerId)
            .orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Player with id " + playerId + " does not exist")
            )
            .getAchievementStatistics();
    }

    /**
     * Returns the achievement statistic for a given player and achievement.
     * @param playerId the id of the player
     * @param achievementTitle the title of the achievement
     * @throws ResponseStatusException (404) if the player or the achievement does not exist
     * @return the achievement statistic for the given player and achievement
     */
    public AchievementStatistic getAchievementStatisticFromPlayer(
        final String playerId,
        final AchievementTitle achievementTitle
    ) {
        return getAchievementStatisticsFromPlayer(playerId)
            .stream()
            .filter(achievementStatistic ->
                achievementStatistic.getAchievement().getAchievementTitle().equals(achievementTitle)
            )
            .findFirst()
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format("There is no achievement statistic for achievement %s", achievementTitle)
                )
            );
    }

    /**
     * Updates the progress of the given achievement statistic
     * @param playerId the if of the player
     * @param achievementTitle the title of the achievement
     * @param achievementStatisticDTO the updated parameters
     * @throws ResponseStatusException (400) if the new progress is smaller than the current one
     * @throws ResponseStatusException (404) if the player or the achievement does not exist
     * @return the updated achievement statistic
     */
    public AchievementStatistic updateAchievementStatistic(
            final String playerId,
            final AchievementTitle achievementTitle,
            final AchievementStatisticDTO achievementStatisticDTO
            ) {
        final AchievementStatistic achievementStatistic = getAchievementStatisticFromPlayer(playerId, achievementTitle);
        try {
            achievementStatistic.setProgress(achievementStatisticDTO.getProgress());
        } catch (IllegalArgumentException e)
        {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                String.format("The new progress cannot be smaller than the current one")
            );
        }
        final AchievementStatistic updatedAchievementStatistic = achievementStatisticRepository.save(achievementStatistic);
        return updatedAchievementStatistic;
    }
}
