package com.tden.chatevent;

import com.tden.TDGameBot;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/**
 * Created by Stanislav Myachenkov on 12/7/16.
 */

@Slf4j
public class LocationEvent extends ChatEvent {

    public LocationEvent(TDGameBot bot, Message message) {
        super(bot, ChatEventType.LOCATION, message, true);
    }

    @Override
    public int processEvent() {

        try {

            SendMessage reply = new SendMessage();
            Message msg = this.getMessage();

            Double longitude = msg.getLocation().getLongitude();
            Double latitude = msg.getLocation().getLatitude();

            reply.setChatId(msg.getChatId());
            reply.setReplyToMessageId(msg.getMessageId());
            reply.setText(longitude.toString() + ", " + latitude.toString());
            this.getBot().sendMessage(reply);
            return 0;

        } catch (TelegramApiException ex){
            log.error(String.format("Error processing event [ %s ] in message [ %s ] from user [ %s ]", this.getType().toString(), message, message.getFrom()));
            return 1;
        }

    }
}
