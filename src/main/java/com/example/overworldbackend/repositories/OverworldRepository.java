package com.example.overworldbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.overworldbackend.data.Configuration;

@Repository
public interface OverworldRepository extends JpaRepository<Configuration, Long> {
    Configuration findByStaticWorldId(String staticWorldId);
}
