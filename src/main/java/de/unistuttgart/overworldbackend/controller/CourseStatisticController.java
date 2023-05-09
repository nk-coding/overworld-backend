package de.unistuttgart.overworldbackend.controller;

import de.unistuttgart.gamifyit.authentificationvalidator.JWTValidatorService;
import de.unistuttgart.overworldbackend.data.statistics.CompletedMinigames;
import de.unistuttgart.overworldbackend.data.statistics.LastPlayed;
import de.unistuttgart.overworldbackend.data.statistics.PlayerJoinedStatistic;
import de.unistuttgart.overworldbackend.data.statistics.UnlockedAreaAmount;
import de.unistuttgart.overworldbackend.service.CourseStatisticService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "CourseStatistic", description = "get the statistics of a course")
@RestController
@Slf4j
@RequestMapping("/courses/{courseId}/statistics")
public class CourseStatisticController {

    @Autowired
    private JWTValidatorService jwtValidatorService;

    @Autowired
    private CourseStatisticService courseStatisticService;

    @Operation(summary = "Player joined the course")
    @GetMapping("/players-joined")
    public PlayerJoinedStatistic getPlayerJoinedStatistic(
        @PathVariable final int courseId,
        @CookieValue("access_token") final String accessToken
    ) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        log.debug("get player joined statistic");
        return courseStatisticService.getPlayerJoinedStatistic(courseId);
    }

    @Operation(summary = "Last time a player played")
    @GetMapping("/last-played")
    public List<LastPlayed> getLastPlayed(
        @PathVariable final int courseId,
        @CookieValue("access_token") final String accessToken
    ) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        log.debug("get last active players statistic");
        return courseStatisticService.getLastPlayed(courseId);
    }

    @Operation(summary = "Unlocked areas")
    @GetMapping("/unlocked-areas")
    public List<UnlockedAreaAmount> getPlayerUnlocked(
        @PathVariable final int courseId,
        @CookieValue("access_token") final String accessToken
    ) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        log.debug("get unlocked areas statistic");
        return courseStatisticService.getUnlockedAreas(courseId);
    }

    @Operation(summary = "Completed minigames by players")
    @GetMapping("/completed-minigames")
    public List<CompletedMinigames> getCompletedMinigames(
        @PathVariable final int courseId,
        @CookieValue("access_token") final String accessToken
    ) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        log.debug("get completed minigames by players");
        return courseStatisticService.getCompletedMinigames(courseId);
    }
}
