package de.unistuttgart.overworldbackend.data.mapper;

import de.unistuttgart.overworldbackend.data.Achievement;
import de.unistuttgart.overworldbackend.data.AchievementDTO;
import de.unistuttgart.overworldbackend.data.AchievementStatistic;
import de.unistuttgart.overworldbackend.data.AchievementStatisticDTO;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AchievementMapper {
    AchievementDTO achievementToAchievementDTO(final Achievement achievement);

    Achievement achievementDTOToAchievement(final AchievementDTO achievementDTO);

    List<AchievementDTO> achievementsToAchievementDTOs(final List<Achievement> achievements);
}
