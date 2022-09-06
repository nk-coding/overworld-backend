package de.unistuttgart.overworldbackend.client;

import de.unistuttgart.overworldbackend.data.minigames.crosswordpuzzle.CrosswordpuzzleConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.UUID;

@FeignClient(value = "crosswordpuzzleClient", url = "${crosswordpuzzle.url}/configurations")
public interface CrosswordpuzzleClient {
  @GetMapping("/{id}")
  CrosswordpuzzleConfiguration getConfiguration(@PathVariable("id") UUID id);

  @PostMapping("/")
  CrosswordpuzzleConfiguration postConfiguration(CrosswordpuzzleConfiguration crosswordpuzzleConfiguration);
}
