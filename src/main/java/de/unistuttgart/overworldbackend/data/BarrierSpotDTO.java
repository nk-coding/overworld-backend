package de.unistuttgart.overworldbackend.data;

import de.unistuttgart.overworldbackend.data.enums.BarrierType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Data Transfer Object for BarrierSpot.
 *
 * @see BarrierSpot
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BarrierSpotDTO extends ObjectSpotDTO{

    BarrierType type;
    int destinationAreaIndex;
}
