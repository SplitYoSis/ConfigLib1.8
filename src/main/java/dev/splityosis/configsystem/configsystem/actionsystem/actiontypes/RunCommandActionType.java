package dev.splityosis.configsystem.configsystem.actionsystem.actiontypes;

import dev.splityosis.configsystem.configsystem.actionsystem.ActionType;

import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RunCommandActionType extends ActionType {

    public RunCommandActionType() {
        super("RUN_COMMANDS", "RUNCOMMANDS", "SUDO", "SUDO_COMMANDS");
    }

    @Override
    public void run(@Nullable Player player, @NotNull List<String> params, @NotNull Map<String, String> placeholders) {
        if (player == null) return;
        params = replace(params, placeholders);
        params = applyPlaceholderAPI(player, params);

        for (String param : params)
            Bukkit.dispatchCommand(player, param);
    }
}
