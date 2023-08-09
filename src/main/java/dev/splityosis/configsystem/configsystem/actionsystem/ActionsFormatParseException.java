package dev.splityosis.configsystem.configsystem.actionsystem;

public class ActionsFormatParseException extends RuntimeException{

    public ActionsFormatParseException(String string) {
        super("Something went wrong with parsing the action string '"+string+"'");
    }
}
