package de.unistuttgart.overworldbackend.controller;

import de.unistuttgart.gamifyit.authentificationvalidator.JWTValidatorService;
import de.unistuttgart.overworldbackend.data.PlayerNPCStatisticDTO;
import de.unistuttgart.overworldbackend.service.PlayerNPCStatisticService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Player statistic", description = "Get NPC statistics for a player")
@RestController
@Slf4j
@RequestMapping("/courses/{courseId}/playerstatistics")
public class PlayerNPCStatisticController {

  @Autowired
  JWTValidatorService jwtValidatorService;

  @Autowired
  private PlayerNPCStatisticService playerNPCStatisticService;

  @Operation(summary = "Get all NPC statistics of a player")
  @GetMapping("/{playerId}/player-npc-statistics")
  public List<PlayerNPCStatisticDTO> getPlayerNPCStatistics(
    @PathVariable int courseId,
    @PathVariable String playerId,
    @CookieValue("access_token") final String accessToken
  ) {
    jwtValidatorService.validateTokenOrThrow(accessToken);
    log.debug("get statistics of player {} in the course {}", playerId, courseId);
    return playerNPCStatisticService.getAllStatisticsOfPlayer(courseId, playerId);
  }

  @Operation(summary = "Get specific NPC statistic of a player")
  @GetMapping("/{playerId}/player-npc-statistics/{statisticId}")
  public PlayerNPCStatisticDTO getPlayerNPCStatistic(
    @PathVariable int courseId,
    @PathVariable String playerId,
    @PathVariable UUID statisticId,
    @CookieValue("access_token") final String accessToken
  ) {
    jwtValidatorService.validateTokenOrThrow(accessToken);
    log.debug("get statistic {}", statisticId);
    return playerNPCStatisticService.getStatisticOfPlayer(courseId, playerId, statisticId);
  }

  @Operation(summary = "Get all NPC statistics of own player, player id is read from cookie")
  @GetMapping("/player-npc-statistics")
  public List<PlayerNPCStatisticDTO> getOwnPlayerNPCStatistics(
    @PathVariable int courseId,
    @CookieValue("access_token") final String accessToken
  ) {
    jwtValidatorService.validateTokenOrThrow(accessToken);
    String playerId = jwtValidatorService.extractUserId(accessToken);
    log.debug("get statistics of player by cookie {} in the course {}", playerId, courseId);
    return playerNPCStatisticService.getAllStatisticsOfPlayer(courseId, playerId);
  }

  @Operation(summary = "Get own NPC statistic, player id is read from cookie")
  @GetMapping("/player-npc-statistics/{statisticId}")
  public PlayerNPCStatisticDTO getOwnPlayerNPCStatistic(
    @PathVariable int courseId,
    @PathVariable UUID statisticId,
    @CookieValue("access_token") final String accessToken
  ) {
    jwtValidatorService.validateTokenOrThrow(accessToken);
    String playerId = jwtValidatorService.extractUserId(accessToken);
    log.debug("get statistic by cookie {}", statisticId);
    return playerNPCStatisticService.getStatisticOfPlayer(courseId, playerId, statisticId);
  }
}
