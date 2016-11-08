package uk.co.ticklethepanda.activity.fitbit;

public interface CacheLayer<K, V> {

    V getValue(K key) throws CacheLayerException;

    void save(V value) throws CacheLayerException;

}
