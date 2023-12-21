package de.unistuttgart.overworldbackend.data;

import de.unistuttgart.overworldbackend.data.enums.FacingDirection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Data Transfer Object for SceneTransitionSpot.
 *
 * @see SceneTransitionSpot
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SceneTransitionSpotDTO extends ObjectSpotDTO{

    PositionDTO size;
    AreaLocationDTO areaToLoad;
    FacingDirection facingDirection;
}
