package de.unistuttgart.overworldbackend.data;

import de.unistuttgart.overworldbackend.data.enums.FacingDirection;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * A SceneTransitionSpot is an instance of an ObjectSpot
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SceneTransitionSpot extends ObjectSpot{

    @ManyToOne(cascade = CascadeType.ALL)
    Position size;

    @ManyToOne(cascade = CascadeType.ALL)
    Area areaToLoad;

    FacingDirection facingDirection;

    public SceneTransitionSpot(
            final AreaLocation area,
            final Position position,
            final Position size,
            final Area areaToLoad,
            final FacingDirection facingDirection
    ) {
        super(area, position);
        this.size = size;
        this.areaToLoad = areaToLoad;
        this.facingDirection = facingDirection;
    }
}
