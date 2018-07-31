package ru.glaizier.algds.ds.concurrent;

import static java.util.stream.Collectors.toList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

/**
 * @author GlaIZier
 */
public class ConcurrencyAtomicRefTest {

    private static final int THREADS_NUMBER = 100;

    private static ExecutorService executorService = Executors.newFixedThreadPool(THREADS_NUMBER);

    private AtomicRef<Integer> ref = new AtomicRef<>(0);

    @AfterClass
    public static void cleanUpFinally() {
        executorService.shutdownNow();
    }

    @Before
    public void cleanUp() {
        ref = new AtomicRef<>(0);
    }

    @Test
    public void get() throws InterruptedException {
        List<Callable<Integer>> getTasks = IntStream.range(0, THREADS_NUMBER)
            .mapToObj(i -> (Callable<Integer>) () -> {
                    Thread.yield();
                    try {
                        Thread.sleep((long) (Math.random() * 100));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return ref.get();
                })
            .collect(toList());

        List<Future<Integer>> futures = executorService.invokeAll(getTasks);

        futures.stream()
            .map(f -> {
                try {
                    return f.get();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            })
            .forEach(i -> assertThat(i, is(0)));
    }

    @Test
    public void set() throws InterruptedException {
        AtomicInteger count = new AtomicInteger(0);
        List<Callable<Object>> setTasks = IntStream.range(0, THREADS_NUMBER)
            .mapToObj(i -> (Runnable) () -> {
                Thread.yield();
                try {
                    Thread.sleep((long) (Math.random() * 100));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                count.incrementAndGet();
                ref.set(i);
            })
            .map(Executors::callable)
            .collect(toList());

        List<Future<Object>> futures = executorService.invokeAll(setTasks);
        assertThat(futures.size(), is(THREADS_NUMBER));
        assertThat(count.get(), is(THREADS_NUMBER));
        assertThat(ref.get(), greaterThan(0));
        assertThat(ref.get(), lessThan(THREADS_NUMBER));
    }

    @Test
    public void compareAndSet() throws InterruptedException {
        // consequent concurrent set
        List<Callable<Object>> compareAndSetTasks = IntStream.range(0, THREADS_NUMBER)
            .mapToObj(i -> (Runnable) () -> {
                Thread.yield();
                try {
                    Thread.sleep((long) (Math.random() * 100));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                while (!ref.compareAndSet(i, i + 1)) {
                    // let another thread to try
                    Thread.yield();
                }
            })
            .map(Executors::callable)
            .collect(toList());

        executorService.invokeAll(compareAndSetTasks);
        assertThat(ref.get(), is(THREADS_NUMBER));
    }

}
