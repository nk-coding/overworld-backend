package de.unistuttgart.overworldbackend.controller;

import de.unistuttgart.gamifyit.authentificationvalidator.JWTValidatorService;
import de.unistuttgart.overworldbackend.data.MinigameTaskDTO;
import de.unistuttgart.overworldbackend.data.statistics.MinigameHighscoreDistribution;
import de.unistuttgart.overworldbackend.data.statistics.MinigameSuccessRateStatistic;
import de.unistuttgart.overworldbackend.service.MinigameTaskService;
import de.unistuttgart.overworldbackend.service.MinigameTaskStatisticService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Tag(name = "Minigame Task", description = "Get and update minigame tasks from areas (world or dungeons)")
@RestController
@Slf4j
@RequestMapping("/courses/{courseId}/worlds/{worldIndex}")
public class MinigameTaskStatisticController {

    @Autowired
    private JWTValidatorService jwtValidatorService;

    @Autowired
    private MinigameTaskService minigameTaskService;

    @Autowired
    private MinigameTaskStatisticService minigameTaskStatisticService;

    @Operation(summary = "Get the success rate statistic of task by its index from a world")
    @GetMapping("/minigame-tasks/{taskIndex}/statistics/success-rate")
    public MinigameSuccessRateStatistic getMinigameTaskSuccessRateFromWorld(
        @PathVariable final int courseId,
        @PathVariable final int worldIndex,
        @PathVariable final int taskIndex,
        @CookieValue("access_token") final String accessToken
    ) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        log.debug("get success rate statistic for task {} of world {} of course {}", taskIndex, worldIndex, courseId);
        final MinigameTaskDTO minigame = minigameTaskService.getMinigameTaskFromArea(
            courseId,
            worldIndex,
            Optional.empty(),
            taskIndex
        );
        return minigameTaskStatisticService.getSuccessRateOfMinigame(minigame.getId());
    }

    @Operation(summary = "Get the success rate statistic of task by its index from a dungeon")
    @GetMapping("/dungeons/{dungoenIndex}/minigame-tasks/{taskIndex}/statistics/success-rate")
    public MinigameSuccessRateStatistic getMinigameTaskSuccessRateFromDungeon(
        @PathVariable final int courseId,
        @PathVariable final int worldIndex,
        @PathVariable final int dungoenIndex,
        @PathVariable final int taskIndex,
        @CookieValue("access_token") final String accessToken
    ) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        log.debug(
            "get success rate statistic for task {} of dungeon {} from world {} of course {}",
            taskIndex,
            dungoenIndex,
            worldIndex,
            courseId
        );
        final MinigameTaskDTO minigame = minigameTaskService.getMinigameTaskFromArea(
            courseId,
            worldIndex,
            Optional.of(dungoenIndex),
            taskIndex
        );
        return minigameTaskStatisticService.getSuccessRateOfMinigame(minigame.getId());
    }

    @Operation(summary = "Get the highscore distribution statistic of task by its index from a world")
    @GetMapping("/minigame-tasks/{taskIndex}/statistics/highscore-distribution")
    public List<MinigameHighscoreDistribution> getMinigameTaskHighscoreDistributionFromWorld(
        @PathVariable final int courseId,
        @PathVariable final int worldIndex,
        @PathVariable final int taskIndex,
        @RequestParam(required = false) final Optional<List<Integer>> timeDistributionPercentages,
        @CookieValue("access_token") final String accessToken
    ) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        log.debug(
            "get highscore distribution statistic for task {} of world {} of course {}",
            taskIndex,
            worldIndex,
            courseId
        );
        final MinigameTaskDTO minigame = minigameTaskService.getMinigameTaskFromArea(
            courseId,
            worldIndex,
            Optional.empty(),
            taskIndex
        );
        try {
            return minigameTaskStatisticService.getPlayerHighscoreDistributions(
                minigame.getId(),
                timeDistributionPercentages
            );
        } catch (final IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @Operation(summary = "Get the success rate statistic of task by its index from a dungeon")
    @GetMapping("/dungeons/{dungoenIndex}/minigame-tasks/{taskIndex}/statistics/highscore-distribution")
    public List<MinigameHighscoreDistribution> getMinigameTaskHighscoreDistributionFromDungeon(
        @PathVariable final int courseId,
        @PathVariable final int worldIndex,
        @PathVariable final int dungoenIndex,
        @PathVariable final int taskIndex,
        @RequestParam(required = false) final Optional<List<Integer>> timeDistributionPercentages,
        @CookieValue("access_token") final String accessToken
    ) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        log.debug(
            "get highscore distribution statistic for task {} of dungeon {} from world {} of course {}",
            taskIndex,
            dungoenIndex,
            worldIndex,
            courseId
        );
        final MinigameTaskDTO minigame = minigameTaskService.getMinigameTaskFromArea(
            courseId,
            worldIndex,
            Optional.of(dungoenIndex),
            taskIndex
        );
        try {
            return minigameTaskStatisticService.getPlayerHighscoreDistributions(
                minigame.getId(),
                timeDistributionPercentages
            );
        } catch (final IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }
}
