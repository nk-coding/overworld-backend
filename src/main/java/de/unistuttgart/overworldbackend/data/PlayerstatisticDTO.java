package de.unistuttgart.overworldbackend.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerstatisticDTO {
    @Nullable
    UUID id;

    List<Area> unlockedAreas;

    List<Dungeon> completedDungeons;

    Area currentArea;

    Lecture lecture;

    String username;

    long knowledge;
}
