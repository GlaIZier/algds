package ru.glaizier.algds.ds.persistent;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
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

    @AfterClass
    public static void cleanUpFinally() {
        executorService.shutdownNow();
    }

    @After
    public void cleanUp() {
        stack = new PersistentStack<>();
    }

    @Test
    public void push() throws InterruptedException {
        List<Callable<Object>> pushTasks = IntStream.range(0, THREADS_NUMBER)
                .mapToObj(i -> (Runnable) () -> stack.push(i))
                .map(Executors::callable)
                .collect(toList());

        executorService.invokeAll(pushTasks);

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

    @Test
    public void pop() throws InterruptedException {
        IntStream.range(0, THREADS_NUMBER)
                .forEach(stack::push);

        List<Callable<Optional<Integer>>> popTasks = IntStream.range(0, THREADS_NUMBER)
                .mapToObj(i -> (Callable<Optional<Integer>>) () -> stack.pop())
                .collect(toList());

        List<Future<Optional<Integer>>> futures = executorService.invokeAll(popTasks);

        assertThat(futures.size(), is(THREADS_NUMBER));
        HashSet<Integer> checked = new HashSet<>();
        futures.stream()
                .map(integerFuture -> {
                    try {
                        return integerFuture.get();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(Optional::get)
                .forEach(i -> {
                    assertThat(i, greaterThanOrEqualTo(0));
                    assertThat(i, lessThan(THREADS_NUMBER));
                    assertThat(checked.contains(i), is(false));
                    checked.add(i);
                });

        assertThat(stack.pop(), is(Optional.empty()));
    }



    @Test
    public void peekGetByIndex() throws InterruptedException {
        IntStream.range(0, THREADS_NUMBER)
                .forEach(stack::push);

        assertThat(stack.peek(), is(Optional.of(THREADS_NUMBER - 1)));

        List<Callable<Optional<Integer>>> getTasks = IntStream.range(0, THREADS_NUMBER)
                .mapToObj(i -> (Callable<Optional<Integer>>) () -> stack.get(i))
                .collect(toList());

        List<Future<Optional<Integer>>> futures = executorService.invokeAll(getTasks);

        assertThat(futures.size(), is(THREADS_NUMBER));
        IntStream.range(0, THREADS_NUMBER)
                .forEach(i -> {
                    try {
                        assertThat(futures.get(THREADS_NUMBER - 1 - i).get().get(), is(i));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Test
    public void getByValue() throws InterruptedException {
        List<Callable<Object>> pushTasks = IntStream.range(0, THREADS_NUMBER)
                .mapToObj(i -> (Runnable) () -> stack.push(i))
                .map(Executors::callable)
                .collect(toList());

        executorService.invokeAll(pushTasks);

        HashSet<Integer> checked = new HashSet<>();
        IntStream.range(0, THREADS_NUMBER)
                .forEach(i -> {
                    Integer get = stack.get((Integer) i).get();
                    assertThat(get, greaterThanOrEqualTo(0));
                    assertThat(get, lessThan(THREADS_NUMBER));
                    assertThat(checked.contains(get), is(false));
                    checked.add(get);
                });
    }

}
