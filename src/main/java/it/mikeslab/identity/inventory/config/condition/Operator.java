package it.mikeslab.identity.inventory.config.condition;

import java.util.Arrays;

public enum Operator {
    EQUAL("=="),
    NOT_EQUAL("!="),
    GREATER(">"),
    LESS("<"),
    GREATER_EQUAL(">="),
    LESS_EQUAL("<="),
    MODULO("%");

    private final String symbol;

    Operator(String symbol) {
        this.symbol = symbol;
    }

    public static Operator fromString(String symbol) {

        for (Operator operator : Operator.values()) {

            if (operator.symbol.equals(symbol)) {
                return operator;
            }
        }
        return null;
    }

    public boolean apply(Operand... operands) {

        if (this == MODULO) {
            if (operands.length != 3) {
                throw new IllegalArgumentException("Three operands are required for the modulo operation.");
            }
        } else {
            if (operands.length != 2) {
                throw new IllegalArgumentException("Two operands are required for this operation.");
            }
        }

        double operand1 = operands[0].asDouble();
        double operand2 = operands[1].asDouble();

        switch (this) {
            case EQUAL:
                return operand1 == operand2;
            case NOT_EQUAL:
                return operand1 != operand2;
            case GREATER:
                return operand1 > operand2;
            case LESS:
                return operand1 < operand2;
            case GREATER_EQUAL:
                return operand1 >= operand2;
            case LESS_EQUAL:
                return operand1 <= operand2;
            case MODULO:
                double operand3 = operands[2].asDouble();
                return operand1 % operand2 == operand3;
            default:
                throw new UnsupportedOperationException("Unsupported operator: " + this);
        }
    }
}
