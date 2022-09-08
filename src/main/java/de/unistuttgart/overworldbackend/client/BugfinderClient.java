package de.unistuttgart.overworldbackend.client;

import de.unistuttgart.overworldbackend.data.minigames.bugfinder.BugfinderConfiguration;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "bugfinderClient", url = "${bugfinder.url}/configuration")
public interface BugfinderClient {
  @GetMapping("/{id}")
  BugfinderConfiguration getConfiguration(@PathVariable("id") UUID id);

  @PostMapping("/")
  BugfinderConfiguration postConfiguration(BugfinderConfiguration bugfinderConfiguration);
}
