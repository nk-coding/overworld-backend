package de.unistuttgart.overworldbackend.service;

import de.unistuttgart.overworldbackend.data.PlayerTaskActionLog;
import de.unistuttgart.overworldbackend.data.PlayerTaskStatistic;
import de.unistuttgart.overworldbackend.data.statistics.MinigameScoreHit;
import de.unistuttgart.overworldbackend.data.statistics.MinigameSuccessRateStatistic;
import de.unistuttgart.overworldbackend.repositories.PlayerTaskStatisticRepository;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class MinigameTaskStatisticService {

    @Autowired
    private PlayerTaskStatisticRepository playerTaskStatisticRepository;

    /**
     * Returns the success rate and amount of tries of a player for a minigame task till success
     * @param minigameTaskId id of the minigame task to get the statistic for
     * @return the minigame success rate statistic for the given minigame task
     */
    public MinigameSuccessRateStatistic getSuccessRateOfMinigame(final UUID minigameTaskId) {
        // get success rate of all PlayerTaskStatistics by minigameTaskId
        final List<PlayerTaskStatistic> playerTaskStatistics = playerTaskStatisticRepository.findByMinigameTaskId(
            minigameTaskId
        );
        final List<PlayerTaskStatistic> successfulPlayerTaskStatistics = playerTaskStatistics
            .stream()
            .filter(PlayerTaskStatistic::isCompleted)
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
     * For every score between 0 and 100 there is a MinigameScoreHit object with the score and the amount of players that hit that score.
     * If a score is not hit by any player, it is not included in the list except for 0 and 100.
     *
     * @param minigameTaskId id of the minigame task to get the statistic for
     * @return a minigame highscore distribution for the given minigame task
     */
    public List<MinigameScoreHit> getPlayerHighscoreDistributions(final UUID minigameTaskId) {
        final List<PlayerTaskStatistic> playerTaskStatistics = playerTaskStatisticRepository.findByMinigameTaskIdOrderByHighscore(
            minigameTaskId
        );
        final Map<Long, List<PlayerTaskStatistic>> results = playerTaskStatistics
            .parallelStream()
            .collect(Collectors.groupingByConcurrent(PlayerTaskStatistic::getHighscore));
        results.putIfAbsent(0l, List.of());
        results.putIfAbsent(100l, List.of());
        return results
            .entrySet()
            .parallelStream()
            .map(entry -> new MinigameScoreHit(entry.getKey(), entry.getValue().size()))
            .sorted(Comparator.comparing(MinigameScoreHit::getScore))
            .toList();
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
            .filter(e -> e.getDate() != null)
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
