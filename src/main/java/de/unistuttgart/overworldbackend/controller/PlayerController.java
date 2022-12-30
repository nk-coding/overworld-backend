package de.unistuttgart.overworldbackend.controller;

import de.unistuttgart.gamifyit.authentificationvalidator.JWTValidatorService;
import de.unistuttgart.overworldbackend.data.Player;
import de.unistuttgart.overworldbackend.data.PlayerDTO;
import de.unistuttgart.overworldbackend.data.PlayerInitialData;
import de.unistuttgart.overworldbackend.data.mapper.PlayerMapper;
import de.unistuttgart.overworldbackend.service.PlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Player", description = "Modify player")
@RestController
@Slf4j
@RequestMapping("/players")
public class PlayerController {

    @Autowired
    JWTValidatorService jwtValidatorService;

    @Autowired
    private PlayerService playerService;

    @Operation(summary = "Get all players")
    @GetMapping("")
    public List<PlayerDTO> getPlayers(@CookieValue("access_token") final String accessToken) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        log.debug("get players");
        return playerService.getPlayers();
    }

    @Operation(summary = "Get player with playerId")
    @GetMapping("/{playerId}")
    public PlayerDTO getPlayer(
        @PathVariable final String playerId,
        @CookieValue("access_token") final String accessToken
    ) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        log.debug("get players");
        return playerService.getPlayer(playerId);
    }

    @Operation(summary = "Create a playerStatistic in a course by playerId")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    public PlayerDTO createPlayer(
        @Valid @RequestBody final PlayerInitialData playerInitialData,
        @CookieValue("access_token") final String accessToken
    ) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        log.debug("create player for userId {}", playerInitialData.getUserId());
        return playerService.createPlayer(playerInitialData);
    }
}
