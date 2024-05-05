package io.github.nextentity.api;

import java.util.Collection;
import java.util.List;

/**
 * @author HuangChengwei
 * @since 2024-05-06 8:38
 */
public
interface GroupBy<T, U> extends OrderBy<T, U> {
    Having<T, U> groupBy(List<? extends TypedExpression<T, ?>> expressions);

    Having<T, U> groupBy(ExpressionsBuilder<T> expressionsBuilder);

    Having<T, U> groupBy(Path<T, ?> path);

    Having<T, U> groupBy(Collection<Path<T, ?>> paths);

    default Having<T, U> groupBy(Path<T, ?> p0, Path<T, ?> p1) {
        return groupBy(List.of(p0, p1));
    }

    default Having<T, U> groupBy(Path<T, ?> p0, Path<T, ?> p1, Path<T, ?> p2) {
        return groupBy(List.of(p0, p1, p2));
    }

    default Having<T, U> groupBy(Path<T, ?> p0, Path<T, ?> p1, Path<T, ?> p2, Path<T, ?> p3) {
        return groupBy(List.of(p0, p1, p2, p3));
    }

    default Having<T, U> groupBy(Path<T, ?> p0, Path<T, ?> p1, Path<T, ?> p2, Path<T, ?> p3, Path<T, ?> p4) {
        return groupBy(List.of(p0, p1, p2, p3, p4));
    }

    default Having<T, U> groupBy(Path<T, ?> p0, Path<T, ?> p1, Path<T, ?> p2, Path<T, ?> p3, Path<T, ?> p4, Path<T, ?> p5) {
        return groupBy(List.of(p0, p1, p2, p3, p4, p5));
    }
}
