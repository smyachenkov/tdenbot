package com.tden.command;

import com.tden.TDGameBot;
import lombok.Getter;
import org.telegram.telegrambots.api.objects.Message;

/**
 * Created by Stanislav Myachenkov on 09.12.2016.
 */
public abstract class BasicCommandImpl implements BasicCommand {

    protected CommandModel.CommandType type;
    protected TDGameBot bot;
    protected String argString;
    protected Message message;

    @Getter
    public CommandModel.SecurityLevel securityLevel;

    // all command have different reply settings based on its availability and security settings
    private boolean doReply = false;

    public BasicCommandImpl(TDGameBot bot,
                            Message message,
                            CommandModel.CommandType type,
                            String argString) {
        this.type = type;
        this.argString = argString;
        this.bot = bot;
        this.message = message;

        // security level is defined in command model
        this.securityLevel = type.getSecLvl();
    }

    public CommandModel.SecurityLevel getSecurityLevel(){
        return this.securityLevel;
    }

    public boolean isDoReply(){
        return doReply;
    }

    public String getArgString() { return argString; }

    public String getArguments(){
        String foo = message.getText();
        return foo.substring(foo.indexOf(" "),
                             foo.length());
    }

    public String getCommandName(){
        return type.getIdentifier();
    }


}
