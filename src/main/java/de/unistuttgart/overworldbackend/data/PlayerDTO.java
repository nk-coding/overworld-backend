package de.unistuttgart.overworldbackend.data;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Data Transfer Object for Player.
 *
 * @see Player
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerDTO {

    String userId;

    String username;

    List<AchievementStatistic> achievementStatistics;

    List<Keybinding> keybindings;
}
