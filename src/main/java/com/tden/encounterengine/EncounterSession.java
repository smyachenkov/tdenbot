package com.tden.encounterengine;

import com.tden.utilities.ConfigurationHelper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;

/**
 * Created by Stanislav Myachenkov on 07.12.2016.
 */

@Slf4j
public class EncounterSession {

    private final String LOGIN_PART = "/Login.aspx??return=/";
    private final String LOGOUT_PART = "/Login.aspx?action=logout";
    private final String ENGINE_PART = "/gameengines/encounter/play/";

    private boolean watchSectors = true;

    public EncounterSession(){
        levelInfo = new EncounterLevelInfo();
        sessionInfo = new EncounterSessionInfo();
        watchSectors = ConfigurationHelper.getProperty("watchsectors", "1").equals("1");
    }

    // id, number and name of the level
    @Getter
    private EncounterLevelInfo levelInfo;

    // cookies, timing, status
    @Getter
    private EncounterSessionInfo sessionInfo;

    // separate thread for monitoring of current level
    private EngineWatcher engineWatcher;
    private Thread engineWatcherThread;

    public int loginToEnCx(String url){
        String login = ConfigurationHelper.getProperty("login");
            String password = ConfigurationHelper.getProperty("password");

        String loginUrl = url.substring(0, nthIndexOf(url, "/", 3)) + LOGIN_PART;
        String gameUrl =  url.substring(0, nthIndexOf(url, "/", 3))
                                            + ENGINE_PART
                                            + url.substring(url.lastIndexOf('=') + 1, url.length());

        try {

            log.info("Trying to log in to " + loginUrl);

            Connection.Response resLogin = Jsoup.connect(loginUrl)
                    .data("Login", login, "Password", password)
                    .method(Connection.Method.POST)
                    .execute();

            this.sessionInfo.setCookies(resLogin.cookies());
            this.sessionInfo.setPLAY_URL(gameUrl);
            this.sessionInfo.setENGINE_URL(url.substring(0, nthIndexOf(url, "/", 3)));
            this.sessionInfo.setLastEventTime(System.currentTimeMillis()/1000L);

            Document resEngine = Jsoup.connect(gameUrl)
                        .cookies(this.sessionInfo.getCookies())
                        .get();

            EncounterEnginePage enginePage = new EncounterEnginePage(resEngine);


            int curLevelNum = enginePage.retrieveLevelNumber();
            int currentLevelId = enginePage.retrieveLevelId();
            String currentLevelName = enginePage.retrieveLevelName();

            if(watchSectors) {
                List<LevelSector> sectors = enginePage.retrieveSectors();
                List<LevelSector> bonuses = enginePage.retrieveBonuses();
                levelInfo.update(currentLevelId, curLevelNum, currentLevelName, sectors, bonuses);
            } else {
                levelInfo.update(currentLevelId, curLevelNum, currentLevelName);
            }


            this.sessionInfo.setActivityStatus(ActivityStatus.ACTIVE);

            sessionInfo.setLastEventTime(System.currentTimeMillis()/1000L);

            engineWatcher = new EngineWatcher(this);
            engineWatcherThread = new Thread(engineWatcher);
            engineWatcherThread.start();
            log.info("Logged in successfully");

            return 0;

        } catch (IOException e) {
            log.error(String.format("Error while trying to login to %s", loginUrl));
            return  -1;
        }

    }

    public int sendCode(String code) {

        try {
            EncounterEnginePage eep = new EncounterEnginePage(
                                            Jsoup.connect(sessionInfo.getPLAY_URL())
                                                .cookies(this.sessionInfo.getCookies())
                                                .data("LevelId", String.valueOf(this.levelInfo.getCurrentLevelId()))
                                                .data("LevelNumber", String.valueOf(this.levelInfo.getCurrentLevelNumber()))
                                                .data("LevelAction.Answer", code)
                                                .post());

            updateLastEventTime(eep);
            return eep.isLastCodeCorrect() ? 1 : 0;

        } catch (IOException e) {
            log.error(String.format("Error while sending request to %s with code %s at level %s", sessionInfo.getPLAY_URL(), code, levelInfo.getCurrentLevelNumber()));
            return -1;
        }
    }

    // refresh level info after an aswer
    // more specific: lastEventTime in sessionInfo and whole levelInfo
    // plus sectores and bonuses
    private void updateLastEventTime(EncounterEnginePage eep){
        //SESSION
        sessionInfo.setLastEventTime(System.currentTimeMillis()/1000L);

        //LEVEL
        int currentLevelId = eep.retrieveLevelId();
        int curLevelNum = eep.retrieveLevelNumber();
        String curLevelName = eep.retrieveLevelName();

        if(watchSectors) {
            List<LevelSector> sectors = eep.retrieveSectors();
            List<LevelSector> bonuses = eep.retrieveBonuses();
            levelInfo.update(currentLevelId, curLevelNum, curLevelName, sectors, bonuses);
        } else {
            levelInfo.update(currentLevelId, curLevelNum, curLevelName);
        }


}

    // refresh page
    // for usage in external threads
    public void updateLastEventTime(){

        try {
            EncounterEnginePage eep = new EncounterEnginePage(Jsoup.connect(sessionInfo.getPLAY_URL())
                                                                                        .cookies(this.sessionInfo.getCookies())
                                                                                        .get());
            updateLastEventTime(eep);

        } catch (IOException e) {
            log.error(String.format("Error while sending request to refresh engine page %s ", sessionInfo.getPLAY_URL()));
        }

    }

    // logaut
    // nullify sessionInfo levelInfo
    // kill watcher
    public void stopGame(){
        try {

            Document resEngine = Jsoup.connect(sessionInfo.getENGINE_URL() + LOGOUT_PART)
                    .cookies(this.sessionInfo.getCookies())
                    .get();

            this.sessionInfo.deinit();
            this.levelInfo.deinit();
            this.engineWatcher.kill();

        } catch (IOException e){
            log.error(String.format("Error while sending request to logout %s", sessionInfo.getENGINE_URL() + LOGOUT_PART));
        }
    }

    private int nthIndexOf(String string, String token, int index) {
        int j = 0;
        for (int i = 0; i < index; i++) {
            j = string.indexOf(token, j + 1);
            if (j == -1) break;
        }
        return j;
    }

}
