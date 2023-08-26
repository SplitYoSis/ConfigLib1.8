package dev.splityosis.configsystem.configsystem.actionsystem;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ActionType {

    private static Map<String, ActionType> actionTypeMap = new HashMap<>();
    private Set<ActionType> actionTypes = new HashSet<>();

    private String[] names;

    public ActionType(String... names) {
        this.names = names;
    }

    public abstract void run(@Nullable Player player, @NotNull List<String> params, @NotNull Map<String, String> placeholders);

    public void register(){
        actionTypes.add(this);
        for (String name : names) {
            actionTypeMap.put(name.toUpperCase(), this);
        }
    }

    /**
     * Applies placeholderAPI if it exists on the server, else returns the original string.
     * @param player The player that the placeholders will be set on
     * @param str
     * @return
     */
    public String applyPlaceholderAPI(Player player, String str){
        try{
            return PlaceholderAPI.setPlaceholders(player, str);
        }catch (Exception e){
            return str;
        }
    }

    /**
     * Applies placeholderAPI if it exists on the server, else returns the original string.
     * @param player The player that the placeholders will be set on
     * @param lst
     * @return
     */
    public List<String> applyPlaceholderAPI(Player player, List<String> lst){
        try{
            return PlaceholderAPI.setPlaceholders(player, lst);
        }catch (Exception e){
            return lst;
        }
    }

    private final Pattern HEX_PATTERN = Pattern.compile("&(#\\w{6})");

    /**
     * Returns the same string but color translated. This supports hex colors
     */
    public String colorize(String str) {
        if (str == null) return null;
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    /**
     * Returns the same List of String but color translated. This supports hex colors
     */
    public List<String> colorize(List<String> lst){
        if (lst == null) return null;
        List<String> newList = new ArrayList<>();
        lst.forEach(s -> {
            newList.add(colorize(s));
        });
        return newList;
    }

    /**
     * Inverse function of {@link #colorize(List)}
     * @param lst
     * @return The list un-colorized
     */
    public List<String> reverseColorize(List<String> lst) {
        if (lst == null) return null;
        List<String> newLst = new ArrayList<>();
        for (String s : lst)
            newLst.add(reverseColorize(s));
        return newLst;
    }

    private final Pattern patternAll = Pattern.compile("§x(§[0-9a-fA-F]){6}");

    /**
     * Inverse function of {@link #colorize(String)}
     * @param input
     * @return The string un-colorized
     */
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

    /**
     * Does the same as String.replace(from, to)
     * @return The new replaced String
     */
    public String replace(String string, String from, String to){
        if (string == null) return null;
        return string.replace(from, to);
    }

    /**
     * Runs {@link #replace(String, String, String)} on every line of given list
     * @param lst The list to replace
     * @param from from
     * @param to to
     * @return A copy of the list.
     */
    public List<String> replace(List<String> lst, String from, String to){
        if (lst == null) return null;
        List<String> newLst = new ArrayList<>();
        for (String s : lst)
            newLst.add(replace(s, from, to));
        return newLst;
    }

    /**
     * Calls {@link #replace(String, String, String)} for every entry of the map.
     * @param string The string that will be replaced.
     * @param replacements Map of <From, To> replacements.
     * @return A copy of the string.
     */
    public String replace(String string, Map<String, String> replacements){
        if (replacements == null)
            return string;
        String newStr = string;
        for (Map.Entry<String, String> entry : replacements.entrySet())
            newStr = replace(newStr, entry.getKey(), entry.getValue());
        return newStr;
    }

    /**
     * Calls {@link #replace(String, Map)} on every line of the List.
     * @param lst List of string to be replaced
     * @param replacements Map of <From, To> replacements.
     * @return A Copy of the list replaced.
     */
    public List<String> replace(List<String> lst, Map<String, String> replacements){
        List<String> newLst = new ArrayList<>();
        for (String s : lst)
            newLst.add(replace(s, replacements));
        return newLst;
    }

    public static ActionType getActionType(String identifier){
        return actionTypeMap.get(identifier.toUpperCase());
    }

    public String[] getNames() {
        return names;
    }

    public static List<ActionType> getAllActionTypes(){
        return new ArrayList<>(actionTypeMap.values());
    }
}
