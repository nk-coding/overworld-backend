package de.unistuttgart.overworldbackend.data;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = AchievementStatistic.class)
    List<AchievementStatistic> achievementStatistics = new ArrayList<>();

    @JsonManagedReference(value = "player-keybindings")
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = Keybinding.class)
    List<Keybinding> keybindings = new ArrayList<>();

    public Player(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }
}
