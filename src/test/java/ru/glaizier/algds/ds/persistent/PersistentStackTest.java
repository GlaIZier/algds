package ru.glaizier.algds.ds.persistent;

import org.junit.After;
import org.junit.Before;
import ru.glaizier.algds.ds.functional.persistent.PersistentStack;

/**
 * @author GlaIZier
 */
public class PersistentStackTest {

    private PersistentStack<Integer> stack;


    @Before
    public void cleanUp() {
        stack = PersistentStack.empty();
    }



}
