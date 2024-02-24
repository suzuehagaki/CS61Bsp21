package deque;

public class ArrayDeque<T> implements Deque<T> {

    T[] array;
    int size;
    /** position of item which will be inserted to head of array */
    int first;
    /** position of item which will be inserted to tail of array */
    int last;
    double usage;
    static final double USAGE_LOWER_BOUND = 0.25;
    static final double USAGE_UPPER_BOUND = 0.75;
    static final int LENGTH_LOWER_BOUND = 16;

    public ArrayDeque() {
        array = (T[]) new Object[8];
        size = 0;
        first = 0;
        last = 1;
        usage = 0.0;
    }

    public void addFirst(T item) {
        array[first] = item;
        first = (first - 1 + array.length) % array.length;
        size += 1;
        usage = (double) size / array.length;
        if (usage > USAGE_UPPER_BOUND) {
            resize(array.length * 2);
        }
    }

    public void addLast(T item) {
        array[last] = item;
        last = (last + 1) % array.length;
        size += 1;
        usage = (double) size / array.length;
        if (usage > USAGE_UPPER_BOUND) {
            resize(array.length * 2);
        }
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        int index = first + 1;
        for (int i = 0; i < size; i += 1) {
            System.out.print(array[index] + " ");
            index = (index + 1) % array.length;
        }
        System.out.println();
    }

    public T removeFirst() {
        if (size > 0) {
            int itemIndex = (first + 1) % array.length;
            first = itemIndex;
            return removeHelper(itemIndex);
        }
        return null;
    }

    public T removeLast() {
        if (size > 0) {
            int itemIndex = (last - 1 + array.length) % array.length;
            last = itemIndex;
            return removeHelper(itemIndex);
        }
        return null;
    }

    private T removeHelper(int itemIndex) {
        T revalue = array[itemIndex];
        array[itemIndex] = null;
        size -= 1;
        usage = (double) size / array.length;
        if (usage < USAGE_LOWER_BOUND && array.length >= LENGTH_LOWER_BOUND) {
            resize(array.length / 2);
        }
        return revalue;
    }

    public T get(int index) {
        return array[(first + 1) % array.length];
    }

    private void resize(int length) {
        T[] newArray = (T[]) new Object[length];
        int tempLast = 1;
        int index = first + 1;
        for (int i = 0; i < size; i += 1) {
            newArray[tempLast] = array[(index + i) % array.length];
            tempLast += 1;
        }
        first = 0;
        last = tempLast;
        array = newArray;
    }

}
