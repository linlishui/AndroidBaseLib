package android.lib.base.executor;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;

/**
 * author : linlishui
 * time   : 2021/12/30
 * desc   : 默认实现TaskExecutor类的任务执行器
 */
final class DefaultTaskExecutor extends TaskExecutor {

    private final Object mLock = new Object();

    private final ExecutorService mDiskIO = DefaultThreadPoolExecutor.newIoThreadPool();

    private static final DefaultThreadPoolExecutor mComputation = DefaultThreadPoolExecutor.newComputationThreadPool();

    @Nullable
    private volatile Handler mMainHandler;

    @Override
    public void executeOnDiskIO(@NonNull Runnable runnable) {
        mDiskIO.execute(runnable);
    }

    @Override
    public void executeOnComputation(@NonNull Runnable runnable) {
        mComputation.execute(runnable);
    }

    @Override
    public boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    @Override
    public Handler getMainHandler() {
        if (mMainHandler == null) {
            synchronized (mLock) {
                if (mMainHandler == null) {
                    mMainHandler = createAsync(Looper.getMainLooper());
                }
            }
        }
        return mMainHandler;
    }

    private static Handler createAsync(@NonNull Looper looper) {
        if (Build.VERSION.SDK_INT >= 28) {
            return Handler.createAsync(looper);
        }
        if (Build.VERSION.SDK_INT >= 16) {
            try {
                return Handler.class.getDeclaredConstructor(Looper.class, Handler.Callback.class, boolean.class)
                        .newInstance(looper, null, true);
            } catch (IllegalAccessException | InstantiationException | NoSuchMethodException ignored) {
                // no-op
            } catch (InvocationTargetException e) {
                return new Handler(looper);
            }
        }
        return new Handler(looper);
    }
}
