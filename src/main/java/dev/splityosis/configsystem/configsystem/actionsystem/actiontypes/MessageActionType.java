package dev.splityosis.configsystem.configsystem.actionsystem.actiontypes;

import dev.splityosis.configsystem.configsystem.actionsystem.ActionType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class MessageActionType extends ActionType {


    public MessageActionType() {
        super("MESSAGE", "MSG", "SEND_MESSAGE");
    }

    @Override
    public void run(@Nullable Player player, @NotNull List<String> params, @NotNull Map<String, String> placeholders) {
        if (player == null) return;

        params = replace(params, placeholders);
        params = applyPlaceholderAPI(player, params);

        for (String s : colorize(params)) {
            player.sendMessage(s);
        }
    }
}
