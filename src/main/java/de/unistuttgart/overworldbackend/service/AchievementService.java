package de.unistuttgart.overworldbackend.service;

import de.unistuttgart.overworldbackend.data.Achievement;
import de.unistuttgart.overworldbackend.data.AchievementStatistic;
import de.unistuttgart.overworldbackend.data.Player;
import de.unistuttgart.overworldbackend.data.enums.AchievementCategory;
import de.unistuttgart.overworldbackend.data.enums.AchievementTitle;
import de.unistuttgart.overworldbackend.repositories.AchievementRepository;
import de.unistuttgart.overworldbackend.repositories.AchievementStatisticRepository;
import de.unistuttgart.overworldbackend.repositories.PlayerRepository;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class AchievementService {

    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private AchievementStatisticRepository achievementStatisticRepository;

    @PostConstruct
    public void init() {
        final Achievement achievement1 = new Achievement(
            AchievementTitle.GO_FOR_A_WALK,
            "Go for a walk",
            "imageName",
            1,
            Arrays.asList(AchievementCategory.EXPLORING)
        );
        final Achievement achievement2 = new Achievement(
            AchievementTitle.GO_FOR_A_LONGER_WALK,
            "Go for a longer walk",
            "imageName",
            1,
            Arrays.asList(AchievementCategory.EXPLORING)
        );

        achievementRepository.save(achievement1);
        achievementRepository.save(achievement2);

        final List<Achievement> achievements = achievementRepository.findAll();

        /* Not working yet, achievement statistics is always empty /:

        for (final Player player : playerRepository.findAll()) {
            log.info("PLAYER " + player.getUserId());
            log.info(player.getAchievementStatistics().toString());
            // add statistic for achievement if not exists
            for (final Achievement achievement : achievements) {
                if (
                    player
                        .getAchievementStatistics()
                        .stream()
                        .noneMatch(achievementStatistic ->
                            achievementStatistic
                                .getAchievement()
                                .getAchievementTitle()
                                .equals(achievement.getAchievementTitle())
                        )
                ) {
                    log.info(
                        "Create new achievement for " + player.getUserId() + " ach " + achievement.getAchievementTitle()
                    );
                    player.getAchievementStatistics().add(new AchievementStatistic(player, achievement));
                }
            }
            // remove statistic for achievement if not exists
            player.getAchievementStatistics().removeIf(achievementStatistic ->
                achievements
                    .stream()
                    .noneMatch(achievement ->
                        achievement
                            .getAchievementTitle()
                            .equals(achievementStatistic.getAchievement().getAchievementTitle())
                    )
            );
            playerRepository.save(player);
        }
        */
    }
}
