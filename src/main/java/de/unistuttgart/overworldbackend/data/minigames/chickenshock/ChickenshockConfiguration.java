package de.unistuttgart.overworldbackend.data.minigames.chickenshock;

import java.util.Set;
import java.util.UUID;
import lombok.Data;

@Data
public class ChickenshockConfiguration {

  UUID id;

  int time;

  Set<ChickenshockQuestion> questions;
}
