package ru.glaizier.algds.ds.persistent;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import ru.glaizier.algds.ds.functional.persistent.PersistentStack;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static ru.glaizier.algds.ds.persistent.AtomicPersistentStackTest.checkAddition;

/**
 * @author GlaIZier
 */
public class ConcurrencyPersistentStackTest {

    private static final int THREADS_NUMBER = 100;

    private static ExecutorService executorService = Executors.newFixedThreadPool(THREADS_NUMBER);

    private AtomicReference<PersistentStack<Integer>> stack;

    @AfterClass
    public static void cleanUpFinally() {
        executorService.shutdownNow();
    }

    @Before
    public void cleanUp() {
        stack = new AtomicReference<>(PersistentStack.empty());
    }

    private List<Callable<Object>> buildPushTasks() {
        return IntStream.range(0, THREADS_NUMBER)
                .mapToObj(i -> (Runnable) () ->
                    stack.getAndUpdate(s -> {
                        Thread.yield();
                        try {
                            Thread.sleep((long) (Math.random() * 100));
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        return s.push(i);
                    })
                )
                .map(Executors::callable)
                .collect(toList());
    }

    @Test
    public void pushPop() throws InterruptedException {
        List<Callable<Object>> pushTasks = buildPushTasks();
        executorService.invokeAll(pushTasks);
        checkAddition(stack.get()::forEach);
    }
}
