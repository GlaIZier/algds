package ru.glaizier.algds.alg.functional.persistent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;

import ru.glaizier.algds.ds.functional.persistent.PersistentStack;

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
    public void pushPop() {

    }

}
