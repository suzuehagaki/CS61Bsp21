package gh2;

import deque.ArrayDeque;
import deque.Deque;
import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

public class GuitarHero {

    public static final String KEYBOARD = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
    public static final double BASE = 220.0;

    public static void main(String[] args) {
        Deque<GuitarString> strings = new ArrayDeque<>();
        for (int i = 0; i < KEYBOARD.length(); i += 1) {
            strings.addLast(new GuitarString(BASE * Math.pow(2, (double) (i - 24) / 12.0)));
        }

        while (true) {

            /* check if the user has typed a key; if so, process it */
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                int index = KEYBOARD.indexOf(key);
                if (index == -1) {
                    continue;
                }
                strings.get(index).pluck();
            }

            /* compute the superposition of samples */
            double sample = 0.0;
            for (int i = 0; i < strings.size(); i += 1) {
                sample += strings.get(i).sample();
            }

            /* play the sample on standard audio */
            StdAudio.play(sample);

            /* advance the simulation of each guitar string by one step */
            for (int i = 0; i < strings.size(); i += 1) {
                strings.get(i).tic();
            }
        }
    }
}
