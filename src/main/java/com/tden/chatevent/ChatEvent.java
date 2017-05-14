package com.tden.chatevent;

import com.tden.TDGameBot;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.api.objects.Message;

/**
 * Created by Stanislav Myachenkov on 12/7/16.
 */

@Slf4j
public abstract class ChatEvent {

    @Getter
    protected Message message = null;
    @Getter
    protected TDGameBot bot = null;
    @Getter
    protected ChatEventType type = null;

    private boolean doReply;



    public ChatEvent(TDGameBot bot, ChatEventType type, Message message, Boolean doReply) {
        this.bot = bot;
        this.type = type;
        this.message = message;
        this.doReply = doReply;
    }

    public boolean isDoReply() { return doReply; }

    // generate bot response
    public abstract int processEvent();

}
