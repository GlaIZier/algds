package ru.glaizier.algds.alg.stream;

import static java.util.stream.Collectors.toList;
import java.util.ArrayList;
import java.util.Spliterator;
import java.util.stream.IntStream;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import org.junit.Test;

import ru.glaizier.algds.alg.stream.spliterator.IteratorSpliterator;

/**
 * @author GlaIZier
 */
public class IteratorSpliteratorTest {

    @Test
    public void trySplit() {
        IteratorSpliterator<Integer> spliterator = new IteratorSpliterator<>(
            IntStream.rangeClosed(1, 1 << 14).boxed().collect(toList()), 0);
        Spliterator<Integer> fork = spliterator.trySplit();
        assertThat(spliterator.estimateSize(), is(1L << 13));
        assertThat(fork.estimateSize(), is(1L << 13));

        assertThat(spliterator.trySplit(), is(nullValue()));
        assertThat(fork.trySplit(), is(nullValue()));

        ArrayList<Integer> result = new ArrayList<>(1 << 13);
        spliterator.forEachRemaining(result::add);
        assertThat(result.size(), is(1 << 13));

        result = new ArrayList<>(1 << 13);
        fork.forEachRemaining(result::add);
        assertThat(result.size(), is(1 << 13));
    }
}
