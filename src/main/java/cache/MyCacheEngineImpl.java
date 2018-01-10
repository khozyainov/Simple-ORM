package cache;

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Created by entony on 30.12.2017.
 */
public class MyCacheEngineImpl<K,V> implements MyCacheEngine<K,V>{

    private static final int TIME_THRESHOLD_MS = 5;
    private static final int DEFAULT_CACHE_LIFE_TIME_MS = 2_000;
    private static final int DEFAULT_CACHE_IDLE_TIME_MS = 1_000;
    private static final int DEFAULT_CACHE_SIZE = 250;
    private static final int DEFAULT_CACHE_TIMER_PERIOD = 3_000;

    private final int maxElements;
    private final long lifeTimeMs;
    private final long idleTimeMs;
    private final boolean isEternal;

    private final Map<K, SoftReference<Element<V>>> cache = new ConcurrentHashMap<K, SoftReference<Element<V>>>();
    private final Timer timer = new Timer();

    private int hit = 0;
    private int miss = 0;

    public MyCacheEngineImpl(int maxElements, long lifeTimeMs, long idleTimeMs, boolean isEternal){
        this.maxElements = maxElements > 0 ? maxElements : DEFAULT_CACHE_SIZE;
        this.lifeTimeMs = lifeTimeMs > 0 ? lifeTimeMs : DEFAULT_CACHE_LIFE_TIME_MS;
        this.idleTimeMs = idleTimeMs > 0 ? idleTimeMs : DEFAULT_CACHE_IDLE_TIME_MS;
        this.isEternal = isEternal;

        startCleaningThread();
    }

    private void startCleaningThread() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkToRemove(idleTimeMs, lifeTimeMs);
            }
        }, TIME_THRESHOLD_MS, DEFAULT_CACHE_TIMER_PERIOD);
    }

    public  void stopCleaningThread(){
        timer.cancel();
    }


    private void checkToRemove(long idleTimeMs, long lifeTimeMs){
        cache.entrySet().removeIf(entry -> entry.getValue().get() != null
        && entry.getValue().get().checkToRemove(idleTimeMs, lifeTimeMs));
    }

    public void put(K key, Element<V> element) {
        if (cache.size() == maxElements){
            K firstKey = cache.keySet().iterator().next();
            cache.remove(firstKey);
        }

        if (!isEternal){
            if (lifeTimeMs != 0){
                TimerTask lifeTimerTask = getTimerTask(key, lifeElement -> lifeElement.getCreationTime() + lifeTimeMs);
                timer.schedule(lifeTimerTask, lifeTimeMs);
            }
            if(idleTimeMs != 0){
                TimerTask idleTimerTask = getTimerTask(key, idleElement -> idleElement.getLastAccessTime() + idleTimeMs);
                timer.schedule(idleTimerTask, idleTimeMs);
            }
        }

        cache.put(key, new SoftReference<Element<V>>(element));
    }

    private TimerTask getTimerTask(final K key, Function<Element<V>, Long> timeFunction){
        return new TimerTask() {
            @Override
            public void run() {
                Element<V> checkElement = cache.get(key).get();
                if (checkElement == null || isT1BeforeT2(timeFunction.apply(checkElement), checkElement.getCurrentTime())){
                    cache.remove(key);
                }
            }
        };
    }

    private boolean isT1BeforeT2(long t1, long t2){
        return t1 < t2 + TIME_THRESHOLD_MS;
    }

    public Element<V> get(K key) {
        SoftReference<Element<V>> softReference = cache.get(key);
        Element<V> element = (softReference == null ? null : softReference.get());
        if (element != null){
            hit++;
            element.setAccessed();
        }else {
            miss++;
        }
        return element;
    }

    public void printMap(){
        for (Map.Entry<K, SoftReference<Element<V>>> entry:cache.entrySet()){
            System.out.println(entry.getKey() + " " + entry.getValue().get());
        }
    }

    public void clear(){
        hit = 0;
        miss = 0;
        cache.clear();
    }

    public long getLifeTimeMs(){
        return lifeTimeMs;
    }

    public long getIdleTimeMs(){
        return idleTimeMs;
    }

    public int getCacheSize(){
        return cache.size();
    }

    public int getHitCount() {
        return hit;
    }

    public int getMissCount() {
        return miss;
    }

    public void dispose() {
        timer.cancel();
    }
}
