package tester;

import static org.junit.Assert.*;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;

public class TestArrayDequeEC {

    @Test
    public void randomizedTest() {
        StudentArrayDeque<Integer> AD = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> ADS = new ArrayDequeSolution<>();

        StringBuilder operationSequence = new StringBuilder("\n");

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 5);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                AD.addLast(randVal);
                ADS.addLast(randVal);
                operationSequence.append("addLast(").append(randVal).append(")\n");
            } else if (operationNumber == 1) {
                // addFirst
                int randVal = StdRandom.uniform(0, 100);
                AD.addFirst(randVal);
                ADS.addFirst(randVal);
                operationSequence.append("addFirst(").append(randVal).append(")\n");
            } else if (operationNumber == 2) {
                // size
                operationSequence.append("size()\n");
                assertEquals(operationSequence.toString(), ADS.size(), AD.size());
            } else if (operationNumber == 3) {
                // removeLast
                if (!AD.isEmpty()) {
                    Integer a = AD.removeLast();
                    Integer b = ADS.removeLast();
                    operationSequence.append("removeLast()\n");
                    assertEquals(operationSequence.toString(), b, a);
                }
            } else if (operationNumber == 4) {
                // removeFirst
                if (!AD.isEmpty()) {
                    Integer a = AD.removeFirst();
                    Integer b = ADS.removeFirst();
                    operationSequence.append("removeFirst()\n");
                    assertEquals(operationSequence.toString(), b, a);
                }
            }
        }
    }
}