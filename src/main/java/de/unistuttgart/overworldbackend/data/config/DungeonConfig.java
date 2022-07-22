package de.unistuttgart.overworldbackend.data.config;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DungeonConfig {

  String staticName;
  int numberOfMinigames;
  int numberOfNPCs;
}
