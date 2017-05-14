package com.tden.encounterengine;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by Stanislav Myachenkov on 13.03.2017
 * To keep information about all sectors in level-info container
 */
@Slf4j
@Getter
public class LevelSector {

    int number;             // !! Attention !! numbers are assigned by order in engine, do not confuse with name !!
    String name;            // can be same for multiple sectors, we have to deal with it
    String answer;          // and we keep answers only from bot, manually entered codes cannot be found here
    boolean isAnswered;     // i like boolean flags

    // empty sector
    public LevelSector(int number, String name){
        this.name = name;
        this.number = number;
        answer = null;
        isAnswered = false;
    }

    // answered sector
    public LevelSector(int number, String name, String answer){
        this.number = number;
        this.name = name;
        this.answer = answer;
        isAnswered = true;
    }


    @Override
    public String toString() {
        return number + ": " + name + " code:" + answer;
    }
}
