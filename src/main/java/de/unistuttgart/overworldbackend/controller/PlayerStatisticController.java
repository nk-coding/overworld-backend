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

@Tag(name = "Player statistic", description = "Statistics of a player")
@RestController
@Slf4j
@RequestMapping("/courses/{courseId}/playerstatistics")
public class PlayerStatisticController {

  @Autowired
  private PlayerStatisticService playerStatisticService;

  @Autowired
  private PlayerStatisticMapper playerStatisticMapper;

  @Operation(summary = "Get a playerStatistic from a player in a course by playerId and courseId")
  @GetMapping("/{playerId}")
  public PlayerStatisticDTO getPlayerstatistic(@PathVariable int courseId, @PathVariable String playerId) {
    log.debug("get statistics from player {} in course {}", playerId, courseId);
    return playerStatisticMapper.playerStatisticToPlayerstatisticDTO(
      playerStatisticService.getPlayerStatisticFromCourse(courseId, playerId)
    );
  }

  @Operation(summary = "Create a playerStatistic in a course by playerId ")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("")
  public PlayerStatisticDTO createPlayerstatistic(@PathVariable int courseId, @Valid @RequestBody Player player) {
    log.debug("create playerstatistic for userId {} in course {}", player, courseId);
    return playerStatisticService.createPlayerStatisticInCourse(courseId, player);
  }

  @Operation(summary = "Update a playerStatistic in a course by playerId ")
  @PutMapping("/{playerId}")
  public PlayerStatisticDTO updatePlayerStatistic(
    @PathVariable int courseId,
    @PathVariable String playerId,
    @RequestBody PlayerStatisticDTO playerstatisticDTO
  ) {
    log.debug("update playerStatistic for userId {} in course {} with {}", playerId, courseId, playerstatisticDTO);
    return playerStatisticService.updatePlayerStatisticInCourse(courseId, playerId, playerstatisticDTO);
  }
}
