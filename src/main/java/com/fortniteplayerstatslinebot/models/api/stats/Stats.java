package com.fortniteplayerstatslinebot.models.api.stats;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fortniteplayerstatslinebot.models.api.stats.mode.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Stats {
    public P2 p2;
    public P9 p9;
    public P10 p10;
    public Trios trios;
    public CurrP2 currP2;
    public CurrP9 currP9;
    public CurrP10 currP10;
    public CurrTrios currTrios;
}
