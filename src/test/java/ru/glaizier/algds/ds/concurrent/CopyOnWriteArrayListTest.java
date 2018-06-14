package ru.glaizier.algds.ds.concurrent;


import static java.util.stream.Collectors.toList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

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
    public void addConcurrently() throws InterruptedException {
        List<Callable<Object>> callables = IntStream.range(0, THREADS_NUMBER)
            .mapToObj(i -> (Runnable) () -> array.add(i))
            .map(Executors::callable)
            .collect(toList());

        executorService.invokeAll(callables);

        assertThat(array.size(), is(THREADS_NUMBER));
        IntStream.range(0, THREADS_NUMBER)
            .forEach(i -> assertThat(array.contains(i), is(true)));
    }

}
