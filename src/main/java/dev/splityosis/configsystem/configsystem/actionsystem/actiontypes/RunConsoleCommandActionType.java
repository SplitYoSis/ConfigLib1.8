package dev.splityosis.configsystem.configsystem.actionsystem.actiontypes;

import dev.splityosis.configsystem.configsystem.actionsystem.ActionType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class RunConsoleCommandActionType extends ActionType {

    public RunConsoleCommandActionType() {
        super("RUN_CONSOLE_COMMANDS", "CONSOLE_COMMAND", "RUN");
    }

    @Override
    public void run(@Nullable Player player, @NotNull List<String> params, @NotNull Map<String, String> placeholders) {
        params = replace(params, placeholders);
        params = applyPlaceholderAPI(player, params);

        for (String param : params)
            Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), param);
    }
}