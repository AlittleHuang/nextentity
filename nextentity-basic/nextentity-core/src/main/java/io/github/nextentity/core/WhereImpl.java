package io.github.nextentity.core;

import io.github.nextentity.api.EntityRoot;
import io.github.nextentity.api.Expression;
import io.github.nextentity.api.ExpressionBuilder.NumberOperator;
import io.github.nextentity.api.ExpressionBuilder.PathOperator;
import io.github.nextentity.api.ExpressionBuilder.StringOperator;
import io.github.nextentity.api.LockModeType;
import io.github.nextentity.api.Operator;
import io.github.nextentity.api.Order;
import io.github.nextentity.api.Path;
import io.github.nextentity.api.Path.NumberPath;
import io.github.nextentity.api.Path.StringPath;
import io.github.nextentity.api.Query.Collector;
import io.github.nextentity.api.Query.ExpressionsBuilder;
import io.github.nextentity.api.Query.Having;
import io.github.nextentity.api.Query.OrderBy;
import io.github.nextentity.api.Query.OrderOperator;
import io.github.nextentity.api.Query.SubQueryBuilder;
import io.github.nextentity.api.Query.Where0;
import io.github.nextentity.api.TypedExpression;
import io.github.nextentity.api.TypedExpression.OperatableExpression;
import io.github.nextentity.core.expression.Operation;
import io.github.nextentity.core.expression.QueryStructure;
import io.github.nextentity.core.expression.QueryStructure.Selected;
import io.github.nextentity.core.expression.QueryStructure.Selected.SelectArray;
import io.github.nextentity.core.expression.QueryStructure.Selected.SelectPrimitive;
import io.github.nextentity.core.expression.impl.ExpressionBuilders;
import io.github.nextentity.core.expression.impl.Expressions;
import io.github.nextentity.core.expression.impl.InternalExpressions;
import io.github.nextentity.core.util.ImmutableList;
import io.github.nextentity.core.util.Paths;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

@SuppressWarnings("PatternVariableCanBeUsed")
public class WhereImpl<T, U> implements Where0<T, U>, Having<T, U>, AbstractCollector<U> {

    static final Selected SELECT_ANY = new SelectPrimitive().expression(InternalExpressions.TRUE).type(Boolean.class);
    static final Selected COUNT_ANY = new SelectPrimitive()
            .expression(InternalExpressions.operate(InternalExpressions.TRUE, Operator.COUNT)).type(Long.class);

    QueryExecutor queryExecutor;
    QueryStructure queryStructure;
    QueryPostProcessor structurePostProcessor;

    public WhereImpl(QueryExecutor queryExecutor, Class<T> type, QueryPostProcessor structurePostProcessor) {
        init(type, queryExecutor, structurePostProcessor);
    }


    WhereImpl(QueryExecutor queryExecutor, QueryStructure queryStructure, QueryPostProcessor structurePostProcessor) {
        init(queryExecutor, queryStructure, structurePostProcessor);
    }

    protected void init(Class<T> type, QueryExecutor queryExecutor, QueryPostProcessor structurePostProcessor) {
        init(queryExecutor, InternalExpressions.queryStructure(queryExecutor.metamodel().getEntity(type)), structurePostProcessor);
    }

    private void init(QueryExecutor queryExecutor, QueryStructure queryStructure, QueryPostProcessor structurePostProcessor) {
        this.queryExecutor = queryExecutor;
        this.queryStructure = queryStructure;
        this.structurePostProcessor = structurePostProcessor == null ? QueryPostProcessor.NONE : structurePostProcessor;
    }

    public WhereImpl() {
    }

    <X, Y> WhereImpl<X, Y> update(QueryStructure queryStructure) {
        return new WhereImpl<>(queryExecutor, queryStructure, structurePostProcessor);
    }

    @Override
    public Where0<T, U> where(TypedExpression<T, Boolean> predicate) {
        if (InternalExpressions.isNullOrTrue(predicate)) {
            return this;
        }
        QueryStructure structure = whereAnd(queryStructure, predicate);
        return update(structure);
    }

    static QueryStructure whereAnd(QueryStructure structure, Expression expression) {
        Expression where;
        if (InternalExpressions.isNullOrTrue(structure.where())) {
            where = expression;
        } else {
            where = InternalExpressions.operate(structure.where(), Operator.AND, expression);
        }
        return InternalExpressions.where(structure, where);
    }

    @Override
    public Collector<U> orderBy(List<? extends Order<T>> orders) {
        return addOrderBy(orders);
    }

    @Override
    public Collector<U> orderBy(Function<EntityRoot<T>, List<? extends Order<T>>> ordersBuilder) {
        return orderBy(ordersBuilder.apply(Paths.root()));
    }

    @Override
    public OrderOperator<T, U> orderBy(Collection<Path<T, Comparable<?>>> paths) {
        return new OrderOperatorImpl<>(this, paths);
    }

    WhereImpl<T, U> addOrderBy(List<? extends Order<T>> orders) {
        List<? extends Order<?>> orderBy = queryStructure.orderBy();
        orderBy = orderBy == null ? orders : ImmutableList.concat(orderBy, orders);
        QueryStructure structure = InternalExpressions.queryStructure(
                queryStructure.select(),
                queryStructure.from(),
                queryStructure.where(),
                queryStructure.groupBy(),
                orderBy,
                queryStructure.having(),
                queryStructure.offset(),
                queryStructure.limit(),
                queryStructure.lockType()
        );
        return update(structure);
    }

    @Override
    public long count() {
        QueryStructure structure = buildCountData();
        structure = structurePostProcessor.preCountQuery(this, structure);
        return queryExecutor.<Number>getList(structure).get(0).longValue();
    }

    @NotNull
    QueryStructure buildCountData() {
        QueryStructure.From from = queryStructure.from();
        Expression where = queryStructure.where();
        if (queryStructure.select().distinct()) {
            return countFrom(queryStructure.select());
        } else if (requiredCountSubQuery(queryStructure.select())) {
            return countFrom(COUNT_ANY);
        } else if (queryStructure.groupBy() != null && !queryStructure.groupBy().isEmpty()) {
            return countFrom(SELECT_ANY);
        } else {
            return InternalExpressions.queryStructure(
                    COUNT_ANY,
                    queryStructure.from(),
                    queryStructure.where(),
                    queryStructure.groupBy(),
                    ImmutableList.of(),
                    queryStructure.having(),
                    null,
                    null,
                    LockModeType.NONE);
        }

    }


    public QueryStructure countFrom(Selected selected) {
        QueryStructure.From from = InternalExpressions.from(InternalExpressions.queryStructure(
                selected,
                queryStructure.from(),
                queryStructure.where(),
                queryStructure.groupBy(),
                ImmutableList.of(),
                queryStructure.having(),
                null,
                null,
                LockModeType.NONE));
        return InternalExpressions.queryStructure(COUNT_ANY, from);
    }


    boolean requiredCountSubQuery(Selected select) {
        if (select instanceof SelectPrimitive) {
            return requiredCountSubQuery(((SelectPrimitive) select).expression());
        }
        if (select instanceof SelectArray) {
            for (Selected expression : ((SelectArray) select).items()) {
                if (requiredCountSubQuery(expression)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean requiredCountSubQuery(Expression expression) {
        if (expression instanceof Operation) {
            Operation operation = (Operation) expression;
            if (operation.operator().isAgg()) {
                return true;
            }
            List<? extends Expression> args = operation.operands();
            if (args != null) {
                for (Expression arg : args) {
                    if (requiredCountSubQuery(arg)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public List<U> getList(int offset, int maxResult, LockModeType lockModeType) {
        QueryStructure structure = buildListData(offset, maxResult, lockModeType);
        structure = structurePostProcessor.preListQuery(this, structure);
        return queryList(structure);
    }

    public <X> List<X> queryList(QueryStructure structure) {
        return queryExecutor.getList(structure);
    }

    @NotNull
    QueryStructure buildListData(int offset, int maxResult, LockModeType lockModeType) {
        return InternalExpressions.queryStructure(
                queryStructure.select(),
                queryStructure.from(),
                queryStructure.where(),
                queryStructure.groupBy(),
                queryStructure.orderBy(),
                queryStructure.having(),
                offset,
                maxResult,
                lockModeType
        );
    }

    @Override
    public boolean exist(int offset) {
        QueryStructure structure = buildExistData(offset);
        structure = structurePostProcessor.preExistQuery(this, structure);
        return !queryList(structure).isEmpty();
    }

    @NotNull
    QueryStructure buildExistData(int offset) {
        return InternalExpressions.queryStructure(
                SELECT_ANY,
                queryStructure.from(),
                queryStructure.where(),
                queryStructure.groupBy(),
                ImmutableList.of(),
                queryStructure.having(),
                offset,
                1,
                queryStructure.lockType()
        );
    }

    @Override
    public <X> SubQueryBuilder<X, U> asSubQuery() {
        return new SubQuery<>();
    }

    @Override
    public Having<T, U> groupBy(List<? extends TypedExpression<T, ?>> expressions) {
        return setGroupBy(expressions);
    }

    private WhereImpl<T, U> setGroupBy(List<? extends Expression> group) {
        QueryStructure structure = InternalExpressions.queryStructure(
                queryStructure.select(),
                queryStructure.from(),
                queryStructure.where(),
                group,
                queryStructure.orderBy(),
                queryStructure.having(),
                queryStructure.offset(),
                queryStructure.limit(),
                queryStructure.lockType()
        );
        return update(structure);
    }

    @Override
    public Having<T, U> groupBy(ExpressionsBuilder<T> expressionsBuilder) {
        return groupBy(expressionsBuilder.apply(Paths.root()));
    }

    @Override
    public Having<T, U> groupBy(Path<T, ?> path) {
        return setGroupBy(ImmutableList.of(InternalExpressions.of(path)));
    }

    @Override
    public Having<T, U> groupBy(Collection<Path<T, ?>> paths) {
        return groupBy(InternalExpressions.toExpressionList(paths));
    }

    @Override
    public OrderBy<T, U> having(TypedExpression<T, Boolean> predicate) {
        QueryStructure structure = InternalExpressions.queryStructure(
                queryStructure.select(),
                queryStructure.from(),
                queryStructure.where(),
                queryStructure.groupBy(),
                queryStructure.orderBy(),
                predicate,
                queryStructure.offset(),
                queryStructure.limit(),
                queryStructure.lockType()
        );
        return update(structure);
    }

    @Override
    public <N extends Number> NumberOperator<T, N, Where0<T, U>> where(NumberPath<T, N> path) {
        return ExpressionBuilders.ofNumber(root().get(path), this::whereAnd);
    }

    @NotNull
    private Where0<T, U> whereAnd(OperatableExpression<?, ?> expression) {
        if (expression == null) {
            return this;
        }
        QueryStructure structure = whereAnd(queryStructure, expression);
        return update(structure);
    }

    @Override
    public StringOperator<T, Where0<T, U>> where(StringPath<T> path) {
        return ExpressionBuilders.ofString(root().get(path), this::whereAnd);
    }

    public EntityRoot<T> root() {
        return Paths.root();
    }

    @Override
    public <N> PathOperator<T, N, Where0<T, U>> where(Path<T, N> path) {
        return ExpressionBuilders.ofPath(root().get(path), this::whereAnd);
    }


    class SubQuery<X> implements SubQueryBuilder<X, U>, QueryStructure {

        @Override
        public TypedExpression<X, Long> count() {
            QueryStructure structure = buildCountData();
            return Expressions.of(structure);
        }

        @Override
        public TypedExpression<X, List<U>> slice(int offset, int maxResult) {
            QueryStructure structure = buildListData(offset, maxResult, null);
            return Expressions.of(structure);
        }

        @Override
        public TypedExpression<X, U> getSingle(int offset) {
            QueryStructure structure = buildListData(offset, 2, null);
            return Expressions.of(structure);
        }

        @Override
        public TypedExpression<X, U> getFirst(int offset) {
            QueryStructure structure = buildListData(offset, 1, null);
            return Expressions.of(structure);
        }

        public LockModeType lockType() {
            return queryStructure.lockType();
        }

        public Integer limit() {
            return queryStructure.limit();
        }

        public Integer offset() {
            return queryStructure.offset();
        }

        public Expression having() {
            return queryStructure.having();
        }

        public List<? extends Order<?>> orderBy() {
            return queryStructure.orderBy();
        }

        public List<? extends Expression> groupBy() {
            return queryStructure.groupBy();
        }

        public Expression where() {
            return queryStructure.where();
        }

        public From from() {
            return queryStructure.from();
        }

        public Selected select() {
            return queryStructure.select();
        }
    }

}
