package com.tden.chatevent;

import com.tden.TDGameBot;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.api.objects.Message;

/**
 * Created by Stanislav Myachenkov on 12/7/16.
 */

@Slf4j
public class UndefinedChatEvent extends ChatEvent {

    public UndefinedChatEvent(TDGameBot bot, Message message) {
        super(bot, ChatEventType.UNDEFINED, message, true);
    }

    @Override
    public int processEvent() {
        return 0;
    }

}
