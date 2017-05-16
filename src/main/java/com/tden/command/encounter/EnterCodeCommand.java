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
 * Created by Stanislav Myachenkov on 14.12.2016.
 */

@Slf4j
public class EnterCodeCommand extends BasicCommandImpl {

    public EnterCodeCommand(Message message, TDGameBot bot, String argString) {
        super(bot, message, CommandModel.CommandType.ENTERCODE, argString);
    }

    public EnterCodeCommand(Message message, TDGameBot bot, String argString, boolean doReply) {
        super(bot, message, CommandModel.CommandType.ENTERCODE, argString, doReply);
    }

    @Override
    public void processCommand() {

        EncounterSession s = bot.getEncounterSession();

        try {

            if (s.getSessionInfo().getActivityStatus() == ActivityStatus.ACTIVE) {

               int levelNumberBefore = s.getLevelInfo().getCurrentLevelNumber();

               int isCorrect = s.sendCode(argString);
               String respText;

               if (isCorrect == 1){
                   respText = String.format(Responses.CODE_CORRECT.toString(), argString );
               } else if(isCorrect == 0){
                   respText = String.format(Responses.CODE_INCORRECT.toString(), argString );
               } else {
                   respText = "Internal error!";
               }

                // message with approve or decline
                SendMessage reply;

                reply = new SendMessage();
                reply.setChatId(message.getChatId());
                if(isDoReply())
                    reply.setReplyToMessageId(message.getMessageId());
                reply.enableMarkdown(true);
                reply.setText(respText);
                bot.sendMessage(reply);

                // if it was last code - level up message
                if(levelNumberBefore < s.getLevelInfo().getCurrentLevelNumber()){

                    // level up
                    SendMessage upMessage = new SendMessage();
                    upMessage.setChatId(message.getChatId());
                    upMessage.enableMarkdown(true);
                    upMessage.setText(Responses.NEXT_LEVEL.toString());
                    bot.sendMessage(upMessage);

                    // new level name
                    SendMessage newLevelNameMessage = new SendMessage();
                    upMessage.setChatId(message.getChatId());
                    upMessage.enableMarkdown(true);
                    upMessage.setText("*" + s.getLevelInfo().getCurrentLevelName() + "*");
                    bot.sendMessage(upMessage);
                }
            }
        }
        catch(TelegramApiException e){
            log.error(String.format("Error processing command [ %s ] in message [ %s ] from user [ %s ]", getCommandName(), message, message.getFrom()));
        }
    }
}
