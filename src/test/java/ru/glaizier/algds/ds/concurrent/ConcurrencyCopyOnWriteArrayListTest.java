package ru.glaizier.algds.ds.concurrent;


import static java.util.stream.Collectors.toList;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.isOneOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;

public class ConcurrencyCopyOnWriteArrayListTest {

    private static final int THREADS_NUMBER = 100;

    private static ExecutorService executorService = Executors.newFixedThreadPool(THREADS_NUMBER);

    private CopyOnWriteArrayList<Integer> array = new CopyOnWriteArrayList<>();

    @After
    public void cleanUp() {
        array = new CopyOnWriteArrayList<>();
    }

    @AfterClass
    public static void cleanUpFinally() {
        executorService.shutdownNow();
    }

    @Test
    public void addSetRemoveConcurrently() throws InterruptedException {
        // add
        List<Callable<Object>> additionTasks = IntStream.range(0, THREADS_NUMBER)
            .mapToObj(i -> (Runnable) () -> {
                Thread.yield();
                try {
                    Thread.sleep((long) (Math.random() * 100));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                array.add(i);
            })
            .map(Executors::callable)
            .collect(toList());

        executorService.invokeAll(additionTasks);

        assertThat(array.size(), is(THREADS_NUMBER));
//        array.stream().forEach(i -> System.out.print(i + "-"));
        IntStream.range(0, THREADS_NUMBER)
            .forEach(i -> {
//                System.out.print(i + "-");
                assertThat(array.contains(i), is(true));
            });
        assertThat(array.get(0), greaterThanOrEqualTo(0));
        List<Integer> addedElements = array.stream().collect(toList());

        // set
        List<Callable<Object>> setTasks = IntStream.range(0, THREADS_NUMBER)
            .mapToObj(i -> (Runnable) () -> {
                Thread.yield();
                try {
                    Thread.sleep((long) (Math.random() * 100));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                array.set(i, array.get(i) + 1);
            })
            .map(Executors::callable)
            .collect(toList());

        executorService.invokeAll(setTasks);

        assertThat(array.size(), is(THREADS_NUMBER));
        IntStream.rangeClosed(1, THREADS_NUMBER)
            .forEach(i -> assertThat(array.contains(i), is(true)));
        IntStream.range(0, THREADS_NUMBER)
            .forEach(i -> assertThat(array.get(i), is(addedElements.get(i) + 1)));

        //remove by Object as remove by index doesn't always lead to an empty list because of elements shift
        List<Callable<Object>> removeTasks = IntStream.rangeClosed(1, THREADS_NUMBER)
            .mapToObj(i -> (Runnable) () -> {
                Thread.yield();
                try {
                    Thread.sleep((long) (Math.random() * 100));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                array.remove((Object) i);
            })
            .map(Executors::callable)
            .collect(toList());

        executorService.invokeAll(removeTasks);

        assertThat(array.size(), is(0));
        IntStream.rangeClosed(1, THREADS_NUMBER)
            .forEach(i -> assertThat(array.contains(i), is(false)));
    }

    @Test
    public void stream() throws InterruptedException {
        IntStream.range(0, THREADS_NUMBER).forEach(array::add);

        List<Callable<Object>> tasks = new ArrayList<>(THREADS_NUMBER + 1);
        // mutate tasks
        List<Callable<Object>> setTasks = IntStream.range(0, THREADS_NUMBER)
                .mapToObj(i -> (Runnable) () -> {
                    Thread.yield();
                    array.set(i, array.get(i) + THREADS_NUMBER);
                })
                .map(Executors::callable)
                .collect(toList());

        // numbers to check
        List<Integer> numbersToCheck = new ArrayList<>(THREADS_NUMBER);
        // stream read tasks
        Callable<Object> streamTask = Executors.callable(() -> {
            numbersToCheck.addAll(array.stream().collect(toList()));
        });
        tasks.addAll(setTasks);
        // add this task somewhere in the middle of all tasks
        tasks.add(THREADS_NUMBER / 2, streamTask);

        executorService.invokeAll(tasks);

        // check
        assertThat(numbersToCheck.size(), is(THREADS_NUMBER));
        // check that we have some
        IntStream.range(0, THREADS_NUMBER).forEach(i -> assertThat(numbersToCheck.get(i), isOneOf(i, i + THREADS_NUMBER)));
    }

}
