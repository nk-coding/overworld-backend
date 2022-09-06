package de.unistuttgart.overworldbackend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unistuttgart.overworldbackend.data.Course;
import de.unistuttgart.overworldbackend.data.CourseDTO;
import de.unistuttgart.overworldbackend.data.CourseInitialData;
import de.unistuttgart.overworldbackend.data.mapper.CourseMapper;
import java.io.File;
import java.time.Duration;
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
import org.testcontainers.containers.ContainerState;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
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
    .withExposedService("overworld-db", 5432, Wait.forListeningPort())
    .withExposedService("reverse-proxy", 80)
    .waitingFor("reverse-proxy", Wait.forHttp("/minigames/chickenshock/api/v1/configurations").forPort(80))
    .waitingFor("reverse-proxy", Wait.forHttp("/minigames/crosswordpuzzle/api/v1/configurations").forPort(80))
    .waitingFor("reverse-proxy", Wait.forHttp("/minigames/finitequiz/api/v1/configurations").forPort(80));

  @DynamicPropertySource
  public static void properties(DynamicPropertyRegistry registry) {
    //wait till setup has finished
    ContainerState state = (ContainerState) compose.getContainerByServiceName("setup_1").get();
    while (state.isRunning()) {}
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
    registry.add(
      "finitequiz.url",
      () -> String.format("http://%s/minigames/finitequiz/api/v1", compose.getServiceHost("reverse-proxy", 80))
    );
    registry.add(
      "crosswordpuzzle.url",
      () -> String.format("http://%s/minigames/crosswordpuzzle/api/v1", compose.getServiceHost("reverse-proxy", 80))
    );
  }

  @Autowired
  MockMvc mvc;

  @Autowired
  private CourseMapper courseMapper;

  private String fullURL;
  private ObjectMapper objectMapper;

  @BeforeEach
  public void createBasicData() {
    fullURL = "/courses";
    objectMapper = new ObjectMapper();
  }

  @Test
  public void cloneCourseTest() throws Exception {
    final MvcResult resultGet = mvc
      .perform(get(fullURL + "/1").contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    final Course course = courseMapper.courseDTOToCourse(
      objectMapper.readValue(resultGet.getResponse().getContentAsString(), CourseDTO.class)
    );

    CourseInitialData initialData = new CourseInitialData();
    initialData.setCourseName("CloneCourse");
    initialData.setDescription("CloneDescription");
    initialData.setSemester("WS-23");

    final String bodyValue = objectMapper.writeValueAsString(initialData);

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
    assertEquals(
      course
        .getWorlds()
        .get(0)
        .getMinigameTasks()
        .stream()
        .filter((minigameTask -> minigameTask.getIndex() == 2))
        .findFirst()
        .get()
        .getGame(),
      cloneCourse
        .getWorlds()
        .get(0)
        .getMinigameTasks()
        .stream()
        .filter((minigameTask -> minigameTask.getIndex() == 2))
        .findFirst()
        .get()
        .getGame()
    );
    assertEquals(
      course
        .getWorlds()
        .get(0)
        .getMinigameTasks()
        .stream()
        .filter((minigameTask -> minigameTask.getIndex() == 3))
        .findFirst()
        .get()
        .getGame(),
      cloneCourse
        .getWorlds()
        .get(0)
        .getMinigameTasks()
        .stream()
        .filter((minigameTask -> minigameTask.getIndex() == 3))
        .findFirst()
        .get()
        .getGame()
    );
  }
}
