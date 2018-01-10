package cache;

/**
 * Created by entony on 30.12.2017.
 */
public class Element<V> {
    private final V value;
    private final long creationTime;
    private long lastAccessTime;
    private long idleTime;
    private long lifeTime;

    public Element(V value, long idleTime, long lifeTime){
        this.value = value;
        this.creationTime = getCurrentTime();
        this.lastAccessTime = getCurrentTime();
        this.idleTime = idleTime;
        this.lifeTime = lifeTime;
    }

    public V getValue(){
        return value;
    }

    long getCurrentTime() {
        return System.currentTimeMillis();
    }

    long getCreationTime() {
        return creationTime;
    }

    long getLastAccessTime() {
        return lastAccessTime;
    }

    void setAccessed() {
        this.lastAccessTime = getCurrentTime();
    }

    boolean checkLifeTime(long lifeTimeMs){
        return getCurrentTime() - creationTime >= lifeTimeMs;
    }

    boolean checkIdleTime(long idleTime, long lifeTime){
        return getCurrentTime() - idleTime >= lifeTime;
    }

    boolean checkToRemove(long idleTime, long lifeTimeMs){
        return checkIdleTime(idleTime, lifeTimeMs) || checkLifeTime(lifeTimeMs);
    }
}
