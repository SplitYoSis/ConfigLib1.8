package dev.splityosis.configsystem.configsystem;


import com.google.common.base.Preconditions;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public abstract class AnnotatedConfig {

    private final File parentDirectory;
    private final String name;
    private File file;
    private FileConfiguration config;
    private Class<? extends AnnotatedConfig> clazz;

    public AnnotatedConfig(File parentDirectory, String name) {
        this.parentDirectory = parentDirectory;
        this.name = name;
    }

    public void initialize(){
        clazz = this.getClass();
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

    private void setFieldInConfig(Field field, ConfigField configField){
        try {
            if (field.getType().equals(ItemStack.class))
                setItemStack(config, configField.path(), (ItemStack) field.get(this));
            else if (field.getType().equals(ConfigSectionHandler.class) || ConfigSectionHandler.class.isAssignableFrom(field.getType()))
                setConfigSectionInConfig(configField.path(), (ConfigSectionHandler<?>) field.get(this));
            else config.set(configField.path(), field.get(this));
            if (!(configField.comment().length == 1 && configField.comment()[0].isEmpty()))
                config.setComments(configField.path(), Arrays.asList(configField.comment()));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateFieldFromConfig(Field field, ConfigField configField){
        try {
            if (field.getType().equals(ItemStack.class))
                field.set(this, getItemStack(config, configField.path()));
            else if (field.getType().equals(ConfigSectionHandler.class) || ConfigSectionHandler.class.isAssignableFrom(field.getType()))
                field.set(this, getConfigSectionFromConfig(configField.path(), (ConfigSectionHandler<?>) field.get(this)));
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
        configSection.reloadAll();
        return configSection;
    }

    public static ItemStack getItemStack(ConfigurationSection config, String path){
        String name = config.getString(path + ".name");
        List<String> lore = config.getStringList(path + ".lore");
        Material material = Material.getMaterial(Objects.requireNonNull(config.getString(path + ".material")));
        int amount = config.getInt(path + ".amount");
        return createItemStack(material, 0, fixColor(name), amount, fixColor(lore));
    }

    public static void setItemStack(ConfigurationSection config, String path, ItemStack item){
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            config.set(path + ".name", unfixColor(meta.getDisplayName()));
            config.set(path + ".lore", unfixColor(meta.getLore()));
        }
        config.set(path + ".material", item.getType().name());
        config.set(path + ".amount", item.getAmount());
    }

    private static ItemStack createItemStack(Material material, int data, String name, int amount, List<String> loreList) {
        ItemStack item = new ItemStack(material, amount, (short) data);
        ItemMeta meta = item.getItemMeta();
        if (name != null)
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        for (int i = 0; i < loreList.size(); i++) {
            String s = ChatColor.translateAlternateColorCodes('&', loreList.get(i));
            loreList.set(i, s);
        }
        meta.setLore(loreList);
        item.setItemMeta(meta);
        return item;
    }

    private static String fixColor(String s){
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    private static List<String> fixColor(List<String> lst){
        if (lst == null) return null;
        List<String> newLst = new ArrayList<>();
        for (String s : lst)
            newLst.add(fixColor(s));
        return newLst;
    }

    private static List<String> unfixColor(List<String> lst) {
        if (lst == null) return null;
        List<String> newLst = new ArrayList<>();
        for (String s : lst)
            newLst.add(unfixColor(s));
        return newLst;
    }

    private static String unfixColor(String textToTranslate) {
        if (textToTranslate == null) return null;

        char[] b = textToTranslate.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == '§' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx".indexOf(b[i + 1]) > -1) {
                b[i] = '&';
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return new String(b);
    }
}