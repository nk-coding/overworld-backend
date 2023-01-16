package de.unistuttgart.overworldbackend.data.minigames.towercrush;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TowercrushQuestion {

    UUID id;

    String text;
    String rightAnswer;

    Set<String> wrongAnswers;
}
