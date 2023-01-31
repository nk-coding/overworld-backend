package de.unistuttgart.overworldbackend;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unistuttgart.gamifyit.authentificationvalidator.JWTValidatorService;
import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.enums.AchievementTitle;
import de.unistuttgart.overworldbackend.data.mapper.AchievementMapper;
import de.unistuttgart.overworldbackend.data.mapper.AchievementStatisticMapper;
import de.unistuttgart.overworldbackend.data.mapper.PlayerMapper;
import de.unistuttgart.overworldbackend.repositories.AchievementStatisticRepository;
import de.unistuttgart.overworldbackend.repositories.PlayerRepository;
import de.unistuttgart.overworldbackend.service.PlayerService;
import java.util.List;
import javax.servlet.http.Cookie;
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

        final PlayerRegistrationDTO playerRegistrationDTO = new PlayerRegistrationDTO("testUser", "testUserName");
        final PlayerDTO initialPlayerDTO = playerService.createPlayer(playerRegistrationDTO);
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
            .perform(get(fullURL + "/achievements").cookie(cookie).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        final List<AchievementStatisticDTO> achievementStatistics = List.of(
            objectMapper.readValue(result.getResponse().getContentAsString(), AchievementStatisticDTO[].class)
        );

        assertSame(AchievementTitle.values().length, achievementStatistics.size());
        for (final AchievementTitle title : AchievementTitle.values()) {
            assertTrue(
                achievementStatistics
                    .stream()
                    .anyMatch(statistic -> statistic.getAchievement().getAchievementTitle().equals(title))
            );
        }
    }

    @Test
    void getPlayerAchievement() throws Exception {
        final AchievementTitle achievementTitle = AchievementTitle.GO_FOR_A_WALK;
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
        final AchievementStatistic achievementStatistic = initialPlayer
            .getAchievementStatistics()
            .stream()
            .filter(statistic -> statistic.getAchievement().getAchievementTitle() == achievementTitle)
            .findFirst()
            .get();

        final AchievementStatisticDTO achievementStatisticDTO = achievementStatisticMapper.achievementStatisticToAchievementStatisticDTO(
            achievementStatistic
        );
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

    @Test
    void updatePlayerAchievement_WrongTitle_ThrowsBadRequest() throws Exception {
        final AchievementStatisticDTO achievementStatisticDTO = new AchievementStatisticDTO();
        final String bodyValue = objectMapper.writeValueAsString(achievementStatisticDTO);
        final MvcResult result = mvc
            .perform(
                put(fullURL + "/achievements/notExistingTitle")
                    .cookie(cookie)
                    .content(bodyValue)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest())
            .andReturn();
    }

    @Test
    void updatePlayerAchievement_InvalidProgress_ThrowsBadRequest() throws Exception {
        final AchievementTitle achievementTitle = AchievementTitle.GO_FOR_A_WALK;
        final AchievementStatistic achievementStatistic = initialPlayer
            .getAchievementStatistics()
            .stream()
            .filter(statistic -> statistic.getAchievement().getAchievementTitle() == achievementTitle)
            .findFirst()
            .get();

        final AchievementStatisticDTO achievementStatisticDTO = achievementStatisticMapper.achievementStatisticToAchievementStatisticDTO(
            achievementStatistic
        );
        achievementStatisticDTO.setProgress(achievementStatisticDTO.getProgress() - 1);
        final String bodyValue = objectMapper.writeValueAsString(achievementStatisticDTO);
        final MvcResult result = mvc
            .perform(
                put(fullURL + "/achievements/" + achievementTitle)
                    .cookie(cookie)
                    .content(bodyValue)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest())
            .andReturn();
    }

    @Test
    void updatePlayerAchievement_UpdateCompletedFlag() throws Exception {
        final AchievementTitle achievementTitle = AchievementTitle.GO_FOR_A_WALK;
        final AchievementStatistic achievementStatistic = initialPlayer
            .getAchievementStatistics()
            .stream()
            .filter(statistic -> statistic.getAchievement().getAchievementTitle() == achievementTitle)
            .findFirst()
            .get();

        final AchievementStatisticDTO achievementStatisticDTO = achievementStatisticMapper.achievementStatisticToAchievementStatisticDTO(
            achievementStatistic
        );
        achievementStatisticDTO.setProgress(achievementStatistic.getAchievement().getAmountRequired());
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
        assertTrue(updatedAchievementStatisticDTO.isCompleted());
    }

    @Test
    void updatePlayerAchievement_NotUpdateCompletedFlag() throws Exception {
        final AchievementTitle achievementTitle = AchievementTitle.GO_FOR_A_WALK;
        final AchievementStatistic achievementStatistic = initialPlayer
            .getAchievementStatistics()
            .stream()
            .filter(statistic -> statistic.getAchievement().getAchievementTitle() == achievementTitle)
            .findFirst()
            .get();

        final AchievementStatisticDTO achievementStatisticDTO = achievementStatisticMapper.achievementStatisticToAchievementStatisticDTO(
            achievementStatistic
        );
        achievementStatisticDTO.setProgress(achievementStatistic.getAchievement().getAmountRequired() - 1);
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
        assertFalse(updatedAchievementStatisticDTO.isCompleted());
    }
}
