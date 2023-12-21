package de.unistuttgart.overworldbackend.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.Nullable;

import java.util.UUID;

/**
 * Data Transfer Object for ObjectSpot.
 *
 * @see ObjectSpot
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ObjectSpotDTO {

    @Nullable
    UUID id;

    AreaLocationDTO area;
    PositionDTO position;
}
