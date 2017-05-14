package com.tden.command;

import com.tden.TDGameBot;
import com.tden.command.general.UndefinedCommand;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.api.objects.Message;

import java.lang.reflect.Constructor;
import java.util.Map;

/**
 * Created by Stanislav Myachenkov on 09.12.2016.
 */

@Slf4j
public class CommandFactory {

    public static BasicCommand getCommand(TDGameBot bot, Message message){

        BasicCommand command = null;

        String messageText = message.getText();

        String commandName;
        String argString = "";

        try {
            messageText = messageText.replaceAll("@" + bot.getBotUsername(), "");

            //remove init slash
            if (messageText.contains(" ")) {
                commandName = messageText.substring(1, messageText.indexOf(" "));
                argString = messageText.substring(messageText.indexOf(" ") + 1, messageText.length());
            } else {
                commandName = messageText.substring(1, messageText.length());
                argString = "";
            }

            // retrieve command class by its name from model and create new instance
            // command are not cached and every command has an instance
            Map commandMap = CommandModel.getCommandMap();

            if (commandMap.containsKey(commandName)) {

                Class<BasicCommand> commandClass = (Class<BasicCommand>) commandMap.get(commandName);

                // find constructor with: message, bot, argstring
                Constructor<BasicCommand> constructor =
                        commandClass.getConstructor(Message.class,
                                TDGameBot.class,
                                String.class);

                log.info("Command " + commandName + " " + commandClass.getSimpleName() + " has been called");

                command = constructor.newInstance( message,bot,argString);

                CommandPrivacy p = bot.getCommandPrivacy();
                // check security levels and process command
               // command.processCommand();

                CommandModel.SecurityLevel secLvl = command.getSecurityLevel();

                switch (secLvl){

                    case EVERYONE:
                        command.processCommand();
                        break;

                    case APPROVED_CHATS:
                        if(p.isChatApproved(message.getChatId().toString()))
                            command.processCommand();
                        break;

                    case APPROVED_USERS:
                        if(p.isUserApproved(message.getFrom().getUserName()))
                            command.processCommand();
                        break;
                }

            } else {
                log.info("Undefined command " + commandName + " has been called");
                command = new UndefinedCommand(bot, message, argString);
                command.processCommand();
            }

        } catch (NoSuchMethodException nsme){
            log.error("No such constructor for command % ", messageText);
            command = new UndefinedCommand(bot, message, argString);
        } catch (Exception e){
            log.error(String.format("Error processing command in message [ %s ] from user [ %s ]", message, message.getFrom()));
            command = new UndefinedCommand(bot, message, argString);
        }
        return command;
    }
}
