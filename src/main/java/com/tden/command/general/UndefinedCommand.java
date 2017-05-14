package com.tden.command.general;

import com.tden.TDGameBot;
import com.tden.command.BasicCommandImpl;
import com.tden.command.CommandModel;
import org.telegram.telegrambots.api.objects.Message;

/**
 * Created by Stanislav Myachenkov on 09.12.2016.
 */
public class UndefinedCommand extends BasicCommandImpl {

    public UndefinedCommand(TDGameBot bot, Message message, String argString) {
        super( bot, message, CommandModel.CommandType.UNDEFINED, argString);
    }

    @Override
    public void processCommand() {
    }
}
