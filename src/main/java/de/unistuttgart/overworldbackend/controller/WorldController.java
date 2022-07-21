package de.unistuttgart.overworldbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Tag(name = "World", description = "Get and update worlds from a lecture")
@RestController
@Slf4j
@RequestMapping("/lectures/{lectureId}/worlds")
public class WorldController {

  @Operation(summary = "Get all worlds from a lecture by its id")
  @GetMapping("")
  public void getWorlds(@PathVariable int lectureId) {
    log.debug("get worlds of lecture {}", lectureId);
  }

  @Operation(summary = "Get a world by its id from a lecture by its id")
  @GetMapping("/{worldId}")
  public void getWorld(@PathVariable int lectureId, @PathVariable UUID worldId) {
    log.debug("get world {} of lecture {}", worldId, lectureId);
  }

  @Operation(summary = "Update a world by its id from a lecture by its id")
  @PutMapping("/{worldId}")
  public void updateWorld(@PathVariable int lectureId, @PathVariable UUID worldId) {
    log.debug("update world {} of lecture {}", worldId, lectureId);
  }
}
