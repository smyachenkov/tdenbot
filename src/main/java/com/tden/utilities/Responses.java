package com.tden.utilities;

/**
 * Created by Stanislav Myachenkov on 14.12.2016.
 */
public enum Responses {

    LOGIN_SUCCESS("`Logged in`"),

    LOGIN_FAIL("`Login failed!`"),

    LOGIN_ALREADY("`Already logged in!`"),

    CODE_CORRECT(Emoji.WHITE_HEAVY_CHECK_MARK + " Принят _%s_"),

    CODE_INCORRECT(Emoji.CROSS_MARK + " Неверный _%s_"),

    CODE_FAIL("`failed to enter code`"),

    GAME_NOT_ACTIVE("Game is not active"),

    NEXT_LEVEL(Emoji.NORTH_EAST_ARROW + " Переход на новый уровень " + Emoji.NORTH_EAST_ARROW),

    BOT_STATUS("*Session stats*\n" +
            "\tBot status: %s\n" +
            "\tLast event time: %s\n" +
            "\tGame url: %s\n" +
            "\n" +
            "*Level Info*\n" +
            "\tid: %s\n" +
            "\tnumber: %s\n" +
            "\tname: %s"),

    LEVEL_STATUS("*Level Info*\n" +
                " %s\n\n" +
                "%s"),

    CHAT_APPROVED("`Chat is approved`"),

    CHAT_DISAPPROVED("`Chat is removed`"),

    GAME_STOPPED("`Game is stopped`"),

    GAME_PAUSED("`Game is paused`"),

    GAME_RESUMED("`Game is resumed`"),

    COMMAND_NOT_FOUND("`Command is not found`");

    String value;

    Responses(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
