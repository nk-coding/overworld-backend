package de.unistuttgart.overworldbackend.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Data Transfer Object for TeleporterSpot.
 *
 * @see TeleporterSpot
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TeleporterSpotDTO extends ObjectSpotDTO{

    int index;
    String name;
}
