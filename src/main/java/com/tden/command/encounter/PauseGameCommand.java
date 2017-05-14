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
 * Created by Stanislav Myachenkov on 10.12.2016.
 *
 * change bot status to PAUSED
 * (do not enter codes from EnCodeEvent)
 */

@Slf4j
public class PauseGameCommand extends BasicCommandImpl {

    public PauseGameCommand(Message message,
                            TDGameBot bot,
                            String argString) {
        super(bot,
                message,
                CommandModel.CommandType.PAUSEGAME,
                argString);
    }

    @Override
    public void processCommand() {

        EncounterSession s = bot.getEncounterSession();

        if (s.getSessionInfo().getActivityStatus() == ActivityStatus.ACTIVE) {

            try {
                s.getSessionInfo().setActivityStatus(ActivityStatus.PAUSED);

                SendMessage reply;

                reply = new SendMessage();
                reply.setChatId(message.getChatId());
                reply.setReplyToMessageId(message.getMessageId());
                reply.setText(Responses.GAME_PAUSED.toString());
                reply.enableMarkdown(true);
                bot.sendMessage(reply);

            } catch (TelegramApiException e) {
                log.error(String.format("Error processing command [ %s ] in message [ %s ] from user [ %s ]", getCommandName(), message, message.getFrom()));
            }
        }
    }
}
