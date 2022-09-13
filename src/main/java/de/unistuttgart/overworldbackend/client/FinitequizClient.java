package de.unistuttgart.overworldbackend.client;

import de.unistuttgart.overworldbackend.data.minigames.finitequiz.FinitequizConfiguration;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "finitequizClient", url = "${finitequiz.url}/configurations")
public interface FinitequizClient {
  @GetMapping("/{id}")
  FinitequizConfiguration getConfiguration(@PathVariable("id") UUID id);

  @PostMapping("/")
  FinitequizConfiguration postConfiguration(FinitequizConfiguration finitequizConfiguration);
}
