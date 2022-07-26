package de.unistuttgart.overworldbackend.controller;

import de.unistuttgart.overworldbackend.data.PlayerNPCStatisticDTO;
import de.unistuttgart.overworldbackend.data.PlayerNPCStatisticData;
import de.unistuttgart.overworldbackend.service.PlayerNPCStatisticService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Submit game data", description = "Submit a NPC statistic")
@RestController
@Slf4j
@RequestMapping("/internal")
public class NPCInputController {

  @Autowired
  PlayerNPCStatisticService playerNPCStatisticService;

  @Valid
  @Operation(summary = "Input Data from a npc")
  @PostMapping("/submit-npc-pass")
  public PlayerNPCStatisticDTO inputData(@RequestBody PlayerNPCStatisticData data) {
    log.debug("submitted data {}", data);
    return playerNPCStatisticService.submitData(data);
  }
}
