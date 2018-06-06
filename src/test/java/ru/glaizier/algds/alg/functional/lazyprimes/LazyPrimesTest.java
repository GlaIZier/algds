package ru.glaizier.algds.alg.functional.lazyprimes;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

import ru.glaizier.algds.ds.functional.lazylist.LazyList;

/**
 * @author GlaIZier
 */
public class LazyPrimesTest {

    @Test
    public void getNextPrime() {
        LazyList.Factory<Integer> sequentialIntegers = new LazyList.Factory<>(i -> i + 1);
        LazyList<Integer> seed = sequentialIntegers.from(2);
        assertThat(LazyPrimes.introducePrimeSeq(seed).getValue(), is(2));
        assertThat(LazyPrimes.introducePrimeSeq(seed).next().getValue(), is(3));
        assertThat(LazyPrimes.introducePrimeSeq(seed).next().next().getValue(), is(5));
        assertThat(LazyPrimes.introducePrimeSeq(seed).next().next().next().getValue(), is(7));
        assertThat(LazyPrimes.introducePrimeSeq(seed).next().next().next().next().getValue(), is(11));
    }
}
