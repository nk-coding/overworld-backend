package de.unistuttgart.overworldbackend;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unistuttgart.gamifyit.authentificationvalidator.JWTValidatorService;
import de.unistuttgart.overworldbackend.data.*;
import de.unistuttgart.overworldbackend.data.enums.Binding;
import de.unistuttgart.overworldbackend.data.mapper.KeybindingMapper;
import de.unistuttgart.overworldbackend.data.mapper.PlayerMapper;
import de.unistuttgart.overworldbackend.repositories.KeybindingRepository;
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
public class KeybindingTest {

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
    private KeybindingRepository keybindingRepository;

    @Autowired
    private KeybindingMapper keybindingMapper;

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
    void getPlayerKeybindings_DoesNotExist_ThrowsNotFound() throws Exception {
        mvc
            .perform(
                get("/players/" + Integer.MAX_VALUE + "/keybindings")
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNotFound())
            .andReturn();
    }

    @Test
    void getPlayerKeybindings() throws Exception {
        final MvcResult result = mvc
            .perform(get(fullURL + "/keybindings").cookie(cookie).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        final List<KeybindingDTO> keybindingStatistics = List.of(
            objectMapper.readValue(result.getResponse().getContentAsString(), KeybindingDTO[].class)
        );

        assertSame(Binding.values().length, keybindingStatistics.size());
        for (final Binding binding : Binding.values()) {
            assertTrue(keybindingStatistics.stream().anyMatch(statistic -> statistic.getBinding().equals(binding)));
        }
    }

    @Test
    void getPlayerKeybinding() throws Exception {
        final Binding binding = Binding.MOVE_UP;
        final MvcResult result = mvc
            .perform(get(fullURL + "/keybindings/" + binding).cookie(cookie).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        final KeybindingDTO keybindingDTO = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            KeybindingDTO.class
        );
        assertSame(binding, keybindingDTO.getBinding());
    }

    @Test
    void getPlayerKeybinding_WrongBinding_ThrowsBadRequest() throws Exception {
        mvc
            .perform(
                get(fullURL + "/keybindings/NotExistingBinding").cookie(cookie).contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest())
            .andReturn();
    }

    @Test
    void updatePlayerKeybinding() throws Exception {
        final Binding binding = Binding.MOVE_UP;
        final Keybinding keybinding = initialPlayer
            .getKeybindings()
            .stream()
            .filter(statistic -> statistic.getBinding().equals(binding))
            .findFirst()
            .get();
        final KeybindingDTO keybindingDTO = keybindingMapper.keybindingStatisticToKeybindingDTO(keybinding);
        keybindingDTO.setKey("H");
        final String bodyValue = objectMapper.writeValueAsString(keybindingDTO);
        final MvcResult result = mvc
            .perform(
                put(fullURL + "/keybindings/" + binding)
                    .cookie(cookie)
                    .content(bodyValue)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andReturn();

        final KeybindingDTO updatedkeybindingDTO = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            KeybindingDTO.class
        );
        assertSame(binding, updatedkeybindingDTO.getBinding());
        assertTrue(keybindingDTO.getKey().equals(updatedkeybindingDTO.getKey()));
    }

    @Test
    void updatePlayerKeybinding_WrongBinding_ThrowsBadRequest() throws Exception {
        final KeybindingDTO keybindingDTO = new KeybindingDTO();
        final String bodyValue = objectMapper.writeValueAsString(keybindingDTO);
        mvc
            .perform(
                put(fullURL + "/keybindings/notExistingBinding")
                    .cookie(cookie)
                    .content(bodyValue)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest())
            .andReturn();
    }

    @Test
    void updatePlayerKeybinding_InvalidBinding_ThrowsBadRequest() throws Exception {
        final Binding binding = Binding.MOVE_UP;
        final Binding differentBinding = Binding.MOVE_DOWN;
        final Keybinding keybinding = initialPlayer
            .getKeybindings()
            .stream()
            .filter(statistic -> statistic.getBinding().equals(binding))
            .findFirst()
            .get();
        final KeybindingDTO keybindingDTO = keybindingMapper.keybindingStatisticToKeybindingDTO(keybinding);
        keybindingDTO.setKey("H");
        final String bodyValue = objectMapper.writeValueAsString(keybindingDTO);
        final MvcResult result = mvc
            .perform(
                put(fullURL + "/keybindings/" + differentBinding)
                    .cookie(cookie)
                    .content(bodyValue)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest())
            .andReturn();
    }
}
