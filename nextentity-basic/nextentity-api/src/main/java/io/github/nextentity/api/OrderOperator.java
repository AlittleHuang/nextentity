package io.github.nextentity.api;

import io.github.nextentity.api.model.SortOrder;

/**
 * @author HuangChengwei
 * @since 2024-05-06 8:38
 */
public
interface OrderOperator<T, U> extends OrderBy<T, U> {
    default OrderBy<T, U> asc() {
        return sort(SortOrder.ASC);
    }

    default OrderBy<T, U> desc() {
        return sort(SortOrder.DESC);
    }

    OrderBy<T, U> sort(SortOrder order);
}
