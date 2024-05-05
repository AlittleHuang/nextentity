package io.github.nextentity.api;

/**
 * @author HuangChengwei
 * @since 2024-05-06 8:38
 */
public
interface Having<T, U> extends OrderBy<T, U> {

    OrderBy<T, U> having(TypedExpression<T, Boolean> predicate);

    default OrderBy<T, U> having(PredicateBuilder<T> predicateBuilder) {
        return having(predicateBuilder.build(root()));
    }

}
