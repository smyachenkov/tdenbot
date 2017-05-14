package com.tden.command;

import com.tden.command.encounter.*;
import com.tden.command.general.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Stanislav Myachenkov on 12/7/16.
 */

public class CommandModel {

    @Getter
    private static final Map<String, Class<BasicCommand>> commandMap;

    static
    {
        commandMap = new HashMap<>();
        for(CommandType foo : CommandType.values()){
            commandMap.put( foo.getIdentifier(), foo.getComClass() );
        }

    }

    @Getter
    @RequiredArgsConstructor
    public enum CommandType {

        FORMAT("format", FormatCommand.class, SecurityLevel.EVERYONE),
        FORMATH("formath", FormatHTMLCommand.class, SecurityLevel.EVERYONE),
        STARTGAME("start", StartGameCommand.class, SecurityLevel.APPROVED_USERS),
        STOPGAME("stop", StopGameCommand.class, SecurityLevel.APPROVED_USERS),
        PAUSEGAME("pause", PauseGameCommand.class, SecurityLevel.APPROVED_USERS),
        RESUMEGAME("resume", ResumeGameCommand.class, SecurityLevel.APPROVED_USERS),
        WHITELISTCHAT("white", WhitelistChatCommand.class, SecurityLevel.APPROVED_USERS),
        BLACKLISTCHAT("black", BlacklistChatCommand.class, SecurityLevel.APPROVED_USERS),
        GETBOTSTATUS("status", BotStatusCommand.class, SecurityLevel.APPROVED_USERS),
        LEVELSTATUS("level", LevelStatusCommand.class, SecurityLevel.APPROVED_USERS),
        UNDEFINED("undefined", UndefinedCommand.class, SecurityLevel.EVERYONE),
        ENTERCODE("code", EnterCodeCommand.class, SecurityLevel.APPROVED_CHATS);

        private final String identifier;
        private final Class comClass;
        private final SecurityLevel secLvl;

    }

    public enum SecurityLevel{
        EVERYONE,
        APPROVED_CHATS,
        APPROVED_USERS
    }

}
