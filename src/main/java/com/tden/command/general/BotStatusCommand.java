package com.tden.command.general;

import com.tden.TDGameBot;
import com.tden.command.BasicCommandImpl;
import com.tden.command.CommandModel;
import com.tden.command.CommandPrivacy;
import com.tden.encounterengine.EncounterSession;
import com.tden.utilities.Responses;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Stanislav Myachenkov on 15.12.2016.
 */

@Slf4j
public class BotStatusCommand extends BasicCommandImpl {

    public BotStatusCommand(Message message, TDGameBot bot,String argString) {
        super(bot, message, CommandModel.CommandType.GETBOTSTATUS, argString);
    }

    @Override
    public void processCommand() {
        EncounterSession s = bot.getEncounterSession();
        CommandPrivacy p = bot.getCommandPrivacy();

        SendMessage reply;

        try {
            if(p.isUserApproved(message.getFrom().getUserName())) {
                reply = new SendMessage();
                reply.setChatId(message.getChatId());
                reply.setReplyToMessageId(message.getMessageId());
                reply.enableMarkdown(true);

                //format unixtime to sdf
                Date date = new Date(s.getSessionInfo().getLastEventTime()*1000L);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm z");
                sdf.setTimeZone(TimeZone.getTimeZone("GMT+3")); // we are in moscow mostly
                String formattedDate = sdf.format(date);

                reply.setText(String.format(Responses.BOT_STATUS.toString(),
                        //CHAT
                        message.getChatId(),
                        //SESSION
                        s.getSessionInfo().getActivityStatus(),
                        formattedDate,
                        s.getSessionInfo().getPLAY_URL(),
                        //LEVEL
                        s.getLevelInfo().getCurrentLevelId(),
                        s.getLevelInfo().getCurrentLevelNumber(),
                        s.getLevelInfo().getCurrentLevelName()
                ));

                bot.sendMessage(reply);
            }

        }
        catch(TelegramApiException e){
            log.error(String.format("Error processing command [ %s ] in message [ %s ] from user [ %s ]", getCommandName(), message, message.getFrom()));
        }

    }
}
