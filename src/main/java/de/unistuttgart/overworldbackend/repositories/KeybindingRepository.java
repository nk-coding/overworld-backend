package de.unistuttgart.overworldbackend.repositories;

import de.unistuttgart.overworldbackend.data.Keybinding;
import de.unistuttgart.overworldbackend.data.enums.Binding;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KeybindingRepository extends JpaRepository<Keybinding, UUID> {
    List<Keybinding> findAllByPlayerUserId(String playerId);

    Optional<Keybinding> findByPlayerUserIdAndBinding(final String playerId, final Binding binding);
}
