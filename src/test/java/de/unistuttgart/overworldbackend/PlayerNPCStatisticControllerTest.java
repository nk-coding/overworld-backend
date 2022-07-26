package de.unistuttgart.overworldbackend;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.mapper.LectureMapper;
import de.unistuttgart.overworldbackend.data.mapper.MinigameTaskMapper;
import de.unistuttgart.overworldbackend.data.mapper.NPCMapper;
import de.unistuttgart.overworldbackend.data.mapper.PlayerstatisticMapper;
import de.unistuttgart.overworldbackend.repositories.*;
import de.unistuttgart.overworldbackend.service.PlayerNPCStatisticService;
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
class PlayerNPCStatisticControllerTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private LectureRepository lectureRepository;

  @Autowired
  private PlayerstatisticRepository playerstatisticRepository;

  @Autowired
  private PlayerNPCStatisticService playerNPCStatisticService;

  @Autowired
  private LectureMapper lectureMapper;

  @Autowired
  private PlayerstatisticMapper playerstatisticMapper;

  @Autowired
  private NPCMapper npcMapper;

  private String fullURL;
  private ObjectMapper objectMapper;

  private Lecture initialLecture;
  private LectureDTO initialLectureDTO;

  private Playerstatistic initialPlayerstatistic;
  private PlayerstatisticDTO initialPlayerstatisticDTO;

  private NPC initialNpc;

  private NPCDTO initialNpcDTO;

  @BeforeEach
  public void createBasicData() {
    lectureRepository.deleteAll();

    final Dungeon dungeon = new Dungeon();
    dungeon.setStaticName("Dark Dungeon");
    dungeon.setTopicName("Dark UML");
    dungeon.setActive(true);
    dungeon.setMinigameTasks(Set.of());
    dungeon.setNpcs(Set.of());
    final List<Dungeon> dungeons = new ArrayList<>();

    final NPC npc = new NPC();
    npc.setText("NPCText");
    npc.setIndex(1);

    final World world = new World();
    world.setStaticName("Winter Wonderland");
    world.setTopicName("UML Winter");
    world.setActive(true);
    world.setMinigameTasks(Set.of());
    world.setNpcs(Set.of(npc));
    world.setDungeons(dungeons);
    List<World> worlds = new ArrayList<>();
    worlds.add(world);

    final Lecture lecture = new Lecture("PSE", "Basic lecture of computer science students", worlds);

    initialLecture = lectureRepository.save(lecture);
    initialLectureDTO = lectureMapper.lectureToLectureDTO(initialLecture);

    initialNpc = initialLecture.getWorlds().stream().findFirst().get().getNpcs().stream().findFirst().get();
    initialNpcDTO = npcMapper.npcToNPCDTO(initialNpc);

    final Playerstatistic playerstatistic = new Playerstatistic();
    playerstatistic.setUserId("45h23o2j432");
    playerstatistic.setUsername("testUser");
    playerstatistic.setLecture(initialLecture);
    AreaLocation areaLocation = new AreaLocation();
    areaLocation.setWorld(initialLecture.getWorlds().stream().findFirst().get());
    playerstatistic.setCurrentAreaLocation(areaLocation);
    playerstatistic.setKnowledge(new Random(10).nextLong());
    playerstatistic.setUnlockedAreas(new ArrayList<>());
    playerstatistic.setCompletedDungeons(new ArrayList<>());
    initialPlayerstatistic = playerstatisticRepository.save(playerstatistic);
    initialPlayerstatisticDTO = playerstatisticMapper.playerstatisticToPlayerstatisticDTO(initialPlayerstatistic);

    assertNotNull(initialLecture.getLectureName());
    assertNotNull(initialLectureDTO.getId());

    assertEquals(initialLecture.getId(), initialNpc.getLecture().getId());
    assertEquals(initialLecture.getId(), initialPlayerstatistic.getLecture().getId());
    fullURL =
      String.format(
        "/lectures/%d/playerstatistics/" + initialPlayerstatistic.getUserId() + "/player-npc-statistics",
        initialLecture.getId()
      );

    objectMapper = new ObjectMapper();
  }

  @Test
  void getNPCStatistics() throws Exception {
    PlayerNPCStatisticDTO statistic = playerNPCStatisticService.submitData(
      new PlayerNPCStatisticData(initialNpc.getId(), true, initialPlayerstatistic.getUserId())
    );

    final MvcResult result = mvc
      .perform(get(fullURL).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    final List<PlayerNPCStatisticDTO> playerNPCStatisticDTOs = Arrays.asList(
      objectMapper.readValue(result.getResponse().getContentAsString(), PlayerNPCStatisticDTO[].class)
    );
    assertEquals(playerNPCStatisticDTOs.get(0), statistic);
  }

  @Test
  void getNPCStatistic() throws Exception {
    PlayerNPCStatisticDTO statistic = playerNPCStatisticService.submitData(
      new PlayerNPCStatisticData(initialNpc.getId(), true, initialPlayerstatistic.getUserId())
    );

    final MvcResult result = mvc
      .perform(get(fullURL + "/" + statistic.getId()).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    final PlayerNPCStatisticDTO playerNPCStatisticDTO = objectMapper.readValue(
      result.getResponse().getContentAsString(),
      PlayerNPCStatisticDTO.class
    );
    assertEquals(playerNPCStatisticDTO, statistic);
  }

  @Test
  void getNPCStatistic_DoesNotExist_ThrowsNotFound() throws Exception {
    PlayerNPCStatisticDTO statistic = playerNPCStatisticService.submitData(
      new PlayerNPCStatisticData(initialNpc.getId(), true, initialPlayerstatistic.getUserId())
    );

    final MvcResult result = mvc
      .perform(get(fullURL + "/" + UUID.randomUUID()).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isNotFound())
      .andReturn();
  }
}
