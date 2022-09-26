package de.unistuttgart.overworldbackend.controller;

import de.unistuttgart.gamifyit.authentificationvalidator.JWTValidatorService;
import de.unistuttgart.overworldbackend.data.PlayerTaskStatisticDTO;
import de.unistuttgart.overworldbackend.repositories.PlayerTaskStatisticRepository;
import de.unistuttgart.overworldbackend.service.PlayerTaskStatisticService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Player statistic", description = "Get minigame statistics for a player")
@RestController
@Slf4j
@RequestMapping("/courses/{courseId}/playerstatistics")
public class PlayerTaskStatisticController {

  @Autowired
  JWTValidatorService jwtValidatorService;

  @Autowired
  private PlayerTaskStatisticService playerTaskStatisticService;

  @Operation(summary = "Get all minigame statistics of a player")
  @GetMapping("/{playerId}/player-task-statistics")
  public List<PlayerTaskStatisticDTO> getPlayerTaskStatistics(
    @PathVariable int courseId,
    @PathVariable String playerId,
    @CookieValue("access_token") final String accessToken
  ) {
    jwtValidatorService.validateTokenOrThrow(accessToken);
    log.debug("get Statistics of Player {} in the course {}", playerId, courseId);
    return playerTaskStatisticService.getAllStatisticsOfPlayer(courseId, playerId);
  }

  @Operation(summary = "Get a minigame statistic of a player by minigame statistic id")
  @GetMapping("/{playerId}/player-task-statistics/{statisticId}")
  public PlayerTaskStatisticDTO getPlayerTaskStatistic(
    @PathVariable int courseId,
    @PathVariable String playerId,
    @PathVariable UUID statisticId,
    @CookieValue("access_token") final String accessToken
  ) {
    jwtValidatorService.validateTokenOrThrow(accessToken);
    log.debug("get statistic {}", statisticId);
    return playerTaskStatisticService.getStatisticOfPlayer(courseId, playerId, statisticId);
  }

  @Operation(summary = "Get all minigame statistics of a player, player id is read from cookie")
  @GetMapping("/player-task-statistics")
  public List<PlayerTaskStatisticDTO> getOwnPlayerTaskStatistics(
    @PathVariable int courseId,
    @CookieValue("access_token") final String accessToken
  ) {
    jwtValidatorService.validateTokenOrThrow(accessToken);
    String playerId = jwtValidatorService.extractUserId(accessToken);
    log.debug("get Statistics of Player {} in the course {}", playerId, courseId);
    return playerTaskStatisticService.getAllStatisticsOfPlayer(courseId, playerId);
  }

  @Operation(summary = "Get minigame statistic of a player by minigame statistic id, player id is read from cookie")
  @GetMapping("/player-task-statistics/{statisticId}")
  public PlayerTaskStatisticDTO getOwnPlayerTaskStatistic(
    @PathVariable int courseId,
    @PathVariable UUID statisticId,
    @CookieValue("access_token") final String accessToken
  ) {
    jwtValidatorService.validateTokenOrThrow(accessToken);
    String playerId = jwtValidatorService.extractUserId(accessToken);
    log.debug("get statistic {}", statisticId);
    return playerTaskStatisticService.getStatisticOfPlayer(courseId, playerId, statisticId);
  }
}
