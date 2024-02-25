package deque;

public class LinkedListDeque<T> implements Deque<T> {

    private final Node<T> sentinel;
    private int size;


    private static class Node<T> {
        private final T item;
        private Node<T> pre;
        private Node<T> next;

        public Node(T item, Node<T> pre, Node<T> next) {
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

    public boolean isEmpty() {
        return size == 0;
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
}
