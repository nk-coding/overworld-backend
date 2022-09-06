package de.unistuttgart.overworldbackend.data.minigames.crosswordpuzzle;

import java.util.Set;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CrosswordpuzzleConfiguration {

    UUID id;

    String name;

    Set<CrosswordpuzzleQuestion> questions;
}
