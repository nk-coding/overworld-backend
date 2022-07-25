package de.unistuttgart.overworldbackend.controller;

import de.unistuttgart.overworldbackend.data.MinigameTaskDTO;
import de.unistuttgart.overworldbackend.service.MinigameTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Set;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Minigame Task", description = "Get and update minigame tasks from areas (world or dungeons)")
@RestController
@Slf4j
@RequestMapping("/lectures/{lectureId}/worlds/{staticWorldName}")
public class MinigameTaskController {

  @Autowired
  private MinigameTaskService minigameTaskService;

  @Operation(summary = "Get all task from a world")
  @GetMapping("/minigame-tasks")
  public Set<MinigameTaskDTO> getMinigameTasksFromWorld(@PathVariable int lectureId, @PathVariable String staticWorldName) {
    log.debug("get tasks of world {} of lecture {}", staticWorldName, lectureId);
    return minigameTaskService.getMinigameTasksFromArea(lectureId, staticWorldName);
  }

  @Operation(summary = "Get all tasks from a dungeon")
  @GetMapping("/dungeons/{staticDungeonName}/minigame-tasks")
  public Set<MinigameTaskDTO> getMinigameTasksFromDungeon(
    @PathVariable int lectureId,
    @PathVariable String staticWorldName,
    @PathVariable String staticDungeonName
  ) {
    log.debug("get tasks of dungeon {} from world {} of lecture {}", staticDungeonName, staticWorldName, lectureId);
    return minigameTaskService.getMinigameTasksFromArea(lectureId, staticWorldName, staticDungeonName);
  }

  @Operation(summary = "Get a task by its id from a world")
  @GetMapping("/minigame-tasks/{taskId}")
  public MinigameTaskDTO getMinigameTaskFromWorld(
    @PathVariable int lectureId,
    @PathVariable String staticWorldName,
    @PathVariable UUID taskId
  ) {
    log.debug("get tasks {} of world {} of lecture {}", taskId, staticWorldName, lectureId);
    return minigameTaskService.getMinigameTaskFromArea(lectureId, staticWorldName, taskId);
  }

  @Operation(summary = "Get a task by its id from a dungeon")
  @GetMapping("/dungeons/{staticDungeonName}/minigame-tasks/{taskId}")
  public MinigameTaskDTO getMinigameTaskFromDungeon(
    @PathVariable int lectureId,
    @PathVariable String staticWorldName,
    @PathVariable String staticDungeonName,
    @PathVariable UUID taskId
  ) {
    log.debug("get task {} of dungeon {} from world {} of lecture {}", taskId, staticDungeonName, staticWorldName, lectureId);
    return minigameTaskService.getMinigameTaskFromArea(lectureId, staticDungeonName, taskId);
  }

  @Operation(summary = "Update a task by its id from a world")
  @PutMapping("/minigame-tasks/{taskId}")
  public MinigameTaskDTO updateMinigameTasksFromWorld(
    @PathVariable int lectureId,
    @PathVariable String staticWorldName,
    @PathVariable UUID taskId,
    @RequestBody MinigameTaskDTO minigameTaskDTO
  ) {
    log.debug("update task {} of world {} of lecture {}", taskId, staticWorldName, lectureId);
    return minigameTaskService.updateMinigameTaskFromArea(lectureId, staticWorldName, taskId, minigameTaskDTO);
  }

  @Operation(summary = "Update a task by its id from a dungeon")
  @PutMapping("/dungeons/{staticDungeonName}/minigame-tasks/{taskId}")
  public MinigameTaskDTO updateMinigameTasksFromDungeon(
    @PathVariable int lectureId,
    @PathVariable String staticWorldName,
    @PathVariable String staticDungeonName,
    @PathVariable UUID taskId,
    @RequestBody MinigameTaskDTO minigameTaskDTO
  ) {
    log.debug("update task {} of dungeon {} from world {} of lecture {}", taskId, staticDungeonName, staticWorldName, lectureId);
    return minigameTaskService.updateMinigameTaskFromArea(lectureId, staticDungeonName, taskId, minigameTaskDTO);
  }
}
