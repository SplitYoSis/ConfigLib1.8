package dev.splityosis.configsystem.configsystem.actionsystem;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActionData {
    private String actionKey;
    private List<String> parameters;

    public ActionData(String actionKey, List<String> parameters) {
        this.actionKey = actionKey.toUpperCase();
        this.parameters = parameters;
    }

    public String getActionKey() {
        return actionKey;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public static ActionData parseString(String input) {
        String actionKey = null;
        List<String> parameters = new ArrayList<>();

        Pattern pattern = Pattern.compile("([A-Za-z]+)|'([^']*)'");
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            if (matcher.group(1) != null) {
                actionKey = matcher.group(1);
            } else if (matcher.group(2) != null) {
                parameters.add(matcher.group(2));
            }
        }

        return new ActionData(actionKey, parameters);
    }
}
