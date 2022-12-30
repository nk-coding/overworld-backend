package de.unistuttgart.overworldbackend.controller;

import de.unistuttgart.gamifyit.authentificationvalidator.JWTValidatorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Achievement", description = "Modify achievement")
@RestController
@Slf4j
@RequestMapping("/players/{playerId}/achievements")
public class AchievementController {

    @Autowired
    JWTValidatorService jwtValidatorService;
}
