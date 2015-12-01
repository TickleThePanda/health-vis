package uk.co.ticklethepanda.fitbit.dao.cache;

import uk.co.ticklethepanda.fitbit.ActivityForDate;

public interface CacheLayer<K, V> {

    V getValue(K key) throws CacheLayerException;

    void save(ActivityForDate value) throws CacheLayerException;

}
