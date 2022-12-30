package de.unistuttgart.overworldbackend.controller;

import de.unistuttgart.gamifyit.authentificationvalidator.JWTValidatorService;
import de.unistuttgart.overworldbackend.data.Player;
import de.unistuttgart.overworldbackend.data.PlayerDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "Player", description = "Modify player")
@RestController
@Slf4j
@RequestMapping("/players")
public class PlayerController {

    @Autowired
    JWTValidatorService jwtValidatorService;

    @Operation(summary = "Create a playerStatistic in a course by playerId")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    public PlayerDTO createPlayer(
            @Valid @RequestBody final PlayerDTO playerDTO,
            @CookieValue("access_token") final String accessToken
    ) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        log.debug("create player for userId {}", playerDTO.getUserId());

    }
}
