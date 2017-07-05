package uk.co.ticklethepanda.health.activity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SelfDescriptiveMap<K, V extends SelfDescriptiveMap.Describing<K>> {

    public interface Describing<E> {
        E getDescriptor();
    }

    private final Map<K, V> map;

    public SelfDescriptiveMap() {
        this.map = new HashMap<>();
    }

    public void put(V value) {
        map.put(value.getDescriptor(), value);
    }

    public V get(K key) {
        return map.get(key);
    }

    public Set<Map.Entry<K, V>> entrySet() {
        return map.entrySet();
    }
}
