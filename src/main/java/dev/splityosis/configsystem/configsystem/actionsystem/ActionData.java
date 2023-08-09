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

        Pattern pattern = Pattern.compile("([A-Za-z_]+)|`([^`]+)`");
        Matcher matcher = pattern.matcher(input);

        boolean isFirst = true;
        while (matcher.find()) {
            if (isFirst && matcher.group(1) != null) {
                actionKey = matcher.group(1);
                isFirst = false;
            } else if (matcher.group(2) != null) {
                parameters.add(matcher.group(2));
            }
        }

        if (actionKey == null)
            actionKey = "null";

        return new ActionData(actionKey, parameters);
    }

//    public static void main(String[] args) {
//
//        String input = "   RUN_COMMAND    `hello``oiskjhdffspid`  `sdopikgjf` aaa";
//
//        ActionData data = parseString(input);
//
//        System.out.println("key: '"+data.getActionKey()+"'");
//        System.out.println("parameters: "+data.getParameters());
//
//    }
}