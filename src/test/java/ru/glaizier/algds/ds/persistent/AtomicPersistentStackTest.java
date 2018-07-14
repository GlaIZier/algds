package ru.glaizier.algds.ds.persistent;

import static java.util.stream.Collectors.toList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;

import ru.glaizier.algds.ds.functional.persistent.AtomicPersistentStack;

/**
 * @author GlaIZier
 */
public class AtomicPersistentStackTest {

    private static final int THREADS_NUMBER = 100;

    private static ExecutorService executorService = Executors.newFixedThreadPool(THREADS_NUMBER);

    private AtomicPersistentStack<Integer> stack = new AtomicPersistentStack<>();

    @AfterClass
    public static void cleanUpFinally() {
        executorService.shutdownNow();
    }

    @After
    public void cleanUp() {
        stack = new AtomicPersistentStack<>();
    }

    @Test
    public void push() throws InterruptedException {
        List<Callable<Object>> pushTasks = buildPushTasks();
        executorService.invokeAll(pushTasks);
        checkAddition(stack);
    }

    @Test
    public void pop() throws InterruptedException {
        IntStream.range(0, THREADS_NUMBER)
                .forEach(stack::push);

        List<Callable<Optional<Integer>>> popTasks = IntStream.range(0, THREADS_NUMBER)
                .mapToObj(i -> (Callable<Optional<Integer>>) () -> {
                    Thread.yield();
                    try {
                        Thread.sleep((long) (Math.random() * 100));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return stack.pop();
                })
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
                .mapToObj(i -> (Callable<Optional<Integer>>) () -> {
                    Thread.yield();
                    try {
                        Thread.sleep((long) (Math.random() * 100));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return stack.get(i);
                })
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
        List<Callable<Object>> pushTasks = buildPushTasks();
        executorService.invokeAll(pushTasks);
        checkAddition(stack);
    }

    @Test
    public void put() throws InterruptedException {
        List<Callable<Object>> pushTasks = IntStream.range(0, THREADS_NUMBER / 2)
            .mapToObj(i -> (Runnable) () -> {
                Thread.yield();
                try {
                    Thread.sleep((long) (Math.random() * 100));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                stack.put(i, i);
            })
            .map(Executors::callable)
            .collect(toList());

        executorService.invokeAll(pushTasks);

        pushTasks = IntStream.range(THREADS_NUMBER / 2, THREADS_NUMBER)
            .mapToObj(i -> (Runnable) () -> {
                Thread.yield();
                try {
                    Thread.sleep((long) (Math.random() * 100));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                int index = (int) (Math.random() * 100);
                stack.put(i, index);
            })
            .map(Executors::callable)
            .collect(toList());

        executorService.invokeAll(pushTasks);
        checkAddition(stack);
    }

    private List<Callable<Object>> buildPushTasks() {
        return IntStream.range(0, THREADS_NUMBER)
            .mapToObj(i -> (Runnable) () -> {
                Thread.yield();
                try {
                    Thread.sleep((long) (Math.random() * 100));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                stack.push(i);
            })
            .map(Executors::callable)
            .collect(toList());
    }

    private void checkAddition(AtomicPersistentStack<Integer> stack) {
        HashSet<Integer> checked = new HashSet<>();
        stack.forEach(i -> {
            assertThat(i, greaterThanOrEqualTo(0));
            assertThat(i, lessThan(THREADS_NUMBER));
            assertThat(checked.contains(i), is(false));
            checked.add(i);
        });
        assertThat(checked.size(), is(THREADS_NUMBER));
    }

}
