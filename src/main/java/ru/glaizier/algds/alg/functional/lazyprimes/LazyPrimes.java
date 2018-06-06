package ru.glaizier.algds.alg.functional.lazyprimes;

import ru.glaizier.algds.ds.functional.lazylist.LazyList;

/**
 * @author GlaIZier
 */
public class LazyPrimes {

    public static LazyList<Integer> introducePrimeSeq(LazyList<Integer> from) {
        if (from.getValue() <= 1) {
            throw new IllegalArgumentException("From value has to be more than 1");
        }

        return new LazyList<>(
            from.getValue(),
            // this recursive call adds one more filter for the next element to check if it can be divided by the current number without remainder
            () -> introducePrimeSeq(from.filter(n -> n % from.getValue() != 0))
        );
    }

    public static void printAll() {
        try {
            LazyList.Factory<Integer> sequentialIntegers = new LazyList.Factory<>(i -> i + 1);
            LazyList<Integer> seed = sequentialIntegers.from(2);
            printAll(introducePrimeSeq(seed));
        } catch (StackOverflowError e) {
            System.out.println("Stack overflowed. Exiting...");
        }
    }

    private static void printAll(LazyList<Integer> from) {
            System.out.println(from.getValue());
            printAll(from.next());
    }

}
