package io.github.nextentity.core.expression.impl;

import io.github.nextentity.api.EntityRoot;
import io.github.nextentity.api.Expression;
import io.github.nextentity.api.ExpressionBuilder;
import io.github.nextentity.api.Operator;
import io.github.nextentity.api.Order;
import io.github.nextentity.api.Path;
import io.github.nextentity.api.Query;
import io.github.nextentity.api.SortOrder;
import io.github.nextentity.api.TypedExpression;
import io.github.nextentity.core.TypeCastUtil;
import io.github.nextentity.core.expression.EntityPath;
import io.github.nextentity.core.util.ImmutableList;
import io.github.nextentity.core.util.Iterators;
import io.github.nextentity.core.util.Paths;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static io.github.nextentity.api.TypedExpression.*;

@Accessors(fluent = true)
@SuppressWarnings("rawtypes")
interface AbstractTypeExpression extends NumberPathExpression, StringPathExpression, BooleanPathExpression, EntityPathExpression {

    @Override
    default EntityRoot root() {
        return Paths.root();
    }

    @Override
    default NumberExpression count() {
        return toTypedExpression(InternalExpressions.operate(this, Operator.COUNT));
    }

    @Override
    default NumberExpression countDistinct() {
        Expression distinct = InternalExpressions.operate(this, Operator.DISTINCT);
        return toTypedExpression(InternalExpressions.operate(distinct, Operator.COUNT));
    }

    @Override
    default Predicate eq(Object value) {
        return eq(Expressions.of(value));
    }

    @Override
    default Predicate eqIfNotNull(Object value) {
        return value == null ? operateNull() : eq(value);
    }

    @Override
    default Predicate eq(TypedExpression value) {
        return operate(Operator.EQ, value);
    }

    @Override
    default Predicate ne(Object value) {
        return ne(Expressions.of(value));
    }

    @Override
    default Predicate neIfNotNull(Object value) {
        return value == null ? operateNull() : ne(value);
    }

    @Override
    default Predicate ne(TypedExpression value) {
        return operate(Operator.NE, value);
    }

    @Override
    default Predicate in(@NotNull TypedExpression expressions) {
        return operate(Operator.IN, expressions);
    }

    @Override
    default Predicate in(Object[] values) {
        List<TypedExpression<?, ?>> collect = Arrays.stream(values)
                .map(Expressions::of)
                .collect(ImmutableList.collector(values.length));
        return in(collect);
    }

    @Override
    default Predicate in(@NotNull Collection values) {
        List<TypedExpression<?, ?>> collect = ((Collection<?>) values).stream()
                .map(Expressions::of)
                .collect(ImmutableList.collector(values.size()));
        return in(collect);
    }

    @Override
    default Predicate notIn(Object[] values) {
        return not(in(values));
    }

    @Override
    default Predicate notIn(@NotNull Collection values) {
        return not(in(values));
    }

    @Override
    default Predicate isNull() {
        return operate(Operator.IS_NULL);
    }

    @Override
    default Predicate isNotNull() {
        return not(isNull());
    }

    @Override
    default Predicate notIn(@NotNull List values) {
        return not(in(values));
    }

    @Override
    default Predicate in(@NotNull List expressions) {
        return operate(Operator.IN, asTypeExpressions(expressions));
    }

    @Override
    default Predicate ge(TypedExpression expression) {
        return operate(Operator.GE, expression);
    }

    @Override
    default Predicate gt(TypedExpression expression) {
        return operate(Operator.GT, expression);
    }

    @Override
    default Predicate le(TypedExpression expression) {
        return operate(Operator.LE, expression);
    }

    @Override
    default Predicate lt(TypedExpression expression) {
        return operate(Operator.LT, expression);
    }

    @Override
    default Predicate between(TypedExpression l, TypedExpression r) {
        return operate(Operator.BETWEEN, ImmutableList.of(l, r));
    }

    @Override
    default Predicate notBetween(TypedExpression l, TypedExpression r) {
        return not(between(l, r));
    }

    @Override
    default Order sort(SortOrder order) {
        return new OrderImpl(this, order);
    }

    @Override
    default Predicate geIfNotNull(Object value) {
        return value == null ? operateNull() : ge(Expressions.of(value));
    }

    @Override
    default Predicate gtIfNotNull(Object value) {
        return value == null ? operateNull() : gt(Expressions.of(value));
    }

    @Override
    default Predicate leIfNotNull(Object value) {
        return value == null ? operateNull() : le(Expressions.of(value));
    }

    @Override
    default Predicate ltIfNotNull(Object value) {
        return value == null ? operateNull() : lt(Expressions.of(value));
    }

    @Override
    default NumberExpression add(TypedExpression expression) {
        return operate(Operator.ADD, expression);
    }

    @Override
    default NumberExpression subtract(TypedExpression expression) {
        return operate(Operator.SUBTRACT, expression);
    }

    @Override
    default NumberExpression multiply(TypedExpression expression) {
        return operate(Operator.MULTIPLY, expression);
    }

    @Override
    default NumberExpression divide(TypedExpression expression) {
        return operate(Operator.DIVIDE, expression);
    }

    @Override
    default NumberExpression mod(TypedExpression expression) {
        return operate(Operator.MOD, expression);
    }

    @Override
    default NumberExpression sum() {
        return operate(Operator.SUM);
    }

    @Override
    default NumberExpression avg() {
        return operate(Operator.AVG);
    }

    @Override
    default NumberExpression max() {
        return operate(Operator.MAX);
    }

    @Override
    default NumberExpression min() {
        return operate(Operator.MIN);
    }

    @Override
    default Predicate like(String value) {
        return operate(Operator.LIKE, InternalExpressions.of(value));
    }

    @Override
    default Predicate notLike(String value) {
        return not(like(value));
    }

    @Override
    default Predicate likeIfNotNull(String value) {
        return value == null ? operateNull() : like(value);
    }

    @Override
    default Predicate notLikeIfNotNull(String value) {
        return value == null ? operateNull() : notLike(value);
    }

    @Override
    default StringExpression lower() {
        return operate(Operator.LOWER);
    }

    @Override
    default StringExpression upper() {
        return operate(Operator.UPPER);
    }

    @Override
    default StringExpression substring(int offset, int length) {
        return operate(Operator.SUBSTRING, ImmutableList.of(InternalExpressions.of(offset), InternalExpressions.of(length)));
    }

    @Override
    default StringExpression trim() {
        return operate(Operator.TRIM);
    }

    @Override
    default NumberExpression length() {
        return operate(Operator.LENGTH);
    }

    @Override
    default ExpressionBuilder.PathOperator and(Path path) {
        return ExpressionBuilders.ofPath(of(path).asBasic(), this::and);
    }

    @NotNull
    default AbstractTypeExpression and(OperatableExpression<?, ?> basicExpression) {
        return basicExpression == null ? this : operate(Operator.AND, basicExpression);
    }

    @NotNull
    default AbstractTypeExpression or(OperatableExpression<?, ?> basicExpression) {
        return basicExpression == null ? this : operate(Operator.OR, basicExpression);
    }

    @NotNull
    default AbstractTypeExpression of(Path path) {
        return toTypedExpression(InternalExpressions.of(path));
    }

    @Override
    default ExpressionBuilder.NumberOperator and(Path.NumberPath path) {
        return ExpressionBuilders.ofNumber(of(path).asNumber(), this::and);
    }

    @Override
    default ExpressionBuilder.StringOperator and(Path.StringPath path) {
        return ExpressionBuilders.ofString(of(path).asString(), this::and);
    }

    @Override
    default ExpressionBuilder.Conjunction andIf(boolean predicate, Query.PredicateBuilder predicateBuilder) {
        return predicate ? and(((Query.PredicateBuilder<?>) predicateBuilder).build(TypeCastUtil.cast(root()))) : this;
    }

    @Override
    default ExpressionBuilder.PathOperator or(Path path) {
        return ExpressionBuilders.ofPath(of(path).asBasic(), this::or);
    }

    @Override
    default ExpressionBuilder.NumberOperator or(Path.NumberPath path) {
        return ExpressionBuilders.ofNumber(of(path).asNumber(), this::or);
    }


    @Override
    default ExpressionBuilder.StringOperator or(Path.StringPath path) {
        return ExpressionBuilders.ofString(of(path).asString(), this::or);
    }

    @Override
    default ExpressionBuilder.Disjunction orIf(boolean predicate, Query.PredicateBuilder predicateBuilder) {
        return predicate ? or(((Query.PredicateBuilder<?>) predicateBuilder).build(TypeCastUtil.cast(root()))) : this;
    }

    @Override
    default Predicate or(TypedExpression predicate) {
        return operate(Operator.OR, predicate);
    }

    @Override
    default Predicate and(TypedExpression expression) {
        return operate(Operator.AND, expression);
    }

    @Override
    default EntityPathExpression get(Path path) {
        // PathChain expression = (PathChain) Paths.get((Path<?, ?>) path);
        String name = InternalExpressions.attributeName(path);
        return toTypedExpression(((EntityPath) this).get(name));
    }

    @Override
    default StringPathExpression get(Path.StringPath path) {
        return get0(path);
    }

    @Override
    default StringPathExpression get(StringPathExpression path) {
        return get0(path);
    }

    @Override
    default BooleanPathExpression get(Path.BooleanPath path) {
        return get0(path);
    }

    @Override
    default NumberPathExpression get(NumberPathExpression path) {
        return get0(path);
    }

    @Override
    default PathExpression get(PathExpression path) {
        return get0(path);
    }

    @Override
    default NumberPathExpression get(Path.NumberPath path) {
        return get0(path);
    }

    @Override
    default Predicate not() {
        return operate(Operator.NOT);
    }

    @Override
    default Predicate and(TypedExpression[] predicate) {
        return operate(Operator.AND, Arrays.asList(predicate));
    }

    @Override
    default Predicate or(TypedExpression[] predicate) {
        return operate(Operator.OR, Arrays.asList(predicate));
    }

    @Override
    default Predicate and(Iterable predicates) {
        return operate(Operator.AND, TypeCastUtil.cast(Iterators.toList((Iterable<?>) predicates)));
    }

    @Override
    default Predicate toPredicate() {
        return Expressions.ofPredicate(this);
    }

    @Override
    default Predicate or(Iterable predicates) {
        return operate(Operator.OR, TypeCastUtil.cast(Iterators.toList((Iterable<?>) predicates)));
    }

    default AbstractTypeExpression get0(Path<?, ?> path) {
        PathExpression<?, ?> pathExpression = Paths.get((Path<?, ?>) path);
        return get0(pathExpression);
    }

    @NotNull
    default AbstractTypeExpression get0(PathExpression<?, ?> pathExpression) {
        EntityPath expression = (EntityPath) pathExpression;
        return toTypedExpression(((EntityPath) this).get(expression));
    }

    default AbstractTypeExpression not(TypedExpression<?, ?> expression) {
        Expression operate = InternalExpressions.operate(expression, Operator.NOT);
        return toTypedExpression(operate);
    }

    @NotNull
    default AbstractTypeExpression operate(Operator operator, Expression expression) {
        return toTypedExpression(InternalExpressions.operate(this, operator, expression));
    }

    @NotNull
    default AbstractTypeExpression operate(Operator operator, List<? extends Expression> expressions) {
        return toTypedExpression(InternalExpressions.operate(this, operator, expressions));
    }

    @NotNull
    default AbstractTypeExpression operate(Operator operator) {
        return toTypedExpression(InternalExpressions.operate(this, operator));
    }

    @NotNull
    static Predicate operateNull() {
        return EmptyExpression.EMPTY;
    }

    @NotNull
    default StringExpression<?> asString() {
        return this;
    }

    default OperatableExpression<?, ?> asBasic() {
        return this;
    }

    default NumberExpression<?, ?> asNumber() {
        return this;
    }

    default List<? extends TypedExpression<?, ?>> asTypeExpressions(List<?> list) {
        return TypeCastUtil.cast(list);
    }


    @Override
    default Predicate likeIfNotEmpty(String value) {
        return value == null || value.isEmpty() ? operateNull() : like(value);
    }

    @Override
    default Predicate notLikeIfNotEmpty(String value) {
        return value == null || value.isEmpty() ? operateNull() : notLike(value);
    }

    @Override
    default Predicate eqIfNotEmpty(String value) {
        return value == null || value.isEmpty() ? operateNull() : eq(value);
    }

    default <T extends TypedExpression<?, ?>> T toTypedExpression(Expression expression) {
        return TypeCastUtil.unsafeCast(expression);
    }

}
