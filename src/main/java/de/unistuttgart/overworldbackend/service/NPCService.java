package de.unistuttgart.overworldbackend.service;

import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.mapper.NPCMapper;
import de.unistuttgart.overworldbackend.repositories.NPCRepository;
import java.util.Set;
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
  private AreaService areaService;

  @Autowired
  private NPCMapper npcMapper;

  /**
   * Get a npc of an area
   *
   * @throws ResponseStatusException (404) if area or task with its id could not be found in the lecture
   * @param lectureId the id of the lecture the npc is part of
   * @param staticName the static name of the area the npc is part of
   * @param npcId the id of the npc searching for
   * @return the found npc object
   */
  public NPC getNPCFromAreaOrThrowNotFound(final int lectureId, final String staticName, final UUID npcId) {
    return areaService
      .getAreaFromLectureOrThrowNotFound(lectureId, staticName)
      .getNpcs()
      .stream()
      .filter(task -> task.getId().equals(npcId))
      .findAny()
      .orElseThrow(() ->
        new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          String.format(
            "There is no npc with id %s world wit id %s in lecture with id %s.",
            npcId,
            staticName,
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
   * @throws ResponseStatusException (404) if lecture, world or dungeon by its id do not exist
   * @param lectureId the id of the lecture the minigame task should be part of
   * @param staticName the static name of the area where the minigame task should be part of
   * @param npcId the id of the minigame task that should get updated
   * @param npcDTO the updated parameters
   * @return the updated area as DTO
   */
  public NPCDTO updateNPCFromArea(final int lectureId, final String staticName, final UUID npcId, final NPCDTO npcDTO) {
    final NPC npc = getNPCFromAreaOrThrowNotFound(lectureId, staticName, npcId);
    npc.setText(npcDTO.getText());
    final NPC updatedNPC = npcRepository.save(npc);
    return npcMapper.npcToNPCDTO(updatedNPC);
  }
}
