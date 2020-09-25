import java.util.HashMap;
import java.util.Map;

public class LRUCache <K, V> implements ILRUCache <K, V> {
    protected final Map <K, Node> DATA = new HashMap<>();
    protected final int CAPACITY;
    protected Node head, tail;

    protected class Node {
        protected Node next, prev;
        public final K KEY;
        public final V VALUE;

        public Node(K key, V value) {
            this.KEY = key;
            this.VALUE = value;
        }
    }

    public LRUCache(int capacity) {
        if (capacity <= 0) {
            String message = "Capacity cannot be less than one.";
            throw new IllegalArgumentException(message);
        }
        this.CAPACITY = capacity;
    }

    protected void moveToHead(Node node) {
        Node next = node.next;
        Node prev = node.prev;
        if (next != null && prev != null) {
            node.next.prev = prev;
            node.prev.next = next;
        } else if (next == null && prev != null) {
            assert tail == node;
            tail = tail.prev;
            prev.next = null;
        } else if (next == null && prev == null) {
            if (head == null && tail == null) {
                head = node;
                tail = node;
                return;
            }
        } else {
            assert head == node;
            return;
        }
        assert head != null && tail != null;
        head.prev = node;
        node.next = head;
        head = node;
    }

    @Override
    public V get(final K key) {
        Node node = DATA.get(key);
        if (node == null) {
            return null;
        }

        moveToHead(node);
        assert head == node;

        return node.VALUE;
    }

    @Override
    public int getCapacity() {
        return CAPACITY;
    }

    @Override
    public int getSize() {
        return DATA.size();
    }

    @Override
    public void put(final K key, final V value) {
        if (key == null) {
            String message = "Key cannot have a null value.";
            throw new IllegalArgumentException(message);
        }

        if (DATA.containsKey(key)) {
            String message = "Such key already exists with " + DATA.get(key).VALUE + " value.";
            throw new IllegalStateException(message);
        }

        if (DATA.size() >= CAPACITY) { // DATA.size() or this.getSize()?
            assert DATA.remove(tail.KEY) != null;
            tail = tail.prev;
            assert DATA.size() < CAPACITY;
        }

        Node node = new Node(key, value);
        assert DATA.put(key, node) == null; // cache had not had such key before
        moveToHead(node);
        assert head == node;
    }
}