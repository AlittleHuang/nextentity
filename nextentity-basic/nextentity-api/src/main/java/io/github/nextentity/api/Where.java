package io.github.nextentity.api;

import io.github.nextentity.api.ExpressionBuilder.NumberOperator;
import io.github.nextentity.api.ExpressionBuilder.PathOperator;
import io.github.nextentity.api.ExpressionBuilder.StringOperator;
import io.github.nextentity.api.Path.NumberPath;
import io.github.nextentity.api.Path.StringPath;

/**
 * @author HuangChengwei
 * @since 2024-05-06 8:39
 */
public
interface Where<T, U> extends OrderBy<T, U> {

    Where<T, U> where(TypedExpression<T, Boolean> predicate);

    default Where<T, U> where(PredicateBuilder<T> predicateBuilder) {
        return where(predicateBuilder.build(root()));
    }

    default Where<T, U> whereIf(boolean predicate, PredicateBuilder<T> predicateBuilder) {
        if (predicate) {
            return where(predicateBuilder);
        } else {
            return this;
        }
    }

    <N> PathOperator<T, N, ? extends Where<T, U>> where(Path<T, N> path);

    <N extends Number> NumberOperator<T, N, ? extends Where<T, U>> where(NumberPath<T, N> path);

    StringOperator<T, ? extends Where<T, U>> where(StringPath<T> path);

}
