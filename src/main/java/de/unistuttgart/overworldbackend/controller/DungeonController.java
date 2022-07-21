package de.unistuttgart.overworldbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Dungeon", description = "Get and update dungeons from worlds")
@RestController
@Slf4j
@RequestMapping("/lectures/{lectureId}/worlds/{worldId}/dungeons")
public class DungeonController {

  @Operation(summary = "Get all dungeons of a world by its id from a lecture by its id")
  @GetMapping("")
  public void getDungeons(@PathVariable int lectureId, @PathVariable UUID worldId) {
    log.debug("get dungeons of world {} of lecture {}", worldId, lectureId);
  }

  @Operation(summary = "Get a dungeon by its id of a world by its id from a lecture by its id")
  @GetMapping("/{dungeonId}")
  public void getDungeon(@PathVariable int lectureId, @PathVariable UUID worldId, @PathVariable UUID dungeonId) {
    log.debug("get dungeon {} of world {} of lecture {}", dungeonId, worldId, lectureId);
  }

  @Operation(summary = "Update a dungeon by its id of a world by its id from a lecture by its id")
  @PutMapping("/{dungeonId}")
  public void updateWorld(@PathVariable int lectureId, @PathVariable UUID worldId, @PathVariable UUID dungeonId) {
    log.debug("update dungeon {} of world {} of lecture {}", dungeonId, worldId, lectureId);
  }
}
