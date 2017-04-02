package uk.co.ticklethepanda.utility.web;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public interface Transformer<I, O> {

    default Set<O> transformSet(Set<I> set) {
        return set
                .stream()
                .map(this::transform)
                .collect(Collectors.toSet());
    }

    default List<O> transformList(List<I> list) {
        return list.stream()
                .map(this::transform)
                .collect(Collectors.toList());
    }

    default <T> Map<T, O> transformMap(Map<T, I> map) {
        return map
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> this.transform(e.getValue())));
    }

    O transform(I input);
}
