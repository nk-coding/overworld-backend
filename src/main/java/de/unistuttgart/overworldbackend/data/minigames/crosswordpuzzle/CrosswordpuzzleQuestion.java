package de.unistuttgart.overworldbackend.data.minigames.crosswordpuzzle;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CrosswordpuzzleQuestion {

  UUID id;

  String questionText;

  String answer;
}
