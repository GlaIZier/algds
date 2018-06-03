package ru.glaizier.algds.ds.functional.stream.pipeline;

import java.util.Spliterator;
import java.util.concurrent.RecursiveAction;
import java.util.function.Consumer;

/**
 * @author GlaIZier
 */
public class StreamRecursiveAction<IN> extends RecursiveAction {

    private final Spliterator<IN> spliterator;

    private final Consumer<? super IN> headAction;

    public StreamRecursiveAction(Spliterator<IN> spliterator, Consumer<? super IN> headAction) {
        this.spliterator = spliterator;
        this.headAction = headAction;
    }

    @Override
    protected void compute() {
        Spliterator<IN> fork = spliterator.trySplit();
        if (fork == null) {
            computeSequentially();
            return;
        }

        StreamRecursiveAction<IN> left = new StreamRecursiveAction<>(fork, headAction);
        left.fork();
//        StreamRecursiveAction<IN> right = new StreamRecursiveAction<>(spliterator, headAction);
//        right.compute();
//        left.join();
        // don't know why this is faster and less memory consuming than the code above
        this.compute();
    }

    private void computeSequentially() {
        spliterator.forEachRemaining(headAction);
    }
}
