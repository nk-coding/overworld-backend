package de.unistuttgart.overworldbackend.repositories;

import de.unistuttgart.overworldbackend.data.KeybindingStatistic;
import de.unistuttgart.overworldbackend.data.enums.Keybinding;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KeybindingStatisticRepository extends JpaRepository<KeybindingStatistic, UUID> {
    List<KeybindingStatistic> findAllByPlayerUserId(String playerId);

    Optional<KeybindingStatistic> findByPlayerUserIdAndKeybinding(final String playerId, final Keybinding keybinding);
}
