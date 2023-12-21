package de.unistuttgart.overworldbackend.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.Nullable;

import java.util.UUID;

/**
 * Data Transfer Object for Position.
 *
 * @see Position
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PositionDTO {

    @Nullable
    UUID id;

    float x;
    float y;
}
