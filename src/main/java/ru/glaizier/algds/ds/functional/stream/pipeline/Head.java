package ru.glaizier.algds.ds.functional.stream.pipeline;

import java.util.Spliterator;

import ru.glaizier.algds.ds.functional.stream.Stream;

/**
 * @author GlaIZier
 */
public class Head<IN> extends Pipeline<IN, IN> implements Stream<IN> {

    private final Spliterator<IN> spliterator;

    public Head(Spliterator<IN> spliterator) {
        super(null, false);
        this.spliterator = spliterator;
    }

    protected Spliterator<IN> getSpliterator() {
        return spliterator;
    }

    @Override
    void apply(IN in) {
        this.downstream.apply(in);
    }
}
