package com.fortniteplayerstatslinebot.models.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fortniteplayerstatslinebot.models.api.lifetimestats.LifeTimeStats;
import com.fortniteplayerstatslinebot.models.api.stats.Stats;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FortnitePlayerStats {
    public String accountId;
    public String epicUserHandle;
    //public Stats stats;
    public List<Map<String,String>> lifeTimeStats;
}

