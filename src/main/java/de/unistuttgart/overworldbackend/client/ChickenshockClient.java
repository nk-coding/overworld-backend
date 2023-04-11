package de.unistuttgart.overworldbackend.client;

import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "chickenshockClient", url = "${chickenshock.url}/configurations")
public interface ChickenshockClient {
    @PostMapping("/{id}/clone")
    UUID postClone(@CookieValue("access_token") final String accessToken, @PathVariable UUID id);
}
