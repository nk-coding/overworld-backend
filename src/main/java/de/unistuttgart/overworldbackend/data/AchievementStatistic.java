package de.unistuttgart.overworldbackend.data;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.util.UUID;
import javax.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "player_user_id", "achievement_achievement_title" }) })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AchievementStatistic {

    @Id
    @GeneratedValue(generator = "uuid")
    UUID id;

    @JsonBackReference(value = "achievement-player")
    @ManyToOne
    Player player;

    @OneToOne
    Achievement achievement;

    int progress;
    boolean completed;

    public AchievementStatistic(Player player, Achievement achievement) {
        this.player = player;
        this.achievement = achievement;
        this.progress = 0;
        this.completed = false;
    }

    /**
     * Sets the progress to the given value, if valid, and updates the completed flag accordingly
     * @param newProgress the new progress
     * @throws IllegalArgumentException if the new progress is smaller than the current one
     */
    public void setProgress(int newProgress)
    {
        if(newProgress < progress)
        {
            throw new IllegalArgumentException("The new progress cannot be smaller than the current one");
        }
        progress = newProgress;
        if(progress >= achievement.getAmountRequired())
        {
            completed = true;
        }
    }
}
