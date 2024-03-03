package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    private BSTNode<K, V> root;
    private int size;

    public BSTMap() {
        this.root = null;
        this.size = 0;
    }

    private static class BSTNode<K, V> {

        private final K key;
        private final V value;
        private BSTNode<K, V> left;
        private BSTNode<K, V> right;

        BSTNode(K key, V value) {
            this.key = key;
            this.value = value;
            this.left = null;
            this.right = null;
        }
    }

    public void clear() {
        this.root = null;
        this.size = 0;
    }

    public boolean containsKey(K key) {
        return containsKeyHelper(key, this.root);
    }

    private boolean containsKeyHelper(K key, BSTNode<K, V> node) {
        if (node == null) {
            return false;
        }
        int comp = key.compareTo(node.key);
        if (comp == 0) {
            return true;
        } else if (comp > 0) {
            return containsKeyHelper(key, node.right);
        }
        return containsKeyHelper(key, node.left);
    }

    public V get(K key) {
        return getHelper(key, this.root);
    }

    private V getHelper(K key, BSTNode<K, V> node) {
        if (node == null) {
            return null;
        }
        int comp = key.compareTo(node.key);
        if (comp == 0) {
            return node.value;
        } else if (comp > 0) {
            return getHelper(key, node.right);
        }
        return getHelper(key, node.left);
    }

    public int size() {
        return this.size;
    }

    public void put(K key, V value) {
        this.root = putHelper(key, value, this.root);
    }

    private BSTNode<K, V> putHelper(K key, V value, BSTNode<K, V> node) {
        if (node == null) {
            this.size += 1;
            return new BSTNode<>(key, value);
        }
        int comp = key.compareTo(node.key);
        if (comp == 0) {
            return node;
        } else if (comp > 0) {
            node.right = putHelper(key, value, node.right);
        } else {
            node.left = putHelper(key, value, node.left);
        }
        return node;
    }

    public Set<K> keySet() {
        throw new UnsupportedOperationException("this function cant available now");
    }

    public V remove(K key) {
        throw new UnsupportedOperationException("this function cant available now");
    }

    public V remove(K key, V value) {
        throw new UnsupportedOperationException("this function cant available now");
    }

    public void printInOrder() {
        printHelper(this.root);
    }

    private void printHelper(BSTNode<K, V> node) {
        if (node == null) {
            return;
        }
        printHelper(node.left);
        System.out.println("key: " + node.key + "\tvalue: " + node.value);
        printHelper(node.right);
    }

    public Iterator<K> iterator() {
        throw new UnsupportedOperationException("this function cant available now");
    }
}
