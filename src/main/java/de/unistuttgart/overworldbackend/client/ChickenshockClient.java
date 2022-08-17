package de.unistuttgart.overworldbackend.client;

import de.unistuttgart.overworldbackend.data.minigames.ChickenshockConfiguration;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "resultClient", url = "${chickenshock.url}/counfigurations")
public interface ChickenshockClient {
  @GetMapping("/{id}")
  ChickenshockConfiguration getConfiguration(@PathVariable UUID id);

  @PostMapping("")
  ChickenshockConfiguration postConfiguration(ChickenshockConfiguration chickenshockConfiguration);
}
