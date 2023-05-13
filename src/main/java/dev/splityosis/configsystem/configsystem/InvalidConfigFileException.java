package dev.splityosis.configsystem.configsystem;

import java.io.File;

public class InvalidConfigFileException extends RuntimeException{

    public InvalidConfigFileException(File file) {
        super("Invalid config file '"+file.getName()+"'. File name must end with .yml");
    }
}
