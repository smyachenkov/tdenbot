package com.tden.command;

/**
 * Created by Stanislav Myachenkov on 09.12.2016.
 */
public interface BasicCommand {

    void processCommand();

    CommandModel.SecurityLevel getSecurityLevel();

    default boolean isDoReply(){ return true; }
}
