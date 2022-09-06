package de.unistuttgart.overworldbackend.data.minigames.finitequiz;

import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class FinitequizConfiguration {

  UUID id;

  Set<FinitequizQuestion> questions;
}
