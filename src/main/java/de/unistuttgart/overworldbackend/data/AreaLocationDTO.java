package de.unistuttgart.overworldbackend.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

/**
 * Data Transfer Object to communicate a location of an area.
 *
 * Providing a worldIndex and an optional dungeonIndex can identify an area of a course.
 * @see Area
 * @see de.unistuttgart.overworldbackend.service.AreaService
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AreaLocationDTO {

    int worldIndex;

    @Nullable
    Integer dungeonIndex;
}
