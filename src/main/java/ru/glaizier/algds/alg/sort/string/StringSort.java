package ru.glaizier.algds.alg.sort.string;

import java.util.Comparator;
import java.util.List;

@FunctionalInterface
public interface StringSort {

    void sort(List<String> a, Comparator<String> c);

}
