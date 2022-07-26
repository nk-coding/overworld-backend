package de.unistuttgart.overworldbackend.controller;

import de.unistuttgart.overworldbackend.data.Player;
import de.unistuttgart.overworldbackend.data.PlayerstatisticDTO;
import de.unistuttgart.overworldbackend.data.mapper.PlayerstatisticMapper;
import de.unistuttgart.overworldbackend.service.PlayerStatisticService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Player Statistic", description = "Player Statistic")
@RestController
@Slf4j
@RequestMapping("/lectures/{lectureId}/playerstatistics")
public class PlayerStatisticController {

  @Autowired
  private PlayerStatisticService playerStatisticService;

  @Autowired
  private PlayerstatisticMapper playerstatisticMapper;

  @Operation(summary = "Get a playerstatistic by its playerId and lectureId in a lecture")
  @GetMapping("/{playerId}")
  public PlayerstatisticDTO getPlayerstatistic(@PathVariable int lectureId, @PathVariable String playerId) {
    log.debug("get statistics from player {} in lecture {}", playerId, lectureId);
    return playerstatisticMapper.playerstatisticToPlayerstatisticDTO(
      playerStatisticService.getPlayerStatisticFromLecture(lectureId, playerId)
    );
  }

  @Operation(summary = "Create a playerstatistic by its playerId in a lecture")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("")
  public PlayerstatisticDTO createPlayerstatistic(@PathVariable int lectureId, @Valid @RequestBody Player player) {
    log.debug("create playerstatistic for userId {} in lecture {}", player, lectureId);
    return playerStatisticService.createPlayerStatisticInLecture(lectureId, player);
  }

  @Operation(summary = "Update a playerstatistic by its playerId in a lecture")
  @PutMapping("/{playerId}")
  public PlayerstatisticDTO updatePlayerStatistic(
    @PathVariable int lectureId,
    @PathVariable String playerId,
    @RequestBody PlayerstatisticDTO playerstatisticDTO
  ) {
    log.debug("update playerstatistic for userId {} in lecture {} with {}", playerId, lectureId, playerstatisticDTO);
    return playerStatisticService.updatePlayerStatisticInLecture(lectureId, playerId, playerstatisticDTO);
  }
}
