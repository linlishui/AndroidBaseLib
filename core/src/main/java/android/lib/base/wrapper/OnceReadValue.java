package android.lib.base.wrapper;

/**
 * author : linlishui
 * time   : 2022/6/11
 * desc   : 读取时进行变量初始化
 */
public abstract class OnceReadValue<P, T> {
    private volatile boolean isRead = false;
    private T cacheValue;

    public T get(P param) {
        if (!this.isRead) {
            synchronized (this) {
                if (!this.isRead) {
                    this.cacheValue = this.read(param);
                    this.isRead = true;
                }
            }

        }
        return this.cacheValue;
    }

    protected abstract T read(P param);
}
