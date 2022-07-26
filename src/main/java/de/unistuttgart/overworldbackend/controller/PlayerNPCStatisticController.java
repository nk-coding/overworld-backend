package de.unistuttgart.overworldbackend.controller;

import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.repositories.PlayerNPCStatisticRepository;
import de.unistuttgart.overworldbackend.repositories.PlayerTaskStatisticRepository;
import de.unistuttgart.overworldbackend.service.PlayerNPCStatisticService;
import de.unistuttgart.overworldbackend.service.PlayerTaskStatisticService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Player Task Statistik", description = "Get and update dungeons from worlds")
@RestController
@Slf4j
@RequestMapping("/lectures/{lectureId}/playerstatistics/{playerId}/player-npc-statistics")
public class PlayerNPCStatisticController {

  @Autowired
  private PlayerNPCStatisticRepository playerNPCStatisticRepository;

  @Autowired
  private PlayerNPCStatisticService playerNPCStatisticService;

  @Operation(summary = "Get all NPC Statistics of Player")
  @GetMapping("")
  public List<PlayerNPCStatisticDTO> getPlayerNPCStatistics(
    @PathVariable int lectureId,
    @PathVariable String playerId
  ) {
    log.debug("get Statistics of Player {} in the Lecture {}", playerId, lectureId);
    return playerNPCStatisticService.getAllStatisticsOfPlayer(lectureId, playerId);
  }

  @Operation(summary = "Get Statistic of a Player by id")
  @GetMapping("/{statisticId}")
  public PlayerNPCStatisticDTO getPlayerNPCStatistic(
    @PathVariable int lectureId,
    @PathVariable String playerId,
    @PathVariable UUID statisticId
  ) {
    log.debug("get statistic {}", statisticId);
    return playerNPCStatisticService.getStatisticOfPlayer(lectureId, playerId, statisticId);
  }
}
