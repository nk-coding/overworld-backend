package de.unistuttgart.overworldbackend;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unistuttgart.gamifyit.authentificationvalidator.JWTValidatorService;
import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.enums.AchievementTitle;
import de.unistuttgart.overworldbackend.data.mapper.*;
import de.unistuttgart.overworldbackend.repositories.AchievementStatisticRepository;
import de.unistuttgart.overworldbackend.repositories.CourseRepository;
import de.unistuttgart.overworldbackend.repositories.PlayerRepository;
import de.unistuttgart.overworldbackend.service.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.servlet.http.Cookie;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Transactional
@SpringBootTest
@Testcontainers
class AchievementStatisticTest {

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

    @MockBean
    JWTValidatorService jwtValidatorService;

    final Cookie cookie = new Cookie("access_token", "testToken");

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private AchievementStatisticRepository achievementStatisticRepository;

    @Autowired
    private AchievementMapper achievementMapper;

    @Autowired
    private AchievementStatisticMapper achievementStatisticMapper;

    @Autowired
    private PlayerService playerService;

    private Player initialPlayer;
    private PlayerMapper playerMapper;
    private ObjectMapper objectMapper;

    private String fullURL;

    @BeforeEach
    public void createBasicData() {
        playerRepository.deleteAll();

        final PlayerInitialData playerInitialData = new PlayerInitialData("testUser", "testUserName");
        final PlayerDTO initialPlayerDTO = playerService.createPlayer(playerInitialData);
        initialPlayer = playerRepository.findById(initialPlayerDTO.getUserId()).get();


        fullURL = String.format("/players/%s", initialPlayer.getUserId());

        objectMapper = new ObjectMapper();

        doNothing().when(jwtValidatorService).validateTokenOrThrow("testToken");
        when(jwtValidatorService.extractUserId("testToken")).thenReturn("testUser");
    }

    @Test
    void getPlayerAchievements_DoesNotExist_ThrowsNotFound() throws Exception {
        mvc
            .perform(
                get("/players/" + Integer.MAX_VALUE + "/achievements")
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNotFound())
            .andReturn();
    }

    @Test
    void getPlayerAchievements() throws Exception {
        final MvcResult result = mvc
            .perform(
                get(fullURL + "/achievements")
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andReturn();

        final List<AchievementStatisticDTO> achievementStatistics = List.of(objectMapper.readValue(
                result.getResponse().getContentAsString(),
                AchievementStatisticDTO[].class
        ));

        assertSame(AchievementTitle.values().length, achievementStatistics.size());
        for (AchievementTitle title : AchievementTitle.values()) {
            assertTrue(achievementStatistics.stream().anyMatch(statistic -> statistic.getAchievement().getAchievementTitle().equals(title)));
        }
    }

    @Test
    void getPlayerAchievement() throws Exception {
        AchievementTitle achievementTitle = AchievementTitle.GO_FOR_A_WALK;
        final MvcResult result = mvc
                .perform(
                        get(fullURL + "/achievements/" + achievementTitle)
                                .cookie(cookie)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        final AchievementStatisticDTO achievementStatisticDTO = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                AchievementStatisticDTO.class
        );
        assertSame(achievementTitle, achievementStatisticDTO.getAchievement().getAchievementTitle());
    }

    @Test
    void updatePlayerAchievement() throws Exception {
        final AchievementTitle achievementTitle = AchievementTitle.GO_FOR_A_WALK;
        final AchievementStatistic achievementStatistic = initialPlayer.getAchievementStatistics()
                .stream().filter(statistic -> statistic.getAchievement().getAchievementTitle() == achievementTitle).findFirst().get();

        final AchievementStatisticDTO achievementStatisticDTO = achievementStatisticMapper.achievementStatisticToAchievementStatisticDTO(achievementStatistic);
        achievementStatisticDTO.setProgress(achievementStatisticDTO.getProgress() + 1);
        final String bodyValue = objectMapper.writeValueAsString(achievementStatisticDTO);
        final MvcResult result = mvc
                .perform(
                        put(fullURL + "/achievements/" + achievementTitle)
                                .cookie(cookie)
                                .content(bodyValue)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        final AchievementStatisticDTO updatedAchievementStatisticDTO = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                AchievementStatisticDTO.class
        );
        assertSame(achievementTitle, updatedAchievementStatisticDTO.getAchievement().getAchievementTitle());
        assertSame(achievementStatisticDTO.getProgress(), updatedAchievementStatisticDTO.getProgress());
    }
}
