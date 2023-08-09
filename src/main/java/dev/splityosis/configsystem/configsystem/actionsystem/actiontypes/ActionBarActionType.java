package dev.splityosis.configsystem.configsystem.actionsystem.actiontypes;

import dev.splityosis.configsystem.configsystem.actionsystem.ActionType;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class ActionBarActionType extends ActionType {

    public ActionBarActionType() {
        super("ACTIONBAR", "SEND_ACTIONBAR", "BAR_OF_ACTION");
    }

    @Override
    public void run(@Nullable Player player, @NotNull List<String> params, @NotNull Map<String, String> placeholders) {
        if (player == null) return;

        params = replace(params, placeholders);
        params = applyPlaceholderAPI(player, params);

        if (params.size() == 0) return;
        String msg = colorize(params.get(0));
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(msg));
    }
}
