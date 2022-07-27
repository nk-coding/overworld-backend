package de.unistuttgart.overworldbackend.controller;

import de.unistuttgart.overworldbackend.data.Player;
import de.unistuttgart.overworldbackend.data.PlayerStatisticDTO;
import de.unistuttgart.overworldbackend.data.mapper.PlayerStatisticMapper;
import de.unistuttgart.overworldbackend.service.PlayerStatisticService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Statistics", description = "Statistics of a player")
@RestController
@Slf4j
@RequestMapping("/lectures/{lectureId}/playerstatistics")
public class PlayerStatisticController {

  @Autowired
  private PlayerStatisticService playerStatisticService;

  @Autowired
  private PlayerStatisticMapper playerStatisticMapper;

  @Operation(summary = "Get a playerStatistic from a player in a lecture by playerId and lectureId")
  @GetMapping("/{playerId}")
  public PlayerStatisticDTO getPlayerstatistic(@PathVariable int lectureId, @PathVariable String playerId) {
    log.debug("get statistics from player {} in lecture {}", playerId, lectureId);
    return playerStatisticMapper.playerStatisticToPlayerstatisticDTO(
      playerStatisticService.getPlayerStatisticFromLecture(lectureId, playerId)
    );
  }

  @Operation(summary = "Create a playerStatistic in a lecture by playerId ")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("")
  public PlayerStatisticDTO createPlayerstatistic(@PathVariable int lectureId, @Valid @RequestBody Player player) {
    log.debug("create playerstatistic for userId {} in lecture {}", player, lectureId);
    return playerStatisticService.createPlayerStatisticInLecture(lectureId, player);
  }

  @Operation(summary = "Update a playerStatistic in a lecture by playerId ")
  @PutMapping("/{playerId}")
  public PlayerStatisticDTO updatePlayerStatistic(
    @PathVariable int lectureId,
    @PathVariable String playerId,
    @RequestBody PlayerStatisticDTO playerstatisticDTO
  ) {
    log.debug("update playerStatistic for userId {} in lecture {} with {}", playerId, lectureId, playerstatisticDTO);
    return playerStatisticService.updatePlayerStatisticInLecture(lectureId, playerId, playerstatisticDTO);
  }
}
