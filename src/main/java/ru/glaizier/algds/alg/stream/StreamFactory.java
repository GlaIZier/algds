package ru.glaizier.algds.alg.stream;

import java.util.Collection;
import java.util.List;

public class StreamFactory {

    public static <T> Stream<T> of(Collection<T> list) {
        return new Pipeline<>(new IteratorSpliterator<>(list, 0));
    }
}
