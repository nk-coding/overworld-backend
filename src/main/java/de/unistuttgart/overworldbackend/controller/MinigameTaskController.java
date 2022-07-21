package de.unistuttgart.overworldbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Minigame Task", description = "Get and update minigame tasks from areas (world or dungeons)")
@RestController
@Slf4j
@RequestMapping("/lectures/{lectureId}/worlds/{worldId}")
public class MinigameTaskController {

  @Operation(summary = "Get all task from a world")
  @GetMapping("/minigame-tasks")
  public void getMinigameTasksFromWorld(@PathVariable int lectureId, @PathVariable UUID worldId) {
    log.debug("get tasks of world {} of lecture {}", worldId, lectureId);
  }

  @Operation(summary = "Get all tasks from a dungeon")
  @GetMapping("/dungeons/{dungeonId}/minigame-tasks")
  public void getMinigameTasksFromDungeon(
    @PathVariable int lectureId,
    @PathVariable UUID worldId,
    @PathVariable UUID dungeonId
  ) {
    log.debug("get tasks of dungeon {} from world {} of lecture {}", dungeonId, worldId, lectureId);
  }

  @Operation(summary = "Get a task by its id from a world")
  @GetMapping("/minigame-tasks/{taskId}")
  public void getMinigameTaskFromWorld(
    @PathVariable int lectureId,
    @PathVariable UUID worldId,
    @PathVariable UUID taskId
  ) {
    log.debug("get tasks {} of world {} of lecture {}", taskId, worldId, lectureId);
  }

  @Operation(summary = "Get a task by its id from a dungeon")
  @GetMapping("/dungeons/{dungeonId}/minigame-tasks/{taskId}")
  public void getMinigameTaskFromDungeon(
    @PathVariable int lectureId,
    @PathVariable UUID worldId,
    @PathVariable UUID dungeonId,
    @PathVariable UUID taskId
  ) {
    log.debug("get task {} of dungeon {} from world {} of lecture {}", taskId, dungeonId, worldId, lectureId);
  }

  @Operation(summary = "Update a task by its id from a world")
  @PutMapping("/minigame-tasks/{taskId}")
  public void updateMinigameTasksFromWorld(
    @PathVariable int lectureId,
    @PathVariable UUID worldId,
    @PathVariable UUID taskId
  ) {
    log.debug("update task {} of world {} of lecture {}", taskId, worldId, lectureId);
  }

  @Operation(summary = "Update a task by its id from a dungeon")
  @PutMapping("/dungeons/{dungeonId}/minigame-tasks/{taskId}")
  public void updateMinigameTasksFromDungeon(
    @PathVariable int lectureId,
    @PathVariable UUID worldId,
    @PathVariable UUID dungeonId,
    @PathVariable UUID taskId
  ) {
    log.debug("update task {} of dungeon {} from world {} of lecture {}", taskId, dungeonId, worldId, lectureId);
  }
}
