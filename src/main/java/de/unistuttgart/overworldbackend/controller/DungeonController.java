package de.unistuttgart.overworldbackend.controller;

import de.unistuttgart.overworldbackend.data.DungeonDTO;
import de.unistuttgart.overworldbackend.data.mapper.DungeonMapper;
import de.unistuttgart.overworldbackend.service.DungeonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Tag(name = "Dungeon", description = "Get and update dungeons from worlds")
@RestController
@Slf4j
@RequestMapping("/lectures/{lectureId}/worlds/{worldIndex}/dungeons")
public class DungeonController {

  @Autowired
  private DungeonService dungeonService;

  @Autowired
  private DungeonMapper dungeonMapper;

  @Operation(summary = "Get all dungeons of a world by its id from a lecture by its id")
  @GetMapping("")
  public Set<DungeonDTO> getDungeons(@PathVariable int lectureId, @PathVariable int worldIndex) {
    log.debug("get dungeons of world by index {} of lecture {}", worldIndex, lectureId);
    return dungeonService.getDungeonsFromWorld(lectureId, worldIndex);
  }

  @Operation(summary = "Get a dungeon by its static name of a world by its id from a lecture by its id")
  @GetMapping("/{dungeonIndex}")
  public DungeonDTO getDungeon(
    @PathVariable int lectureId,
    @PathVariable int worldIndex,
    @PathVariable int dungeonIndex
  ) {
    log.debug("get dungeon {} of world {} of lecture {}", dungeonIndex, worldIndex, lectureId);
    return dungeonMapper.dungeonToDungeonDTO(
      dungeonService.getDungeonByIndexFromLecture(lectureId, worldIndex, dungeonIndex)
    );
  }

  @Operation(summary = "Update a dungeon by its static name of a world by its id from a lecture by its id")
  @PutMapping("/{dungeonIndex}")
  public DungeonDTO updateDungeon(
    @PathVariable int lectureId,
    @PathVariable int worldIndex,
    @PathVariable int dungeonIndex,
    @RequestBody DungeonDTO dungeonDTO
  ) {
    log.debug("update dungeon {} of world {} of lecture {}", dungeonIndex, worldIndex, lectureId);
    return dungeonService.updateDungeonFromLecture(lectureId, worldIndex, dungeonIndex, dungeonDTO);
  }
}
