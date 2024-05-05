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
interface Where0<T, U> extends GroupBy<T, U>, Where<T, U> {

    Where0<T, U> where(TypedExpression<T, Boolean> predicate);

    default Where0<T, U> where(PredicateBuilder<T> predicateBuilder) {
        return where(predicateBuilder.build(root()));
    }

    default Where0<T, U> whereIf(boolean predicate, PredicateBuilder<T> predicateBuilder) {
        if (predicate) {
            return where(predicateBuilder.build(root()));
        }
        return this;
    }

    <N> PathOperator<T, N, Where0<T, U>> where(Path<T, N> path);

    <N extends Number> NumberOperator<T, N, Where0<T, U>> where(NumberPath<T, N> path);

    StringOperator<T, Where0<T, U>> where(StringPath<T> path);

}
