package com.tden.chatevent;

import com.tden.TDGameBot;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.api.methods.send.SendLocation;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/**
 * Created by Stanislav Myachenkov on 12/7/16.
 */

@Slf4j
public class CoordinatesEvent extends ChatEvent {

    public CoordinatesEvent(TDGameBot bot, Message message) {
        super(bot, ChatEventType.COORDINATES, message,true);
    }

    @Override
    public int processEvent() {

        try {

            SendLocation reply = new SendLocation();
            Message msg = this.getMessage();

            String text = msg.getText().replaceAll("\\s+", " ");
            String longitude, latitude;
            String[] foo;

            foo = text.split(text.contains(",") ? "," : " ");
            latitude = foo[0].trim();
            longitude = foo[1].trim();

            reply.setChatId(msg.getChatId());
            reply.setReplyToMessageId(msg.getMessageId());
            reply.setLatitude(Float.parseFloat(latitude));
            reply.setLongitude(Float.parseFloat(longitude));

            getBot().sendLocation(reply);
            return 0;

        } catch (TelegramApiException ex) {
            log.error(String.format("Error processing message [%s] from user [%s]", message, message.getFrom()));
            return 1;
        }
    }
}
