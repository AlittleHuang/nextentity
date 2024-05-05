package io.github.nextentity.core.expression.impl;

import io.github.nextentity.core.expression.Literal;
import lombok.experimental.Accessors;

@lombok.Data
@Accessors(fluent = true)
final class LiteralExpression implements Literal, AbstractTypeExpression {
    private final Object value;
}
