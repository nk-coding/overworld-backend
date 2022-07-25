package de.unistuttgart.overworldbackend.controller;

import de.unistuttgart.overworldbackend.data.MinigameTaskDTO;
import de.unistuttgart.overworldbackend.service.MinigameTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Tag(name = "Minigame Task", description = "Get and update minigame tasks from areas (world or dungeons)")
@RestController
@Slf4j
@RequestMapping("/lectures/{lectureId}/worlds/{worldIndex}")
public class MinigameTaskController {

  @Autowired
  private MinigameTaskService minigameTaskService;

  @Operation(summary = "Get all task from a world")
  @GetMapping("/minigame-tasks")
  public Set<MinigameTaskDTO> getMinigameTasksFromWorld(@PathVariable int lectureId, @PathVariable int worldIndex) {
    log.debug("get tasks of world {} of lecture {}", worldIndex, lectureId);
    return minigameTaskService.getMinigameTasksFromArea(lectureId, worldIndex, Optional.empty());
  }

  @Operation(summary = "Get all tasks from a dungeon")
  @GetMapping("/dungeons/{dungeonIndex}/minigame-tasks")
  public Set<MinigameTaskDTO> getMinigameTasksFromDungeon(
    @PathVariable int lectureId,
    @PathVariable int worldIndex,
    @PathVariable int dungeonIndex
  ) {
    log.debug("get tasks of dungeon {} from world {} of lecture {}", dungeonIndex, worldIndex, lectureId);
    return minigameTaskService.getMinigameTasksFromArea(lectureId, worldIndex, dungeonIndex);
  }

  @Operation(summary = "Get a task by its id from a world")
  @GetMapping("/minigame-tasks/{taskId}")
  public MinigameTaskDTO getMinigameTaskFromWorld(
    @PathVariable int lectureId,
    @PathVariable int worldIndex,
    @PathVariable UUID taskId
  ) {
    log.debug("get tasks {} of world {} of lecture {}", taskId, worldIndex, lectureId);
    return minigameTaskService.getMinigameTaskFromArea(lectureId, worldIndex, Optional.empty(), taskId);
  }

  @Operation(summary = "Get a task by its id from a dungeon")
  @GetMapping("/dungeons/{dungoenIndex}/minigame-tasks/{taskId}")
  public MinigameTaskDTO getMinigameTaskFromDungeon(
    @PathVariable int lectureId,
    @PathVariable int worldIndex,
    @PathVariable int dungoenIndex,
    @PathVariable UUID taskId
  ) {
    log.debug("get task {} of dungeon {} from world {} of lecture {}", taskId, dungoenIndex, worldIndex, lectureId);
    return minigameTaskService.getMinigameTaskFromArea(lectureId, worldIndex, Optional.of(dungoenIndex), taskId);
  }

  @Operation(summary = "Update a task by its id from a world")
  @PutMapping("/minigame-tasks/{taskId}")
  public MinigameTaskDTO updateMinigameTasksFromWorld(
    @PathVariable int lectureId,
    @PathVariable int worldIndex,
    @PathVariable UUID taskId,
    @RequestBody MinigameTaskDTO minigameTaskDTO
  ) {
    log.debug("update task {} of world {} of lecture {}", taskId, worldIndex, lectureId);
    return minigameTaskService.updateMinigameTaskFromArea(
      lectureId,
      worldIndex,
      Optional.empty(),
      taskId,
      minigameTaskDTO
    );
  }

  @Operation(summary = "Update a task by its id from a dungeon")
  @PutMapping("/dungeons/{dungeonIndex}/minigame-tasks/{taskId}")
  public MinigameTaskDTO updateMinigameTasksFromDungeon(
    @PathVariable int lectureId,
    @PathVariable int worldIndex,
    @PathVariable int dungeonIndex,
    @PathVariable UUID taskId,
    @RequestBody MinigameTaskDTO minigameTaskDTO
  ) {
    log.debug("update task {} of dungeon {} from world {} of lecture {}", taskId, dungeonIndex, worldIndex, lectureId);
    return minigameTaskService.updateMinigameTaskFromArea(
      lectureId,
      worldIndex,
      Optional.of(dungeonIndex),
      taskId,
      minigameTaskDTO
    );
  }
}
