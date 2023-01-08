package de.unistuttgart.overworldbackend.data;

import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Data Transfer Object to communicate a change of a teleporter for a player
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerTeleporterData {

    @NotNull
    int index;

    @NotNull
    AreaLocationDTO area;

    @NotNull
    String userId;
}
