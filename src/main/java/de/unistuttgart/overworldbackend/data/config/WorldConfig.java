package de.unistuttgart.overworldbackend.data.config;

import de.unistuttgart.overworldbackend.data.config.DungeonConfig;
import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WorldConfig {

  List<DungeonConfig> dungeons;
  String staticName;
  int numberOfMinigames;
  int numberOfNPCs;
}
