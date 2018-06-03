package ru.glaizier.algds.ds.functional.stream;

import java.util.Collection;

import ru.glaizier.algds.ds.functional.stream.pipeline.Head;
import ru.glaizier.algds.ds.functional.stream.spliterator.IteratorSpliterator;

public class StreamFactory {

    public static <T> Stream<T> of(Collection<T> list) {
        return new Head<>(new IteratorSpliterator<>(list, 0));
    }
}
