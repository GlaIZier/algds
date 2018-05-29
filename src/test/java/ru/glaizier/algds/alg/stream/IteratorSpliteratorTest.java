package ru.glaizier.algds.alg.stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Spliterator;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import org.hamcrest.Matchers;
import org.junit.Test;

import ru.glaizier.algds.alg.stream.spliterator.IteratorSpliterator;

/**
 * @author GlaIZier
 */
public class IteratorSpliteratorTest {

    @Test
    public void trySplit() {
        IteratorSpliterator<Integer> spliterator = new IteratorSpliterator<>(Arrays.asList(1, 2, 3, 4),
            0);
        Spliterator<Integer> fork = spliterator.trySplit();
        assertThat(spliterator.estimateSize(), is(2L));
        assertThat(fork.estimateSize(), is(2L));

        assertThat(spliterator.trySplit(), is(nullValue()));
        assertThat(fork.trySplit(), is(nullValue()));

        ArrayList<Integer> result = new ArrayList<>(2);
        spliterator.forEachRemaining(result::add);
        assertThat(result, Matchers.containsInAnyOrder(3, 4));

        result = new ArrayList<>(2);
        fork.forEachRemaining(result::add);
        assertThat(result, Matchers.containsInAnyOrder(1, 2));
    }
}
