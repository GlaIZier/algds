package ru.glaizier.algds.ds.persistent;

import org.junit.Before;
import org.junit.Test;
import ru.glaizier.algds.ds.functional.persistent.PersistentStack;

import java.util.Map;
import java.util.Optional;

import static java.util.Optional.of;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author GlaIZier
 */
public class PersistentStackTest {

    private PersistentStack<Integer> empty;


    @Before
    public void cleanUp() {
        empty = PersistentStack.empty();
    }

    @Test
    public void pushPop() {
        PersistentStack<Integer> stack = empty.push(1).push(2).push(3);

        assertThat(stack.peek(), is(of(3)));
        Map.Entry<Integer, PersistentStack<Integer>> pop3 = stack.pop().get();
        assertThat(pop3.getKey(), is(3));
        Map.Entry<Integer, PersistentStack<Integer>> pop2 = pop3.getValue().pop().get();
        assertThat(pop2.getKey(), is(2));
        Map.Entry<Integer, PersistentStack<Integer>> pop1 = pop2.getValue().pop().get();
        assertThat(pop1.getKey(), is(1));
        Optional<Map.Entry<Integer, PersistentStack<Integer>>> emptyPop = pop1.getValue().pop();
        assertThat(emptyPop.isPresent(), is(false));
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
