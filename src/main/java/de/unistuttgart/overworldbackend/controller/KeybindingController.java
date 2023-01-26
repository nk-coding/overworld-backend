package de.unistuttgart.overworldbackend.controller;

import de.unistuttgart.gamifyit.authentificationvalidator.JWTValidatorService;
import de.unistuttgart.overworldbackend.data.KeybindingDTO;
import de.unistuttgart.overworldbackend.data.enums.Binding;
import de.unistuttgart.overworldbackend.data.mapper.KeybindingMapper;
import de.unistuttgart.overworldbackend.service.KeybindingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "KeybindingStatistic", description = "Modify keybinding statistic")
@RestController
@Slf4j
@RequestMapping("/players/{playerId}/keybindings")
public class KeybindingController {

    @Autowired
    JWTValidatorService jwtValidatorService;

    @Autowired
    private KeybindingMapper keybindingMapper;

    @Autowired
    private KeybindingService keybindingService;

    @Operation(summary = "Get all keybindings")
    @GetMapping("")
    public List<KeybindingDTO> getKeybindingStatistics(
        @PathVariable final String playerId,
        @CookieValue("access_token") final String accessToken
    ) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        log.debug("get keybindings");
        return keybindingMapper.keybindingStatisticsToKeybindingDTOs(
            keybindingService.getKeybindingStatisticsFromPlayer(playerId)
        );
    }

    @Operation(summary = "Get keybinding by its binding")
    @GetMapping("/{binding}")
    public KeybindingDTO getKeybindingStatistic(
        @PathVariable final String playerId,
        @PathVariable final Binding binding,
        @CookieValue("access_token") final String accessToken
    ) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        log.debug("get keybindings {} ", binding);
        return keybindingMapper.keybindingStatisticToKeybindingDTO(
            keybindingService.getKeybindingStatisticFromPlayer(playerId, binding)
        );
    }

    @Operation(summary = "Update keybinding")
    @PutMapping("/{binding}")
    public KeybindingDTO updateKeybindingStatistic(
        @PathVariable final String playerId,
        @PathVariable final Binding binding,
        @Valid @RequestBody final KeybindingDTO keybindingDTO,
        @CookieValue("access_token") final String accessToken
    ) {
        jwtValidatorService.validateTokenOrThrow(accessToken);
        log.debug("update keybinding {} to {}", binding, keybindingDTO.getKey());
        return keybindingMapper.keybindingStatisticToKeybindingDTO(
            keybindingService.updateKeybindingStatistic(playerId, binding, keybindingDTO)
        );
    }
}
