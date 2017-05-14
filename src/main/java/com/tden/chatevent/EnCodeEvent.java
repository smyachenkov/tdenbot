package com.tden.chatevent;

import com.tden.TDGameBot;
import com.tden.command.encounter.EnterCodeCommand;
import com.tden.encounterengine.ActivityStatus;
import com.tden.encounterengine.EncounterSession;
import com.tden.utilities.Responses;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/**
 * Created by Stanislav Myachenkov on 12/7/16.
 */

@Slf4j
public class EnCodeEvent extends ChatEvent {

    public EnCodeEvent(TDGameBot bot, Message message) {
        super(bot, ChatEventType.ENCODE, message,true );
    }

    @Override
    public int processEvent() {
        try {
            EncounterSession s = bot.getEncounterSession();
            if (s.getSessionInfo().getActivityStatus() == ActivityStatus.ACTIVE) {

                String argString = new String();       // we need only code from arg list

                if (message.getText().startsWith(".")) {

                    String foo = message.getText()
                            .substring(1, message.getText().length())
                            .trim();
                    argString = foo.split("\\s+")[0];


                } else if (message.getText().startsWith("!")) {
                    argString = message.getText().substring(1, message.getText().length());
                }

                EnterCodeCommand comm = new EnterCodeCommand(message, bot, argString);
                comm.processCommand();

            } else {
                SendMessage reply;
                reply = new SendMessage();
                reply.setChatId(message.getChatId());
                reply.setReplyToMessageId(message.getMessageId());
                reply.enableMarkdown(true);
                reply.setText(Responses.GAME_NOT_ACTIVE.toString());
                bot.sendMessage(reply);
            }
        } catch (TelegramApiException e){
            log.error(String.format("Error processing message [%s] from user [%s]", message, message.getFrom()));
            return 1;
        }

        return 0;
    }
}
