package de.unistuttgart.overworldbackend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.config.DungeonConfig;
import de.unistuttgart.overworldbackend.data.config.LectureConfig;
import de.unistuttgart.overworldbackend.data.config.WorldConfig;
import de.unistuttgart.overworldbackend.data.mapper.LectureMapper;
import de.unistuttgart.overworldbackend.repositories.LectureRepository;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LectureService {

  LectureConfig configLecture;

  @Autowired
  private LectureRepository lectureRepository;

  @Autowired
  private LectureMapper lectureMapper;

  public LectureService() {
    configLecture = new LectureConfig();
    ObjectMapper mapper = new ObjectMapper();

    InputStream inputStream = TypeReference.class.getResourceAsStream("/config.json");
    try {
      configLecture = mapper.readValue(inputStream, LectureConfig.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * @throws ResponseStatusException when lecture by its id could not be found
   * @param id the id of the lecture searching for
   * @return the found lecture
   */
  public Lecture getLecture(final int id) {
    return lectureRepository
      .findById(id)
      .orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("There is no lecture with id %s.", id))
      );
  }

  /**
   * Update a lecture by its id.
   *
   * Only the lecture name and description is updatable.
   *
   * @throws ResponseStatusException if lecture, world or dungeon by its id do not exist
   * @param lectureId the id of the lecture thath should get updated
   * @param lectureDTO the updated parameters
   * @return the updated lecture as DTO
   */
  public LectureDTO updateLecture(final int lectureId, final LectureDTO lectureDTO) {
    Lecture lecture = getLecture(lectureId);
    lecture.setLectureName(lectureDTO.getLectureName());
    lecture.setDescription(lectureDTO.getDescription());
    Lecture updatedLecture = lectureRepository.save(lecture);
    return lectureMapper.lectureToLectureDTO(updatedLecture);
  }

  /**
   * Create a lecture with initial data.
   *
   * Creates a lecture with pre generated worlds, dungeons, minigame tasks and npcs.
   *
   * @param lectureInit the initial data with the lecture should be created with
   * @return the created lecture as DTO with all its generated worlds, dungeons, minigame tasks and npcs
   */
  public LectureDTO createLecture(final LectureInitialData lectureInit) {
    List<World> worlds = new ArrayList<>();
    AtomicInteger worldId = new AtomicInteger(0);
    configLecture.getWorlds().forEach(worldConfig -> configureWorld(worlds, worldId.getAndIncrement(), worldConfig));

    Lecture lecture = new Lecture(lectureInit.getLectureName(), lectureInit.getDescription(), worlds);
    lectureRepository.save(lecture);
    return lectureMapper.lectureToLectureDTO(lecture);
  }

  /**
   * Delete a lecture by its id
   *
   * @throws ResponseStatusException when lecture with its id does not exist
   * @param id the lecture that should be deleted
   * @return the deleted lecture as DTO
   */
  public LectureDTO deleteLecture(final int id) {
    Lecture lecture = getLecture(id);
    lectureRepository.delete(lecture);
    return lectureMapper.lectureToLectureDTO(lecture);
  }

  private void configureWorld(List<World> worlds, int worldId, WorldConfig worldConfig) {
    Set<MinigameTask> minigames = new HashSet<>();
    List<NPC> npcs = new ArrayList<>();
    List<Dungeon> dungeons = new ArrayList<>();
    AtomicInteger dungeonId = new AtomicInteger(0);
    worldConfig
      .getDungeons()
      .forEach(dungeonConfig -> dungeons.add(configureDungeon(worldId, dungeonId.incrementAndGet(), dungeonConfig)));
    for (int minigameId = 0; minigameId < worldConfig.getNumberOfMinigames(); minigameId++) {
      MinigameTask minigame = new MinigameTask("w" + worldId + "g" + minigameId, "empty", null);
      minigames.add(minigame);
    }
    for (int npcId = 0; npcId < worldConfig.getNumberOfNPCs(); npcId++) {
      NPC npc = new NPC("w" + worldId + "n" + npcId, "");
      npcs.add(npc);
    }
    World world = new World(worldConfig.getStaticName(), "", false, minigames, npcs, dungeons);
    worlds.add(world);
  }

  private Dungeon configureDungeon(int worldId, int dungoenId, DungeonConfig dungeonConfig) {
    Set<MinigameTask> minigames = new HashSet<>();
    List<NPC> npcs = new ArrayList<>();
    for (int k = 0; k < dungeonConfig.getNumberOfMinigames(); k++) {
      MinigameTask minigame = new MinigameTask("w" + worldId + "d" + dungoenId + "g" + k, "empty", null);
      minigames.add(minigame);
    }
    for (int k = 0; k < dungeonConfig.getNumberOfNPCs(); k++) {
      NPC npc = new NPC("w" + worldId + "d" + dungoenId + "n" + k, "");
      npcs.add(npc);
    }
    return new Dungeon(dungeonConfig.getStaticName(), "", false, minigames, npcs);
  }
}
