package dev.splityosis.configsystem.configsystem.actionsystem.actiontypes;

import dev.splityosis.configsystem.configsystem.actionsystem.ActionType;
import dev.splityosis.configsystem.configsystem.actionsystem.InvalidActionParameterException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class SendTitleAllActionType extends ActionType {

    public SendTitleAllActionType() {
        super("SEND_TITLE_ALL", "TITLE_ALL", "TITLEALL", "SENDTITLEALL");
    }

    @Override
    public void run(@Nullable Player player, @NotNull List<String> params, @NotNull Map<String, String> placeholders) {

        params = replace(params, placeholders);
        params = applyPlaceholderAPI(player, params);

        if (params.size() < 5) {
            new InvalidActionParameterException("Not enough parameters for send title action [title] [subtitle] [fadein] [stay] [fadeout]").printStackTrace();
            return;
        }

        try {
            String title = colorize(params.get(0));
            String subtitle = colorize(params.get(1));
            int fadeIn = Integer.parseInt(params.get(2));
            int stay = Integer.parseInt(params.get(3));
            int fadeOut = Integer.parseInt(params.get(4));

            for (Player onlinePlayer : Bukkit.getOnlinePlayers())
                onlinePlayer.sendTitle(title, subtitle, fadeIn, stay, fadeOut);

        } catch (NumberFormatException e) {
            new InvalidActionParameterException("Invalid parameters for send title action").printStackTrace();
        }
    }
}
