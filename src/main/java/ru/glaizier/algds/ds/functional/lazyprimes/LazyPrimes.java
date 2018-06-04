package ru.glaizier.algds.ds.functional.lazyprimes;

import ru.glaizier.algds.ds.functional.lazylist.LazyList;

/**
 * @author GlaIZier
 */
public class LazyPrimes {

    public static LazyList<Integer> primes(LazyList<Integer> from) {
        if (from.getValue() <= 1) {
            throw new IllegalArgumentException("From value has to be more than 1");
        }

        return new LazyList<>(
            from.getValue(),
            () -> primes(from.filter(n -> n % from.getValue() != 0))
        );
    }

    public static void printAll() {
        try {
            LazyList.Factory<Integer> sequentialIntegers = new LazyList.Factory<>(i -> i + 1);
            LazyList<Integer> seed = sequentialIntegers.from(2);
            printAll(primes(seed));
        } catch (StackOverflowError e) {
            System.out.println("Stack overflowed. Exiting...");
        }
    }

    private static void printAll(LazyList<Integer> from) {
            System.out.println(from.getValue());
            printAll(from.next());
    }

    public static void main(String[] args) {
//        LazyList.Factory<Integer> sequentialIntegers = new LazyList.Factory<>(i -> i + 1);
//        LazyList<Integer> seed = sequentialIntegers.from(2);
//        System.out.println(primes(seed).getValue());
//        System.out.println(primes(seed).next().getValue());
//        System.out.println(primes(seed).next().next().getValue());
//        System.out.println(primes(seed).next().next().next().getValue());
//        System.out.println(primes(seed).next().next().next().next().getValue());
        printAll();
    }
}
