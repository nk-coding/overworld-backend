package com.example.overworldbackend.repositories;

import com.example.overworldbackend.baseClasses.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OverworldRepository extends JpaRepository<Configuration, Long> {
}
