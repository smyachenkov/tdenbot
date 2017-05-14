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
 * Login
 * if game is not active - init all data
 * if active - already loggedin message
 * if paused - already logged in but paused
 *
 */
@Slf4j
public class StartGameCommand extends BasicCommandImpl{

    public StartGameCommand(Message message, TDGameBot bot, String argString) {
        super(bot, message, CommandModel.CommandType.STARTGAME,  argString);
    }

    @Override
    public void processCommand() {

        EncounterSession s = bot.getEncounterSession();

        String gameUrl = argString.trim();
        SendMessage reply;

        try {

            reply = new SendMessage();
            reply.setChatId(message.getChatId());
            reply.setReplyToMessageId(message.getMessageId());
            reply.enableMarkdown(true);

            if (s.getSessionInfo().getActivityStatus() == ActivityStatus.NOTACTIVE) {

                int loginStatus = s.loginToEnCx(gameUrl);

                if (loginStatus == 0) {
                    reply.setText(Responses.LOGIN_SUCCESS.toString());

                } else {
                    reply.setText(Responses.LOGIN_FAIL.toString());
                }

            } else {
                reply.setText(Responses.LOGIN_ALREADY.toString());
                bot.sendMessage(reply);
            }

            bot.sendMessage(reply);

        }
        catch(TelegramApiException e){
            log.error(String.format("Error processing command [ %s ] in message [ %s ] from user [ %s ]", getCommandName(), message, message.getFrom()));
        }

    }
}
