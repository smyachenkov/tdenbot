package com.tden.encounterengine;

import com.tden.utilities.ConfigurationHelper;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by Stanislav Myachenkov on 21.12.2016.
 */
public class EncounterSessionInfo {

    @Getter
    @Setter
    private ActivityStatus activityStatus = ActivityStatus.NOTACTIVE;

    // time of last game event in bot
    @Getter
    @Setter
    private long lastEventTime = 0;

    // http session cookies
    @Getter
    @Setter
    private Map<String, String> cookies = null;

    // url for engine
    @Getter
    @Setter
    private String PLAY_URL;

    @Getter
    @Setter
    private String ENGINE_URL;

    public void deinit(){
        activityStatus = ActivityStatus.NOTACTIVE;
        cookies = null;
        PLAY_URL = null;
        ENGINE_URL = null;
    }
}
