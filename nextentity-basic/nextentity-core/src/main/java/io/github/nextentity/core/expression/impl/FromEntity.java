package io.github.nextentity.core.expression.impl;

import io.github.nextentity.core.expression.QueryStructure;
import lombok.experimental.Accessors;

@lombok.Data
@Accessors(fluent = true)
final class FromEntity implements QueryStructure.From.Entity {
    private final Class<?> type;
}
