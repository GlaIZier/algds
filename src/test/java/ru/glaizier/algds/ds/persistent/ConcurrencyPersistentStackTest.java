package ru.glaizier.algds.ds.persistent;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import ru.glaizier.algds.ds.functional.persistent.AtomicPersistentStack;
import ru.glaizier.algds.ds.functional.persistent.PersistentStack;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author GlaIZier
 */
public class ConcurrencyPersistentStackTest {

    private static final int THREADS_NUMBER = 100;

    private static ExecutorService executorService = Executors.newFixedThreadPool(THREADS_NUMBER);

    private PersistentStack<Integer> empty;

    @AfterClass
    public static void cleanUpFinally() {
        executorService.shutdownNow();
    }

    @Before
    public void cleanUp() {
        empty = PersistentStack.empty();
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
                    empty = empty.push(i);
                })
                .map(Executors::callable)
                .collect(toList());
    }

    private void checkAddition(PersistentStack<Integer> stack) {
        HashSet<Integer> checked = new HashSet<>();
        stack.forEach(i -> {
            assertThat(i, greaterThanOrEqualTo(0));
            assertThat(i, lessThan(THREADS_NUMBER));
            assertThat(checked.contains(i), is(false));
            checked.add(i);
            System.out.print(i + "->");
        });
        assertThat(checked.size(), is(THREADS_NUMBER));
    }

    @Test
    public void pushPop() throws InterruptedException {
        List<Callable<Object>> pushTasks = buildPushTasks();
        executorService.invokeAll(pushTasks);
        checkAddition(empty);
    }

    @Test
    public void addGet() {
        PersistentStack<Integer> stack = empty.push(1).push(2).push(3);

        PersistentStack<Integer> add4 = stack.add(0, 4);
        assertThat(add4.peek().get(), is(4));
        assertThat(add4.get(0).get(), is(4));
        assertThat(add4.get(1).get(), is(3));
        assertThat(add4.get(2).get(), is(2));
        assertThat(add4.get(3).get(), is(1));
        assertThat(add4.get(4).isPresent(), is(false));

        PersistentStack<Integer> add5 = add4.add(2, 5);
        assertThat(add5.peek().get(), is(4));
        assertThat(add5.get(0).get(), is(4));
        assertThat(add5.get(1).get(), is(3));
        assertThat(add5.get(2).get(), is(5));
        assertThat(add5.get(3).get(), is(2));
        assertThat(add5.get(4).get(), is(1));
        assertThat(add5.get(5).isPresent(), is(false));

        PersistentStack<Integer> add6 = add5.add(1000, 6);
        assertThat(add6.peek().get(), is(4));
        assertThat(add6.get(0).get(), is(4));
        assertThat(add6.get(1).get(), is(3));
        assertThat(add6.get(2).get(), is(5));
        assertThat(add6.get(3).get(), is(2));
        assertThat(add6.get(4).get(), is(1));
        assertThat(add6.get(5).get(), is(6));
        assertThat(add6.get(6).isPresent(), is(false));

        PersistentStack<Integer> add6Again = add6.add(1000, 6);
        assertThat(add6Again.peek().get(), is(4));
        assertThat(add6Again.get(0).get(), is(4));
        assertThat(add6Again.get(1).get(), is(3));
        assertThat(add6Again.get(2).get(), is(5));
        assertThat(add6Again.get(3).get(), is(2));
        assertThat(add6Again.get(4).get(), is(1));
        assertThat(add6Again.get(5).get(), is(6));
        assertThat(add6Again.get(6).get(), is(6));
        assertThat(add6Again.get(7).isPresent(), is(false));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void updateGet() {
        PersistentStack<Integer> stack = empty.push(1).push(2).push(3);

        PersistentStack<Integer> update0 = stack.update(0, -3);
        assertThat(update0.peek().get(), is(-3));
        assertThat(update0.get(0).get(), is(-3));
        assertThat(update0.get(1).get(), is(2));
        assertThat(update0.get(2).get(), is(1));
        assertThat(update0.get(3).isPresent(), is(false));

        PersistentStack<Integer> update1 = update0.update(1, -2);
        assertThat(update1.peek().get(), is(-3));
        assertThat(update1.get(0).get(), is(-3));
        assertThat(update1.get(1).get(), is(-2));
        assertThat(update1.get(2).get(), is(1));
        assertThat(update1.get(3).isPresent(), is(false));

        PersistentStack<Integer> update2 = update1.update(2, -1);
        assertThat(update2.peek().get(), is(-3));
        assertThat(update2.get(0).get(), is(-3));
        assertThat(update2.get(1).get(), is(-2));
        assertThat(update2.get(2).get(), is(-1));
        assertThat(update2.get(3).isPresent(), is(false));

        update2.update(3, -100);
    }

    @Test
    public void contains() {
        PersistentStack<Integer> stack = empty.push(1).push(2).push(3);

        assertThat(stack.contains(1), is(true));
        assertThat(stack.contains(2), is(true));
        assertThat(stack.contains(3), is(true));
        assertThat(stack.contains(0), is(false));
    }

}
