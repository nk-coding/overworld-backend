package de.unistuttgart.overworldbackend.data.minigames;

import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChickenshockQuestion {

  UUID id;

  String text;
  String rightAnswer;

  Set<String> wrongAnswers;
}
