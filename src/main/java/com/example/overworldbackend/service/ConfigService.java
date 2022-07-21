package com.example.overworldbackend.service;

import com.example.overworldbackend.repositories.OverworldRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfigService {

    @Autowired
    OverworldRepository overworldRepository;




}
