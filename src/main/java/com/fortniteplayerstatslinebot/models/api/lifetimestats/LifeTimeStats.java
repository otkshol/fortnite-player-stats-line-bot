package com.fortniteplayerstatslinebot.models.api.lifetimestats;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LifeTimeStats {
    public List<Map<String,String>> lifeTimeStatsMapList;
}