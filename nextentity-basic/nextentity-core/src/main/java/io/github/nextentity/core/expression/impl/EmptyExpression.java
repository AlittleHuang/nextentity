package io.github.nextentity.core.expression.impl;

import io.github.nextentity.api.Expression;
import io.github.nextentity.api.Operator;
import io.github.nextentity.core.expression.Literal;
import io.github.nextentity.core.util.ImmutableList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

final class EmptyExpression implements Literal, AbstractTypeExpression {
    public static final EmptyExpression EMPTY = new EmptyExpression();

    @Override
    public @NotNull AbstractTypeExpression operate(Operator operator, Expression expression) {
        return toTypedExpression(expression);
    }

    @Override
    public @NotNull AbstractTypeExpression operate(Operator operator, List<? extends Expression> expressions) {
        if (operator.isMultivalued()) {
            int count = (int) expressions.stream()
                    .filter(EmptyExpression::notEmpty)
                    .count();
            if (count == 0) {
                return this;
            }
            if (count != expressions.size()) {
                expressions = expressions.stream()
                        .filter(EmptyExpression::notEmpty)
                        .collect(ImmutableList.collector(count));
            }
            Expression baseExpression = InternalExpressions.newOperation(expressions, operator);
            return toTypedExpression(baseExpression);
        }
        throw new UnsupportedOperationException();
    }

    private static boolean notEmpty(Expression e) {
        return e != InternalExpressions.EMPTY;
    }

    @Override
    public @NotNull AbstractTypeExpression operate(Operator operator) {
        return this;
    }

    @Override
    public Object value() {
        throw new UnsupportedOperationException();
    }
}
