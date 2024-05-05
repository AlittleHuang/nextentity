package io.github.nextentity.api;

import io.github.nextentity.api.model.EntityRoot;

/**
 * @author HuangChengwei
 * @since 2024-05-06 8:38
 */
@FunctionalInterface
public
interface PredicateBuilder<T> {
    TypedExpression<T, Boolean> build(EntityRoot<T> root);
}
