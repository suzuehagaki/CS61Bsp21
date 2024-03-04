package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author suzue
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    /* elements in the map */
    private int size;
    private final double loadFactor;

    /** Constructors */
    public MyHashMap() {
        this.size = 0;
        this.loadFactor = 0.75;
        this.buckets = createTable(16);
    }

    public MyHashMap(int initialSize) {
        this.size = 0;
        this.loadFactor = 0.75;
        this.buckets = createTable(initialSize);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param loadFactor maximum load factor
     */
    public MyHashMap(int initialSize, double loadFactor) {
        this.size = 0;
        this.loadFactor = loadFactor;
        this.buckets = createTable(initialSize);
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new ArrayList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] table = new Collection[tableSize];
        for (int i = 0; i < table.length; i += 1) {
            table[i] = createBucket();
        }
        return table;
    }

    public void clear() {
        this.size = 0;
        this.buckets = createTable(16);
    }

    public boolean containsKey(K key) {
        int hash = key.hashCode();
        int index = Math.floorMod(hash, this.buckets.length);
        for (Node node : this.buckets[index]) {
            if (node.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    public V get(K key) {
        int hash = key.hashCode();
        int index = Math.floorMod(hash, this.buckets.length);
        for (Node node : this.buckets[index]) {
            if (node.key.equals(key)) {
                return node.value;
            }
        }
        return null;
    }

    public int size() {
        return this.size;
    }

    public void put(K key, V value) {
        int hash = key.hashCode();
        int index = Math.floorMod(hash, this.buckets.length);
        if (containsKey(key)) {
            remove(key);
        }
        this.buckets[index].add(new Node(key, value));
        this.size += 1;
        if ((double) this.size / this.buckets.length > this.loadFactor) {
            resize();
        }
    }

    private void resize() {
        Collection<Node>[] c = createTable(this.buckets.length * 2);
        for (Collection<Node> bucket : this.buckets) {
            for (Node node : bucket) {
                int hash = node.key.hashCode();
                int index = Math.floorMod(hash, c.length);
                c[index].add(node);
            }
        }
        this.buckets = c;
    }

    public Set<K> keySet() {
        Set<K> set = new HashSet<>();
        for (Collection<Node> bucket : this.buckets) {
            for (Node node : bucket) {
                set.add(node.key);
            }
        }
        return set;
    }

    public V remove(K key) {
        int hash = key.hashCode();
        int index = Math.floorMod(hash, this.buckets.length);
        V returnValue = null;
        Node temp = null;
        for (Node node : this.buckets[index]) {
            if (node.key.equals(key)) {
                temp = node;
                returnValue = node.value;
                size -= 1;
            }
        }
        this.buckets[index].remove(temp);
        return returnValue;
    }

    public V remove(K key, V value) {
        int hash = key.hashCode();
        int index = Math.floorMod(hash, this.buckets.length);
        V returnValue = null;
        Node temp = null;
        for (Node node : this.buckets[index]) {
            if (node.key.equals(key) && node.value.equals(value)) {
                temp = node;
                returnValue = node.value;
                this.buckets[index].remove(node);
                size -= 1;
            }
        }
        this.buckets[index].remove(temp);
        return returnValue;
    }

    public Iterator<K> iterator() {
        return keySet().iterator();
    }

}
