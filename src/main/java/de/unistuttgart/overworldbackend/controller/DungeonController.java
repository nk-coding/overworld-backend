package de.unistuttgart.overworldbackend.controller;

import de.unistuttgart.overworldbackend.data.DungeonDTO;
import de.unistuttgart.overworldbackend.service.DungeonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Set;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Dungeon", description = "Get and update dungeons from worlds")
@RestController
@Slf4j
@RequestMapping("/lectures/{lectureId}/worlds/{worldId}/dungeons")
public class DungeonController {

  @Autowired
  private DungeonService dungeonService;

  @Operation(summary = "Get all dungeons of a world by its id from a lecture by its id")
  @GetMapping("")
  public Set<DungeonDTO> getDungeons(@PathVariable int lectureId, @PathVariable UUID worldId) {
    log.debug("get dungeons of world {} of lecture {}", worldId, lectureId);
    return dungeonService.getDungeonsFromLectureAndWorld(lectureId, worldId);
  }

  @Operation(summary = "Get a dungeon by its id of a world by its id from a lecture by its id")
  @GetMapping("/{dungeonId}")
  public DungeonDTO getDungeon(@PathVariable int lectureId, @PathVariable UUID worldId, @PathVariable UUID dungeonId) {
    log.debug("get dungeon {} of world {} of lecture {}", dungeonId, worldId, lectureId);
    return dungeonService.getDungeonFromLectureAndWorld(lectureId, worldId, dungeonId);
  }

  @Operation(summary = "Update a dungeon by its id of a world by its id from a lecture by its id")
  @PutMapping("/{dungeonId}")
  public DungeonDTO updateDungeon(
    @PathVariable int lectureId,
    @PathVariable UUID worldId,
    @PathVariable UUID dungeonId,
    @RequestBody DungeonDTO dungeonDTO
  ) {
    log.debug("update dungeon {} of world {} of lecture {}", dungeonId, worldId, lectureId);
    return dungeonService.updateDungeonFromLecture(lectureId, worldId, dungeonId, dungeonDTO);
  }
}
