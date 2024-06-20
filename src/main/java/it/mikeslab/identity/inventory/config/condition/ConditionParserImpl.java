package it.mikeslab.identity.inventory.config.condition;

import it.mikeslab.commons.api.inventory.config.ConditionParser;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.function.Supplier;

public class ConditionParserImpl implements ConditionParser {

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

        GuiCondition condition = new GuiCondition(s);

        if(s.contains("||")) {
            return this.or(condition);
        } else if(s.contains("&&")) {
            return this.and(condition);
        } else {
            return condition.isValid() && parseCondition(condition);
        }
    }

    private boolean parseCondition(GuiCondition condition) {
        Operator operator = condition.getOperator();
        Operand[] operands = condition.getOperands();
        return operator.apply(operands);
    }


    /**
     * Parse the or condition
     * @param condition The condition
     * @return The result
     */
    private boolean or(GuiCondition condition) {
        String[] orConditions = condition.getCondition().split("\\|\\|");

        for (String conditionString : orConditions) {

            GuiCondition subCondition = new GuiCondition(conditionString);

            if (subCondition.isValid() && parseCondition(subCondition)) {
                return true;
            }

        }
        return false;

    }

    /**
     * Parse the and condition
     * @param condition The condition
     * @return The result
     */
    private boolean and(GuiCondition condition) {
        String[] andConditions = condition.getCondition().split("&&");
        for (String conditionString : andConditions) {

            GuiCondition subCondition = new GuiCondition(conditionString);

            if (!subCondition.isValid() || !parseCondition(subCondition)) {
                return false;
            }

        }
        return true;
    }
}