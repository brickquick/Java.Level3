package lesson6.homework6;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Homework1_2Test {
    private static Homework1_2 hw12;

    @BeforeAll
    static void init() {
        hw12 = new Homework1_2();
    }

    @ParameterizedTest
    @MethodSource("massTestArrays")
    public void massTestSecond4(int[] arr, int[] result) {
        try {
            Assertions.assertArrayEquals(result, hw12.first4(arr));
        } catch (RuntimeException ignored) {
        }
    }

    private static Stream<Arguments> massTestArrays() {
        List<Arguments> out = new ArrayList<>();
        out.add(Arguments.arguments(new int[] {1, 2, 4, 4, 2, 3, 4, 1, 7}, new int[] {1, 7}));
        out.add(Arguments.arguments(new int[] {1, 2, 4, 4, 2, 3, 1, 7}, new int[] {2, 3, 1, 7}));
        out.add(Arguments.arguments(new int[] {1, 2, 4, 4, 2, 3, 1, 4}, new int[] {}));
        out.add(Arguments.arguments(new int[] {1, 2, 2, 3, 1, 7}, new int[] {}));
        out.add(Arguments.arguments(new int[] {4444444, 4, 44444444}, new int[] {44444444}));
        return out.stream();
    }

    @ParameterizedTest
    @MethodSource("massTestSecond")
    public void massTestSecond4(int[] arr, boolean result) {
        Assertions.assertEquals(result, hw12.second4(arr));
    }

    private static Stream<Arguments> massTestSecond() {
        List<Arguments> out = new ArrayList<>();
        out.add(Arguments.arguments(new int[] {1, 1, 1, 4, 4, 1, 4, 4}, true));
        out.add(Arguments.arguments(new int[] {1, 1, 1, 1, 1, 1}, false));
        out.add(Arguments.arguments(new int[] {4, 4, 4, 4}, false));
        out.add(Arguments.arguments(new int[] {1, 4, 4, 1, 1, 4, 3}, false));
        out.add(Arguments.arguments(new int[] {}, false));
        return out.stream();
    }

}
