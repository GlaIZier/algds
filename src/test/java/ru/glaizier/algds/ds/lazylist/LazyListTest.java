package ru.glaizier.algds.ds.lazylist;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class LazyListTest {

    @Test
    public void nextValue() {
        LazyList.Factory<Integer> factory = new LazyList.Factory<>(i -> i + 1);

        assertThat(factory.from(0).getValue(), is(0));
        assertThat(factory.from(0).next().next().getValue(), is(2));
        assertThat(factory.from(0).next().next().next().getValue(), is(3));
    }

}
