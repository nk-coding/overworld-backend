package de.unistuttgart.overworldbackend.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerTaskActionLog {
    @Id
    @GeneratedValue(generator = "uuid")
    UUID id;

    @ManyToOne
    PlayerTaskStatistic playerTaskStatistic;

    @ManyToOne
    Lecture lecture;

    Date date;

    long score;

    long currentHighscore;

    long gainedKnowledge;

    UUID configurationId;

    String game;
}
