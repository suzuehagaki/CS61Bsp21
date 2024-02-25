package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {

    Comparator<T> comparator;

    public MaxArrayDeque(Comparator<T> c) {
        super();
        comparator = c;
    }

    public T max() {
        T max = get(0);
        for (int i = 1; i < size; i += 1) {
            T temp = get(i);
            if (comparator.compare(max, temp) < 0) {
                max = temp;
            }
        }
        return max;
    }

    public T max(Comparator<T> c) {
        T max = get(0);
        for (int i = 1; i < size; i += 1) {
            T temp = get(i);
            if (c.compare(max, temp) < 0) {
                max = temp;
            }
        }
        return max;
    }

}
