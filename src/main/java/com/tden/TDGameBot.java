package com.tden;

import com.google.cloud.speech.spi.v1.SpeechClient;
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

import java.io.IOException;
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
    @Getter
    SpeechClient speechClient;

    private static final String MESSAGE_GROUP_LOG =
            "[Chat] %s" + " [ChatId] %s" + " [User] %s" + " [Message] %s";

    private static final String MESSAGE_PRIVATE_LOG =
            "[User] %s" + " [Message] %s";

    private long delay;

    @Override
    public String getBotToken() {
        return ConfigurationHelper.getProperty("private_token");
    }

    public String getBotUsername() {
        return ConfigurationHelper.getProperty("bot_name");
    }

    public TDGameBot() throws IOException{
        encounterSession = new EncounterSession();
        commandPrivacy = new CommandPrivacy();
        delay =  Long.parseLong(ConfigurationHelper.getProperty("message_delay", "300000"));;
        speechClient = SpeechClient.create();
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message;

        // all incoming messages are chatevent, they are processed inside this event
        if(update.hasMessage()){

            message = update.getMessage();

            Chat chat = message.getChat();
            String chatTitle;
            String user;

            Optional<String> firstName = Optional.of(message.getFrom().getFirstName());
            Optional<String> lastName = Optional.ofNullable(message.getFrom().getLastName());
            Optional<String> userName = Optional.ofNullable(message.getFrom().getUserName());

            // first name is mandatory, last- and user- are optional
            user = firstName.get() + " " + (lastName.isPresent() ? lastName.get() : "") + " " + (userName.isPresent() ? "@" + userName.get() : "");

            // private or group chat
            if (chat.isUserChat()){
                log.info(String.format(MESSAGE_PRIVATE_LOG, user, message.getText()));
            } else {
                chatTitle =  chat.getTitle();
                log.info(String.format(MESSAGE_GROUP_LOG, chatTitle, chat.getId(), user, message.getText()));
            }

            // only messages from last N minutes
            if( System.currentTimeMillis()/1000L - message.getDate() < delay){
                Optional<ChatEvent> event = Optional.of(ChatEventFactory.getEvent(this, message));
                event.get().processEvent();
            }
        }
    }

    public static void main(String[] args) {

        try {
        // we need to create it with an own log4j appender, because default is messing with all logs
        Logger logger = Logger.getRootLogger();
        Appender appender = logger.getAppender("telegrambots");

        BasicConfigurator.configure(appender);

        ApiContextInitializer.init();

        TelegramBotsApi botsApi = new TelegramBotsApi();

        TDGameBot tdGameBot = new TDGameBot();

        botsApi.registerBot(tdGameBot);

        } catch (TelegramApiException e) {
            log.error(String.format("Error registering telegram bot: %s", e.getMessage()));
        } catch (IOException e) {
            log.error(String.format("Error creating bot client: %s", e.getMessage()));
        }
    }

    public void onClosing() { }

}
