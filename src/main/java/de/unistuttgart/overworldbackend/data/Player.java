package de.unistuttgart.overworldbackend.data;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.List;

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
    @OneToMany(cascade = CascadeType.ALL, targetEntity = AchievementStatistic.class)
    List<AchievementStatistic> achievementStatistics;


}
