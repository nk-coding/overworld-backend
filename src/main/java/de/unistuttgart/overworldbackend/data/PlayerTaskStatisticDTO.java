package de.unistuttgart.overworldbackend.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerTaskStatisticDTO {
    @Nullable
    UUID id;

    Playerstatistic playerstatistic;

    MinigameTask minigameTask;

    Lecture lecture;

    @Min(0)
    @Max(100)
    long highscore;

    boolean completed;
}
