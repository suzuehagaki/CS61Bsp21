package deque;

public interface Deque<T> {
    /**  Adds an item of type T to the front of the deque. Assume that item is never null. */
    public void addFirst(T item);

    /** Adds an item of type T to the end of the deque. Assume that item is never null. */
    public void addLast(T item);

    /** Returns true if deque is empty, false otherwise. */
    default public boolean isEmpty() {
        return size() == 0;
    }

    /** Returns the number of items in the deque. */
    public int size();

    /** Prints the items in the deque from first to last, separated by a space.
     * Once all the items have been printed, print out a new line. */
    public void printDeque();

    /** Removes and returns the item at the front of the deque. If no such item exists, returns null. */
    public T removeFirst();

    /** Removes and returns the item at the end of the deque. If no such item exists, returns null. */
    public T removeLast();

    /** Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth.
     * If no such item exists, returns null. Must not alter the deque! */
    public T get(int index);

}
