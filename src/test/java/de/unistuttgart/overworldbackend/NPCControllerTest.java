package de.unistuttgart.overworldbackend;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.mapper.MinigameTaskMapper;
import de.unistuttgart.overworldbackend.data.mapper.NPCMapper;
import de.unistuttgart.overworldbackend.data.mapper.WorldMapper;
import de.unistuttgart.overworldbackend.repositories.LectureRepository;
import java.util.Arrays;
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
class NPCControllerTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private LectureRepository lectureRepository;

  @Autowired
  private WorldMapper worldMapper;

  @Autowired
  private NPCMapper npcMapper;

  private final String API_URL = "/api/v1/overworld";
  private String fullURL;
  private ObjectMapper objectMapper;

  private Lecture initialLecture;
  private World initialWorld;
  private WorldDTO initialWorldDTO;

  private NPC initialNPC;
  private NPCDTO initialNPCDTO;

  @BeforeEach
  public void createBasicData() {
    lectureRepository.deleteAll();

    final NPC npc = new NPC();
    npc.setText("You want to learn PSE?\n" + "This is so cool\n" + "Let's go!");
    npc.setStartLocation("w0-n0");

    final World world = new World();
    world.setStaticName("Winter Wonderland");
    world.setTopicName("UML Winter");
    world.setActive(true);
    world.setMinigameTasks(Set.of());
    world.setNpcs(Arrays.asList(npc));
    world.setDungeons(Arrays.asList());

    final Lecture lecture = new Lecture("PSE", "Basic lecture of computer science students", Arrays.asList(world));
    initialLecture = lectureRepository.save(lecture);

    initialWorld = initialLecture.getWorlds().stream().findFirst().get();
    initialWorldDTO = worldMapper.worldToWorldDTO(initialWorld);

    initialNPC = initialWorld.getNpcs().stream().findFirst().get();
    initialNPCDTO = npcMapper.npcToNPCDTO(initialNPC);

    assertNotNull(initialWorld.getId());
    assertNotNull(initialWorldDTO.getId());

    assertNotNull(initialNPC.getId());
    assertNotNull(initialNPCDTO.getId());

    fullURL = "/lectures/" + initialLecture.getId() + "/worlds/" + initialWorld.getId() + "/npcs";

    objectMapper = new ObjectMapper();
  }

  @Test
  void updateNPCFromWorld_DoesNotExist_ThrowsNotFound() throws Exception {
    final NPCDTO npcDTO = new NPCDTO();
    npcDTO.setText("Hey ho");
    final String bodyValue = objectMapper.writeValueAsString(npcDTO);
    mvc
      .perform(put(fullURL + "/" + UUID.randomUUID()).content(bodyValue).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isNotFound())
      .andReturn();
  }

  @Test
  void updateNPCTaskFromWorld() throws Exception {
    final String newText = "New text incoming";
    final NPC updateNPCDTO = new NPC();
    updateNPCDTO.setText(newText);

    final String bodyValue = objectMapper.writeValueAsString(updateNPCDTO);

    final MvcResult result = mvc
      .perform(put(fullURL + "/" + initialNPCDTO.getId()).content(bodyValue).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    final NPCDTO updatedNPCDTOResult = objectMapper.readValue(result.getResponse().getContentAsString(), NPCDTO.class);

    assertEquals(initialNPCDTO.getId(), updatedNPCDTOResult.getId());
    assertEquals(newText, updatedNPCDTOResult.getText());
    assertEquals(initialNPCDTO.getStartLocation(), updatedNPCDTOResult.getStartLocation());
  }
}
