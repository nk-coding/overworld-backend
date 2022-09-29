package de.unistuttgart.overworldbackend;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.net.URIBuilder;
import de.unistuttgart.gamifyit.authentificationvalidator.JWTValidatorService;
import de.unistuttgart.overworldbackend.client.ChickenshockClient;
import de.unistuttgart.overworldbackend.client.CrosswordpuzzleClient;
import de.unistuttgart.overworldbackend.client.FinitequizClient;
import de.unistuttgart.overworldbackend.data.CourseCloneDTO;
import de.unistuttgart.overworldbackend.data.CourseInitialData;
import java.io.File;
import java.net.URI;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@AutoConfigureMockMvc
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CloneTestWithoutBackends {

    @Container
    public static PostgreSQLContainer postgresDB = new PostgreSQLContainer("postgres:14-alpine")
        .withDatabaseName("postgres")
        .withUsername("postgres")
        .withPassword("postgres");

    @Container
    public static DockerComposeContainer compose = new DockerComposeContainer(
        new File("src/test/resources/docker-compose-test-without-backends.yaml")
    )
        .withPull(true)
        .withRemoveImages(DockerComposeContainer.RemoveImages.LOCAL)
        .withEnv("LOCAL_URL", postgresDB.getHost())
        .withExposedService("overworld-db", 5432, Wait.forListeningPort())
        .withExposedService("reverse-proxy", 80)
        .waitingFor(
            "reverse-proxy",
            Wait
                .forHttp("/keycloak/realms/Gamify-IT/.well-known/openid-configuration")
                .forPort(80)
                .forStatusCode(200)
                .withStartupTimeout(Duration.ofSeconds(120))
        );

    @DynamicPropertySource
    public static void properties(final DynamicPropertyRegistry registry) {
        //wait till setup has finished
        final ContainerState state = (ContainerState) compose.getContainerByServiceName("setup_1").get();
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
    }

    @Autowired
    MockMvc mvc;

    @MockBean
    JWTValidatorService jwtValidatorService;

    @Autowired
    ChickenshockClient chickenshockClient;

    @Autowired
    CrosswordpuzzleClient crosswordpuzzleClient;

    @Autowired
    FinitequizClient finitequizClient;

    private String fullURL;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void createBasicData() {
        fullURL = "/courses";
        objectMapper = new ObjectMapper();

        doNothing().when(jwtValidatorService).validateTokenOrThrow("testToken");
        when(jwtValidatorService.extractUserId("testToken")).thenReturn("testUser");
    }

    @Test
    void cloneCourseTest() throws Exception {
        final URI authorizationURI = new URIBuilder(
            compose.getServiceHost("reverse-proxy", 80) + "/keycloak/realms/Gamify-IT/protocol/openid-connect/token"
        )
            .build();
        final WebClient webclient = WebClient.builder().build();
        final MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.put("grant_type", Collections.singletonList("password"));
        formData.put("client_id", Collections.singletonList("game"));
        formData.put("username", Collections.singletonList("lecturer"));
        formData.put("password", Collections.singletonList("lecturer"));

        final String result = webclient
            .post()
            .uri(authorizationURI)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(BodyInserters.fromFormData(formData))
            .retrieve()
            .bodyToMono(String.class)
            .block();

        final JacksonJsonParser jsonParser = new JacksonJsonParser();

        final String access_token = jsonParser.parseMap(result).get("access_token").toString();

        final Cookie cookie = new Cookie("access_token", access_token);

        final CourseInitialData initialData = new CourseInitialData();
        initialData.setCourseName("CloneCourse");
        initialData.setDescription("CloneDescription");
        initialData.setSemester("WS-23");

        final String bodyValue = objectMapper.writeValueAsString(initialData);

        final MvcResult resultClone = mvc
            .perform(
                post(fullURL + "/1/clone")
                    .cookie(cookie)
                    .content(bodyValue)
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isCreated())
            .andReturn();
        final CourseCloneDTO courseCloneDTO = objectMapper.readValue(
            resultClone.getResponse().getContentAsString(),
            CourseCloneDTO.class
        );
        final List<String> errorMessages = courseCloneDTO.getErrorMessages();
        assertTrue(errorMessages.contains("chickenshock-backend not present"));
        assertTrue(errorMessages.contains("finitequiz-backend not present"));
        assertTrue(errorMessages.contains("crosswordpuzzle-backend not present"));
        assertTrue(errorMessages.contains("bugfinder-backend not present"));
    }
}
