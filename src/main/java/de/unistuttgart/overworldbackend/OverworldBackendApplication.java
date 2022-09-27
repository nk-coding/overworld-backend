package de.unistuttgart.overworldbackend;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableFeignClients
public class OverworldBackendApplication {

  public static void main(final String[] args) {
    SpringApplication.run(OverworldBackendApplication.class, args);
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(@NotNull final CorsRegistry registry) {
        // allow CORS requests for all resources and HTTP methods from the frontend origin
        registry.addMapping("/**").allowedMethods("OPTIONS", "HEAD", "GET", "PUT", "POST", "DELETE");
      }
    };
  }
}
