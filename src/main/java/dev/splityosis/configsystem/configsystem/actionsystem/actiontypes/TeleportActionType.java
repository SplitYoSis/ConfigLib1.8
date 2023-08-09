package dev.splityosis.configsystem.configsystem.actionsystem.actiontypes;

import dev.splityosis.configsystem.configsystem.actionsystem.ActionType;
import dev.splityosis.configsystem.configsystem.actionsystem.InvalidActionParameterException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class TeleportActionType extends ActionType {

    public TeleportActionType() {
        super("TELEPORT", "TP");
    }

    @Override
    public void run(@Nullable Player player, @NotNull List<String> params, @NotNull Map<String, String> placeholders) {
        if (player == null) return;

        params = replace(params, placeholders);
        params = applyPlaceholderAPI(player, params);

        if (params.size() < 3) return;

        try {
            double x = Double.parseDouble(params.get(0));
            double y = Double.parseDouble(params.get(1));
            double z = Double.parseDouble(params.get(2));

            Location location;

            if (params.size() >= 4) {
                String worldName = params.get(3);
                location = new Location(Bukkit.getWorld(worldName), x, y, z);
            } else {
                location = new Location(player.getWorld(), x, y, z);
            }

            player.teleport(location);

        } catch (NumberFormatException e) {
            new InvalidActionParameterException("Invalid coordinates parameters for teleport").printStackTrace();
            return;
        }
    }
}
