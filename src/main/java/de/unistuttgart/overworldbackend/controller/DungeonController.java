package de.unistuttgart.overworldbackend.controller;

import de.unistuttgart.overworldbackend.data.DungeonDTO;
import de.unistuttgart.overworldbackend.data.mapper.DungeonMapper;
import de.unistuttgart.overworldbackend.service.DungeonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Dungeon", description = "Get and update dungeons from worlds")
@RestController
@Slf4j
@RequestMapping("/courses/{courseId}/worlds/{worldIndex}/dungeons")
public class DungeonController {

  @Autowired
  private DungeonService dungeonService;

  @Autowired
  private DungeonMapper dungeonMapper;

  @Operation(summary = "Get all dungeons of a world")
  @GetMapping("")
  public Set<DungeonDTO> getDungeons(@PathVariable int courseId, @PathVariable int worldIndex) {
    log.debug("get dungeons of world {} of course {}", worldIndex, courseId);
    return dungeonService.getDungeonsFromWorld(courseId, worldIndex);
  }

  @Operation(summary = "Get a dungeon by its index in a world")
  @GetMapping("/{dungeonIndex}")
  public DungeonDTO getDungeon(
    @PathVariable int courseId,
    @PathVariable int worldIndex,
    @PathVariable int dungeonIndex
  ) {
    log.debug("get dungeon {} of world {} of course {}", dungeonIndex, worldIndex, courseId);
    return dungeonMapper.dungeonToDungeonDTO(
      dungeonService.getDungeonByIndexFromCourse(courseId, worldIndex, dungeonIndex)
    );
  }

  @Operation(summary = "Update a dungeon by its index in a world")
  @PutMapping("/{dungeonIndex}")
  public DungeonDTO updateDungeon(
    @PathVariable int courseId,
    @PathVariable int worldIndex,
    @PathVariable int dungeonIndex,
    @RequestBody DungeonDTO dungeonDTO
  ) {
    log.debug("update dungeon {} of world {} of course {} with {}", dungeonIndex, worldIndex, courseId, dungeonDTO);
    return dungeonService.updateDungeonFromCourse(courseId, worldIndex, dungeonIndex, dungeonDTO);
  }
}
