package lambdas.lesson1;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Lambdas03 {

    private interface GenericSum<T> {
        T sum(T a, T b);

        default T twice(T t) {
            return sum(t, t);
        }
    }

    @Test
    public void generic1() {
        final GenericSum<Integer> integerGenericSum =
                //anonymous inner class net lambda
                (Integer i1, Integer i2) -> {
                    System.out.println("before sum: " + i1 + " " + i2);
                    return i1 + i2;
                };

        assertEquals(integerGenericSum.sum(1, 2), Integer.valueOf(3));

        assertEquals(integerGenericSum.twice(10), Integer.valueOf(20));
    }

    @Test
    public void generic2() {
        final GenericSum<Integer> sum = (i1, i2) -> i1 + i2;

        assertEquals(sum.twice(1), Integer.valueOf(2));
    }

    private static String stringSum(String s1, String s2) {
        return s1 + s2;
    }

    @Test
    public void strSum() {
        final GenericSum<String> stringGenericSum = Lambdas03::stringSum; //this will be used as override method

        assertEquals(stringGenericSum.sum("a", "b"), "ab");

        assertEquals(stringGenericSum.twice("c"), "cc");
    }

    private final String delimeter = "-";

    private String stringSumWithDelimeter(String s1, String s2) {
        return s1 + delimeter + s2;
    }

    @Test
    public void strSum2() {
        final GenericSum<String> sum = this::stringSumWithDelimeter;

        assertEquals(sum.sum("a", "b"), "a-b");
    }

}
