package de.unistuttgart.overworldbackend;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.mapper.LectureMapper;
import de.unistuttgart.overworldbackend.data.mapper.MinigameTaskMapper;
import de.unistuttgart.overworldbackend.data.mapper.PlayerStatisticMapper;
import de.unistuttgart.overworldbackend.repositories.LectureRepository;
import de.unistuttgart.overworldbackend.repositories.MinigameTaskRepository;
import de.unistuttgart.overworldbackend.repositories.PlayerStatisticRepository;
import de.unistuttgart.overworldbackend.service.PlayerTaskStatisticService;
import java.util.*;
import javax.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@AutoConfigureMockMvc
@Transactional
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PlayerTaskStatisticControllerTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private LectureRepository lectureRepository;

  @Autowired
  private PlayerStatisticRepository playerStatisticRepository;

  @Autowired
  private PlayerTaskStatisticService playerTaskStatisticService;

  @Autowired
  private LectureMapper lectureMapper;

  @Autowired
  private PlayerStatisticMapper playerstatisticMapper;

  @Autowired
  private MinigameTaskMapper minigameTaskMapper;

  private String fullURL;
  private ObjectMapper objectMapper;

  private Lecture initialLecture;
  private LectureDTO initialLectureDTO;

  private PlayerStatistic initialPlayerStatistic;
  private PlayerStatisticDTO initialPlayerStatisticDTO;

  private MinigameTask initialMinigameTask;

  private MinigameTaskDTO initialMinigameTaskDTO;

  @Autowired
  private MinigameTaskRepository minigameTaskRepository;

  @BeforeEach
  public void createBasicData() {
    lectureRepository.deleteAll();

    final MinigameTask minigameTask1 = new MinigameTask();
    minigameTask1.setConfigurationId(UUID.randomUUID());
    minigameTask1.setGame("Bugfinder");
    minigameTask1.setIndex(1);

    final MinigameTask minigameTask2 = new MinigameTask();
    minigameTask2.setConfigurationId(UUID.randomUUID());
    minigameTask2.setGame("Moorhuhn");
    minigameTask2.setIndex(2);

    final MinigameTask minigameTask3 = new MinigameTask();
    minigameTask2.setConfigurationId(UUID.randomUUID());
    minigameTask2.setGame("Crosswordpuzzle");
    minigameTask2.setIndex(3);

    final Set<MinigameTask> worldMinigameTasks = new HashSet<>();
    worldMinigameTasks.add(minigameTask1);
    worldMinigameTasks.add(minigameTask2);

    final Set<MinigameTask> dungeonMinigameTasks = new HashSet<>();
    dungeonMinigameTasks.add(minigameTask3);

    final Dungeon dungeon = new Dungeon();
    dungeon.setStaticName("Dark Dungeon");
    dungeon.setTopicName("Dark UML");
    dungeon.setActive(true);
    dungeon.setMinigameTasks(dungeonMinigameTasks);
    dungeon.setNpcs(Set.of());
    final List<Dungeon> dungeons = new ArrayList<>();

    final World world = new World();
    world.setStaticName("Winter Wonderland");
    world.setTopicName("UML Winter");
    world.setActive(true);
    world.setMinigameTasks(worldMinigameTasks);
    world.setNpcs(Set.of());
    world.setDungeons(dungeons);
    List<World> worlds = new ArrayList<>();
    worlds.add(world);

    final Lecture lecture = new Lecture("PSE", "Basic lecture of computer science students", worlds);

    initialLecture = lectureRepository.save(lecture);
    initialLectureDTO = lectureMapper.lectureToLectureDTO(initialLecture);

    initialMinigameTask =
      initialLecture.getWorlds().stream().findFirst().get().getMinigameTasks().stream().findFirst().get();
    initialMinigameTaskDTO = minigameTaskMapper.minigameTaskToMinigameTaskDTO(initialMinigameTask);

    final PlayerStatistic playerstatistic = new PlayerStatistic();
    playerstatistic.setUserId("45h23o2j432");
    playerstatistic.setUsername("testUser");
    playerstatistic.setLecture(initialLecture);
    playerstatistic.setCurrentArea(initialLecture.getWorlds().stream().findFirst().get());
    playerstatistic.setKnowledge(new Random(10).nextLong());
    playerstatistic.setUnlockedAreas(new ArrayList<>());
    playerstatistic.setCompletedDungeons(new ArrayList<>());
    initialPlayerStatistic = playerStatisticRepository.save(playerstatistic);
    initialPlayerStatisticDTO = playerstatisticMapper.playerStatisticToPlayerstatisticDTO(initialPlayerStatistic);

    assertNotNull(initialLecture.getLectureName());

    assertEquals(initialLecture.getId(), initialMinigameTask.getLecture().getId());
    assertEquals(initialLecture.getId(), initialPlayerStatistic.getLecture().getId());
    fullURL =
      String.format(
        "/lectures/%d/playerstatistics/" + initialPlayerStatistic.getUserId() + "/player-task-statistics",
        initialLecture.getId()
      );

    objectMapper = new ObjectMapper();
  }

  @Test
  void getTaskStatistics() throws Exception {
    PlayerTaskStatisticDTO statistic = playerTaskStatisticService.submitData(
      new PlayerTaskStatisticData(
        initialMinigameTask.getGame(),
        initialMinigameTask.getConfigurationId(),
        80,
        initialPlayerStatisticDTO.getUserId()
      )
    );

    final MvcResult result = mvc
      .perform(get(fullURL).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    final List<PlayerTaskStatisticDTO> playerTaskStatisticDTOs = Arrays.asList(
      objectMapper.readValue(result.getResponse().getContentAsString(), PlayerTaskStatisticDTO[].class)
    );
    assertEquals(playerTaskStatisticDTOs.get(0), statistic);
  }

  @Test
  void getTaskStatistic() throws Exception {
    PlayerTaskStatisticDTO statistic = playerTaskStatisticService.submitData(
      new PlayerTaskStatisticData(
        initialMinigameTask.getGame(),
        initialMinigameTask.getConfigurationId(),
        80,
        initialPlayerStatisticDTO.getUserId()
      )
    );

    final MvcResult result = mvc
      .perform(get(fullURL + "/" + statistic.getId()).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    final PlayerTaskStatisticDTO playerTaskStatisticDTO = objectMapper.readValue(
      result.getResponse().getContentAsString(),
      PlayerTaskStatisticDTO.class
    );
    assertEquals(statistic, playerTaskStatisticDTO);
    assertEquals(initialMinigameTaskDTO, playerTaskStatisticDTO.getMinigameTask());
    assertNotNull(playerTaskStatisticDTO.getMinigameTask().getArea());
    assertEquals(initialMinigameTaskDTO.getArea(), playerTaskStatisticDTO.getMinigameTask().getArea());
  }

  @Test
  void getTaskStatistic_DoesNotExist_ThrowsNotFound() throws Exception {
    final MvcResult result = mvc
      .perform(get(fullURL + "/" + UUID.randomUUID()).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isNotFound())
      .andReturn();
  }
}
