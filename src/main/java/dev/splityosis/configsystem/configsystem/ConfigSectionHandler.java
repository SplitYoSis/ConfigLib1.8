package dev.splityosis.configsystem.configsystem;

import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public abstract class ConfigSectionHandler<T> {

    protected ConfigurationSection generalSection;
    private final Map<String, T> instances = new HashMap<>();

    public abstract void setDefaults(ConfigurationSection generalSection);
    public abstract T getInstanceFromConfig(ConfigurationSection instanceSection);
    public abstract void saveInstanceToConfig(ConfigurationSection instanceSection, T instance);

    public void saveAllToConfig(){
        for (String key : generalSection.getKeys(false))
            generalSection.set(key, null);

        for (String key : instances.keySet()){
            ConfigurationSection instanceSection = generalSection.createSection(key);
            saveInstanceToConfig(instanceSection, instances.get(key));
        }
    }

    public void reloadAll(){
        instances.clear();
        for (String key : generalSection.getKeys(false))
            instances.put(key, getInstanceFromConfig(generalSection.getConfigurationSection(key)));
    }

    public Map<String, T> getInstances() {
        return new HashMap<>(instances);
    }

    public void addInstance(String key, T instance){
        instances.put(key, instance);
    }

    public ConfigurationSection getGeneralSection() {
        return generalSection;
    }
}