package android.lib.base.util;

import android.lib.base.executor.GlobalTaskExecutor;
import android.os.Handler;

import androidx.annotation.NonNull;

public class ThreadUtils {

    /**
     * Returns true if the current thread is the UI thread.
     */
    public static boolean isMainThread() {
        return GlobalTaskExecutor.get().isMainThread();
    }

    /**
     * Returns a shared UI thread handler.
     */
    public static Handler getUiThreadHandler() {
        return GlobalTaskExecutor.get().getMainHandler();
    }

    /**
     * Checks that the current thread is the UI thread. Otherwise throws an exception.
     */
    public static void ensureMainThread() {
        if (!isMainThread()) {
            throw new RuntimeException("Must be called on the UI thread");
        }
    }

    public static void postOnMainThread(Runnable runnable) {
        getUiThreadHandler().post(runnable);
    }

    public static void executeOnMainThread(@NonNull Runnable runnable) {
        GlobalTaskExecutor.get().executeOnMainThread(runnable);
    }

    public static void executeOnDiskIO(@NonNull Runnable runnable) {
        GlobalTaskExecutor.get().executeOnDiskIO(runnable);
    }

    public static void executeOnComputation(@NonNull Runnable runnable) {
        GlobalTaskExecutor.get().executeOnComputation(runnable);
    }
}
