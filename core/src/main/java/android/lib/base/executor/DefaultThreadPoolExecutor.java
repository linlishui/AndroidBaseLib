package android.lib.base.executor;

import android.lib.base.util.EnvironmentUtils;
import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * author : linlishui
 * time   : 2021/12/30
 * desc   : 默认实现ThreadPoolExecutor的线程池，支持观察线程执行状况。
 */
public class DefaultThreadPoolExecutor extends ThreadPoolExecutor {

    private static final String TAG = "DefaultThreadPoolExecutor";

    /* Computation thread pool */
    public static DefaultThreadPoolExecutor newComputationThreadPool() {
        int corePoolSize = Math.max(EnvironmentUtils.CPU_COUNT, 1);
        return new DefaultThreadPoolExecutor(corePoolSize, corePoolSize,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new DefaultThreadFactory("computation_thread"));
    }

    /* Io thread pool */
    public static DefaultThreadPoolExecutor newIoThreadPool() {
        return new DefaultThreadPoolExecutor(1, Integer.MAX_VALUE,
                1L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                new DefaultThreadFactory("io_thread"));
    }

    private boolean isLoggable = true;

    public DefaultThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public void enableLog(boolean enable) {
        this.isLoggable = enable;
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        debugLog("beforeExecute # " + t.getName() + ", runnable=" + r);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (t == null) {
            debugLog("afterExecute # run completely with runnable=" + r);
        } else {
            debugLog("afterExecute # occur exception with runnable=" + r + ", throwable=" + t.toString());
        }
    }

    @Override
    protected void terminated() {
        super.terminated();
        debugLog("-----> terminated <-----");
    }

    private void debugLog(String content) {
        if (isLoggable) {
            Log.d(TAG, content);
        }
    }

}
