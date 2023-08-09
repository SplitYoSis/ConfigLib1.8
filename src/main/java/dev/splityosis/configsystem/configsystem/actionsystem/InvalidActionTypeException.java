package dev.splityosis.configsystem.configsystem.actionsystem;

public class InvalidActionTypeException extends RuntimeException{

    public InvalidActionTypeException(String type) {
        super("Invalid ActionType '"+type+"'");
    }
}
