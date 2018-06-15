package ru.glaizier.algds.ds.concurrent;


import static java.util.stream.Collectors.toList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;

public class CopyOnWriteArrayListTest {

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

    // Todo check how to finish, check exception and how to check results
    @Test
    public void addSetRemoveConcurrently() throws InterruptedException {
        // add
        List<Callable<Object>> additionTasks = IntStream.range(0, THREADS_NUMBER)
            .mapToObj(i -> (Runnable) () -> array.add(i))
            .map(Executors::callable)
            .collect(toList());

        executorService.invokeAll(additionTasks);

        assertThat(array.size(), is(THREADS_NUMBER));
        IntStream.range(0, THREADS_NUMBER)
            .forEach(i -> assertThat(array.contains(i), is(true)));
        assertThat(array.get(0), greaterThanOrEqualTo(0));
        List<Integer> addedElements = array.stream().collect(toList());

        // set
        List<Callable<Object>> setTasks = IntStream.range(0, THREADS_NUMBER)
            .mapToObj(i -> (Runnable) () -> array.set(i, array.get(i) + 1))
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
            .mapToObj(i -> (Runnable) () -> array.remove((Object) i))
            .map(Executors::callable)
            .collect(toList());

        executorService.invokeAll(removeTasks);

        assertThat(array.size(), is(0));
        IntStream.rangeClosed(1, THREADS_NUMBER)
            .forEach(i -> assertThat(array.contains(i), is(false)));
    }

}
