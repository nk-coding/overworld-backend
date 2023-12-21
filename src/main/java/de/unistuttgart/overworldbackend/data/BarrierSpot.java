package de.unistuttgart.overworldbackend.data;

import de.unistuttgart.overworldbackend.data.enums.BarrierType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;

/**
 * A BarrierSpot is an instance of an ObjectSpot
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BarrierSpot extends ObjectSpot{

    BarrierType type;
    int destinationAreaIndex;

    public BarrierSpot(
            final AreaLocation area,
            final Position position,
            final BarrierType type,
            final int destinationAreaIndex
    ) {
        super(area, position);
        this.type = type;
        this.destinationAreaIndex = destinationAreaIndex;
    }
}
