package ru.glaizier.algds.ds.concurrent;


import org.junit.After;

public class CopyOnWriteArrayListTest {

    private CopyOnWriteArrayList<String> array = new CopyOnWriteArrayList<>();

    @After
    public void cleanUp() {
        array = new CopyOnWriteArrayList<>();
    }

    public void test() {
    }

}
