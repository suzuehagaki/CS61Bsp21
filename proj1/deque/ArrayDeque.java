package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T> {

    private T[] array;
    private int size;
    /** position of item which will be inserted to head of array */
    private int first;
    /** position of item which will be inserted to tail of array */
    private int last;
    private double usage;
    private static final double USAGE_LOWER_BOUND = 0.25;
    private static final double USAGE_UPPER_BOUND = 0.75;
    private static final int LENGTH_LOWER_BOUND = 16;

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
        if (index > size) {
            return null;
        }
        return array[(first + 1 + index) % array.length];
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

    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int wizPos;

        ArrayDequeIterator() {
            wizPos = 0;
        }


        public boolean hasNext() {
            return wizPos < size;
        }

        public T next() {
            T returnItem = get(wizPos);
            wizPos += 1;
            return returnItem;
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (this == other) {
            return true;
        }
        if (!(other instanceof Deque)) {
            return false;
        }
        Deque<T> o = (Deque<T>) other;
        if (this.size() != o.size()) {
            return false;
        }
        if (this.get(0).getClass() != o.get(0).getClass()) {
            return false;
        }
        for (int i = 0; i < this.size(); i += 1) {
            if (this.get(i) != o.get(i)) {
                return false;
            }
        }
        return true;
    }

}
