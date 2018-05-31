package ru.glaizier.algds.ds.stream;

import static java.util.stream.Collectors.toList;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Test;

/**
 * @author GlaIZier
 */

public class StreamFunctionalTest {

    @After
    public void cleanUp() {
        System.setOut(System.out);
    }

    @Test
    public void filterMapForEach() {
        final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        StreamFactory.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7))
            .filter(i -> i >= 3)
            .map(i -> i * -1)
            .filter(i -> i == -7)
            .forEach(System.out::println);

        assertThat(outContent.toString(), is("-7\n"));
    }

    @Test
    public void filterMapCollect() {
        List<Integer> collected = StreamFactory.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7))
            .filter(i -> i >= 3)
            .map(i -> i * -1)
            .collect(toList());

        assertThat(collected, Matchers.containsInAnyOrder(-3, -4, -5, -6, -7));
    }

    @Test
    public void filterMapMapFilter() {
        List<Integer> collected = StreamFactory.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7))
            .filter(i -> i < 3)
            .map(i -> i * -1)
            .map(i -> i + 1)
            .filter(i -> i >= 0)
            .collect(toList());

        assertThat(collected, Matchers.containsInAnyOrder(0));
    }

    @Test
    public void parallelFilterMapCollect() {
        List<Integer> collected = StreamFactory.of(Arrays.asList(1, 2, 3, 4, 5, 6, 7))
            .parallel()
            .filter(i -> i >= 3)
            .map(i -> i * -1)
            .collect(toList());

        assertThat(collected, Matchers.containsInAnyOrder(-3, -4, -5, -6, -7));
    }
}
