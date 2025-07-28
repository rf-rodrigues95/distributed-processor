package com.crossjoin.processor.util;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class ExpressionEvaluator {
    public static int evaluate(String expression) {
        Expression exp = new ExpressionBuilder(expression).build();
        double result = exp.evaluate();
        return (int) result;
    }
}
