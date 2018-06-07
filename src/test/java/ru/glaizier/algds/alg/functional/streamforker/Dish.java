package ru.glaizier.algds.alg.functional.streamforker;

import java.util.stream.Stream;

import lombok.Value;

/**
 * @author GlaIZier
 */
@Value
public class Dish {
    String name;
    int calories;
    String type;

    public static Stream<Dish> buildSimpleStream() {
        return Stream.of(
            new Dish("roast beef", 100, "meat"),
            new Dish("salad", 10, "vegetarian"),
            new Dish("soup", 50, "meat")
        );
    }
}
