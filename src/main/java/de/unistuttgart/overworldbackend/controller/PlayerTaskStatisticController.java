package de.unistuttgart.overworldbackend.controller;

import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.repositories.PlayerTaskStatisticRepository;
import de.unistuttgart.overworldbackend.service.PlayerTaskStatisticService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Player statistic", description = "Get minigame statistics for a player")
@RestController
@Slf4j
@RequestMapping("/lectures/{lectureId}/playerstatistics/{playerId}/player-task-statistics")
public class PlayerTaskStatisticController {

  @Autowired
  private PlayerTaskStatisticRepository playerTaskStatisticRepository;

  @Autowired
  private PlayerTaskStatisticService playerTaskStatisticService;

  @Operation(summary = "Get all minigame Statistics of Player")
  @GetMapping("")
  public List<PlayerTaskStatisticDTO> getPlayerTaskStatistics(
    @PathVariable int lectureId,
    @PathVariable String playerId
  ) {
    log.debug("get Statistics of Player {} in the Lecture {}", playerId, lectureId);
    return playerTaskStatisticService.getAllStatisticsOfPlayer(lectureId, playerId);
  }

  @Operation(summary = "Get Statistic of a Player by id")
  @GetMapping("/{statisticId}")
  public PlayerTaskStatisticDTO getPlayerTaskStatistic(
    @PathVariable int lectureId,
    @PathVariable String playerId,
    @PathVariable UUID statisticId
  ) {
    log.debug("get statistic {}", statisticId);
    return playerTaskStatisticService.getStatisticOfPlayer(lectureId, playerId, statisticId);
  }
}
