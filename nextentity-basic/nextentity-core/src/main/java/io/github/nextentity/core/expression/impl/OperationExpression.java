package io.github.nextentity.core.expression.impl;

import io.github.nextentity.api.Expression;
import io.github.nextentity.api.Operator;
import io.github.nextentity.core.expression.Operation;
import lombok.experimental.Accessors;

import java.util.List;

@lombok.Data
@Accessors(fluent = true)
final class OperationExpression implements Operation, AbstractTypeExpression {
    private final List<? extends Expression> operands;
    private final Operator operator;
}
