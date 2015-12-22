package uk.co.ticklethepanda.fitbit.caching;

import uk.co.ticklethepanda.fitbit.activity.IntradayActivity;

public interface CacheLayer<K, V> {

    V getValue(K key) throws CacheLayerException;

    void save(IntradayActivity value) throws CacheLayerException;

}
