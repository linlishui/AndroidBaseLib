package android.lib.base.executor;

import java.util.Locale;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * author : linlishui
 * time   : 2021/12/30
 * desc   : 带有线程名称的线程工厂
 */
public class DefaultThreadFactory implements ThreadFactory {

    private final String namePrefix;

    public DefaultThreadFactory(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    private final AtomicInteger mThreadId = new AtomicInteger(0);

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        t.setName(String.format(Locale.US, namePrefix + "_%d", mThreadId.getAndIncrement()));
        return t;
    }
}
