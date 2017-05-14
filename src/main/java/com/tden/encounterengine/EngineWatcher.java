package com.tden.encounterengine;

import com.tden.utilities.ConfigurationHelper;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by Stanislav Myachenkov on 05.01.2017.
 *
 * This thread monitors engine page and watches it for updates and closed sectors
 *
 */

@Slf4j
public class EngineWatcher implements Runnable {

    private volatile boolean isRunning = true;
    private EncounterSession session;
    private int refreshDelta;

    public EngineWatcher(EncounterSession session) {
        isRunning = true;
        this.session = session;
        refreshDelta = Integer.parseInt(ConfigurationHelper.getProperty("refresh_delay", "30"));
    }

    @Override
    public void run() {

        while (isRunning) {
            try {

                Thread.sleep(1000 * 3);         // mmmagic numbers, just for this thread for refresh

                if(session.getSessionInfo().getActivityStatus() == ActivityStatus.ACTIVE) {

                    // if there werent any events from bot during last secondsToWait - refresh engine from bo session
                    // refresh session data and level info
                    // send message about next level if it happened

                    long currTime = System.currentTimeMillis() / 1000L;
                    long lastEventTime = session.getSessionInfo().getLastEventTime();

                    if (currTime - lastEventTime > refreshDelta) {

                        log.info("Nothing's happening, refreshing engine page");
                        log.info("Current level:" + session.getLevelInfo().toString());

                        int levelNumberBefore = session.getLevelInfo().getCurrentLevelNumber();

                        session.updateLastEventTime();

                        if (session.getLevelInfo().getCurrentLevelNumber() > levelNumberBefore) {
                            log.info("New level! ");
                        }

                        log.info(session.getLevelInfo().toString());

                    }
                }
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

        }
    }

    public void kill() {
        isRunning = false;
    }
}
