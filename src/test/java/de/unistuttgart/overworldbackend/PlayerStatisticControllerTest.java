package de.unistuttgart.overworldbackend;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.mapper.DungeonMapper;
import de.unistuttgart.overworldbackend.data.mapper.LectureMapper;
import de.unistuttgart.overworldbackend.data.mapper.PlayerStatisticMapper;
import de.unistuttgart.overworldbackend.data.mapper.WorldMapper;
import de.unistuttgart.overworldbackend.repositories.LectureRepository;
import de.unistuttgart.overworldbackend.repositories.PlayerStatisticRepository;
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
class PlayerStatisticControllerTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private LectureRepository lectureRepository;

  @Autowired
  private PlayerStatisticRepository playerstatisticRepository;

  @Autowired
  private LectureMapper lectureMapper;

  @Autowired
  private WorldMapper worldMapper;

  @Autowired
  private DungeonMapper dungeonMapper;

  @Autowired
  private PlayerStatisticMapper playerstatisticMapper;

  private String fullURL;
  private ObjectMapper objectMapper;

  private Lecture initialLecture;
  private LectureDTO initialLectureDTO;
  private World initialWorld;
  private WorldDTO initialWorldDTO;

  private Dungeon initialDungeon;
  private DungeonDTO initialDungeonDTO;

  private PlayerStatistic initialPlayerStatistic;
  private PlayerStatisticDTO initialPlayerStatisticDTO;

  @BeforeEach
  public void createBasicData() {
    playerstatisticRepository.deleteAll();
    lectureRepository.deleteAll();

    final Dungeon dungeon = new Dungeon();
    dungeon.setIndex(1);
    dungeon.setStaticName("Dark Dungeon");
    dungeon.setTopicName("Dark UML");
    dungeon.setActive(true);
    dungeon.setMinigameTasks(Set.of());
    dungeon.setNpcs(Set.of());

    final World world = new World();
    world.setIndex(1);
    world.setStaticName("Winter Wonderland");
    world.setTopicName("UML Winter");
    world.setActive(true);
    world.setMinigameTasks(Set.of());
    world.setNpcs(Set.of());
    world.setDungeons(List.of(dungeon));
    List<World> worlds = new ArrayList<>();
    worlds.add(world);

    final Lecture lecture = new Lecture("PSE", "Basic lecture of computer science students", worlds);
    initialLecture = lectureRepository.save(lecture);
    initialLectureDTO = lectureMapper.lectureToLectureDTO(initialLecture);

    initialWorld = initialLecture.getWorlds().stream().findFirst().get();
    initialWorldDTO = worldMapper.worldToWorldDTO(initialWorld);

    initialDungeon = initialWorld.getDungeons().stream().findFirst().get();
    initialDungeonDTO = dungeonMapper.dungeonToDungeonDTO(initialDungeon);

    final AreaLocation areaLocation = new AreaLocation(initialWorld);
    final PlayerStatistic playerstatistic = new PlayerStatistic();

    playerstatistic.setUserId("45h23o2j432");
    playerstatistic.setUsername("testUser");
    playerstatistic.setLecture(initialLecture);
    playerstatistic.setCurrentAreaLocation(areaLocation);
    playerstatistic.setKnowledge(new Random(10).nextLong());
    final ArrayList<AreaLocation> unlockedAreas = new ArrayList<>();
    unlockedAreas.add(areaLocation);
    playerstatistic.setUnlockedAreas(unlockedAreas);
    playerstatistic.setCompletedDungeons(new ArrayList<>());
    initialPlayerStatistic = playerstatisticRepository.save(playerstatistic);
    initialPlayerStatisticDTO = playerstatisticMapper.playerStatisticToPlayerstatisticDTO(initialPlayerStatistic);

    assertNotNull(initialLecture.getLectureName());

    assertNotNull(initialPlayerStatistic.getId());
    assertNotNull(initialPlayerStatisticDTO.getId());

    fullURL = "/lectures/" + initialLectureDTO.getId() + "/playerstatistics";

    objectMapper = new ObjectMapper();
  }

  @Test
  void getPlayerStatistic() throws Exception {
    final MvcResult result = mvc
      .perform(get(fullURL + "/" + initialPlayerStatisticDTO.getUserId()).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    final PlayerStatisticDTO playerStatisticDTOResult = objectMapper.readValue(
      result.getResponse().getContentAsString(),
      PlayerStatisticDTO.class
    );

    assertEquals(initialPlayerStatisticDTO, playerStatisticDTOResult);
    assertEquals(initialPlayerStatisticDTO.getId(), playerStatisticDTOResult.getId());
  }

  @Test
  void getPlayerStatistic_DoesNotExist_ThrowsNotFound() throws Exception {
    mvc
      .perform(get(fullURL + "/" + UUID.randomUUID()).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isNotFound())
      .andReturn();
  }

  @Test
  void createPlayerStatistic() throws Exception {
    final Player newPlayer = new Player("n423l34213", "newPlayer");
    final String bodyValue = objectMapper.writeValueAsString(newPlayer);

    final MvcResult result = mvc
      .perform(post(fullURL).content(bodyValue).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isCreated())
      .andReturn();

    final PlayerStatisticDTO createdPlayerStatisticDTOResult = objectMapper.readValue(
      result.getResponse().getContentAsString(),
      PlayerStatisticDTO.class
    );

    assertEquals(0, createdPlayerStatisticDTOResult.getKnowledge());
    assertEquals(newPlayer.getUserId(), createdPlayerStatisticDTOResult.getUserId());
    assertEquals(newPlayer.getUsername(), createdPlayerStatisticDTOResult.getUsername());
    assertEquals(new AreaLocationDTO(1, null), createdPlayerStatisticDTOResult.getCurrentAreaLocation());
    assertEquals(Arrays.asList(new AreaLocationDTO(1, null)), createdPlayerStatisticDTOResult.getUnlockedAreas());
  }

  @Test
  void updatePlayerStatistic() throws Exception {
    final PlayerStatisticDTO updatedPlayerStatistic = playerstatisticMapper.playerStatisticToPlayerstatisticDTO(
      initialPlayerStatistic
    );
    final AreaLocationDTO newAreaLocation = new AreaLocationDTO(initialWorld.getIndex(), initialDungeon.getIndex());
    updatedPlayerStatistic.setCurrentAreaLocation(newAreaLocation);

    final String bodyValue = objectMapper.writeValueAsString(updatedPlayerStatistic);

    final MvcResult result = mvc
      .perform(
        put(fullURL + "/" + initialPlayerStatisticDTO.getUserId())
          .content(bodyValue)
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andReturn();

    final PlayerStatisticDTO updatedPlayerStatisticDTOResult = objectMapper.readValue(
      result.getResponse().getContentAsString(),
      PlayerStatisticDTO.class
    );

    assertEquals(newAreaLocation, updatedPlayerStatisticDTOResult.getCurrentAreaLocation());
    assertEquals(initialPlayerStatistic.getId(), updatedPlayerStatisticDTOResult.getId());
    assertEquals(initialPlayerStatistic.getUserId(), updatedPlayerStatisticDTOResult.getUserId());
    assertEquals(initialPlayerStatistic.getUsername(), updatedPlayerStatisticDTOResult.getUsername());
  }

  @Test
  void updatePlayerStatistic_AreaLocationDoesNotExist_ThrowsNotFound() throws Exception {
    final PlayerStatisticDTO updatedPlayerStatistic = playerstatisticMapper.playerStatisticToPlayerstatisticDTO(
      initialPlayerStatistic
    );
    final AreaLocationDTO newAreaLocation = new AreaLocationDTO(Integer.MAX_VALUE, Integer.MAX_VALUE);
    updatedPlayerStatistic.setCurrentAreaLocation(newAreaLocation);

    final String bodyValue = objectMapper.writeValueAsString(updatedPlayerStatistic);

    mvc
      .perform(
        put(fullURL + "/" + initialPlayerStatisticDTO.getUserId())
          .content(bodyValue)
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isNotFound());
  }
}
