package de.unistuttgart.overworldbackend;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unistuttgart.overworldbackend.data.Lecture;
import de.unistuttgart.overworldbackend.data.LectureDTO;
import de.unistuttgart.overworldbackend.data.World;
import de.unistuttgart.overworldbackend.data.WorldDTO;
import de.unistuttgart.overworldbackend.data.mapper.LectureMapper;
import de.unistuttgart.overworldbackend.repositories.LectureRepository;
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
class LectureControllerTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private LectureRepository lectureRepository;

  @Autowired
  private LectureMapper lectureMapper;

  private final String API_URL = "/api/v1/overworld";
  private String fullURL;
  private ObjectMapper objectMapper;

  private Lecture initialLecture;
  private LectureDTO initialLectureDTO;

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
    initialLectureDTO = lectureMapper.lectureToLectureDTO(initialLecture);

    assertNotNull(initialLecture.getLectureName());
    assertNotNull(initialLectureDTO.getId());

    fullURL = "/lectures";

    objectMapper = new ObjectMapper();
  }

  @Test
  void deleteLecture() throws Exception {
    final MvcResult result = mvc
      .perform(delete(fullURL + "/" + initialLectureDTO.getId()).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    final LectureDTO lectureDTOResult = objectMapper.readValue(
      result.getResponse().getContentAsString(),
      LectureDTO.class
    );

    assertEquals(initialLectureDTO, lectureDTOResult);
    assertEquals(initialLectureDTO.getId(), lectureDTOResult.getId());
    assertSame(0, lectureRepository.findAll().size());
  }
}
