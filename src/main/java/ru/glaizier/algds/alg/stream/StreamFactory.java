package ru.glaizier.algds.alg.stream;

import java.util.AbstractList;
import java.util.List;

public class StreamFactory {

    <T> Stream<T> of(List<T> list) {
        return new Pipeline<>(new ListSpliterator<>(list));
    }
}
