package ru.glaizier.algds.ds.persistent;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;

import ru.glaizier.algds.ds.functional.persistent.PersistentStack;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author GlaIZier
 */
public class PersistentStackTest {

    private static final int THREADS_NUMBER = 100;

    private static ExecutorService executorService = Executors.newFixedThreadPool(THREADS_NUMBER);

    private PersistentStack<Integer> stack = new PersistentStack<>();

    @After
    public void cleanUp() {
        stack = new PersistentStack<>();
    }

    @AfterClass
    public static void cleanUpFinally() {
        executorService.shutdownNow();
    }

    @Test
    public void push() throws InterruptedException {
        List<Callable<Object>> additionTasks = IntStream.range(0, THREADS_NUMBER)
                .mapToObj(i -> (Runnable) () -> stack.push(i))
                .map(Executors::callable)
                .collect(toList());

        executorService.invokeAll(additionTasks);

        HashSet<Integer> checked = new HashSet<>();
        IntStream.range(0, THREADS_NUMBER)
                .forEach(i -> {
                    Integer pop = stack.pop().get();
                    assertThat(pop, greaterThanOrEqualTo(0));
                    assertThat(pop, lessThan(THREADS_NUMBER));
                    assertThat(checked.contains(pop), is(false));
                    checked.add(pop);
                });
    }

}
