package de.unistuttgart.overworldbackend.data;

import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.Nullable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseCloneDTO {

    @Nullable
    int id;

    @NotNull
    String courseName;

    @Pattern(regexp = Course.SEMESTER_PATTERN)
    String semester;

    String description;
    boolean active;
    List<WorldDTO> worlds;
    List<String> errorMessages;
}
