package dev.splityosis.configsystem.configsystem;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ConfigTypeLogic<T> {

    protected static Map<Class<?>, Map<String, ConfigTypeLogic<?>>> configTypeLogicMap = new HashMap<>();

    public abstract T getFromConfig(ConfigurationSection config, String path);

    public abstract void setInConfig(T instance, ConfigurationSection config, String path);
    public void register(String formatName){
        Class<?> type = getGenericClass();
        Map<String, ConfigTypeLogic<?>> map = configTypeLogicMap.get(type);
        if (map == null)
            map = new HashMap<>();
        map.put(formatName.toLowerCase(), this);
        configTypeLogicMap.put(type, map);
    }

    public void register(){
        register("");
    }

    @SuppressWarnings("unchecked")
    public Class<?> getGenericClass(){
        ParameterizedType superClass = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class<T>) superClass.getActualTypeArguments()[0];
    }

    public static ConfigTypeLogic<?> getConfigTypeLogic(Class<?> type, String formatName){
        Map<String, ConfigTypeLogic<?>> map = configTypeLogicMap.get(type);
        if (map == null) return null;
        return map.get(formatName.toLowerCase());
    }

    private final Pattern HEX_PATTERN = Pattern.compile("&(#\\w{6})");
    public String colorize(String str) {
        if (str == null) return null;
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public List<String> colorize(List<String> lst){
        if (lst == null) return null;
        List<String> newList = new ArrayList<>();
        lst.forEach(s -> {
            newList.add(colorize(s));
        });
        return newList;
    }

    public List<String> reverseColorize(List<String> lst) {
        if (lst == null) return null;
        List<String> newLst = new ArrayList<>();
        for (String s : lst)
            newLst.add(reverseColorize(s));
        return newLst;
    }

    private final Pattern patternAll = Pattern.compile("§x(§[0-9a-fA-F]){6}");
    public String reverseColorize(String input) {
        Matcher matcher = patternAll.matcher(input);

        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String colorCode = matcher.group().replaceAll("§", "");
            matcher.appendReplacement(sb, "&#" + colorCode.substring(1));
        }
        matcher.appendTail(sb);

        return sb.toString().replaceAll("§([0-9a-fklmnorx])", "&$1");
    }
}
