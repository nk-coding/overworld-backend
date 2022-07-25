package de.unistuttgart.overworldbackend.controller;

import de.unistuttgart.overworldbackend.data.NPCDTO;
import de.unistuttgart.overworldbackend.service.NPCService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "NPC", description = "Get and update npcs from areas (world or dungeons)")
@RestController
@Slf4j
@RequestMapping("/lectures/{lectureId}/worlds/{worldIndex}")
public class NPCController {

  @Autowired
  private NPCService npcService;

  @Operation(summary = "Update a npc by its id from a world")
  @PutMapping("/npcs/{npcIndex}")
  public NPCDTO updateNPCFromWorld(
    @PathVariable int lectureId,
    @PathVariable int worldIndex,
    @PathVariable int npcIndex,
    @RequestBody NPCDTO npcDTO
  ) {
    log.debug("update npc {} of world {} of lecture {}", npcIndex, worldIndex, lectureId);
    return npcService.updateNPCFromWorld(lectureId, worldIndex, npcIndex, npcDTO);
  }

  @Operation(summary = "Update a npc by its id from a dungeon")
  @PutMapping("/dungeons/{dungeonIndex}/npcs/{npcIndex}")
  public NPCDTO updateNPCFromDungeon(
    @PathVariable int lectureId,
    @PathVariable int worldIndex,
    @PathVariable int dungeonIndex,
    @PathVariable int npcIndex,
    @RequestBody NPCDTO npcDTO
  ) {
    log.debug("update npc {} of dungeon {} from world {} of lecture {} with {}", npcIndex, dungeonIndex, worldIndex, lectureId, npcDTO);
    return npcService.updateNPCFromDungeon(lectureId, worldIndex, dungeonIndex, npcIndex, npcDTO);
  }
}
