package de.unistuttgart.overworldbackend.client;

import de.unistuttgart.overworldbackend.data.minigames.chickenshock.ChickenshockConfiguration;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "chickenshockClient", url = "${chickenshock.url}/configurations")
public interface ChickenshockClient {
    @GetMapping("/{id}")
    ChickenshockConfiguration getConfiguration(
        @CookieValue("access_token") final String accessToken,
        @PathVariable("id") UUID id
    );

    @PostMapping("/")
    ChickenshockConfiguration postConfiguration(
        @CookieValue("access_token") final String accessToken,
        ChickenshockConfiguration chickenshockConfiguration
    );
}
