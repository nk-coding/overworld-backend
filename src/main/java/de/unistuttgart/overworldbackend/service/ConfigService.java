package de.unistuttgart.overworldbackend.service;

import de.unistuttgart.overworldbackend.repositories.OverworldRepository;
import javax.transaction.Transactional;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfigService {

  @Autowired
  OverworldRepository overworldRepository;
}
