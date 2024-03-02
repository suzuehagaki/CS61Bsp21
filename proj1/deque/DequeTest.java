package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DequeTest {
    @Test
    public void randomizedTest() {
        ArrayDeque<Integer> A = new ArrayDeque<>();
        LinkedListDeque<Integer> L = new LinkedListDeque<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                A.addLast(randVal);
                L.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                assertEquals(A.size(), L.size());
            } else if (operationNumber == 2) {
                if (!L.isEmpty()) {
                    int a = A.removeLast();
                    int b = L.removeLast();
                    assertEquals(a, b);
                }
            } else if (operationNumber == 3) {
                if (!A.isEmpty()) {
                    int randomIndex = StdRandom.uniform(0, A.size());
                    assertEquals(A.get(randomIndex), L.get(randomIndex));
                }
            }
        }
    }

    @Test
    public void equalsTest() {
        ArrayDeque<Integer> A = new ArrayDeque<>();
        ArrayDeque<Integer> A2 = new ArrayDeque<>();
        LinkedListDeque<Integer> L = new LinkedListDeque<>();
        LinkedListDeque<Integer> L2 = new LinkedListDeque<>();
        for (int i = 0; i < 10; i += 1) {
            A.addLast(i);
            L.addLast(i);
            A2.addLast(i);
            L2.addLast(i);
            assertTrue(A.equals(L));
            assertTrue(L.equals(A));
        }
        assertTrue(A.equals(A));
        assertTrue(L.equals(L));
        assertTrue(A.equals(A2));
        assertTrue(L.equals(L2));
        ArrayDeque<ArrayDeque<Integer>> A3 = new ArrayDeque<>();
        LinkedListDeque<LinkedListDeque<Integer>> L3 = new LinkedListDeque<>();
        A3.addLast(A);
        L3.addLast(L);
        A3.addLast(A2);
        L3.addLast(L2);
        boolean b = A3.equals(L3);
        assertTrue(b);
    }

    @Test
    public void hashTest() {
        ArrayDeque<Integer> A = new ArrayDeque<>();
        ArrayDeque<Integer> A2 = new ArrayDeque<>();
        LinkedListDeque<Integer> L = new LinkedListDeque<>();
        LinkedListDeque<Integer> L2 = new LinkedListDeque<>();
        for (int i = 0; i < 10; i += 1) {
            A.addLast(i);
            L.addLast(i);
            A2.addLast(i);
            L2.addLast(i);
            assertEquals(A.hashCode(), L.hashCode());
        }
        assertEquals(A.hashCode(), A2.hashCode());
        assertEquals(L.hashCode(), L2.hashCode());
        ArrayDeque<ArrayDeque<Integer>> A3 = new ArrayDeque<>();
        LinkedListDeque<LinkedListDeque<Integer>> L3 = new LinkedListDeque<>();
        A3.addLast(A);
        L3.addLast(L);
        A3.addLast(A2);
        L3.addLast(L2);
        assertEquals(A3.hashCode(), L3.hashCode());
    }

}
