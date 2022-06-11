package android.lib.base.executor;

import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.Executor;

/**
 * author : linlishui
 * time   : 2021/12/30
 * desc   : 全局的任务执行器，暴露给外部使用，单例类。
 */
public final class GlobalTaskExecutor extends TaskExecutor {

    private static volatile GlobalTaskExecutor sInstance;

    @NonNull
    private TaskExecutor mDelegate;
    @NonNull
    private final TaskExecutor mDefaultTaskExecutor = new DefaultTaskExecutor();

    @NonNull
    private static final Executor sMainThreadExecutor = command -> GlobalTaskExecutor.get().postToMainThread(command);
    @NonNull
    private static final Executor sIOThreadExecutor = command -> GlobalTaskExecutor.get().executeOnDiskIO(command);
    @NonNull
    private static final Executor sComputationExecutor = command -> GlobalTaskExecutor.get().executeOnComputation(command);

    private GlobalTaskExecutor() {
        this.mDelegate = this.mDefaultTaskExecutor;
    }

    @NonNull
    public synchronized static GlobalTaskExecutor get() {

        if (sInstance == null) {
            synchronized (GlobalTaskExecutor.class) {
                if (sInstance == null) {
                    sInstance = new GlobalTaskExecutor();
                }
            }
        }
        return sInstance;
    }

    public void setDelegate(@Nullable TaskExecutor taskExecutor) {
        this.mDelegate = taskExecutor == null ? this.mDefaultTaskExecutor : taskExecutor;
    }

    @Override
    public void executeOnDiskIO(@NonNull Runnable runnable) {
        this.mDelegate.executeOnDiskIO(runnable);
    }

    @Override
    public void executeOnComputation(@NonNull Runnable runnable) {
        this.mDelegate.executeOnComputation(runnable);
    }

    @Override
    public boolean isMainThread() {
        return this.mDelegate.isMainThread();
    }

    @Override
    public Handler getMainHandler() {
        return this.mDelegate.getMainHandler();
    }

    @NonNull
    public static Executor getMainThreadExecutor() {
        return sMainThreadExecutor;
    }

    @NonNull
    public static Executor getIOThreadExecutor() {
        return sIOThreadExecutor;
    }

    @NonNull
    public static Executor getComputationThreadExecutor() {
        return sComputationExecutor;
    }
}
