package com.tden.chatevent;

import com.tden.TDGameBot;
import com.tden.command.BasicCommand;
import com.tden.command.CommandFactory;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.api.objects.Message;

/**
 * Created by Stanislav Myachenkov on 12/7/16.
 */

@Slf4j
public class CommandEvent extends ChatEvent {

    public CommandEvent(TDGameBot bot, Message message) {
        super(bot, ChatEventType.COMMAND, message, true);
    }

    @Override
    public int processEvent() {
        BasicCommand command = CommandFactory.getCommand(getBot(), getMessage());
        return 0;
    }
}
