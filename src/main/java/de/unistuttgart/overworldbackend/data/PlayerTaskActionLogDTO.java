package de.unistuttgart.overworldbackend.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.Nullable;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerTaskActionLogDTO {
    @Nullable
    UUID id;

    PlayerTaskStatistic playerTaskStatistic;

    Lecture lecture;

    Date date;

    long score;

    long currentHighscore;

    long gainedKnowledge;
}
