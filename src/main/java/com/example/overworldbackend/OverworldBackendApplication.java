package com.example.overworldbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@PropertySource(value = "classpath:db.properties")
public class OverworldBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(OverworldBackendApplication.class, args);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				// allow CORS requests for all resources and HTTP methods from the frontend origin
				registry.addMapping("/**")
						.allowedMethods("OPTIONS", "HEAD", "GET", "PUT", "POST", "DELETE");
			}
		};
	}

}
