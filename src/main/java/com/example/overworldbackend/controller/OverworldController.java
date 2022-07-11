package com.example.overworldbackend.controller;

import com.example.overworldbackend.data.Configuration;
import com.example.overworldbackend.repositories.OverworldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;


@RestController
public class OverworldController {

    @Autowired
    OverworldRepository overworldRepository;

    @PostMapping("/config/minigame")
    public Configuration saveConfiguration(@RequestBody Configuration configuration) {
        return overworldRepository.save(configuration);
    }

    @PutMapping("/config/minigame")
    public Configuration changeConfiguration(@RequestBody Configuration configuration) {
        Optional<Configuration> configurationToChange = overworldRepository.findById(configuration.getId());
        if(configurationToChange.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no configuration with id"+ configuration.getId());
        }
        configurationToChange.get().setConfigurationString(configuration.getConfigurationString());
        return overworldRepository.save(configurationToChange.get());
    }

    @GetMapping("/get-configurationString-by-staticWorldId/{staticWorldId}")
    public Configuration getConfigurationString(@PathVariable String staticWorldId) {
        Optional<Configuration> configuration = Optional.ofNullable(overworldRepository.findByStaticWorldId(staticWorldId));
        if(configuration.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no configuration with id"+ staticWorldId);
        }
        return configuration.get();
    }
}
