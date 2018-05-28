package ru.glaizier.algds.alg.stream.pipeline;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import ru.glaizier.algds.alg.stream.Stream;
import ru.glaizier.algds.alg.stream.StreamFactory;

abstract class Pipeline<IN, OUT> implements Stream<OUT> {

    private Pipeline<?, IN> upstream;

    protected Pipeline<OUT, ?> downstream;

    protected Pipeline(Pipeline<?, IN> upstream) {
        this.upstream = upstream;
    }

    abstract void apply(IN in);

    private Pipeline<?, IN> getUpstream() {
        return upstream;
    }

    private void setDownstream(Pipeline<OUT, ?> pipeline) {
        this.downstream = pipeline;
    }

    @SuppressWarnings("unchecked")
    private Head<IN> getHead() {
        Pipeline<?, ?> upstream = this.upstream;
        while (!(upstream instanceof Head)) {
            upstream = upstream.getUpstream();
        }
        return (Head<IN>) upstream;
    }

    @Override
    public Stream<OUT> filter(Predicate<? super OUT> predicate) {
        Pipeline<OUT, OUT> filter = new Pipeline<OUT, OUT>(this) {
            @Override
            void apply(OUT in) {
                if (predicate.test(in)) {
                    this.downstream.apply(in);
                }
            }
        };
        setDownstream(filter);
        return filter;
    }

    @Override
    public <R> Stream<R> map(Function<? super OUT, ? extends R> mapper) {
        Pipeline<OUT, R> map = new Pipeline<OUT, R>(this) {
            @Override
            void apply(OUT in) {
                this.downstream.apply(mapper.apply(in));
            }
        };
        setDownstream(map);
        return map;
    }

    @Override
    public void forEach(Consumer<? super OUT> action) {
        Pipeline<OUT, OUT> forEach = new Pipeline<OUT, OUT>(this) {
            @Override
            void apply(OUT in) {
                action.accept(in);
            }
        };
        setDownstream(forEach);

        Head<IN> head = getHead();
        head.getSpliterator().forEachRemaining(head::apply);
    }

    public static void main(String[] args) {
        StreamFactory.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7))
            .filter(i -> i >= 3)
            .map(i -> i * -1)
            .forEach(System.out::println);
    }
}
