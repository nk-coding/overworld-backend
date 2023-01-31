package de.unistuttgart.overworldbackend.client;

import de.unistuttgart.overworldbackend.data.minigames.towercrush.TowercrushConfiguration;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "towercrushClient", url = "${towercrush.url}/configurations")
public interface TowercrushClient {
    @GetMapping("/{id}")
    TowercrushConfiguration getConfiguration(
        @CookieValue("access_token") final String accessToken,
        @PathVariable("id") UUID id
    );

    @PostMapping("/")
    TowercrushConfiguration postConfiguration(
        @CookieValue("access_token") final String accessToken,
        TowercrushConfiguration towercrushConfiguration
    );
}
