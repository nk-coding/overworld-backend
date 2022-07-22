package de.unistuttgart.overworldbackend.service;

import de.unistuttgart.overworldbackend.data.LectureDTO;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.mapper.LectureMapper;
import de.unistuttgart.overworldbackend.repositories.LectureRepository;
import io.github.classgraph.Resource;
import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
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
  public LectureDTO getLecture(final int id) {
    return lectureMapper.lectureToLectureDTO(
      lectureRepository
        .findById(id)
        .orElseThrow(() ->
          new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("There is no lecture with id %s.", id))
        )
    );
  }

  public LectureDTO createLecture(final LectureInitialData lectureInit) {
    Set<World> worlds = new HashSet<>();
    AtomicInteger i = new AtomicInteger(0);
    configLecture
      .getWorlds()
      .forEach(worldConfig -> {
        configureWorld(worlds, i.getAndIncrement(), worldConfig);
      });

    Lecture lecture = new Lecture(lectureInit.getLectureName(), lectureInit.getDescription(), worlds);
    lectureRepository.save(lecture);
    return lectureMapper.lectureToLectureDTO(lecture);
  }

  private void configureWorld(Set<World> worlds, int i, WorldConfig worldConfig) {
    Set<MinigameTask> minigames = new HashSet<>();
    Set<NPC> npcs = new HashSet<>();
    Set<Dungeon> dungeons = new HashSet<>();
    AtomicInteger j = new AtomicInteger(0);
    worldConfig
      .getDungeons()
      .forEach(dungeonConfig -> {
        dungeons.add(configureDungeon(i, j.incrementAndGet(), dungeonConfig));
      });
    for (int k = 0; k < worldConfig.getNumberOfMinigames(); k++) {
      MinigameTask minigame = new MinigameTask("w" + i + "g" + k, "empty", null);
      minigames.add(minigame);
    }
    for (int k = 0; k < worldConfig.getNumberOfNPCs(); k++) {
      NPC npc = new NPC("w" + i + "n" + k, "");
      npcs.add(npc);
    }
    World world = new World(worldConfig.getStaticName(), "", false, minigames, npcs, dungeons);
    worlds.add(world);
  }

  private Dungeon configureDungeon(int i, int j, DungeonConfig dungeonConfig) {
    Set<MinigameTask> dungeonMinigames = new HashSet<>();
    Set<NPC> dungeonNpcs = new HashSet<>();
    for (int k = 0; k < dungeonConfig.getNumberOfMinigames(); k++) {
      MinigameTask minigame = new MinigameTask("w" + i + "d" + j + "g" + k, "empty", null);
      dungeonMinigames.add(minigame);
    }
    for (int k = 0; k < dungeonConfig.getNumberOfNPCs(); k++) {
      NPC npc = new NPC("w" + i + "d" + j + "n" + k, "");
      dungeonNpcs.add(npc);
    }
    return new Dungeon(dungeonConfig.getStaticName(), "", false, dungeonMinigames, dungeonNpcs);
  }
}
