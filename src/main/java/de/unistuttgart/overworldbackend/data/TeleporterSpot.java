package de.unistuttgart.overworldbackend.data;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;

/**
 * A MinigameSpot is an instance of an ObjectSpot
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TeleporterSpot extends ObjectSpot{

    int index;

    String name;

    public TeleporterSpot(
            final AreaLocation area,
            final Position position,
            final int index,
            final String name
    ) {
        super(area, position);
        this.index = index;
        this.name = name;
    }
}
