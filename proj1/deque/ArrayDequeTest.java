package deque;

import org.junit.Test;
import static org.junit.Assert.*;

public class ArrayDequeTest {

    @Test
    public void addIsEmptySizeTest() {
        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();

        assertTrue("arrayDeque should now empty.", arrayDeque.isEmpty());
        arrayDeque.addFirst(1);
        assertEquals("arrayDeque should now contain 1 item", 1, arrayDeque.size());
        arrayDeque.addFirst(2);
        assertEquals("arrayDeque should now contain 2 item", 2, arrayDeque.size());
        arrayDeque.addFirst(2);
        assertEquals("arrayDeque should now contain 3 item", 3, arrayDeque.size());
    }

    @Test
    public void addRemoveTest() {
        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();

        for (int i = 1; i < 4; i += 1) {
            arrayDeque.addLast(i);
        }

        for (int i = 1; i < 4; i += 1) {
            int temp = arrayDeque.removeFirst();
            assertEquals(i, temp);
        }

        for (int i = 1; i < 4; i += 1) {
            arrayDeque.addFirst(i);
        }
        for (int i = 1; i < 4; i += 1) {
            int temp = arrayDeque.removeLast();
            assertEquals(i, temp);
        }
    }

    @Test
    public void emptyNullReturnTest() {


        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();

        boolean passed1 = false;
        boolean passed2 = false;
        assertEquals("Should return null when removeFirst is called on an empty Deque,", null, arrayDeque.removeFirst());
        assertEquals("Should return null when removeLast is called on an empty Deque,", null, arrayDeque.removeLast());

    }

    @Test
    public void bigADequeTest() {

        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();
        for (int i = 0; i < 1000000; i++) {
            arrayDeque.addLast(i);
        }

        for (double i = 0; i < 500000; i++) {
            assertEquals("Should have the same value", i, (double) arrayDeque.removeFirst(), 0.0);
        }

        for (double i = 999999; i > 500000; i--) {
            assertEquals("Should have the same value", i, (double) arrayDeque.removeLast(), 0.0);
        }

    }

}
