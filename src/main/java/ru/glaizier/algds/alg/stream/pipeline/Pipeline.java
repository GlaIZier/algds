package ru.glaizier.algds.alg.stream.pipeline;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;

import ru.glaizier.algds.alg.stream.Stream;

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
        terminate();
    }

    @Override
    public <R, A> R collect(Collector<? super OUT,  A, ? extends R> collector) {
        A accumulator = collector.supplier().get();
        final BiConsumer<A, ? super OUT> accumulatorFunc = collector.accumulator();
        Pipeline<OUT, OUT> collect = new Pipeline<OUT, OUT>(this) {
            @Override
            void apply(OUT in) {
                accumulatorFunc.accept(accumulator, in);
            }
        };
        setDownstream(collect);
        terminate();
        return collector.finisher().apply(accumulator);
    }


    private void terminate() {
        Head<IN> head = getHead();
        head.getSpliterator().forEachRemaining(head::apply);
    }

}
