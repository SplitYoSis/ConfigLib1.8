package dev.splityosis.configsystem.configsystem.actionsystem.actiontypes;

import dev.splityosis.configsystem.configsystem.actionsystem.ActionType;
import dev.splityosis.configsystem.configsystem.actionsystem.InvalidActionParameterException;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class PlaySoundActionType extends ActionType {

    public PlaySoundActionType() {
        super("PLAY_SOUND", "PLAYSOUND", "SOUND");
    }

    @Override
    public void run(@Nullable Player player, @NotNull List<String> params, @NotNull Map<String, String> placeholders) {
        if (player == null) return;
        params = replace(params, placeholders);
        params = applyPlaceholderAPI(player, params);

        if (params.size() < 1){
            new InvalidActionParameterException("Invalid parameter amount for play sound, must be at least one. [sound] {volume} {pitch}").printStackTrace();
            return;
        }

        Sound sound = null;
        try {
            sound = Sound.valueOf(params.get(0));
        }catch (Exception e){
            new InvalidActionParameterException("Invalid sound type '"+params.get(0)+"'").printStackTrace();
            return;
        }

        float volume = 1.0f;
        float pitch = 1.0f;

        if (params.size() > 1){
            try{
                volume = Float.parseFloat(params.get(1));
            }catch (Exception e){
                new InvalidActionParameterException("Invalid volume '"+params.get(1)+"'").printStackTrace();
                return;
            }

            if (params.size() > 2){
                try{
                    pitch = Float.parseFloat(params.get(2));
                }catch (Exception e){
                    new InvalidActionParameterException("Invalid pitch '"+params.get(2)+"'").printStackTrace();
                    return;
                }
            }
        }

        player.playSound(player.getLocation(), sound, volume, pitch);
    }
}
