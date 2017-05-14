package com.tden.command;

import com.tden.utilities.ConfigurationHelper;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by Stanislav Myachenkov on 05.01.2017.
 *
 * Container with a lis of allowed users and chats
 * allow dynamic modification of approvedChats via whitelist and blacklist command
 */
public class CommandPrivacy {

    private HashSet<String> approvedChats;

    private HashSet<String> approvedUsers;

    public CommandPrivacy() {
        this.approvedChats = new HashSet<>(Arrays.asList(ConfigurationHelper.getProperty("approved_chats").split("\\s*,\\s*")));;
        this.approvedUsers = new HashSet<>(Arrays.asList(ConfigurationHelper.getProperty("approved_users").split("\\s*,\\s*")));;
    }

    public CommandPrivacy(HashSet approvedChats, HashSet approvedUsers) {
        this.approvedChats = approvedChats;
        this.approvedUsers = approvedUsers;
    }

    public void addChat(String chatId){
        approvedChats.add(chatId);
    }

    public void removeChat(String chatId){
        approvedChats.remove(chatId);
    }

    //  username in format @vasya, message.getfrom.getusername()
    public boolean isUserApproved(String username){
        return approvedUsers.contains('@' + username);
    }

    public boolean isChatApproved(String chatId){
        return approvedChats.contains(chatId);
    }
}
