package de.unistuttgart.overworldbackend.data;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.unistuttgart.overworldbackend.data.enums.AchievementTitle;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
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
