package ru.glaizier.algds.ds.functional.lazylist;

import org.junit.Test;
import ru.glaizier.algds.ds.functional.lazylist.LazyDoubleLinkedList;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author GlaIZier
 */
public class LazyDoubleLinkedListTest {

    @Test
    public void nextPrev() {
        LazyDoubleLinkedList.Factory<Integer> factory = new LazyDoubleLinkedList.Factory<>(i -> i + 1);
        assertThat(factory.from(0).getValue(), is(0));
        assertThat(factory.from(0).next().next().getValue(), is(2));
        assertThat(factory.from(0).next().next().getPrev().getValue(), is(1));
    }

}
