package uk.co.ticklethepanda.health.activity.domain.data;

import java.time.temporal.TemporalAccessor;
import java.util.Map;

public class TemporalEntry<K extends Enum<K> & TemporalAccessor, V> implements Map.Entry<K, V> {

    private final K key;
    private V value;

    public TemporalEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public V setValue(V value) {
        this.value = value;
        return value;
    }
}
