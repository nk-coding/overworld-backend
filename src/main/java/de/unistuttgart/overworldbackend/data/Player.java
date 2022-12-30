package de.unistuttgart.overworldbackend.data;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.unistuttgart.overworldbackend.data.enums.AchievementTitle;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Player {

    @Id
    String userId;

    String username;

    @JsonManagedReference(value = "player-achievements")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, targetEntity = AchievementStatistic.class)
    List<AchievementStatistic> achievementStatistics = new ArrayList<>();

    public Player(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }
}
