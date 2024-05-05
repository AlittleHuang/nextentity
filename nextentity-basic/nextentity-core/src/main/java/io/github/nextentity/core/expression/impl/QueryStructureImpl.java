package io.github.nextentity.core.expression.impl;

import io.github.nextentity.api.Expression;
import io.github.nextentity.api.LockModeType;
import io.github.nextentity.api.Order;
import io.github.nextentity.core.expression.QueryStructure;
import io.github.nextentity.core.meta.EntitySchema;
import io.github.nextentity.core.util.Exceptions;
import io.github.nextentity.core.util.ImmutableList;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode
class QueryStructureImpl implements QueryStructure.From.FromSubQuery, Cloneable {

    Selected select;

    From from;

    Expression where = InternalExpressions.TRUE;

    List<? extends Expression> groupBy = ImmutableList.of();

    List<? extends Order<?>> orderBy = ImmutableList.of();

    Expression having = InternalExpressions.TRUE;

    Integer offset;

    Integer limit;

    LockModeType lockType = LockModeType.NONE;

    public QueryStructureImpl(QueryStructure queryStructure) {
        this(
                queryStructure.select(),
                queryStructure.from(),
                queryStructure.where(),
                queryStructure.groupBy(),
                queryStructure.orderBy(),
                queryStructure.having(),
                queryStructure.offset(),
                queryStructure.limit(),
                queryStructure.lockType()
        );
    }

    public QueryStructureImpl(Selected select,
                              From from,
                              Expression where,
                              List<? extends Expression> groupBy,
                              List<? extends Order<?>> orderBy,
                              Expression having,
                              Integer offset,
                              Integer limit,
                              LockModeType lockType) {
        this.select = select;
        this.from = from;
        this.where = where;
        this.groupBy = groupBy;
        this.orderBy = orderBy;
        this.having = having;
        this.offset = offset;
        this.limit = limit;
        this.lockType = lockType;
    }

    public QueryStructureImpl(Selected select, From from) {
        this.select = select;
        this.from = from;
    }

    public QueryStructureImpl(EntitySchema entityType) {
        this.from = new FromEntity(entityType.type());
        this.select = new Selected.SelectEntity().type(entityType.type());
    }

    protected QueryStructureImpl copy() {
        try {
            return (QueryStructureImpl) super.clone();
        } catch (CloneNotSupportedException e) {
            throw Exceptions.sneakyThrow(e);
        }
    }

    @Override
    public Selected select() {
        return select;
    }

    @Override
    public From from() {
        return from;
    }

    @Override
    public Expression where() {
        return where;
    }

    @Override
    public List<? extends Expression> groupBy() {
        return groupBy;
    }

    @Override
    public List<? extends Order<?>> orderBy() {
        return orderBy;
    }

    @Override
    public Expression having() {
        return having;
    }

    @Override
    public Integer offset() {
        return offset;
    }

    @Override
    public Integer limit() {
        return limit;
    }

    @Override
    public LockModeType lockType() {
        return lockType;
    }

}
