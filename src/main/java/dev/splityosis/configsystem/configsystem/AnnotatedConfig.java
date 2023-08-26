package dev.splityosis.configsystem.configsystem;

import dev.splityosis.configsystem.configsystem.actionsystem.actiontypes.*;
import dev.splityosis.configsystem.configsystem.logics.ActionsConfigLogic;
import dev.splityosis.configsystem.configsystem.logics.ItemStackConfigLogic;
import dev.splityosis.configsystem.configsystem.logics.LocationConfigLogic;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;

public abstract class AnnotatedConfig {

    private final File parentDirectory;
    private String name;
    private File file;
    private FileConfiguration config;
    private Class<? extends AnnotatedConfig> clazz;

    public AnnotatedConfig(File parentDirectory, String name) {
        this.parentDirectory = parentDirectory;
        this.name = name;
    }

    public AnnotatedConfig(File file) throws InvalidConfigFileException {
        this.parentDirectory = file.getParentFile();
        this.file = file;
        this.name = file.getName();
        if (!this.name.endsWith(".yml")) {
            throw new InvalidConfigFileException(file);
        }
        this.name = this.name.substring(0, this.name.length()-4);
    }

    private static boolean isSetup = false;

    public void initialize(){
        if (!isSetup){
            // On first time initialize

            // Logics
            new ItemStackConfigLogic().register();
            new LocationConfigLogic().register();
            new ActionsConfigLogic().register();

            // ActionTypes
            new MessageActionType().register();
            new MessageAllActionType().register();

            new RunCommandActionType().register();
            new RunConsoleCommandActionType().register();

            new SendTitleAllActionType().register();
            new SendTitleActionType().register();

            new TeleportActionType().register();
            new TeleportAllActionType().register();

            new PlaySoundActionType().register();
            new PlaySoundAllActionType().register();

            new ActionBarActionType().register();
            new ActionBarAllActionType().register();

            isSetup = true;
        }

        clazz = this.getClass();
        if (!parentDirectory.exists())
            parentDirectory.mkdirs();
        file = new File(parentDirectory, name+".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
                config = YamlConfiguration.loadConfiguration(file);
                saveToFile();
                return;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
        writeMissingFields();
        updateFields();
        saveToFile();
    }

    public void saveToFile(){
        if (clazz.isAnnotationPresent(ConfigHeader.class)){
            ConfigHeader configHeader = clazz.getAnnotation(ConfigHeader.class);
            config.options().setHeader(Arrays.asList(configHeader.header()));
        }

        for (Field field : clazz.getDeclaredFields()){
            if (!field.isAnnotationPresent(ConfigField.class)) continue;
            ConfigField configField = field.getAnnotation(ConfigField.class);
            setFieldInConfig(field, configField);
        }
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeMissingFields(){
        for (Field field : clazz.getDeclaredFields()) {
            if (!field.isAnnotationPresent(ConfigField.class)) continue;
            ConfigField configField = field.getAnnotation(ConfigField.class);
            if (config.isSet(configField.path())) continue;
            setFieldInConfig(field, configField);
        }
    }

    public void updateFields(){
        for (Field field : clazz.getDeclaredFields()){
            if (!field.isAnnotationPresent(ConfigField.class)) continue;
            ConfigField configField = field.getAnnotation(ConfigField.class);
            updateFieldFromConfig(field, configField);
        }
    }

    public void reload(){
        config = YamlConfiguration.loadConfiguration(file);
        updateFields();
    }

    @SuppressWarnings("all")
    private void setFieldInConfig(Field field, ConfigField configField){
        try {
            ConfigTypeLogic configTypeLogic = ConfigTypeLogic.getConfigTypeLogic(field.getType(), configField.formatName());
            if (configTypeLogic != null) {
                configTypeLogic.setInConfig(field.get(this), config, configField.path());
            }
            else if(field.getType().equals(ConfigSectionHandler.class)) {
                setConfigSectionInConfig(configField.path(), (ConfigSectionHandler<?>) field.get(this));
            }
            else config.set(configField.path(), field.get(this));
//            if (!(configField.comment().length == 1 && configField.comment()[0].isEmpty()))
//                config.setComments(configField.path(), Arrays.asList(configField.comment()));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateFieldFromConfig(Field field, ConfigField configField){
        try {
            ConfigTypeLogic<?> configTypeLogic = ConfigTypeLogic.getConfigTypeLogic(field.getType(), configField.formatName());
            if (configTypeLogic != null)
                field.set(this, configTypeLogic.getFromConfig(config, configField.path()));

            else if(field.getType().equals(ConfigSectionHandler.class)) {
                field.set(this, getConfigSectionFromConfig(configField.path(), (ConfigSectionHandler<?>) field.get(this)));
            }

            else field.set(this, config.get(configField.path()));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void setConfigSectionInConfig(String path, ConfigSectionHandler<?> configSection){
        ConfigurationSection current = config.getConfigurationSection(path);
        if (current == null) {
            configSection.generalSection = config.createSection(path);
            configSection.setDefaults(configSection.generalSection);
            configSection.reloadAll();
            return;
        }
        configSection.generalSection = config.getConfigurationSection(path);
        configSection.saveAllToConfig();
    }

    private ConfigSectionHandler<?> getConfigSectionFromConfig(String path, ConfigSectionHandler<?> configSection){
        configSection.generalSection = config.getConfigurationSection(path);
        if(configSection.generalSection == null) return configSection;
        configSection.reloadAll();
        return configSection;
    }
}