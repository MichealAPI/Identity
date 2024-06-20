package it.mikeslab.identity.inventory.config;

import it.mikeslab.commons.api.inventory.config.ConditionParser;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConditionParserImpl implements ConditionParser {

    private final static String operatorRegex = "\\s*[^a-zA-Z-0-9-\\s-.]+\\s*";

    @Override
    public boolean parse(Player player, String s, Map<String, Supplier<String>> injectedValues) {

        if(s == null) return false;

        for(Map.Entry<String, Supplier<String>> entry : injectedValues.entrySet()) {
            s = s.replace(
                    entry.getKey(),
                    entry.getValue().get()
            );
        }

        return this.parse(player, s);
    }

    @Override
    public boolean parse(Player player, String s) {

        if(s == null) return false;

        s = PlaceholderAPI.setPlaceholders(player, s)
                .replace(" ", "");

        if(s.contains("||")) {
            String[] orConditions = s.split("\\|\\|");
            for (String condition : orConditions) {
                if (isConditionValid(condition) && parseCondition(condition)) {
                    return true;
                }
            }
            return false;

        } else if(s.contains("&&")) {
            String[] andConditions = s.split("&&");
            for (String condition : andConditions) {
                if (!isConditionValid(condition) || !parseCondition(condition)) {
                    return false;
                }
            }
            return true;

        } else {
            return isConditionValid(s) && parseCondition(s);
        }
    }

    private boolean parseCondition(String s) {
        String[] values = s.split(operatorRegex);

        if (values.length == 3 && getOperator(s).equals("%")) { // todo In future, you could remove % to allow for more operations
            return executeOperation(
                    values[0],
                    values[1],
                    values[2],
                    getOperator(s)
            );
        } else {
            return executeOperation(
                    values[0],
                    values[1],
                    getOperator(s)
            );
        }
    }

    private boolean isConditionValid(String s) {

        Pattern pattern = Pattern.compile(operatorRegex);
        Matcher matcher = pattern.matcher(s);

        return matcher.find();
    }

    private String getOperator(String s) {
        Pattern pattern = Pattern.compile("==|!=|>=|<=|>|<|%");
        Matcher matcher = pattern.matcher(s);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }

    private boolean executeOperation(String v1, String v2, String operator) {
        switch(operator) {
            case "==":
                return v1.equals(v2);
            case "!=":
                return !v1.equals(v2);
            case ">":
                return Double.parseDouble(v1) > Double.parseDouble(v2);
            case "<":
                return Double.parseDouble(v1) < Double.parseDouble(v2);
            case ">=":
                return Double.parseDouble(v1) >= Double.parseDouble(v2);
            case "<=":
                return Double.parseDouble(v1) <= Double.parseDouble(v2);
            default:
                return false;
        }
    }

    private boolean executeOperation(String v1, String v2, String v3, String operator) {
        if (operator.equals("%")) {
            return Double.parseDouble(v1) % Double.parseDouble(v2) == Double.parseDouble(v3);
        } else {
            return false;
        }
    }


}
