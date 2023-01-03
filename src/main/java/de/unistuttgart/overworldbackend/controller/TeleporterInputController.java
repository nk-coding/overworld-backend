package de.unistuttgart.overworldbackend.controller;

import de.unistuttgart.gamifyit.authentificationvalidator.JWTValidatorService;
import de.unistuttgart.overworldbackend.data.PlayerStatisticDTO;
import de.unistuttgart.overworldbackend.data.PlayerTeleporterData;
import de.unistuttgart.overworldbackend.service.PlayerStatisticService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Submit statistic", description = "Submit statistics")
@RestController
@Slf4j
@RequestMapping("/courses/{courseId}/teleporters")
public class TeleporterInputController {

    @Autowired
    private JWTValidatorService jwtValidatorService;

    @Autowired
    private PlayerStatisticService playerStatisticService;

    @Operation(summary = "Add a teleporter to a players unlocked teleporter list")
    @PostMapping("")
    public PlayerStatisticDTO inputData(
        @PathVariable final int courseId,
        @Valid @RequestBody final PlayerTeleporterData data,
        @CookieValue("access_token") final String accessToken
    ) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        log.debug("submitted data from teleporter unlock {}", data);
        return playerStatisticService.addUnlockedTeleporter(courseId, data);
    }
}
