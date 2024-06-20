package it.mikeslab.identity.inventory.config.condition;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.BiFunction;

@Getter
@RequiredArgsConstructor
public enum Operator {
    EQUAL("==", (a, b) -> a == b),
    NOT_EQUAL("!=", (a, b) -> a != b),
    GREATER(">", (a, b) -> a > b),
    LESS("<", (a, b) -> a < b),
    GREATER_EQUAL(">=", (a, b) -> a >= b),
    LESS_EQUAL("<=", (a, b) -> a <= b),
    MODULO("%", (a, b) -> a == b); // Where 'a' is the result of 'a' modulo 'b' and 'b' is the third operand

    private final String symbol;
    private final BiFunction<Double, Double, Boolean> operation;

    public static Operator fromString(String symbol) {

        for (Operator operator : Operator.values()) {

            if (operator.symbol.equals(symbol)) {
                return operator;
            }
        }
        return null;
    }

    public boolean apply(Operand... operands) {

        if(operands.length < 2) {
            throw new IllegalArgumentException("At least two operands are required for this operation.");
        }

        if(operands.length != 3 && this == MODULO) {
            throw new IllegalArgumentException("Three operands are required for this operation.");
        }

        double operand1 = operands[0].asDouble();
        double operand2 = operands[1].asDouble();

        if(this == MODULO) {
            double operand3 = operands[2].asDouble();
            return operation.apply(operand1 % operand2, operand3);
        }

        return operation.apply(operand1, operand2);
    }
}
