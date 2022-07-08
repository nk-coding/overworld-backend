package com.example.overworldbackend.baseClasses;

import javax.persistence.*;

@Entity
@Table(name = "configurations")
public class Configuration {

    @Id
    @Column(name = "id", nullable = false)
    private long id;
    @Column(nullable = false)
    private String configurationString;

    public Configuration(long id, String configurationString) {
        this.id = id;
        this.configurationString = configurationString;
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
}
