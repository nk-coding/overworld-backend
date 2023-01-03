package de.unistuttgart.overworldbackend.data.config;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TeleporterConfig {

    String name;
    float x;
    float y;
}
