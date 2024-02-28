package flik;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class FlikTest {

    @Test
    public void flikTest() {
        int i = 0;
        int j = 0;
        while (i < 500) {
            assertTrue(Flik.isSameNumber(i, j));
            i += 1;
            j += 1;
        }
    }

}