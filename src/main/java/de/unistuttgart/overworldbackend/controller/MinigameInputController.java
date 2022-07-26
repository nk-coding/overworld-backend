package de.unistuttgart.overworldbackend.controller;

import de.unistuttgart.overworldbackend.data.PlayerTaskStatisticDTO;
import de.unistuttgart.overworldbackend.data.PlayerTaskStatisticData;
import de.unistuttgart.overworldbackend.service.PlayerTaskStatisticService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Submit game data", description = "Submit a game statistic")
@RestController
@Slf4j
@RequestMapping("/internal")
public class MinigameInputController {

  @Autowired
  PlayerTaskStatisticService playerTaskStatisticService;

  @Valid
  @Operation(summary = "Input Data from a minigame")
  @PostMapping("/submit-game-pass")
  public PlayerTaskStatisticDTO inputData(@RequestBody PlayerTaskStatisticData data) {
    log.debug("submitted data {}", data);
    return playerTaskStatisticService.submitData(data);
  }
}
