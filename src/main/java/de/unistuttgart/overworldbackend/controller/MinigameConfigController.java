package de.unistuttgart.overworldbackend.controller;

import de.unistuttgart.overworldbackend.data.Configuration;
import de.unistuttgart.overworldbackend.repositories.OverworldRepository;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Slf4j
@RequestMapping("/config")
public class MinigameConfigController {

  @Autowired
  OverworldRepository overworldRepository;

  @PostMapping("/minigame")
  public Configuration saveConfiguration(@RequestBody Configuration configuration) {
    log.debug("saved config {}", configuration.toString());
    return overworldRepository.save(configuration);
  }

  @PutMapping("/config/minigame")
  public Configuration changeConfiguration(@RequestBody Configuration configuration) {
    Optional<Configuration> configurationToChange = overworldRepository.findById(configuration.getId());
    if (configurationToChange.isEmpty()) {
      throw new ResponseStatusException(
        HttpStatus.NOT_FOUND,
        "There is no configuration with id" + configuration.getId()
      );
    }
    configurationToChange.get().setConfigurationString(configuration.getConfigurationString());
    return overworldRepository.save(configurationToChange.get());
  }

  @GetMapping("/get-configurationString-by-staticWorldId/{staticWorldId}")
  public Configuration getConfigurationString(@PathVariable String staticWorldId) {
    Optional<Configuration> configuration = Optional.ofNullable(overworldRepository.findByStaticWorldId(staticWorldId));
    if (configuration.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no configuration with id" + staticWorldId);
    }
    return configuration.get();
  }
}
