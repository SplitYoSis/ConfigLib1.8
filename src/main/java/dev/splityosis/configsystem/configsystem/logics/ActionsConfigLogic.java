package dev.splityosis.configsystem.configsystem.logics;

import dev.splityosis.configsystem.configsystem.ConfigTypeLogic;
import dev.splityosis.configsystem.configsystem.actionsystem.ActionData;
import dev.splityosis.configsystem.configsystem.actionsystem.Actions;
import dev.splityosis.configsystem.configsystem.actionsystem.ActionsFormatParseException;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class ActionsConfigLogic extends ConfigTypeLogic<Actions> {

    @Override
    public Actions getFromConfig(ConfigurationSection config, String path) {
        List<ActionData> actionDataList = new ArrayList<>();
        List<String> lst = config.getStringList(path);
        for (String s : lst)
            try {
                actionDataList.add(ActionData.parseString(s));
            }catch (Exception e){
                new ActionsFormatParseException(s).printStackTrace();
            }
        return new Actions(actionDataList);
    }

    @Override
    public void setInConfig(Actions instance, ConfigurationSection config, String path) {
        List<String> lst = new ArrayList<>();
        for (ActionData actionData : instance.getActionDataList()) {
            StringBuilder stringBuilder = new StringBuilder(actionData.getActionKey().toUpperCase());
            for (String parameter : actionData.getParameters())
                stringBuilder.append("'").append(parameter).append("'");
            lst.add(stringBuilder.toString());
        }
        config.set(path, lst);
    }
}
