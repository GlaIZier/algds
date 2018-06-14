package ru.glaizier.algds.ds.functional.stream.pipeline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;

import ru.glaizier.algds.ds.functional.stream.Stream;

abstract class Pipeline<IN, OUT> implements Stream<OUT> {

    private Pipeline<?, IN> upstream;

    protected Pipeline<OUT, ?> downstream;

    private boolean parallel = false;

    protected Pipeline(Pipeline<?, IN> upstream, boolean parallel) {
        this.upstream = upstream;
        this.parallel = parallel;
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
    public Stream<OUT> parallel() {
        parallel = true;
        return this;
    }

    @Override
    public Stream<OUT> filter(Predicate<? super OUT> predicate) {
        Pipeline<OUT, OUT> filter = new Pipeline<OUT, OUT>(this, parallel) {
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
        Pipeline<OUT, R> map = new Pipeline<OUT, R>(this, parallel) {
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
        Collection<OUT> terminated = parallel ? terminateParallel() : terminate();
        for (OUT out : terminated) {
            action.accept(out);
        }
    }

    @Override
    public <R, A> R collect(Collector<? super OUT, A, ? extends R> collector) {
        Collection<OUT> terminated = parallel ? terminateParallel() : terminate();
        A accumulator = collector.supplier().get();
        for (OUT out : terminated) {
            collector.accumulator().accept(accumulator, out);
        }
        return collector.finisher().apply(accumulator);
    }


    private Collection<OUT> terminate() {
        Head<IN> head = getHead();
        Spliterator<IN> spliterator = head.getSpliterator();

        List<OUT> list = new ArrayList<>(Math.toIntExact(spliterator.getExactSizeIfKnown()));
        Pipeline<OUT, OUT> collect = new Pipeline<OUT, OUT>(this, parallel) {
            @Override
            void apply(OUT in) {
                list.add(in);
            }
        };
        setDownstream(collect);
        spliterator.forEachRemaining(head::apply);
        return list;
    }

    private Collection<OUT> terminateParallel() {
        Head<IN> head = getHead();
        Spliterator<IN> spliterator = head.getSpliterator();

        Collection<OUT> collection = new ConcurrentSkipListSet<>();
        Pipeline<OUT, OUT> collect = new Pipeline<OUT, OUT>(this, parallel) {
            @Override
            void apply(OUT in) {
                collection.add(in);
            }
        };
        setDownstream(collect);
        ForkJoinPool.commonPool().invoke(new StreamRecursiveAction<>(spliterator, head::apply));
        return collection;
    }


    /**
     * Previous sequential implementation
     * @param action
     */
    public void forEachSeq(Consumer<? super OUT> action) {
        Pipeline<OUT, OUT> forEach = new Pipeline<OUT, OUT>(this, parallel) {
            @Override
            void apply(OUT in) {
                action.accept(in);
            }
        };
        setDownstream(forEach);
        terminateSeq();
    }

    public <R, A> R collectSeq(Collector<? super OUT,  A, ? extends R> collector) {
        A accumulator = collector.supplier().get();
        final BiConsumer<A, ? super OUT> accumulatorFunc = collector.accumulator();
        Pipeline<OUT, OUT> collect = new Pipeline<OUT, OUT>(this, parallel) {
            @Override
            void apply(OUT in) {
                accumulatorFunc.accept(accumulator, in);
            }
        };
        setDownstream(collect);
        terminateSeq();
        return collector.finisher().apply(accumulator);
    }

    private void terminateSeq() {
        Head<IN> head = getHead();
        head.getSpliterator().forEachRemaining(head::apply);
    }

}
