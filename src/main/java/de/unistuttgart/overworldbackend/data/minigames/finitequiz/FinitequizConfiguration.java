package de.unistuttgart.overworldbackend.data.minigames.finitequiz;

import java.util.Set;
import java.util.UUID;
import lombok.Data;

@Data
public class FinitequizConfiguration {

    UUID id;

    Set<FinitequizQuestion> questions;
}
