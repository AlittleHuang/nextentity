package io.github.nextentity.api;

import io.github.nextentity.api.TypedExpression.PathExpression;
import io.github.nextentity.api.model.EntityRoot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author HuangChengwei
 * @since 2024-05-06 8:37
 */
public
interface Fetch<T> extends Where<T, T> {

    Where<T, T> fetch(List<PathExpression<T, ?>> expressions);

    default Where<T, T> fetch(PathExpression<T, ?> path) {
        return fetch(List.of(path));
    }

    default Where<T, T> fetch(PathExpression<T, ?> p0, PathExpression<T, ?> p1) {
        return fetch(List.of(p0, p1));
    }

    default Where<T, T> fetch(PathExpression<T, ?> p0, PathExpression<T, ?> p1, PathExpression<T, ?> p3) {
        return fetch(List.of(p0, p1, p3));
    }

    default Where<T, T> fetch(Collection<Path<T, ?>> paths) {
        EntityRoot<T> root = root();
        List<PathExpression<T, ?>> result = new ArrayList<>(paths.size());
        for (Path<T, ?> path : paths) {
            TypedExpression.EntityPathExpression<T, ?> tEntityPathExpression = root.get(path);
            result.add(tEntityPathExpression);
        }
        List<PathExpression<T, ?>> list = Collections.unmodifiableList(result);
        return fetch(list);
    }

    default Where<T, T> fetch(Path<T, ?> path) {
        EntityRoot<T> root = root();
        return fetch(root.get(path));
    }

    default Where<T, T> fetch(Path<T, ?> p0, Path<T, ?> p1) {
        EntityRoot<T> root = root();
        return fetch(root.get(p0), root.get(p1));
    }

    default Where<T, T> fetch(Path<T, ?> p0, Path<T, ?> p1, Path<T, ?> p3) {
        EntityRoot<T> root = root();
        return fetch(root.get(p0), root.get(p1), root.get(p3));
    }

}
