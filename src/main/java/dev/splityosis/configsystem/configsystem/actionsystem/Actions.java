package dev.splityosis.configsystem.configsystem.actionsystem;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Actions {

    private List<ActionData> actionDataList;

    public Actions(List<ActionData> actionDataList) {
        this.actionDataList = actionDataList;
        if (actionDataList == null)
            actionDataList = new ArrayList<>();
    }

    /**
     * Runs all the actions where the target is the given player and sets PlaceholderAPI (if exists) on given player.
     * @param player Target
     * @param placeholders Placeholders that will be set, Map in the form of <From, To>
     */
    public void perform(@Nullable Player player, @Nullable Map<String, String> placeholders){
        if (placeholders == null)
            placeholders = new HashMap<>();
        for (ActionData actionData : actionDataList) {
            ActionType actionType = ActionType.getActionType(actionData.getActionKey());
            if (actionType == null)
                new InvalidActionTypeException(actionData.getActionKey()).printStackTrace();
            else
                actionType.run(player, actionData.getParameters(), placeholders);
        }
    }


    /**
     * Runs the same logic as {@link #perform(Player)} on all the players online.
     */
    public void performOnAll(@Nullable Map<String, String> placeholders){
        if (placeholders == null)
            placeholders = new HashMap<>();
        for (ActionData actionData : actionDataList) {
            ActionType actionType = ActionType.getActionType(actionData.getActionKey());
            if (actionType == null)
                new InvalidActionTypeException(actionData.getActionKey()).printStackTrace();
            else
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    actionType.run(onlinePlayer, actionData.getParameters(), placeholders);
                }
        }
    }

    public List<ActionData> getActionDataList() {
        return actionDataList;
    }
}
