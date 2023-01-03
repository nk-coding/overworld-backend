package de.unistuttgart.overworldbackend.controller;

import de.unistuttgart.gamifyit.authentificationvalidator.JWTValidatorService;
import de.unistuttgart.overworldbackend.data.PlayerStatisticDTO;
import de.unistuttgart.overworldbackend.data.PlayerTaskStatisticDTO;
import de.unistuttgart.overworldbackend.data.PlayerTaskStatisticData;
import de.unistuttgart.overworldbackend.data.PlayerTeleportData;
import de.unistuttgart.overworldbackend.service.PlayerStatisticService;
import de.unistuttgart.overworldbackend.service.PlayerTaskStatisticService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Submit statistic", description = "Submit statistics")
@RestController
@Slf4j
@RequestMapping("/internal")
public class TeleporterInputController {

    @Autowired
    private JWTValidatorService jwtValidatorService;

    @Autowired
    private PlayerStatisticService playerStatisticService;

    @Operation(summary = "Submit change of teleporter status")
    @PostMapping("/submit-teleporter-pass")
    public PlayerStatisticDTO inputData(
        @Valid @RequestBody final PlayerTeleportData data,
        @CookieValue("access_token") final String accessToken
    ) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        log.debug("submitted data from teleporter unlock {}", data);
        return playerStatisticService.addUnlockedTeleporter(data);
    }
}
