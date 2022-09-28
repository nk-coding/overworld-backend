package de.unistuttgart.overworldbackend.client;

import de.unistuttgart.overworldbackend.data.minigames.crosswordpuzzle.CrosswordpuzzleConfiguration;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "crosswordpuzzleClient", url = "${crosswordpuzzle.url}/configurations")
public interface CrosswordpuzzleClient {
    @GetMapping("/{id}")
    CrosswordpuzzleConfiguration getConfiguration(
        @CookieValue("access_token") final String accessToken,
        @PathVariable("id") UUID id
    );

    @PostMapping("/")
    CrosswordpuzzleConfiguration postConfiguration(
        @CookieValue("access_token") final String accessToken,
        CrosswordpuzzleConfiguration crosswordpuzzleConfiguration
    );
}
