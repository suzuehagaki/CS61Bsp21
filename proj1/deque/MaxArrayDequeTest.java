package deque;

import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.assertEquals;

public class MaxArrayDequeTest {
    @Test
    public void integerTest() {
        Comparator<Integer> comparator = new Comparator<Integer>() {
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
        };
        MaxArrayDeque<Integer> A = new MaxArrayDeque<>(comparator);
        for (int i = 0; i < 10; i += 1) {
            A.addLast(i);
            assertEquals(i, A.max(), 0);
        }

        /* one way to get minItem */
        Comparator<Integer> comparator1 = new Comparator<Integer>() {
            public int compare(Integer o1, Integer o2) {
                return o2 - o1;
            }
        };

        for (int i = 0; i < 10; i += 1) {
            assertEquals(i, A.max(comparator1), 0);
            A.removeFirst();
        }
    }

    @Test
    public void doubleTest() {
        Comparator<String> comparator = new Comparator<String>() {
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        };
        MaxArrayDeque<String> A = new MaxArrayDeque<>(comparator);
        String[] strings = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"};
        for (int i = 0; i < 10; i += 1) {
            A.addLast(strings[i]);
            assertEquals(strings[i], A.max());
        }

        /* one way to get minItem */
        Comparator<String> comparator1 = new Comparator<String>() {
            public int compare(String o1, String o2) {
                return o2.compareTo(o1);
            }
        };

        for (int i = 0; i < 10; i += 1) {
            assertEquals(strings[i], A.max(comparator1));
            A.removeFirst();
        }
    }

}
