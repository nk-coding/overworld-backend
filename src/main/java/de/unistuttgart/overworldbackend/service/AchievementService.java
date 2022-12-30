package de.unistuttgart.overworldbackend.service;

import de.unistuttgart.overworldbackend.data.Achievement;
import de.unistuttgart.overworldbackend.data.enums.AchievementCategory;
import de.unistuttgart.overworldbackend.data.enums.AchievementTitle;
import de.unistuttgart.overworldbackend.repositories.AchievementRepository;
import java.util.Arrays;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AchievementService {

    @Autowired
    private AchievementRepository achievementRepository;

    @PostConstruct
    public void init() {
        Achievement achievement1 = new Achievement(
            AchievementTitle.GO_FOR_A_WALK,
            "Go for a walk",
            "imageName",
            1,
            Arrays.asList(AchievementCategory.EXPLORING)
        );
        Achievement achievement2 = new Achievement(
            AchievementTitle.GO_FOR_A_LONGER_WALK,
            "Go for a longer walk",
            "imageName",
            1,
            Arrays.asList(AchievementCategory.EXPLORING)
        );

        achievementRepository.save(achievement1);
        achievementRepository.save(achievement2);
    }
}
