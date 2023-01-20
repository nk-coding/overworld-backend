package de.unistuttgart.overworldbackend.controller;

import de.unistuttgart.gamifyit.authentificationvalidator.JWTValidatorService;
import de.unistuttgart.overworldbackend.data.KeybindingStatisticDTO;
import de.unistuttgart.overworldbackend.data.enums.Keybinding;
import de.unistuttgart.overworldbackend.data.mapper.KeybindingStatisticMapper;
import de.unistuttgart.overworldbackend.service.KeybindingStatisticService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@Tag(name = "KeybindingStatistic", description = "Modify keybinding statistic")
@RestController
@Slf4j
@RequestMapping("/players/{playerId}/keybindings")
public class KeybindingStatisticController {

    @Autowired
    JWTValidatorService jwtValidatorService;

    @Autowired
    private KeybindingStatisticMapper keybindingStatisticMapper;

    @Autowired
    private KeybindingStatisticService keybindingStatisticService;

    @Operation(summary = "Get all keybindings")
    @GetMapping("")
    public List<KeybindingStatisticDTO> getKeybindingStatistics(
            @PathVariable final String playerId,
            @CookieValue("access_token") final String accessToken
    ) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        log.debug("get keybindings");
        return keybindingStatisticMapper.keybindingStatisticsToKeybindingDTOs(
                keybindingStatisticService.getKeybindingStatisticsFromPlayer(playerId)
        );
    }

    @Operation(summary = "Get keybinding by its binding")
    @GetMapping("/{binding}")
    public KeybindingStatisticDTO getKeybindingStatistic(
            @PathVariable final String playerId,
            @PathVariable final Keybinding binding,
            @CookieValue("access_token") final String accessToken
    ){
        jwtValidatorService.validateTokenOrThrow(accessToken);
        log.debug("get keybindings {} ", binding);
        return keybindingStatisticMapper.keybindingStatisticToKeybindingDTO(
                keybindingStatisticService.getKeybindingStatisticFromPlayer(playerId, binding)
        );
    }

    @Operation(summary = "Update keybinding")
    @PutMapping("/{binding}")
    public KeybindingStatisticDTO updateKeybindingStatistic(
            @PathVariable final String playerId,
            @PathVariable final Keybinding binding,
            @Valid @RequestBody final KeybindingStatisticDTO keybindingStatisticDTO,
            @CookieValue("access_token") final String accessToken
    ) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        log.debug("update keybinding {} to {}", binding, keybindingStatisticDTO.getKey());
        return keybindingStatisticMapper.keybindingStatisticToKeybindingDTO(
                keybindingStatisticService.updateKeybindingStatistic(playerId, binding, keybindingStatisticDTO)
        );
    }
}
