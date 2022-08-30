package de.unistuttgart.overworldbackend.controller;

import de.unistuttgart.overworldbackend.data.PlayerTaskStatisticDTO;
import de.unistuttgart.overworldbackend.data.PlayerTaskStatisticData;
import de.unistuttgart.overworldbackend.service.PlayerTaskStatisticService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Submit statistic", description = "Submit statistics")
@RestController
@Slf4j
@RequestMapping("/internal")
public class MinigameInputController {

  @Autowired
  PlayerTaskStatisticService playerTaskStatisticService;

  @Operation(summary = "Submit statistics from a minigame run")
  @PostMapping("/submit-game-pass")
  public PlayerTaskStatisticDTO inputData(@Valid @RequestBody PlayerTaskStatisticData data) {
    log.debug("submitted data from game run {}", data);
    return playerTaskStatisticService.submitData(data);
  }
}
