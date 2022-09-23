package de.unistuttgart.overworldbackend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.mapper.DungeonMapper;
import de.unistuttgart.overworldbackend.data.mapper.NPCMapper;
import de.unistuttgart.overworldbackend.data.mapper.WorldMapper;
import de.unistuttgart.overworldbackend.repositories.CourseRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@AutoConfigureMockMvc
@Transactional
@SpringBootTest
@Testcontainers
class NPCControllerTest {

  @Container
  public static PostgreSQLContainer postgresDB = new PostgreSQLContainer("postgres:14-alpine")
    .withDatabaseName("postgres")
    .withUsername("postgres")
    .withPassword("postgres");

  @DynamicPropertySource
  public static void properties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgresDB::getJdbcUrl);
    registry.add("spring.datasource.username", postgresDB::getUsername);
    registry.add("spring.datasource.password", postgresDB::getPassword);
  }

  @Autowired
  private MockMvc mvc;

  @Autowired
  private CourseRepository courseRepository;

  @Autowired
  private WorldMapper worldMapper;

  @Autowired
  private DungeonMapper dungeonMapper;

  @Autowired
  private NPCMapper npcMapper;

  private String fullURL;
  private String fullDungeonURL;
  private ObjectMapper objectMapper;

  private Course initialCourse;
  private World initialWorld;
  private WorldDTO initialWorldDTO;
  private Dungeon initialDungoen;
  private DungeonDTO initialDungeonDTO;

  private NPC initialNPC;
  private NPCDTO initialNPCDTO;
  private NPC initialDungeonNPC;
  private NPCDTO initialDungeonNPCDTO;

  @BeforeEach
  public void createBasicData() {
    courseRepository.deleteAll();

    final List<String> npcText = new ArrayList<>();
    npcText.add("You want to learn PSE?");
    npcText.add("This is so cool");
    npcText.add("Let's go!");

    final NPC npc = new NPC();
    npc.setText(npcText);
    npc.setIndex(1);

    final List<String> dungeonNPCText = new ArrayList<>();
    dungeonNPCText.add("You want to learn DSA?");
    dungeonNPCText.add("This is so cool");
    dungeonNPCText.add("Let's go!");

    final NPC dungeonNPC = new NPC();
    dungeonNPC.setText(dungeonNPCText);
    dungeonNPC.setIndex(1);

    final Dungeon dungeon = new Dungeon();
    dungeon.setIndex(1);
    dungeon.setStaticName("Dark Dungeon");
    dungeon.setTopicName("Dark UML");
    dungeon.setActive(true);
    dungeon.setMinigameTasks(Set.of());
    dungeon.setNpcs(Set.of(dungeonNPC));
    dungeon.setBooks(Set.of());

    final World world = new World();
    world.setIndex(1);
    world.setStaticName("Winter Wonderland");
    world.setTopicName("UML Winter");
    world.setActive(true);
    world.setMinigameTasks(Set.of());
    world.setNpcs(Set.of(npc));
    world.setDungeons(Arrays.asList(dungeon));
    world.setBooks(Set.of());

    final Course course = new Course(
      "PSE",
      "SS-22",
      "Basic lecture of computer science students",
      true,
      Arrays.asList(world)
    );
    initialCourse = courseRepository.save(course);

    initialWorld = initialCourse.getWorlds().stream().findFirst().get();
    initialWorldDTO = worldMapper.worldToWorldDTO(initialWorld);

    initialDungoen = initialWorld.getDungeons().stream().findFirst().get();
    initialDungeonDTO = dungeonMapper.dungeonToDungeonDTO(initialDungoen);

    initialNPC = initialWorld.getNpcs().stream().findFirst().get();
    initialNPCDTO = npcMapper.npcToNPCDTO(initialNPC);

    initialDungeonNPC = initialDungoen.getNpcs().stream().findFirst().get();
    initialDungeonNPCDTO = npcMapper.npcToNPCDTO(initialDungeonNPC);

    assertNotNull(initialWorld.getId());
    assertNotNull(initialWorldDTO.getId());

    assertNotNull(initialNPC.getId());
    assertNotNull(initialNPCDTO.getId());

    assertNotNull(initialDungeonNPC.getId());
    assertNotNull(initialDungeonNPCDTO.getId());

    fullURL = String.format("/courses/%d/worlds/%d/npcs", initialCourse.getId(), initialWorld.getIndex());
    fullDungeonURL =
      String.format(
        "/courses/%d/worlds/%d/dungeons/%d/npcs",
        initialCourse.getId(),
        initialWorld.getIndex(),
        initialDungoen.getIndex()
      );

    objectMapper = new ObjectMapper();
  }

  @Test
  void updateNPCFromWorld_DoesNotExist_ThrowsNotFound() throws Exception {
    final NPCDTO npcDTO = new NPCDTO();
    npcDTO.setText(Arrays.asList("Hey ho"));
    final String bodyValue = objectMapper.writeValueAsString(npcDTO);
    mvc
      .perform(put(fullURL + "/" + Integer.MAX_VALUE).content(bodyValue).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isNotFound())
      .andReturn();
  }

  @Test
  void updateNPCTaskFromWorld() throws Exception {
    final List<String> newText = Arrays.asList("New text incoming");
    final String newDescription = "NPC with new text";
    final NPC updateNPCDTO = new NPC();
    updateNPCDTO.setText(newText);
    updateNPCDTO.setDescription(newDescription);

    final String bodyValue = objectMapper.writeValueAsString(updateNPCDTO);

    final MvcResult result = mvc
      .perform(put(fullURL + "/" + initialNPCDTO.getIndex()).content(bodyValue).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    final NPCDTO updatedNPCDTOResult = objectMapper.readValue(result.getResponse().getContentAsString(), NPCDTO.class);

    assertEquals(initialNPCDTO.getId(), updatedNPCDTOResult.getId());
    assertEquals(newText, updatedNPCDTOResult.getText());
    assertEquals(newDescription, updatedNPCDTOResult.getDescription());
    assertEquals(initialNPCDTO.getIndex(), updatedNPCDTOResult.getIndex());
  }

  @Test
  void updateNPCFromDungeon_DoesNotExist_ThrowsNotFound() throws Exception {
    final NPCDTO npcDTO = new NPCDTO();
    npcDTO.setText(Arrays.asList("Hey ho"));
    final String bodyValue = objectMapper.writeValueAsString(npcDTO);
    mvc
      .perform(put(fullDungeonURL + "/" + Integer.MAX_VALUE).content(bodyValue).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isNotFound())
      .andReturn();
  }

  @Test
  void updateNPCTaskFromDungeon() throws Exception {
    final List<String> newText = Arrays.asList("New text incoming");
    final NPC updateNPCDTO = new NPC();
    updateNPCDTO.setText(newText);

    final String bodyValue = objectMapper.writeValueAsString(updateNPCDTO);

    final MvcResult result = mvc
      .perform(
        put(fullDungeonURL + "/" + initialDungeonNPCDTO.getIndex())
          .content(bodyValue)
          .contentType(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andReturn();

    final NPCDTO updatedNPCDTOResult = objectMapper.readValue(result.getResponse().getContentAsString(), NPCDTO.class);

    assertEquals(initialDungeonNPCDTO.getId(), updatedNPCDTOResult.getId());
    assertEquals(newText, updatedNPCDTOResult.getText());
    assertEquals(initialDungeonNPCDTO.getIndex(), updatedNPCDTOResult.getIndex());
  }
}
