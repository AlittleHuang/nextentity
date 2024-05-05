package io.github.nextentity.api;

import io.github.nextentity.api.model.EntityRoot;

import java.util.List;

/**
 * @author HuangChengwei
 * @since 2024-05-06 8:37
 */
@FunctionalInterface
public
interface ExpressionsBuilder<T> {
    List<? extends TypedExpression<T, ?>> apply(EntityRoot<T> root);
}
