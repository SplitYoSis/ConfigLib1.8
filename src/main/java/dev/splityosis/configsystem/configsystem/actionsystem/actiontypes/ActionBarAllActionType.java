package dev.splityosis.configsystem.configsystem.actionsystem.actiontypes;

import dev.splityosis.configsystem.configsystem.actionsystem.ActionType;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class ActionBarAllActionType extends ActionType {

    public ActionBarAllActionType() {
        super("ACTIONBAR_ALL", "SEND_ACTIONBAR_ALL");
    }

    @Override
    public void run(@Nullable Player player, @NotNull List<String> params, @NotNull Map<String, String> placeholders) {
        params = replace(params, placeholders);
        params = applyPlaceholderAPI(player, params);

        if (params.size() == 0) return;
        String msg = colorize(params.get(0));
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(msg));
        }
    }
}
