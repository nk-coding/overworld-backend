package de.unistuttgart.overworldbackend.controller;

import de.unistuttgart.overworldbackend.data.DungeonDTO;
import de.unistuttgart.overworldbackend.data.mapper.DungeonMapper;
import de.unistuttgart.overworldbackend.repositories.DungeonRepository;
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
@RequestMapping("/lectures/{lectureId}/worlds/{staticWorldName}/dungeons")
public class DungeonController {

  @Autowired
  private DungeonService dungeonService;

  @Autowired
  private DungeonRepository dungeonRepository;

  @Autowired
  private DungeonMapper dungeonMapper;

  @Operation(summary = "Get all dungeons of a world by its id from a lecture by its id")
  @GetMapping("")
  public Set<DungeonDTO> getDungeons(@PathVariable int lectureId, @PathVariable String staticWorldName) {
    log.debug("get dungeons of world by static name {} of lecture {}", staticWorldName, lectureId);
    return dungeonService.getDungeonsFromWorld(lectureId, staticWorldName);
  }

  @Operation(summary = "Get a dungeon by its static name of a world by its id from a lecture by its id")
  @GetMapping("/{staticDungeonName}")
  public DungeonDTO getDungeon(
    @PathVariable int lectureId,
    @PathVariable String staticWorldName,
    @PathVariable String staticDungeonName
  ) {
    log.debug("get dungeon {} of world {} of lecture {}", staticDungeonName, staticWorldName, lectureId);
    return dungeonMapper.dungeonToDungeonDTO(
      dungeonService.getDungeonByStaticNameFromLecture(lectureId, staticWorldName, staticDungeonName)
    );
  }

  @Operation(summary = "Update a dungeon by its static name of a world by its id from a lecture by its id")
  @PutMapping("/{staticDungeonName}")
  public DungeonDTO updateDungeon(
    @PathVariable int lectureId,
    @PathVariable String staticWorldName,
    @PathVariable String staticDungeonName,
    @RequestBody DungeonDTO dungeonDTO
  ) {
    log.debug("update dungeon {} of world {} of lecture {}", staticDungeonName, staticWorldName, lectureId);
    return dungeonService.updateDungeonFromLecture(lectureId, staticWorldName, staticDungeonName, dungeonDTO);
  }
}
