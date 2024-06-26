package io.github.nextentity.core.api;

import io.github.nextentity.core.api.expression.BaseExpression;

import java.io.Serializable;

/**
 * @author HuangChengwei
 * @since 2024/4/17 下午1:27
 */
@SuppressWarnings("unused")
public interface Order<T> extends Serializable {

    BaseExpression expression();

    SortOrder order();

}
