package com.fortniteplayerstatslinebot.models.api.stats.mode.detailstats;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Top1 {
    public String label;
    public String field;
    public String category;
    public int valueInt;
    public String value;
    public int rank;
    public double percentile;
    public int displayType;
    public String displayValue;
}
