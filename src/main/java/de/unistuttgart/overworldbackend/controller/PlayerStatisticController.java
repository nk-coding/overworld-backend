package de.unistuttgart.overworldbackend.controller;

import de.unistuttgart.overworldbackend.data.LectureDTO;
import de.unistuttgart.overworldbackend.data.LectureInitialData;
import de.unistuttgart.overworldbackend.data.Player;
import de.unistuttgart.overworldbackend.data.PlayerstatisticDTO;
import de.unistuttgart.overworldbackend.data.mapper.PlayerstatisticMapper;
import de.unistuttgart.overworldbackend.service.PlayerStatisticService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
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

  @Operation(summary = "Get a playerstatistic by its playerId and lectureId")
  @GetMapping("/{userId}")
  public PlayerstatisticDTO getLecture(@PathVariable int lectureId, @PathVariable String userId) {
    log.debug("get statistics from player {} in lecture {}", userId, lectureId);
    return playerstatisticMapper.playerstatisticToPlayerstatisticDTO(
      playerStatisticService.getPlayerStatisticFromLecture(lectureId, userId)
    );
  }

  @Operation(summary = "Create a playerstatistic by its playerId in a lecture by its id")
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("")
  public PlayerstatisticDTO createLecture(@PathVariable int lectureId, @Valid @RequestBody Player player) {
    log.debug("create playerstatistic for playerId {} in lecture {}", player, lectureId);
    return playerStatisticService.createPlayerStatisticInLecture(lectureId, player);
  }
}
