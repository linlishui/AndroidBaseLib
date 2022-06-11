package android.lib.base.executor;

import androidx.annotation.NonNull;

import java.util.ArrayDeque;
import java.util.concurrent.Executor;

/**
 * author : linlishui
 * time   : 2021/12/30
 * desc   : 顺序执行的执行器
 */
public class SerialExecutor implements Executor {

    private final Executor executor;

    private Runnable mActive;
    private final ArrayDeque<Runnable> mTasks = new ArrayDeque<>();

    public SerialExecutor(@NonNull Executor executor) {
        this.executor = executor;
    }

    public synchronized void execute(final Runnable r) {
        mTasks.offer(new Runnable() {
            public void run() {
                try {
                    r.run();
                } finally {
                    scheduleNext();
                }
            }
        });
        if (mActive == null) {
            scheduleNext();
        }
    }

    protected synchronized void scheduleNext() {
        if ((mActive = mTasks.poll()) != null) {
            executor.execute(mActive);
        }
    }
}