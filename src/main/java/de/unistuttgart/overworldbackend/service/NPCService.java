package de.unistuttgart.overworldbackend.service;

import de.unistuttgart.overworldbackend.data.NPC;
import de.unistuttgart.overworldbackend.data.NPCDTO;
import de.unistuttgart.overworldbackend.data.mapper.NPCMapper;
import de.unistuttgart.overworldbackend.repositories.NPCRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class NPCService {

  @Autowired
  private NPCRepository npcRepository;

  @Autowired
  private WorldService worldService;

  @Autowired
  private DungeonService dungeonService;

  @Autowired
  private NPCMapper npcMapper;

  /**
   * Get a NPC from a world by its index and a course by its id
   *
   * @throws ResponseStatusException (404) if npc not found
   * @param courseId the id of the course
   * @param worldIndex the index of the word
   * @param npcIndex the index of the npc
   * @return the found npc
   */
  public NPC getNPCFromWorld(final int courseId, final int worldIndex, int npcIndex) {
    return worldService
      .getWorldByIndexFromCourse(courseId, worldIndex)
      .getNpcs()
      .parallelStream()
      .filter(npc -> npc.getIndex() == npcIndex)
      .findAny()
      .orElseThrow(() ->
        new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          String.format("There is no NPC %d in world %d, course %d.", npcIndex, worldIndex, courseId)
        )
      );
  }

  /**
   * Get a NPC from a world by its index and a course by its id
   *
   * @throws ResponseStatusException (404) if npc not found
   * @param courseId the id of the course
   * @param worldIndex the index of the word
   * @param dungeonIndex the index of the dungeon
   * @param npcIndex the index of the npc
   * @return the found npc
   */
  public NPC getNPCFromDungeon(final int courseId, final int worldIndex, final int dungeonIndex, int npcIndex) {
    return dungeonService
      .getDungeonByIndexFromCourse(courseId, worldIndex, dungeonIndex)
      .getNpcs()
      .parallelStream()
      .filter(npc -> npc.getIndex() == npcIndex)
      .findAny()
      .orElseThrow(() ->
        new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          String.format(
            "There is no NPC %d in dungeon %d, world %d, course %d.",
            npcIndex,
            dungeonIndex,
            worldIndex,
            courseId
          )
        )
      );
  }

  /**
   * Update a npc by its id from a course and an area.
   *
   * Only the text is updatable.
   *
   * @throws ResponseStatusException (404) if npc not found
   * @param courseId the id of the course the npc should be part of
   * @param worldIndex the index of the world
   * @param npcIndex the index of the npc
   * @param npcDTO the updated parameters
   * @return the npc area as DTO
   */
  public NPCDTO updateNPCFromWorld(final int courseId, final int worldIndex, final int npcIndex, final NPCDTO npcDTO) {
    final NPC npc = getNPCFromWorld(courseId, worldIndex, npcIndex);
    npc.setText(npcDTO.getText());
    final NPC updatedNPC = npcRepository.save(npc);
    return npcMapper.npcToNPCDTO(updatedNPC);
  }

  /**
   * Update a npc by its id from a course and an area.
   *
   * Only the text is updatable.
   *
   * @throws ResponseStatusException (404) if npc not found
   * @param courseId the id of the course the npc should be part of
   * @param worldIndex the index of the world
   * @param dungeonIndex the index of the dungeon
   * @param npcIndex the index of the npc
   * @param npcDTO the updated parameters
   * @return the npc area as DTO
   */
  public NPCDTO updateNPCFromDungeon(
    final int courseId,
    final int worldIndex,
    final int dungeonIndex,
    final int npcIndex,
    final NPCDTO npcDTO
  ) {
    final NPC npc = getNPCFromDungeon(courseId, worldIndex, dungeonIndex, npcIndex);
    npc.setText(npcDTO.getText());
    final NPC updatedNPC = npcRepository.save(npc);
    return npcMapper.npcToNPCDTO(updatedNPC);
  }
}
