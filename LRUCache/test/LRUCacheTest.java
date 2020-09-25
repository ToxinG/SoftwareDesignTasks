import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class LRUCacheTest {
    private final Random random = new Random();

    private static <K, V> ILRUCache <K, V> getInstance(int capacity) {
        return new LRUCache<>(capacity);
    }

    @Nested
    @DisplayName("Initialization test")
    public class InitializationTest {

        @Test
        @DisplayName("Test cache on a valid constructor argument")
        public void testSuccessfulInit() {
            assertNotNull(getInstance(16));
        }

        @Test
        @DisplayName("Test cache on an invalid constructor argument")
        public void testUnsuccessfulInit() {
            try {
                @SuppressWarnings("unused")
                ILRUCache <Integer, String> cache = getInstance(0);
                cache = getInstance(-1);
            } catch (Exception | AssertionError e) {
                return;
            }

            fail("Created an instance whose capacity is less than one");
        }

        @Test
        @DisplayName("Test cache on a valid capacity value")
        public void testCapacity() {
            int capacity = random.nextInt(1024) + 1;
            ILRUCache <Integer, String> cache = getInstance(capacity);
            assertEquals(cache.getCapacity(), capacity);
        }
    }

    @Nested
    @DisplayName("Insertion test")
    public class InsertionTest {

        @Test
        @DisplayName("Test cache on a single insertion")
        public void testSingleInsertion() {
            ILRUCache <Integer, String> cache = getInstance(128);
            cache.put(8, "sample text");
            assertEquals(1, cache.getSize());
        }

        @Test
        @DisplayName("Test cache on a null value insertion")
        public void testNullValueInsertion() {
            ILRUCache <Integer, String> cache = getInstance(128);
            cache.put(8, null);
            assertEquals(1, cache.getSize());
        }

        @Test
        @DisplayName("Test cache on a null key insertion")
        public void testNullKeyInsertion() {
            ILRUCache <Integer, String> cache = getInstance(128);
            try {
                cache.put(null, "sample text");
            } catch (Exception | AssertionError e) {
                return;
            }

            fail("Inserted a value with a null key");
        }

        @RepeatedTest(8)
        @DisplayName("Test cache on multiple random insertions")
        public void testMultipleInsertions() {
            int capacity = random.nextInt(8092) + 2;
            int size = random.nextInt(capacity / 2) + 1;
            ILRUCache <Integer, String> cache = getInstance(capacity);
            for (int i = 0; i < size; i++) {
                int val = random.nextInt(8092);
                cache.put(i, "" + val);
            }

            assertEquals(size, cache.getSize());
        }

        @RepeatedTest(8)
        @DisplayName("Test cache on exceeding the capacity amount of insertions")
        public void testExceedingCapacityInsertions() {
            int capacity = random.nextInt(8092) + 2;
            int size = capacity + random.nextInt(capacity / 2) + 1;
            ILRUCache <Integer, String> cache = getInstance(capacity);
            for (int i = 0; i < size; i++) {
                int val = random.nextInt(4096);
                cache.put(i, "" + val);
            }

            assertEquals(capacity, cache.getSize());
        }

        @RepeatedTest(8)
        @DisplayName("Test cache on repeating keys insertions")
        public void testRepeatedKeysInsertions() {
            ILRUCache <Integer, String> cache = getInstance(1024);
            int size = 128;
            Set<Integer> keys = new HashSet<>();
            for (int i = 0; i < size; i++) {
                int key = random.nextInt(64);
                if (keys.contains(key)) {
                    try {
                        cache.put(key, "" + key);
                    } catch (Exception | AssertionError e) {
                        continue;
                    }

                    fail("Inserted value with a key that already exists in the cache");
                } else {
                    keys.add(key);
                    cache.put(key, "" + key);
                    assertEquals(keys.size(), cache.getSize());
                }
            }
        }
    }

    @Nested
    @DisplayName("General functionality test")
    public class GeneralTest {

        @Test
        @DisplayName("Test getting from an empty cache")
        public void testGetFromEmpty() {
            ILRUCache <Integer, String> cache = getInstance(128);
            assertNull(cache.get(random.nextInt(256)));
        }

        @RepeatedTest(8)
        @DisplayName("Test getting after a single insertion")
        public void testPutAndGet() {
            ILRUCache <Integer, String> cache = getInstance(128);
            int key = random.nextInt(1024);
            String value = "" + key * 4;
            cache.put(key, value);
            String answer = cache.get(key);
            assertEquals(value, answer);
        }

        @RepeatedTest(8)
        @DisplayName("Test getting on correctness")
        public void testGettingCorrectness() {
            int capacity = 8092;
            ILRUCache <Integer, String> cache = getInstance(capacity);
            int size = capacity + random.nextInt(1024) + 8;
            for (int i = 0; i < size; i++) {
                cache.put(i, "put " + i);
            }

            int missed = 0;
            for (int i = size - 1; i >= 0; i--) {
                missed += (cache.get(i) == null ? 1 : 0);
            }

            assert missed == size - capacity;
        }

        @RepeatedTest(8)
        @DisplayName("Test getting on tail requests")
        public void testGettingFromTail() {
            int capacity = 8092;
            ILRUCache <Integer, String> cache = getInstance(capacity);
            cache.put(0, "put " + 0);
            for (int i = 1; i < capacity; i++) {
                cache.put(i, "put " + i);
                assert cache.get(i - 1) != null;
            }
            cache.put(-1, "another one");
            assertNull(cache.get(0));
        }
    }
}
