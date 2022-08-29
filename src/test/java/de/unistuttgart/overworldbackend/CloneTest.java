package de.unistuttgart.overworldbackend;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unistuttgart.overworldbackend.client.ChickenshockClient;
import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.mapper.CourseMapper;
import de.unistuttgart.overworldbackend.repositories.CourseRepository;
import java.io.File;
import java.util.UUID;
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
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@AutoConfigureMockMvc
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CloneTest {

  @Container
  public static DockerComposeContainer compose = new DockerComposeContainer(
    new File("src/test/resources/docker-compose-test.yaml")
  )
    .withExposedService("overworld-db", 5432)
    .withExposedService("reverse-proxy", 80);

  @DynamicPropertySource
  public static void properties(DynamicPropertyRegistry registry) {
    registry.add(
      "spring.datasource.url",
      () ->
        String.format(
          "jdbc:postgresql://%s:%d/postgres",
          compose.getServiceHost("overworld-db", 5432),
          compose.getServicePort("overworld-db", 5432)
        )
    );
    registry.add(
      "chickenshock.url",
      () -> String.format("http://%s/minigames/chickenshock/api/v1", compose.getServiceHost("reverse-proxy", 80))
    );
  }

  @Autowired
  MockMvc mvc;

  @Autowired
  private CourseRepository courseRepository;

  @Autowired
  private ChickenshockClient chickenshockClient;

  @Autowired
  private CourseMapper courseMapper;

  private String fullURL;
  private ObjectMapper objectMapper;

  @BeforeEach
  public void createBasicData() {
    fullURL = "/courses";
    objectMapper = new ObjectMapper();

    //wait till chickenshock-backend is ready
    boolean completed = false;
    while (!completed) {
      try {
        chickenshockClient.getConfiguration(UUID.fromString("70fcd00c-b67c-46f2-be73-961dc0bc8de1"));
        completed = true;
      } catch (Exception e) {
        completed = false;
      }
    }
  }

  @Test
  public void cloneCourseTest() throws Exception {
    CourseInitialData initialData = new CourseInitialData();
    initialData.setCourseName("CloneCourse");
    initialData.setDescription("CloneDescription");
    initialData.setSemester("WS-23");

    final String bodyValue = objectMapper.writeValueAsString(initialData);

    final MvcResult resultGet = mvc
      .perform(get(fullURL + "/1").contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    final Course course = courseMapper.courseDTOToCourse(
      objectMapper.readValue(resultGet.getResponse().getContentAsString(), CourseDTO.class)
    );

    final MvcResult resultClone = mvc
      .perform(post(fullURL + "/1/clone").content(bodyValue).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isCreated())
      .andReturn();
    final Course cloneCourse = courseMapper.courseDTOToCourse(
      objectMapper.readValue(resultClone.getResponse().getContentAsString(), CourseDTO.class)
    );

    assertEquals(
      course
        .getWorlds()
        .get(0)
        .getMinigameTasks()
        .stream()
        .filter((minigameTask -> minigameTask.getIndex() == 1))
        .findFirst()
        .get()
        .getGame(),
      cloneCourse
        .getWorlds()
        .get(0)
        .getMinigameTasks()
        .stream()
        .filter((minigameTask -> minigameTask.getIndex() == 1))
        .findFirst()
        .get()
        .getGame()
    );
  }
}
