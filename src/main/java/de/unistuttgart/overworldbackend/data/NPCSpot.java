package de.unistuttgart.overworldbackend.data;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

/**
 * A NPCSpot is an instance of an ObjectSpot
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NPCSpot extends ObjectSpot{

    int index;

    String name;

    String spriteName;

    String iconName;

    public NPCSpot(
            final AreaLocation area,
            final Position position,
            final int index,
            final String name,
            final String spriteName,
            final String iconName
    ) {
        super(area, position);
        this.index = index;
        this.name = name;
        this.spriteName = spriteName;
        this.iconName = iconName;
    }
}
