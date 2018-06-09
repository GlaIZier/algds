package ru.glaizier.algds.alg.functional.streamforker;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

import ru.glaizier.algds.alg.functional.streamforker.java8inaction.StreamForker;
import ru.glaizier.algds.alg.functional.streamforker.simple.SimpleStreamForker;

/**
 * @author GlaIZier
 */
public class StreamForkerTest {

    @Test
    public void simpleExecuteOperations() {
        testForker(new SimpleStreamForker<>(Dish.buildSimpleStream()));
    }

    @Test
    public void java8InActionExecuteOperations() {
        testForker(new StreamForker<>(Dish.buildSimpleStream()));
    }

    private void testForker(Forker<? extends Dish> forker) {
        ForkResult result = forker
            .fork("shortMenu", s -> s.map(Dish::getName).collect(joining(", ")))
            .fork("totalCalories", s -> s.mapToInt(Dish::getCalories).sum())
            .fork("mostCaloricDishName", s -> s.reduce((d1, d2) -> d1.getCalories() > d2.getCalories() ? d1 : d2)
                .get().getName())
            .fork("dishesByType", s -> s.collect(groupingBy(Dish::getType)))
            .getResult();

//        assertThat(result.get("shortMenu"), is("roast beef, salad, soup"));
        assertThat(result.get("totalCalories"), is(160));
        assertThat(result.get("mostCaloricDishName"), is("roast beef"));

        Map<String, List<Dish>> dishesByType = result.get("dishesByType");
        assertThat(dishesByType.size(), is(2));
        assertThat(dishesByType.get("meat").size(), is(2));
        assertThat(dishesByType.get("vegetarian").size(), is(1));
        assertThat(dishesByType.get("vegetarian").get(0).getName(), is("salad"));
    }
}
