package android.lib.base.wrapper;

/**
 * author : linlishui
 * time   : 2021/12/30
 * desc   : Singleton helper class for lazily initialization.
 */

public abstract class Singleton<T> {

    public Singleton() {}

    private T mInstance;

    protected abstract T create();

    public final T get() {
        synchronized (this) {
            if (mInstance == null) {
                mInstance = create();
            }
            return mInstance;
        }
    }
}