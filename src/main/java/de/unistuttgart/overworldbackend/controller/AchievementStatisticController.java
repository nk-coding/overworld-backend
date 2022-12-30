package de.unistuttgart.overworldbackend.controller;

import de.unistuttgart.gamifyit.authentificationvalidator.JWTValidatorService;
import de.unistuttgart.overworldbackend.data.AchievementStatistic;
import de.unistuttgart.overworldbackend.data.enums.AchievementTitle;
import de.unistuttgart.overworldbackend.repositories.PlayerRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Tag(name = "AchievementStatistic", description = "Modify achievement statistic")
@RestController
@Slf4j
@RequestMapping("/players/{playerId}/achievements")
public class AchievementStatisticController {

    @Autowired
    JWTValidatorService jwtValidatorService;

    @Autowired
    private PlayerRepository playerRepository;

    @Operation(summary = "Get all achievements")
    @GetMapping("")
    public List<AchievementStatistic> getAchievementStatistics(@PathVariable final int playerId, @CookieValue("access_token") final String accessToken) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        log.debug("get achievements");
        return playerRepository.findById(playerId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("There is no player with id %s.", playerId))
        ).getAchievementStatistics();
    }

    @Operation(summary = "Get achievement by its title")
    @GetMapping("/{title}")
    public AchievementStatistic getAchievementStatisitc(
            @PathVariable final int playerId,
            @PathVariable final AchievementTitle title,
            @CookieValue("access_token") final String accessToken
    ) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        log.debug("get achievements {} ", title);
        return playerRepository.findById(playerId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("There is no player with id %s.", playerId))
        ).getAchievementStatistics().stream().filter(achievementStatistic -> achievementStatistic.getAchievement().getAchievementTitle().equals(title)).findFirst().orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("There is no achievement with title %s.", title))
        );
    }
}
