package ru.glaizier.algds.alg.stream;

import static java.util.stream.Collectors.toList;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 * @author GlaIZier
 */
public class StreamPerformanceTest {

    @Test
    public void parallelIsFasterSequentialOnBigNumbers() {
        List<Integer> collection = IntStream.rangeClosed(1, 1 << 25).boxed().collect(toList());
        Duration sequentialStart = Duration.ofMillis(System.currentTimeMillis());
        runSequentially(collection);
        Duration sequentialDuration = Duration.ofMillis(System.currentTimeMillis()).minus(sequentialStart);

        Duration parallelStart = Duration.ofMillis(System.currentTimeMillis());
        runParallel(collection);
        Duration parallelDuration = Duration.ofMillis(System.currentTimeMillis()).minus(parallelStart);

        System.out.println("parallelDuration = " + parallelDuration);
        System.out.println("sequentialDuration = " + sequentialDuration);
        assertThat(parallelDuration.compareTo(sequentialDuration), is(-1));

    }

    private void runSequentially(Collection<? extends Integer> collection) {
        StreamFactory.of(collection)
            .filter(i -> i > 1 >> 10)
            .map(i -> i + 1)
            .collect(toList());
    }

    private void runParallel(Collection<? extends Integer> collection) {
        StreamFactory.of(collection)
            .parallel()
            .filter(i -> i > 1 >> 10)
            .map(i -> i + 1)
            .collect(toList());
    }
}
