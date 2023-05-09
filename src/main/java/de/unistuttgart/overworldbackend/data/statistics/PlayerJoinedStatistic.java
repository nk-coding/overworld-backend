package de.unistuttgart.overworldbackend.data.statistics;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerJoinedStatistic {

    int totalPlayers;
    List<PlayerJoined> joined;

    public PlayerJoinedStatistic() {
        totalPlayers = 0;
        joined = new ArrayList<>();
    }

    public void addPlayer() {
        totalPlayers++;
    }
}
