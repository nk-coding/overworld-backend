package de.unistuttgart.overworldbackend.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Data Transfer Object for NPCSpot.
 *
 * @see NPCSpot
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NPCSpotDTO extends ObjectSpotDTO{

    int index;
    String name;
    String spriteName;
    String iconName;
}
