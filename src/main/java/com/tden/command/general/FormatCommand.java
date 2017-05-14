package com.tden.command.general;

import com.tden.TDGameBot;
import com.tden.command.BasicCommandImpl;
import com.tden.command.CommandModel;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/**
 * Created by Stanislav Myachenkov on 12/7/16.
 */
@Slf4j
public class FormatCommand extends BasicCommandImpl {

    public FormatCommand(Message message, TDGameBot bot, String argString) {
        super(bot, message, CommandModel.CommandType.FORMAT, argString);
    }

    @Override
    public void processCommand() {
        try {
            SendMessage reply = new SendMessage();

            reply.setChatId(message.getChatId());
            reply.setReplyToMessageId(message.getMessageId());
            reply.setText(getArguments());
            reply.enableMarkdown(true);
            reply.disableWebPagePreview();

            this.bot.sendMessage(reply);

        } catch (TelegramApiException e){
            log.error(String.format("Error processing command [ %s ] in message [ %s ] from user [ %s ]", getCommandName(), message, message.getFrom()));
        }
    }
}
