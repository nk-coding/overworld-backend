package de.unistuttgart.overworldbackend.controller;

import de.unistuttgart.overworldbackend.data.WorldDTO;
import de.unistuttgart.overworldbackend.service.WorldService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Set;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "World", description = "Get and update worlds from a lecture")
@RestController
@Slf4j
@RequestMapping("/lectures/{lectureId}/worlds")
public class WorldController {

  @Autowired
  private WorldService worldService;

  @Operation(summary = "Get all worlds from a lecture by its id")
  @GetMapping("")
  public Set<WorldDTO> getWorlds(@PathVariable int lectureId) {
    log.debug("get worlds of lecture {}", lectureId);
    return worldService.getWorldsFromLecture(lectureId);
  }

  @Operation(summary = "Get a world by its id from a lecture by its id")
  @GetMapping("/{worldId}")
  public WorldDTO getWorld(@PathVariable int lectureId, @PathVariable UUID worldId) {
    log.debug("get world {} of lecture {}", worldId, lectureId);
    return worldService.getWorldFromLecture(lectureId, worldId);
  }

  @Operation(summary = "Update a world by its id from a lecture by its id")
  @PutMapping("/{worldId}")
  public WorldDTO updateWorld(@PathVariable int lectureId, @PathVariable UUID worldId, @RequestBody WorldDTO worldDTO) {
    log.debug("update world {} of lecture {}", worldId, lectureId);
    return worldService.updateWorldFromLecture(lectureId, worldId, worldDTO);
  }
}
