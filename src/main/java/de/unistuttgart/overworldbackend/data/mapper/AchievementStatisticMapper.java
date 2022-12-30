package de.unistuttgart.overworldbackend.data.mapper;

import de.unistuttgart.overworldbackend.data.AchievementStatistic;
import de.unistuttgart.overworldbackend.data.AchievementStatisticDTO;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { AchievementMapper.class })
public interface AchievementStatisticMapper {
    AchievementStatisticDTO achievementStatisticToAchievementStatisticDTO(
        final AchievementStatistic achievementStatistic
    );

    AchievementStatistic achievementStatisticDTOToAchievementStatistic(
        final AchievementStatisticDTO achievementStatisticDTO
    );

    List<AchievementStatisticDTO> achievementStatisticsToAchievementStatisticDTOs(
        final List<AchievementStatistic> achievementStatistics
    );
}
