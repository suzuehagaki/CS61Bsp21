package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T> {

    private final Node<T> sentinel;
    private int size;


    private static class Node<T> {
        private final T item;
        private Node<T> pre;
        private Node<T> next;

        Node(T item, Node<T> pre, Node<T> next) {
            this.item = item;
            this.pre = pre;
            this.next = next;
        }
    }



    public LinkedListDeque() {
        size = 0;
        sentinel = new Node<>(null, null, null);
        sentinel.next = sentinel;
        sentinel.pre = sentinel;
    }

    public void addFirst(T item) {
        Node<T> node = new Node<>(item, sentinel, sentinel.next);
        sentinel.next.pre = node;
        sentinel.next = node;
        size += 1;
    }

    public void addLast(T item) {
        Node<T> node = new Node<>(item, sentinel.pre, sentinel);
        sentinel.pre.next = node;
        sentinel.pre = node;
        size += 1;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        Node<T> node = sentinel.next;
        while (node != sentinel) {
            System.out.print(node.item + " ");
            node = node.next;
        }
        System.out.println();
    }

    public T removeFirst() {
        if (size > 0) {
            return removeHelper(sentinel.next);
        }
        return null;
    }

    public T removeLast() {
        if (size > 0) {
            return removeHelper(sentinel.pre);
        }
        return null;
    }

    private T removeHelper(Node<T> node) {
        T revalue = node.item;
        node.next.pre = node.pre;
        node.pre.next = node.next;
        size -= 1;
        return revalue;
    }

    public T get(int index) {
        Node<T> node = sentinel;
        for (int i = 0; i <= index; i += 1) {
            node = node.next;
            if (node == sentinel) {
                break;
            }
        }
        return node.item;
    }

    public T getRecursive(int index) {
        if (index > size) {
            return null;
        }
        return getRecursiveHelper(index, sentinel.next);
    }

    private T getRecursiveHelper(int index, Node<T> node) {
        if (index == 0) {
            return node.item;
        }
        return getRecursiveHelper(index - 1, node.next);
    }

    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    private class LinkedListDequeIterator implements Iterator<T> {
        private int wizPos;

        LinkedListDequeIterator() {
            wizPos = 0;
        }

        @Override
        public boolean hasNext() {
            return wizPos < size;
        }

        @Override
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
        for (int i = 0; i < this.size(); i += 1) {
            if (this.get(i).getClass() != o.get(i).getClass()) {
                return false;
            }
            if (!this.get(i).equals(o.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        for (Object o : this) {
            hashCode = hashCode * 31;
            hashCode = hashCode + o.hashCode();
        }
        return hashCode;
    }

}
