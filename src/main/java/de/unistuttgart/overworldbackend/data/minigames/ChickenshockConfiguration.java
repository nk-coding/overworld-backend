package de.unistuttgart.overworldbackend.data.minigames;

import java.util.Set;
import java.util.UUID;
import lombok.Data;

@Data
public class ChickenshockConfiguration {

  UUID id;

  Set<Object> questions;
}
