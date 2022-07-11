package com.example.overworldbackend.baseClasses;

import org.hibernate.annotations.GeneratorType;

import javax.persistence.*;

@Entity
@Table(name = "configurations")
public class Configuration {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(nullable = false, unique = true)
    private String staticWorldId;
    @Column(nullable = false, unique = true)
    private String configurationString;

    @Column(nullable = false)
    private String minigameType;

    public Configuration(String staticWorldId, String configurationString, String minigameType) {
        this.staticWorldId = staticWorldId;
        this.configurationString = configurationString;
        this.minigameType = minigameType;
    }

    public Configuration() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getConfigurationString() {
        return configurationString;
    }

    public void setConfigurationString(String configurationString) {
        this.configurationString = configurationString;
    }

    public String getStaticWorldId() {
        return staticWorldId;
    }

    public void setStaticWorldId(String staticWorldId) {
        this.staticWorldId = staticWorldId;
    }

    public String getMinigameType() {
        return minigameType;
    }

    public void setMinigameType(String minigameType) {
        this.minigameType = minigameType;
    }
}
