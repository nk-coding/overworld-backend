package de.unistuttgart.overworldbackend.data.config;

import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseConfig {

  List<WorldConfig> worlds;
}
