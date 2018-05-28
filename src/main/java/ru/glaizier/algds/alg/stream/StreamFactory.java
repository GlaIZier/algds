package ru.glaizier.algds.alg.stream;

import java.util.Collection;

import ru.glaizier.algds.alg.stream.pipeline.Head;
import ru.glaizier.algds.alg.stream.spliterator.IteratorSpliterator;

public class StreamFactory {

    public static <T> Stream<T> of(Collection<T> list) {
        return new Head<>(new IteratorSpliterator<>(list, 0));
    }
}
