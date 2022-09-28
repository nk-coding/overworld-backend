package de.unistuttgart.overworldbackend.data.minigames.bugfinder;

import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BugfinderConfiguration {

    UUID id;
    List<BugfinderCode> codes;
}
