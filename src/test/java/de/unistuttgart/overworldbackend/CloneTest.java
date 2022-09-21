package de.unistuttgart.overworldbackend;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.net.URIBuilder;
import de.unistuttgart.overworldbackend.client.ChickenshockClient;
import de.unistuttgart.overworldbackend.client.CrosswordpuzzleClient;
import de.unistuttgart.overworldbackend.client.FinitequizClient;
import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.mapper.CourseMapper;
import de.unistuttgart.overworldbackend.data.minigames.chickenshock.ChickenshockConfiguration;
import de.unistuttgart.overworldbackend.data.minigames.chickenshock.ChickenshockQuestion;
import de.unistuttgart.overworldbackend.data.minigames.crosswordpuzzle.CrosswordpuzzleConfiguration;
import de.unistuttgart.overworldbackend.data.minigames.crosswordpuzzle.CrosswordpuzzleQuestion;
import de.unistuttgart.overworldbackend.data.minigames.finitequiz.FinitequizConfiguration;
import de.unistuttgart.overworldbackend.data.minigames.finitequiz.FinitequizQuestion;
import java.io.File;
import java.net.URI;
import java.time.Duration;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import javax.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.containers.ContainerState;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@AutoConfigureMockMvc
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CloneTest {

  @Container
  public static PostgreSQLContainer postgresDB = new PostgreSQLContainer("postgres:14-alpine")
          .withDatabaseName("postgres")
          .withUsername("postgres")
          .withPassword("postgres");

  @Container
  public static DockerComposeContainer compose = new DockerComposeContainer(
    new File("src/test/resources/docker-compose-test.yaml")
  )
    .withPull(true)
    .withRemoveImages(DockerComposeContainer.RemoveImages.LOCAL)
    .withEnv("LOCAL_URL", postgresDB.getHost())
    .withExposedService("overworld-db", 5432, Wait.forListeningPort())
    .withExposedService("reverse-proxy", 80)
    .waitingFor(
      "reverse-proxy",
      Wait.forHttp("/minigames/chickenshock/api/v1/configurations").forPort(80).forStatusCode(400)
    )
    .waitingFor(
      "reverse-proxy",
      Wait.forHttp("/minigames/crosswordpuzzle/api/v1/configurations").forPort(80).forStatusCode(200)
    )
    .waitingFor(
      "reverse-proxy",
      Wait.forHttp("/minigames/finitequiz/api/v1/configurations").forPort(80).forStatusCode(400)
    )
    .waitingFor(
      "reverse-proxy",
      Wait.forHttp("/minigames/bugfinder/api/v1/configurations").forPort(80).forStatusCode(200)
    )
    .waitingFor(
      "reverse-proxy",
      Wait
        .forHttp("/keycloak/realms/Gamify-IT/.well-known/openid-configuration")
        .forPort(80)
        .forStatusCode(200)
        .withStartupTimeout(Duration.ofSeconds(120))
    );

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
    registry.add(
      "bugfinder.url",
      () -> String.format("http://%s/minigames/bugfinder/api/v1", compose.getServiceHost("reverse-proxy", 80))
    );
  }

  @Autowired
  MockMvc mvc;

  @Autowired
  ChickenshockClient chickenshockClient;

  @Autowired
  CrosswordpuzzleClient crosswordpuzzleClient;

  @Autowired
  FinitequizClient finitequizClient;

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
  void cloneCourseTest() throws Exception {
    URI authorizationURI = new URIBuilder(
      compose.getServiceHost("reverse-proxy", 80) + "/keycloak/realms/Gamify-IT/protocol/openid-connect/token"
    )
      .build();
    WebClient webclient = WebClient.builder().build();
    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    formData.put("grant_type", Collections.singletonList("password"));
    formData.put("client_id", Collections.singletonList("game"));
    formData.put("username", Collections.singletonList("lecturer"));
    formData.put("password", Collections.singletonList("lecturer"));

    String result = webclient
      .post()
      .uri(authorizationURI)
      .contentType(MediaType.APPLICATION_FORM_URLENCODED)
      .body(BodyInserters.fromFormData(formData))
      .retrieve()
      .bodyToMono(String.class)
      .block();

    JacksonJsonParser jsonParser = new JacksonJsonParser();

    String access_token = jsonParser.parseMap(result).get("access_token").toString();

    final MvcResult resultGet = mvc
      .perform(get(fullURL + "/1").contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    final Course course = courseMapper.courseDTOToCourse(
      objectMapper.readValue(resultGet.getResponse().getContentAsString(), CourseDTO.class)
    );

    final Cookie cookie = new Cookie("access_token", access_token);

    CourseInitialData initialData = new CourseInitialData();
    initialData.setCourseName("CloneCourse");
    initialData.setDescription("CloneDescription");
    initialData.setSemester("WS-23");

    final String bodyValue = objectMapper.writeValueAsString(initialData);

    final MvcResult resultClone = mvc
      .perform(post(fullURL + "/1/clone").content(bodyValue).cookie(cookie).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isCreated())
      .andReturn();
    final Course cloneCourse = courseMapper.courseDTOToCourse(
      objectMapper.readValue(resultClone.getResponse().getContentAsString(), CourseDTO.class)
    );

    for (int i = 0; i < course.getWorlds().size(); i++) {
      World world = course.getWorlds().get(i);
      World cloneWorld = cloneCourse.getWorlds().get(i);
      world
        .getMinigameTasks()
        .forEach(minigameTask -> {
          MinigameTask cloneMinigameTask = cloneWorld
            .getMinigameTasks()
            .stream()
            .filter(minigameTask1 -> minigameTask1.getIndex() == minigameTask.getIndex())
            .findAny()
            .get();
          assertEquals(minigameTask.getGame(), cloneMinigameTask.getGame());
          assertEquals(minigameTask.getDescription(), cloneMinigameTask.getDescription());
          compareMinigame(minigameTask, cloneMinigameTask, access_token);
        });
      world
        .getNpcs()
        .forEach(npc -> {
          NPC cloneNpc = cloneWorld
            .getNpcs()
            .stream()
            .filter(npc1 -> npc1.getIndex() == npc.getIndex())
            .findAny()
            .get();
          AtomicInteger k = new AtomicInteger(0);
          for (String text : npc.getText()) {
            cloneNpc.getText().get(k.getAndIncrement());
          }
          assertEquals(npc.getDescription(), cloneNpc.getDescription());
        });
      for (int j = 0; j < world.getDungeons().size(); j++) {
        Dungeon dungeon = world.getDungeons().get(i);
        Dungeon cloneDungeon = cloneWorld.getDungeons().get(i);
        dungeon
          .getMinigameTasks()
          .forEach(minigameTask -> {
            MinigameTask cloneMinigameTask = cloneDungeon
              .getMinigameTasks()
              .stream()
              .filter(minigameTask1 -> minigameTask1.getIndex() == minigameTask.getIndex())
              .findAny()
              .get();
            assertEquals(minigameTask.getGame(), cloneMinigameTask.getGame());
            assertEquals(minigameTask.getDescription(), cloneMinigameTask.getDescription());
            compareMinigame(minigameTask, cloneMinigameTask, access_token);
          });
        dungeon
          .getNpcs()
          .forEach(npc -> {
            NPC cloneNpc = cloneDungeon
              .getNpcs()
              .stream()
              .filter(npc1 -> npc1.getIndex() == npc.getIndex())
              .findAny()
              .get();
            AtomicInteger k = new AtomicInteger(0);
            for (String text : npc.getText()) {
              cloneNpc.getText().get(k.getAndIncrement());
            }
            assertEquals(npc.getDescription(), cloneNpc.getDescription());
          });
      }
    }
  }

  private void compareMinigame(MinigameTask minigameTask1, MinigameTask minigameTask2, String access_token) {
    if (minigameTask1 == null) {
      return;
    }
    if (minigameTask1.getGame() == null) {
      return;
    }
    switch (minigameTask1.getGame()) {
      case CHICKENSHOCK -> {
        ChickenshockConfiguration config1 = chickenshockClient.getConfiguration(
          access_token,
          minigameTask1.getConfigurationId()
        );
        ChickenshockConfiguration config2 = chickenshockClient.getConfiguration(
          access_token,
          minigameTask2.getConfigurationId()
        );
        config1
          .getQuestions()
          .forEach(chickenshockQuestion -> {
            Optional<ChickenshockQuestion> question = config2
              .getQuestions()
              .stream()
              .filter(chickenshockQuestion1 -> chickenshockQuestion1.getText().equals(chickenshockQuestion.getText()))
              .findAny();
            assertFalse(question.isEmpty());
          });
      }
      case CROSSWORDPUZZLE -> {
        CrosswordpuzzleConfiguration config1 = crosswordpuzzleClient.getConfiguration(
          access_token,
          minigameTask1.getConfigurationId()
        );
        CrosswordpuzzleConfiguration config2 = crosswordpuzzleClient.getConfiguration(
          access_token,
          minigameTask2.getConfigurationId()
        );
        config1
          .getQuestions()
          .forEach(crosswordpuzzleQuestion -> {
            Optional<CrosswordpuzzleQuestion> question = config2
              .getQuestions()
              .stream()
              .filter(crosswordpuzzleQuestion1 ->
                crosswordpuzzleQuestion1.getQuestionText().equals(crosswordpuzzleQuestion.getQuestionText())
              )
              .findAny();
            assertFalse(question.isEmpty());
          });
      }
      case FINITEQUIZ -> {
        FinitequizConfiguration config1 = finitequizClient.getConfiguration(
          access_token,
          minigameTask1.getConfigurationId()
        );
        FinitequizConfiguration config2 = finitequizClient.getConfiguration(
          access_token,
          minigameTask2.getConfigurationId()
        );
        config1
          .getQuestions()
          .forEach(finitequizQuestion -> {
            Optional<FinitequizQuestion> question = config2
              .getQuestions()
              .stream()
              .filter(finitequizQuestion1 -> finitequizQuestion1.getText().equals(finitequizQuestion.getText()))
              .findAny();
            assertFalse(question.isEmpty());
          });
      }
    }
  }
}