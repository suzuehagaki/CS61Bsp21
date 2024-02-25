package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
                    int randomIndex = StdRandom.uniform(0, A.size);
                    assertEquals(A.get(randomIndex), L.get(randomIndex));
                }
            }
        }
    }
}
