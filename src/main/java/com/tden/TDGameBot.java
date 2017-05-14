package com.tden;

import com.tden.chatevent.ChatEvent;
import com.tden.chatevent.ChatEventFactory;
import com.tden.command.CommandPrivacy;
import com.tden.encounterengine.EncounterSession;
import com.tden.utilities.ConfigurationHelper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.Optional;


/**
 * Created by Stanislav Myachenkov on 12/7/16.
 */

@Slf4j
public class TDGameBot extends TelegramLongPollingBot {

    @Getter
    EncounterSession encounterSession;
    @Getter
    CommandPrivacy commandPrivacy;

    private long delay;

    @Override
    public String getBotToken() {
        return ConfigurationHelper.getProperty("private_token");
    }

    public String getBotUsername() {
        return ConfigurationHelper.getProperty("bot_name");
    }

    public TDGameBot() {
        encounterSession = new EncounterSession();
        commandPrivacy = new CommandPrivacy();
        delay =  Long.parseLong(ConfigurationHelper.getProperty("message_delay", "300000"));;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message;

        // all incoming messages are chatevent, they are processed inside this event
        if(update.hasMessage()){

            message = update.getMessage();

            Chat chatType = message.getChat();               // private or group chat
            String chatTitle = "";

            if(chatType.isGroupChat() || chatType.isSuperGroupChat() || chatType.isChannelChat()){
                chatTitle =  chatType.getTitle();
            } else if (chatType.isUserChat() ){
                Optional<String> firstName = Optional.of(chatType.getFirstName());
                Optional<String> lastName = Optional.ofNullable(chatType.getLastName());
                chatTitle = firstName.get() + " " + (lastName.isPresent() ? lastName.get() : "");
            }

            log.info("[Chat] " + chatTitle +
                    " [ChatId] " + chatType.getId() +
                    " [User] @" + message.getFrom().getUserName() +
                    " [Message] " + message.getText());

            // only messages from last N minutes
            if( System.currentTimeMillis()/1000L - message.getDate() < delay){
                Optional<ChatEvent> event = Optional.of(ChatEventFactory.getEvent(this, message));
                event.get().processEvent();
            }
        }
    }

    public static void main(String[] args) {

        // we need to create it with an own log4j appender, because default is messing with all logs
        Logger logger = Logger.getRootLogger();
        Appender appender = logger.getAppender("telegrambots");

        BasicConfigurator.configure(appender);

        ApiContextInitializer.init();

        TelegramBotsApi botsApi = new TelegramBotsApi();

        TDGameBot tdGameBot = new TDGameBot();

        try {
            botsApi.registerBot(tdGameBot);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }

    }

    public void onClosing() { }

}
