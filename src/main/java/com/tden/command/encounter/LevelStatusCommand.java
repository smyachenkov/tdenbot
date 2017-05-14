package com.tden.command.encounter;

import com.tden.TDGameBot;
import com.tden.command.BasicCommandImpl;
import com.tden.command.CommandModel;
import com.tden.encounterengine.EncounterSession;
import com.tden.encounterengine.LevelSector;
import com.tden.utilities.Responses;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/**
 * Created by Stanislav Myachenkov on 14.05.2017.
 */
@Slf4j
public class LevelStatusCommand extends BasicCommandImpl {

    public LevelStatusCommand(Message message,
                            TDGameBot bot,
                            String argString) {
        super(bot, message, CommandModel.CommandType.LEVELSTATUS, argString);
    }


    @Override
    public void processCommand() {
        EncounterSession s = bot.getEncounterSession();

        SendMessage reply;

        try {
            reply = new SendMessage();
            reply.setChatId(message.getChatId());
            reply.setReplyToMessageId(message.getMessageId());
            reply.enableMarkdown(true);

            StringBuilder sb = new StringBuilder();
            for(LevelSector ls : s.getLevelInfo().getSectors()){
                sb.append(ls.getNumber())
                    .append(" ")
                    .append(ls.getName());

                if(ls.getAnswer() != null && !ls.getAnswer().isEmpty()){
                    sb.append("_").append(ls.getAnswer()).append("_");
                } else {
                    sb.append(" — ");
                }

                sb.append("\n");

            }

            reply.setText(String.format(Responses.LEVEL_STATUS.toString(),
                    s.getLevelInfo().getCurrentLevelName(),
                    sb.toString()
            ));

            bot.sendMessage(reply);
        }
        catch(TelegramApiException e){
            log.error(String.format("Error processing command [ %s ] in message [ %s ] from user [ %s ]", getCommandName(), message, message.getFrom()));
        }
    }

}
