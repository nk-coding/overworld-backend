package de.unistuttgart.overworldbackend.service;

import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.mapper.NPCMapper;
import de.unistuttgart.overworldbackend.repositories.NPCRepository;
import java.util.UUID;
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
   * Get a NPC from a world by its index and a lecture by its id
   *
   * @throws ResponseStatusException (404) if npc not found
   * @param lectureId the id of the lecture
   * @param worldIndex the index of the word
   * @param npcIndex the index of the npc
   * @return the found npc
   */
  public NPC getNPCFromWorld(final int lectureId, final int worldIndex, int npcIndex) {
    UUID worldId = worldService.getWorldByIndexFromLecture(lectureId, worldIndex).getId();
    return npcRepository
      .findByIndexAndLectureIdAndAreaId(npcIndex, lectureId, worldId)
      .orElseThrow(() ->
        new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          String.format(
            "There is no npc with index %s world with index %s in lecture with id %s.",
            npcIndex,
            worldId,
            lectureId
          )
        )
      );
  }

  /**
   * Get a NPC from a world by its index and a lecture by its id
   *
   * @throws ResponseStatusException (404) if npc not found
   * @param lectureId the id of the lecture
   * @param worldIndex the index of the word
   * @param dungeonIndex the index of the dungeon
   * @param npcIndex the index of the npc
   * @return the found npc
   */
  public NPC getNPCFromDungeon(final int lectureId, final int worldIndex, final int dungeonIndex, int npcIndex) {
    UUID dungeonId = dungeonService.getDungeonByIndexFromLecture(lectureId, worldIndex, dungeonIndex).getId();
    return npcRepository
      .findByIndexAndLectureIdAndAreaId(npcIndex, lectureId, dungeonId)
      .orElseThrow(() ->
        new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          String.format(
            "There is no npc with index %s dungeon with index %s in lecture with id %s.",
            npcIndex,
            dungeonId,
            lectureId
          )
        )
      );
  }

  /**
   * Update a npc by its id from a lecture and an area.
   *
   * Only the text is updatable.
   *
   * @throws ResponseStatusException (404) if npc not found
   * @param lectureId the id of the lecture the npc should be part of
   * @param worldIndex the index of the world
   * @param npcIndex the index of the npc
   * @param npcDTO the updated parameters
   * @return the npc area as DTO
   */
  public NPCDTO updateNPCFromWorld(final int lectureId, final int worldIndex, final int npcIndex, final NPCDTO npcDTO) {
    final NPC npc = getNPCFromWorld(lectureId, worldIndex, npcIndex);
    npc.setText(npcDTO.getText());
    final NPC updatedNPC = npcRepository.save(npc);
    return npcMapper.npcToNPCDTO(updatedNPC);
  }

  /**
   * Update a npc by its id from a lecture and an area.
   *
   * Only the text is updatable.
   *
   * @throws ResponseStatusException (404) if npc not found
   * @param lectureId the id of the lecture the npc should be part of
   * @param worldIndex the index of the world
   * @param dungeonIndex the index of the dungeon
   * @param npcIndex the index of the npc
   * @param npcDTO the updated parameters
   * @return the npc area as DTO
   */
  public NPCDTO updateNPCFromDungeon(
    final int lectureId,
    final int worldIndex,
    final int dungeonIndex,
    final int npcIndex,
    final NPCDTO npcDTO
  ) {
    final NPC npc = getNPCFromDungeon(lectureId, worldIndex, dungeonIndex, npcIndex);
    npc.setText(npcDTO.getText());
    final NPC updatedNPC = npcRepository.save(npc);
    return npcMapper.npcToNPCDTO(updatedNPC);
  }
}
