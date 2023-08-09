package dev.splityosis.configsystem.configsystem.actionsystem.actiontypes;

import dev.splityosis.configsystem.configsystem.actionsystem.ActionType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class MessageAllActionType extends ActionType {

    public MessageAllActionType() {
        super("MESSAGE_ALL", "MESSAGEALL", "SEND_MESSAGE_ALL", "MSG_ALL", "MSGALL", "BROADCAST");
    }

    @Override
    public void run(@Nullable Player player, @NotNull List<String> params, @NotNull Map<String, String> placeholders) {
        params = replace(params, placeholders);
        if (player != null) {
            params = applyPlaceholderAPI(player, params);
        }

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            for (String s : colorize(params)) {
                onlinePlayer.sendMessage(s);
            }
        }
    }
}
