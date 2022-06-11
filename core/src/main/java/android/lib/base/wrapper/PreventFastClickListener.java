package android.lib.base.wrapper;

import android.view.View;

/**
 * author : linlishui
 * time   : 2021/12/30
 * desc   : 防止快速点击，可设置点击间隔时间阀值
 */
public abstract class PreventFastClickListener implements View.OnClickListener {

    private long lastClickTime = -1;

    private int clickInterval = 500;

    public PreventFastClickListener() {
    }

    public PreventFastClickListener(int clickInterval) {
        this.clickInterval = Math.max(clickInterval, 0);
    }

    @Override
    public void onClick(View v) {
        final long currentClickTime = System.currentTimeMillis();
        if (currentClickTime - lastClickTime > clickInterval) {
            onItemClick(v);
            lastClickTime = currentClickTime;
        }
    }

    public abstract void onItemClick(View v);
}
