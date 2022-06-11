package android.lib.base.executor;

import android.os.Handler;

import androidx.annotation.NonNull;

import java.util.concurrent.RejectedExecutionException;

/**
 * author : linlishui
 * time   : 2021/12/30
 * desc   : 任务执行器
 */
public abstract class TaskExecutor {

    /**
     * Executes the given task in the disk IO thread pool.
     *
     * @param runnable The runnable to run in the disk IO thread pool.
     */
    public abstract void executeOnDiskIO(@NonNull Runnable runnable);

    /**
     * Executes the given task in the Computation thread pool.
     *
     * @param runnable The runnable to run in the Computation thread pool.
     */
    public abstract void executeOnComputation(@NonNull Runnable runnable);

    /**
     * Returns true if the current thread is the main thread, false otherwise.
     *
     * @return true if we are on the main thread, false otherwise.
     */
    public abstract boolean isMainThread();

    /**
     *
     * @return a handler object in main thread
     */
    public abstract Handler getMainHandler();

    /**
     * Posts the given task to the main thread.
     *
     * @param runnable The runnable to run on the main thread.
     */
    public void postToMainThread(@NonNull Runnable runnable) {
        if (!getMainHandler().post(runnable)) {
            throw new RejectedExecutionException(getMainHandler() + " is shutting down");
        }
    }

    /**
     * Executes the given task on the main thread.
     * <p>
     * If the current thread is a main thread, immediately runs the given runnable.
     *
     * @param runnable The runnable to run on the main thread.
     */
    public void executeOnMainThread(@NonNull Runnable runnable) {
        if (isMainThread()) {
            runnable.run();
        } else {
            postToMainThread(runnable);
        }
    }
}
