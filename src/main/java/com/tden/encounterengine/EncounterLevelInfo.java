package com.tden.encounterengine;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Stanislav Myachenkov on 10.12.2016.
 *
 * Info-container with data of current level and parsers
 */
@ToString
public class EncounterLevelInfo {

    @Getter
    private int currentLevelId;

    @Getter
    private int currentLevelNumber;

    @Getter
    private String currentLevelName;

    @Getter
    private List<LevelSector> sectors;
    private List<LevelSector> bonuses;

    public void update(int levelId, int levelNumber, String levelName){
        this.currentLevelId = levelId;
        this.currentLevelNumber = levelNumber;
        this.currentLevelName = levelName;
    }

    public void update(int levelId, int levelNumber, String levelName, List<LevelSector> sectors, List<LevelSector> bonuses ){
        this.currentLevelId = levelId;
        this.currentLevelNumber = levelNumber;
        this.currentLevelName = levelName;
        this.sectors = sectors;
        this.bonuses = bonuses;
    }

    public void deinit(){
        currentLevelId = 0;
        currentLevelNumber = 0;
        currentLevelName = "";
        sectors = null;
        bonuses = null;
    }

}
