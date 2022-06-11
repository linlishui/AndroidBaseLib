package android.lib.base.executor;

import android.os.Handler;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

/**
 * author : linlishui
 * time   : 2021/12/30
 * desc   : An adapter {@link Executor} that posts all executed tasks onto the given
 */

public class HandlerExecutor implements Executor {

    private final Handler mHandler;

    public HandlerExecutor(@NonNull Handler handler) {
        this.mHandler = handler;
    }

    @Override
    public void execute(Runnable command) {
        if (!mHandler.post(command)) {
            throw new RejectedExecutionException(mHandler + " is shutting down");
        }
    }
}
