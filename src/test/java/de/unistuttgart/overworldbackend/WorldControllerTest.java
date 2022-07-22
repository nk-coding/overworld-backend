package de.unistuttgart.overworldbackend;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unistuttgart.overworldbackend.data.Lecture;
import de.unistuttgart.overworldbackend.data.World;
import de.unistuttgart.overworldbackend.data.WorldDTO;
import de.unistuttgart.overworldbackend.data.mapper.WorldMapper;
import de.unistuttgart.overworldbackend.repositories.LectureRepository;
import de.unistuttgart.overworldbackend.repositories.WorldRepository;
import java.util.Set;
import java.util.UUID;
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
class WorldControllerTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private LectureRepository lectureRepository;

  @Autowired
  private WorldMapper worldMapper;

  private final String API_URL = "/api/v1/overworld";
  private String fullURL;
  private ObjectMapper objectMapper;

  private Lecture initialLecture;
  private World initialWorld;
  private WorldDTO initialWorldDTO;

  @BeforeEach
  public void createBasicData() {
    lectureRepository.deleteAll();

    World world = new World();
    world.setStaticName("Winter Wonderland");
    world.setTopicName("UML Winter");
    world.setActive(true);
    world.setMinigameTasks(Set.of());
    world.setNpcs(Set.of());
    world.setDungeons(Set.of());

    Lecture lecture = new Lecture("PSE", "Basic lecture of computer science students", Set.of(world));
    initialLecture = lectureRepository.save(lecture);
    initialWorld = initialLecture.getWorlds().stream().findFirst().get();
    initialWorldDTO = worldMapper.worldToWorldDTO(initialWorld);

    assertNotNull(initialWorld.getId());
    assertNotNull(initialWorldDTO.getId());

    fullURL = "/lectures/" + initialLecture.getId() + "/worlds";

    objectMapper = new ObjectMapper();
  }

  @Test
  void getWorldsFromLecture() throws Exception {
    final MvcResult result = mvc
      .perform(get(fullURL).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    final Set<WorldDTO> worlds = Set.of(
      objectMapper.readValue(result.getResponse().getContentAsString(), WorldDTO[].class)
    );
    final WorldDTO worldDTO = worlds.stream().findFirst().get();
    assertSame(1, worlds.size());
    assertEquals(initialWorldDTO.getId(), worldDTO.getId());
    assertEquals(initialWorldDTO, worldDTO);
  }

  @Test
  void getWorldFromLecture() throws Exception {
    final MvcResult result = mvc
      .perform(get(fullURL + "/" + initialWorldDTO.getId()).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    final WorldDTO worldDTO = objectMapper.readValue(result.getResponse().getContentAsString(), WorldDTO.class);

    assertEquals(initialWorldDTO.getId(), worldDTO.getId());
    assertEquals(initialWorldDTO, worldDTO);
  }

  @Test
  void getWorldFromLecture_DoesNotExist_ThrowsNotFound() throws Exception {
    mvc
      .perform(get(fullURL + "/" + UUID.randomUUID()).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isNotFound())
      .andReturn();
  }

  @Test
  void updateWorldFromLecture() throws Exception {
    final String newTopicName = "Closed Topic";
    final boolean newActiveStatus = false;
    final WorldDTO updatedWorldDTO = worldMapper.worldToWorldDTO(initialWorld);
    updatedWorldDTO.setActive(newActiveStatus);
    updatedWorldDTO.setTopicName(newTopicName);
    final String bodyValue = objectMapper.writeValueAsString(updatedWorldDTO);

    final MvcResult result = mvc
      .perform(put(fullURL + "/" + initialWorldDTO.getId()).content(bodyValue).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    final WorldDTO updatedWorldDTOResult = objectMapper.readValue(
      result.getResponse().getContentAsString(),
      WorldDTO.class
    );

    assertEquals(initialWorldDTO.getId(), updatedWorldDTOResult.getId());
    assertEquals(newTopicName, updatedWorldDTOResult.getTopicName());
    assertEquals(newActiveStatus, updatedWorldDTOResult.isActive());
    assertEquals(initialWorldDTO.getStaticName(), updatedWorldDTOResult.getStaticName());
    assertEquals(initialWorldDTO.getDungeons(), updatedWorldDTOResult.getDungeons());
    assertEquals(initialWorldDTO.getNpcs(), updatedWorldDTOResult.getNpcs());
    assertEquals(initialWorldDTO.getMinigameTasks(), updatedWorldDTOResult.getMinigameTasks());
    assertEquals(initialWorldDTO, updatedWorldDTOResult);
  }

  @Test
  void updateWorldFromLecture_DoNotUpdatedStaticName() throws Exception {
    final String newTopicName = "Closed Topic";
    final String newStaticName = "PSE World Override Static Name";
    final boolean newActiveStatus = false;
    final WorldDTO updatedWorldDTO = worldMapper.worldToWorldDTO(initialWorld);
    updatedWorldDTO.setActive(newActiveStatus);
    updatedWorldDTO.setTopicName(newTopicName);
    updatedWorldDTO.setStaticName(newStaticName);
    final String bodyValue = objectMapper.writeValueAsString(updatedWorldDTO);

    final MvcResult result = mvc
      .perform(put(fullURL + "/" + initialWorld.getId()).content(bodyValue).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    final WorldDTO updatedWorldDTOResult = objectMapper.readValue(
      result.getResponse().getContentAsString(),
      WorldDTO.class
    );

    assertEquals(initialWorldDTO.getId(), updatedWorldDTOResult.getId());
    assertEquals(newTopicName, updatedWorldDTOResult.getTopicName());
    assertEquals(newActiveStatus, updatedWorldDTOResult.isActive());
    assertNotEquals(newStaticName, updatedWorldDTOResult.getStaticName());
    assertEquals(initialWorldDTO.getStaticName(), updatedWorldDTOResult.getStaticName());
    assertEquals(initialWorldDTO.getDungeons(), updatedWorldDTOResult.getDungeons());
    assertEquals(initialWorldDTO.getNpcs(), updatedWorldDTOResult.getNpcs());
    assertEquals(initialWorldDTO.getMinigameTasks(), updatedWorldDTOResult.getMinigameTasks());
    assertEquals(initialWorldDTO, updatedWorldDTOResult);
  }
}
