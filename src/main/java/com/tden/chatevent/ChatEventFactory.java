package com.tden.chatevent;

import com.tden.TDGameBot;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.api.objects.Message;

import java.util.regex.Pattern;

/**
 * Created by Stanislav Myachenkov on 12/7/16.
 *
 * Factory with some detection logic
 *
 */

@Slf4j
public class ChatEventFactory {

    private static final Pattern locRegexp = Pattern.compile("^(-?\\d{1,2}\\.\\d{2,8})[,\\s+][-\\s]*(-?\\d{1,2}\\.\\d{2,8})$");

    public static ChatEvent getEvent(TDGameBot bot, Message message){

        ChatEvent chatEvent = null;


        if(message.hasLocation()){
            chatEvent = new LocationEvent(bot, message);

        } else if(message.hasText()){
            String t = message.getText();

            if(isCommand(t, bot.getBotUsername())) {
                chatEvent = new CommandEvent(bot,message);
            }else if(isLocation(t)) {
                chatEvent = new CoordinatesEvent(bot,message);
            }else if(isCode(t)) {
                chatEvent = new EnCodeEvent(bot, message);
            }
        }

        if(chatEvent == null)
            chatEvent = new UndefinedChatEvent(bot, message);

        return chatEvent;

    }

    private static boolean isCommand(String t, String botName){

        String fullCommand = t.contains(" ") ?  t.substring(0, t.indexOf(" ")) : t;

        return ( fullCommand.startsWith("/")
                && ( fullCommand.endsWith("@"+botName) || !fullCommand.contains("@")));
    }

    private static boolean isLocation(String t){
        return locRegexp.matcher(t).matches();
    }

    private static boolean isCode(String t){
        return (t.startsWith(".") || t.startsWith("!"));
    }
}
