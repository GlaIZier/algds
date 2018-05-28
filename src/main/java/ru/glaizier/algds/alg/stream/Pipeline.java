package ru.glaizier.algds.alg.stream;

import java.util.LinkedList;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Pipeline<IN, OUT> implements Stream<OUT> {

    private final Spliterator<IN> spliterator;

    private LinkedList<Operation<?, ?>> operations = new LinkedList<>();

    private static abstract class Operation<IN, OUT> {
        protected Operation<OUT, ?> downstream = null;

        public void setDownstream(Operation<OUT, ?> downstream) {
            this.downstream = downstream;
        }

        abstract void apply(IN in);
    }

    public Pipeline(Spliterator<IN> spliterator) {
        this.spliterator = spliterator;
    }

    private Pipeline(Spliterator<IN> spliterator, LinkedList<Operation<?, ?>> operations) {
        this.spliterator = spliterator;
        this.operations = operations;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Stream<OUT> filter(Predicate<? super OUT> predicate) {
        Operation<OUT, OUT> filter = new Operation<OUT, OUT>() {
            @Override
            void apply(OUT in) {
                if (predicate.test(in)) {
                    downstream.apply(in);
                }
            }
        };
        Operation<?, OUT> last = (Operation<?, OUT>) operations.peekLast();
        if (last != null) {
            last.setDownstream(filter);
        }
        operations.add(filter);
        return new Pipeline<>(spliterator, operations);
    }

    @Override
    public <R> Stream<R> map(Function<? super OUT, ? extends R> mapper) {
        Operation<OUT, R> map = new Operation<OUT, R>() {
            @Override
            void apply(OUT in) {
                downstream.apply(mapper.apply(in));
            }
        };
        Operation<?, OUT> last = (Operation<?, OUT>) operations.peekLast();
        if (last != null) {
            last.setDownstream(map);
        }
        operations.add(map);
        return new Pipeline<>(spliterator, operations);
    }

    @Override
    public void forEach(Consumer<? super OUT> action) {

    }

    public static void main(String[] args) {
        LinkedList<?> a = new LinkedList<Integer>();
        Function<?, ?> b = new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer integer) {
                return integer + 1;
            }
        };
    }
}
