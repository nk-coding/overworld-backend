package de.unistuttgart.overworldbackend.service;

import de.unistuttgart.overworldbackend.data.PlayerTaskActionLog;
import de.unistuttgart.overworldbackend.data.PlayerTaskStatistic;
import de.unistuttgart.overworldbackend.data.statistics.MinigameHighscoreDistribution;
import de.unistuttgart.overworldbackend.data.statistics.MinigameSuccessRateStatistic;
import de.unistuttgart.overworldbackend.repositories.MinigameTaskRepository;
import de.unistuttgart.overworldbackend.repositories.PlayerTaskStatisticRepository;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MinigameTaskStatisticService {

    @Autowired
    private PlayerTaskStatisticRepository playerTaskStatisticRepository;

    static final List DEFAULT_DISTRIBUTION_PERCENTAGES = Arrays.asList(0, 25, 50, 75, 100);

    /**
     * Returns the success rate and amount of tries of a player for a minigame task till success
     * @param minigameTaskId id of the minigame task to get the statistic for
     * @returns a the minigame success rate statistic for the given minigame task
     */
    public MinigameSuccessRateStatistic getSuccessRateOfMinigame(final UUID minigameTaskId) {
        // get success rate of all PlayerTaskStatistics by minigameTaskId
        final List<PlayerTaskStatistic> playerTaskStatistics = playerTaskStatisticRepository.findByMinigameTaskId(
            minigameTaskId
        );
        final List<PlayerTaskStatistic> successfulPlayerTaskStatistics = playerTaskStatistics
            .stream()
            .filter(playerTaskStatistic -> playerTaskStatistic.isCompleted())
            .toList();
        final List<PlayerTaskStatistic> failedPlayerTaskStatistics = playerTaskStatistics
            .stream()
            .filter(playerTaskStatistic -> !playerTaskStatistic.isCompleted())
            .toList();

        // collect tries of playerstatistic till success by counting up action logs till success
        final Map<PlayerTaskStatistic, Integer> successfulTries = successfulPlayerTaskStatistics
            .stream()
            .collect(
                Collectors.toMap(
                    Function.identity(),
                    taskStatistic ->
                        (int) getOrderedPlayerTaskActionLogByDate(taskStatistic)
                            .stream()
                            .takeWhile(log -> !this.wasTaskActionLogSuccessful(log))
                            .count() +
                        1
                )
            );
        final Map<PlayerTaskStatistic, Integer> failedTries = failedPlayerTaskStatistics
            .stream()
            .collect(Collectors.toMap(Function.identity(), statistic -> statistic.getPlayerTaskActionLogs().size()));
        final double successRate = (double) successfulPlayerTaskStatistics.size() / playerTaskStatistics.size();
        final Map<Integer, Integer> successRateDistribution = new HashMap<>();
        successfulTries.forEach((playerTaskStatistic, tries) ->
            successRateDistribution.put(tries, successRateDistribution.getOrDefault(tries, 0) + 1)
        );
        final Map<Integer, Integer> failRateDistribution = new HashMap<>();
        failedTries.forEach((playerTaskStatistic, tries) ->
            failRateDistribution.put(tries, failRateDistribution.getOrDefault(tries, 0) + 1)
        );
        return new MinigameSuccessRateStatistic(successRateDistribution, failRateDistribution, successRate);
    }

    /**
     * Returns the highscore distribution of a minigame task
     *
     * @param minigameTaskId id of the minigame task to get the statistic for
     * @param highscorePercentages list of percentages to get the highscore distribution for, e.g. [0, 25, 50, 75, 100]
     * @throws IllegalArgumentException if highscorePercentages is not sorted, has not as first value 0, at last value 100 or contains values < 0 or > 100
     * @return a minigame highscore distribution for the given minigame task
     */
    public List<MinigameHighscoreDistribution> getPlayerHighscoreDistributions(
        final UUID minigameTaskId,
        final Optional<List<Integer>> highscorePercentages
    ) {
        final List<Integer> highscorePercentagesToUse;
        if (highscorePercentages.isEmpty()) {
            highscorePercentagesToUse = DEFAULT_DISTRIBUTION_PERCENTAGES;
        } else {
            highscorePercentagesToUse = highscorePercentages.get();
        }
        if (highscorePercentagesToUse.size() < 2) {
            throw new IllegalArgumentException("high score percentages must have at least 2 elements");
        }
        if (highscorePercentagesToUse.get(0) != 0) {
            throw new IllegalArgumentException("high score percentages must start with 0");
        }
        if (highscorePercentagesToUse.get(highscorePercentagesToUse.size() - 1) != 100) {
            throw new IllegalArgumentException("high score percentages must end with 100");
        }
        final List<PlayerTaskStatistic> playerTaskStatistics = playerTaskStatisticRepository.findByMinigameTaskIdOrderByHighscore(
            minigameTaskId
        );
        final List<MinigameHighscoreDistribution> highscoreDistributions = new ArrayList<>();
        for (int i = 0; i < highscorePercentagesToUse.size() - 1; i++) {
            MinigameHighscoreDistribution highscoreDistribution = new MinigameHighscoreDistribution();
            highscoreDistribution.setFromPercentage(highscorePercentagesToUse.get(i));
            highscoreDistribution.setToPercentage(highscorePercentagesToUse.get(i + 1));
            highscoreDistributions.add(highscoreDistribution);
        }

        // calculate score time borders to time spent distribution percentage
        int currentStatisticIndex = 0;
        for (final MinigameHighscoreDistribution highscoreDistribution : highscoreDistributions) {
            final int endIndex = (int) (
                (highscoreDistribution.getToPercentage() / 100.0) * (playerTaskStatistics.size() - 1)
            );
            PlayerTaskStatistic currentStatistic = playerTaskStatistics.get(currentStatisticIndex);
            highscoreDistribution.setFromScore(currentStatistic.getHighscore());
            while (currentStatisticIndex <= endIndex) {
                currentStatistic = playerTaskStatistics.get(currentStatisticIndex);
                highscoreDistribution.addCount();
                currentStatisticIndex++;
            }
            highscoreDistribution.setToScore(currentStatistic.getHighscore());
        }
        return highscoreDistributions;
    }

    /**
     * Gets the ordered player task action logs by date
     * @param successfulPlayerTaskStatistic the player task statistic to get the logs for
     * @return the ordered player task action logs by date
     */
    private List<PlayerTaskActionLog> getOrderedPlayerTaskActionLogByDate(
        final PlayerTaskStatistic successfulPlayerTaskStatistic
    ) {
        return successfulPlayerTaskStatistic
            .getPlayerTaskActionLogs()
            .stream()
            .sorted(Comparator.comparing(PlayerTaskActionLog::getDate))
            .toList();
    }

    /**
     * @param log the action log of the game run
     * @return wether the game run of the action run was successful or not
     */
    private boolean wasTaskActionLogSuccessful(final PlayerTaskActionLog log) {
        return log.getScore() >= PlayerTaskStatisticService.COMPLETED_SCORE;
    }
}
