public interface ILRUCache <K, V> {

    /**
     * Returns the value to which the specified key is mapped
     * or <b>null</b> if the cache contains no mapping for the key.
     *
     * <pre>
     *     Contract:
     *
     *     value get (key)
     *
     *     pre:
     *     1. key != null
     *     2. size <= capacity
     *
     *     post:
     *     1. value = null | &lt;key, value&gt;.second
     *     2. (?value != null) cache'[0] = &lt;key, value&gt;
     *     3. size' = size
     * </pre>
     *
     * @param key - the key whose associated value is to be returned
     *
     * @return the value to which the specified key is mapped,
     * or null if this map contains no mapping for the key
     *
     * @see #put(Object, Object) put
     */
    public V get(K key);

    /**
     * Returns constant maximum number of elements that can be stored in the cache.
     *
     * @return capacity of the cache
     */
    public int getCapacity();

    /**
     * Returns current number of elements stored in the cache which cannot be decreased.
     * @return size of the cache
     */
    public int getSize();

    /**
     * Adds a new key-value pair to the {@link ILRUCache}.
     * If the number of stored pairs is more than {@link #getCapacity() capacity}
     * of the cache then the oldest pair will be removed from cache.
     *
     * <pre>
     *     Contract:
     *
     *     put (key, value)
     *
     *     pre:
     *     1. key != null
     *     2. size <= capacity
     *     3. !cache.containsKey(key)
     *
     *     post:
     *     1. cache'[0] == &lt;key, value&gt;
     *     2. size' <= capacity
     * </pre>
     *
     * @param key - key with which the specified value is to be associated
     * @param value - value to be associated with the specified key
     *
     * @throws IllegalArgumentException if the key is a <b>null</b> value
     * or if such key already exists in the cache.
     *
     * @see #getCapacity() getCapacity
     */
    public void put(K key, V value);
}
