package com.tden.command.encounter;

import com.tden.TDGameBot;
import com.tden.command.BasicCommandImpl;
import com.tden.command.CommandModel;
import com.tden.encounterengine.ActivityStatus;
import com.tden.encounterengine.EncounterSession;
import com.tden.utilities.Responses;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/**
 * Created by Stanislav Myachenkov on 12/7/16.
 *
 * logaut
 * kill cookies
 * kill session and all data
 */
@Slf4j
public class StopGameCommand extends BasicCommandImpl {

    public StopGameCommand(Message message, TDGameBot bot, String argString) {
        super(bot, message, CommandModel.CommandType.STOPGAME, argString);
    }

    @Override
    public void processCommand() {

        EncounterSession s = bot.getEncounterSession();

        if (s.getSessionInfo().getActivityStatus() != ActivityStatus.NOTACTIVE) {

            try {
                s.stopGame();

                SendMessage reply;

                reply = new SendMessage();
                reply.setChatId(message.getChatId());
                reply.setReplyToMessageId(message.getMessageId());
                reply.setText(Responses.GAME_STOPPED.toString());
                reply.enableMarkdown(true);
                bot.sendMessage(reply);

            } catch (TelegramApiException e) {
                log.error(String.format("Error processing command [ %s ] in message [ %s ] from user [ %s ]", getCommandName(), message, message.getFrom()));
            }
        }
    }
}
