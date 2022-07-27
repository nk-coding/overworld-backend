package de.unistuttgart.overworldbackend;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.mapper.DungeonMapper;
import de.unistuttgart.overworldbackend.data.mapper.WorldMapper;
import de.unistuttgart.overworldbackend.repositories.LectureRepository;
import java.util.Arrays;
import java.util.Set;
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
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DungeonControllerTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private LectureRepository lectureRepository;

  @Autowired
  private WorldMapper worldMapper;

  @Autowired
  private DungeonMapper dungeonMapper;

  private String fullURL;
  private ObjectMapper objectMapper;

  private Lecture initialLecture;
  private World initialWorld;
  private WorldDTO initialWorldDTO;

  private Dungeon initialDungeon;
  private DungeonDTO initialDungeonDTO;

  @BeforeEach
  public void createBasicData() {
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
    world.setDungeons(Arrays.asList(dungeon));

    final Lecture lecture = new Lecture("PSE", "Basic lecture of computer science students", Arrays.asList(world));
    initialLecture = lectureRepository.save(lecture);

    initialWorld = initialLecture.getWorlds().stream().findFirst().get();
    initialWorldDTO = worldMapper.worldToWorldDTO(initialWorld);

    initialDungeon = initialWorld.getDungeons().stream().findFirst().get();
    initialDungeonDTO = dungeonMapper.dungeonToDungeonDTO(initialDungeon);

    assertNotNull(initialWorld.getId());
    assertNotNull(initialWorldDTO.getId());

    assertNotNull(initialDungeon.getId());
    assertNotNull(initialDungeonDTO.getId());

    fullURL = String.format("/lectures/%d/worlds/%d/dungeons", initialLecture.getId(), initialWorld.getIndex());

    objectMapper = new ObjectMapper();
  }

  @Test
  void getDungeonsFromWorld() throws Exception {
    final MvcResult result = mvc
      .perform(get(fullURL).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    final Set<DungeonDTO> dungeons = Set.of(
      objectMapper.readValue(result.getResponse().getContentAsString(), DungeonDTO[].class)
    );
    final DungeonDTO dungeon = dungeons.stream().findFirst().get();
    assertSame(1, dungeons.size());
    assertEquals(initialDungeonDTO.getId(), dungeon.getId());
    assertEquals(initialDungeonDTO, dungeon);
  }

  @Test
  void getDungeonFromWorld() throws Exception {
    final MvcResult result = mvc
      .perform(get(fullURL + "/" + initialDungeon.getIndex()).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    final DungeonDTO dungeonDTO = objectMapper.readValue(result.getResponse().getContentAsString(), DungeonDTO.class);

    assertEquals(initialDungeonDTO.getId(), dungeonDTO.getId());
    assertEquals(initialDungeonDTO, dungeonDTO);
  }

  @Test
  void getDungeonFromWorld_DoesNotExist_ThrowsNotFound() throws Exception {
    mvc
      .perform(get(fullURL + "/" + Integer.MAX_VALUE).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isNotFound())
      .andReturn();
  }

  @Test
  void updateDungeonFromWorld() throws Exception {
    final String newTopicName = "Closed Topic";
    final boolean newActiveStatus = false;
    final DungeonDTO updateDungeonDTO = dungeonMapper.dungeonToDungeonDTO(initialDungeon);
    updateDungeonDTO.setActive(newActiveStatus);
    updateDungeonDTO.setTopicName(newTopicName);
    final String bodyValue = objectMapper.writeValueAsString(updateDungeonDTO);

    final MvcResult result = mvc
      .perform(
        put(fullURL + "/" + initialDungeon.getIndex()).content(bodyValue).contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andReturn();

    final DungeonDTO updatedDungeonDTOResult = objectMapper.readValue(
      result.getResponse().getContentAsString(),
      DungeonDTO.class
    );

    assertEquals(initialDungeonDTO.getId(), updatedDungeonDTOResult.getId());
    assertEquals(initialDungeonDTO.getIndex(), updatedDungeonDTOResult.getIndex());
    assertEquals(newTopicName, updatedDungeonDTOResult.getTopicName());
    assertEquals(newActiveStatus, updatedDungeonDTOResult.isActive());
    assertEquals(initialDungeonDTO.getStaticName(), updatedDungeonDTOResult.getStaticName());
    assertEquals(initialDungeonDTO.getNpcs(), updatedDungeonDTOResult.getNpcs());
    assertEquals(initialDungeonDTO.getMinigameTasks(), updatedDungeonDTOResult.getMinigameTasks());
    assertEquals(initialDungeonDTO, updatedDungeonDTOResult);
  }

  @Test
  void updateDungeonFromWorld_DoNotUpdatedStaticName() throws Exception {
    final String newTopicName = "Closed Topic";
    final String newStaticName = "PSE World Override Static Name";
    final boolean newActiveStatus = false;
    final DungeonDTO updatedDungeonDTO = dungeonMapper.dungeonToDungeonDTO(initialDungeon);
    updatedDungeonDTO.setActive(newActiveStatus);
    updatedDungeonDTO.setTopicName(newTopicName);
    updatedDungeonDTO.setStaticName(newStaticName);
    final String bodyValue = objectMapper.writeValueAsString(updatedDungeonDTO);

    final MvcResult result = mvc
      .perform(
        put(fullURL + "/" + initialDungeon.getIndex()).content(bodyValue).contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andReturn();

    final DungeonDTO updatedDungeonDTOResult = objectMapper.readValue(
      result.getResponse().getContentAsString(),
      DungeonDTO.class
    );

    assertEquals(initialDungeonDTO.getId(), updatedDungeonDTOResult.getId());
    assertEquals(initialDungeonDTO.getIndex(), updatedDungeonDTOResult.getIndex());
    assertEquals(newTopicName, updatedDungeonDTOResult.getTopicName());
    assertEquals(newActiveStatus, updatedDungeonDTOResult.isActive());
    assertNotEquals(newStaticName, updatedDungeonDTOResult.getStaticName());
    assertEquals(initialDungeonDTO.getStaticName(), updatedDungeonDTOResult.getStaticName());
    assertEquals(initialDungeonDTO.getNpcs(), updatedDungeonDTOResult.getNpcs());
    assertEquals(initialDungeonDTO.getMinigameTasks(), updatedDungeonDTOResult.getMinigameTasks());
    assertEquals(initialDungeonDTO, updatedDungeonDTOResult);
  }
}
