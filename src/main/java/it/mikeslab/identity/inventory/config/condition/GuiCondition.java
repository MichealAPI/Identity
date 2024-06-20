package it.mikeslab.identity.inventory.config.condition;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@RequiredArgsConstructor
public class GuiCondition {

    private final String condition;

    public boolean isValid() {
        Pattern pattern = Pattern.compile("\\s*[^a-zA-Z-0-9-\\s-.]+\\s*");
        Matcher matcher = pattern.matcher(condition);
        return matcher.find();
    }

    public Operator getOperator() {
        Pattern pattern = Pattern.compile("%|==|!=|>=|<=|>|<");
        Matcher matcher = pattern.matcher(condition);
        if (matcher.find()) {
            return Operator.fromString(matcher.group());
        }
        return null;
    }

    public Operand[] getOperands() {
        String[] values = condition.split("\\s*[^a-zA-Z-0-9-\\s-.]+\\s*");
        return Arrays
                .stream(values)
                .map(Operand::new)
                .toArray(Operand[]::new);
    }
}
