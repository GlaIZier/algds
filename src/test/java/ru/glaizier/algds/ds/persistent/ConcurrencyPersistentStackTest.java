package ru.glaizier.algds.ds.persistent;

import static java.util.stream.Collectors.toList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import ru.glaizier.algds.ds.functional.persistent.PersistentStack;
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

    @Test
    public void add() throws InterruptedException {
        List<Callable<Object>> pushTasks = IntStream.range(0, THREADS_NUMBER / 2)
            .mapToObj(i -> (Runnable) () ->
                stack.getAndUpdate(s -> {
                    Thread.yield();
                    try {
                        Thread.sleep((long) (Math.random() * 100));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return s.add(i, i);
                })
            )
            .map(Executors::callable)
            .collect(toList());

        executorService.invokeAll(pushTasks);

        // we wait inside each thread (consequently and atomically)
        pushTasks = IntStream.range(THREADS_NUMBER / 2, THREADS_NUMBER)
            .mapToObj(i -> (Runnable) () ->
                stack.getAndUpdate(s -> {
                    Thread.yield();
                    try {
                        Thread.sleep((long) (Math.random() * 100));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    int index = (int) (Math.random() * 100);
                    return s.add(index, i);
                })
            )
            .map(Executors::callable)
            .collect(toList());

        executorService.invokeAll(pushTasks);
        checkAddition(stack.get()::forEach);
    }

    @Test
    public void update() throws InterruptedException {
        List<Callable<Object>> pushTasks = IntStream.range(0, THREADS_NUMBER)
            .mapToObj(i -> (Runnable) () ->
                stack.getAndUpdate(s -> {
                    Thread.yield();
                    try {
                        Thread.sleep((long) (Math.random() * 100));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return s.add(i, i - 1);
                })
            )
            .map(Executors::callable)
            .collect(toList());

        executorService.invokeAll(pushTasks);

        // we wait inside each thread (consequently and atomically)
        pushTasks = IntStream.range(0, THREADS_NUMBER)
            .mapToObj(i -> (Runnable) () ->
                stack.getAndUpdate(s -> {
                    Thread.yield();
                    try {
                        Thread.sleep((long) (Math.random() * 100));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return s.update(i, s.get(i).orElseThrow(IllegalStateException::new) + 1);
                })
            )
            .map(Executors::callable)
            .collect(toList());

        executorService.invokeAll(pushTasks);
        checkAddition(stack.get()::forEach);
    }

    @Test
    public void pop() throws InterruptedException {
        IntStream.range(0, THREADS_NUMBER)
            .forEach(i -> stack.set(stack.get().push(i)));
        // we wait inside each concurrently and then change atomically, but we could wait it consequently too
        List<Callable<Integer>> popTasks = IntStream.range(0, THREADS_NUMBER)
            .mapToObj(i -> (Callable<Integer>) () -> {
                Thread.yield();
                try {
                    Thread.sleep((long) (Math.random() * 100));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                PersistentStack<Integer> popped = stack
                    .getAndUpdate(s -> s.pop().orElseThrow(IllegalStateException::new).getValue());
                System.out.print(popped.pop().get().getKey() + "->");
                return popped.pop().orElseThrow(IllegalStateException::new).getKey();
            })
            .collect(toList());

        List<Future<Integer>> futures = executorService.invokeAll(popTasks);

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
            .forEach(i -> {
                assertThat(i, greaterThanOrEqualTo(0));
                assertThat(i, lessThan(THREADS_NUMBER));
                assertThat(checked.contains(i), is(false));
                checked.add(i);
            });

        assertThat(stack.get().pop(), is(Optional.empty()));
    }

    @Test
    public void get() throws InterruptedException {
        IntStream.range(0, THREADS_NUMBER)
            .forEach(i -> stack.set(stack.get().push(i)));

        List<Callable<Optional<Integer>>> getTasks = IntStream.range(0, THREADS_NUMBER)
            .mapToObj(i -> (Callable<Optional<Integer>>) () -> {
                Thread.yield();
                try {
                    Thread.sleep((long) (Math.random() * 100));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return stack.get().get(i);
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
    public void peek() throws InterruptedException {
        IntStream.range(0, THREADS_NUMBER)
            .forEach(i -> stack.set(stack.get().push(i)));

        List<Callable<Optional<Integer>>> peekTasks = IntStream.range(0, THREADS_NUMBER)
            .mapToObj(i -> (Callable<Optional<Integer>>) () -> {
                Thread.yield();
                try {
                    Thread.sleep((long) (Math.random() * 100));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return stack.get().peek();
            })
            .collect(toList());

        List<Future<Optional<Integer>>> futures = executorService.invokeAll(peekTasks);

        assertThat(futures.size(), is(THREADS_NUMBER));
        IntStream.range(0, THREADS_NUMBER)
            .forEach(i -> {
                try {
                    assertThat(futures.get(THREADS_NUMBER - 1 - i).get().get(), is(THREADS_NUMBER - 1));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
    }

    @Test
    public void contains() throws InterruptedException {
        List<Callable<Object>> pushTasks = buildPushTasks();
        executorService.invokeAll(pushTasks);
        checkAddition(stack.get()::forEach);

        List<Callable<Boolean>> containsTasks = IntStream.range(0, THREADS_NUMBER)
            .mapToObj(i -> (Callable<Boolean>) () -> {
                Thread.yield();
                try {
                    Thread.sleep((long) (Math.random() * 100));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return stack.get().contains(i);
            })
            .collect(toList());
        List<Future<Boolean>> containsFutures = executorService.invokeAll(containsTasks);
        containsFutures.forEach(future -> {
            try {
                assertThat(future.get(), is(true));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        assertThat(stack.get().contains(-1), is(false));
    }
}
