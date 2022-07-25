package de.unistuttgart.overworldbackend.controller;

import de.unistuttgart.overworldbackend.data.MinigameTaskDTO;
import de.unistuttgart.overworldbackend.data.NPCDTO;
import de.unistuttgart.overworldbackend.service.MinigameTaskService;
import de.unistuttgart.overworldbackend.service.NPCService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "NPC", description = "Get and update npcs from areas (world or dungeons)")
@RestController
@Slf4j
@RequestMapping("/lectures/{lectureId}/worlds/{staticWorldName}")
public class NPCController {

  @Autowired
  private NPCService npcService;

  @Operation(summary = "Update a npc by its id from a world")
  @PutMapping("/npcs/{npcId}")
  public NPCDTO updateNPCFromWorld(
    @PathVariable int lectureId,
    @PathVariable String staticWorldName,
    @PathVariable UUID npcId,
    @RequestBody NPCDTO npcDTO
  ) {
    log.debug("update npc {} of world {} of lecture {}", npcId, staticWorldName, lectureId);
    return npcService.updateNPCFromArea(lectureId, staticWorldName, npcId, npcDTO);
  }

  @Operation(summary = "Update a npc by its id from a dungeon")
  @PutMapping("/dungeons/{staticDungeonName}/npcs/{npcId}")
  public NPCDTO updateNPCFromDungeon(
    @PathVariable int lectureId,
    @PathVariable String staticWorldName,
    @PathVariable String staticDungeonName,
    @PathVariable UUID npcId,
    @RequestBody NPCDTO npcDTO
  ) {
    log.debug("update npc {} of dungeon {} from world {} of lecture {}", npcId, staticDungeonName, staticWorldName, lectureId);
    return npcService.updateNPCFromArea(lectureId, staticDungeonName, npcId, npcDTO);
  }
}
