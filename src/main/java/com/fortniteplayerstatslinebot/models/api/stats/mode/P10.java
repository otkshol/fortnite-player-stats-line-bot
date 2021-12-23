package com.fortniteplayerstatslinebot.models.api.stats.mode;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fortniteplayerstatslinebot.models.api.stats.mode.detailstats.Kd;
import com.fortniteplayerstatslinebot.models.api.stats.mode.detailstats.Top1;
import com.fortniteplayerstatslinebot.models.api.stats.mode.detailstats.WinRatio;

// duo
@JsonIgnoreProperties(ignoreUnknown = true)
public class P10 {
    public Top1 top1;
    public Kd kd;
    public WinRatio winRatio;
}
