package de.unistuttgart.overworldbackend.data.config;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WorldConfig {

  List<DungeonConfig> dungeons;
  String staticName;
  int numberOfMinigames;
  int numberOfNPCs;
}
