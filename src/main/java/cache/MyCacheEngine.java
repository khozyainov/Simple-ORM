package cache;

/**
 * Created by entony on 30.12.2017.
 */
public interface MyCacheEngine<K,V> {

    void put(K key, Element<V> element);

    Element<V> get(K key);

    int getHitCount();

    int getMissCount();

    void dispose();
}
