package android.lib.base.component

import android.app.Application
import android.lib.base.log.LogUtils

/**
 *  author : linlishui
 *  time   : 2021/8/18
 *  desc   : 启动组件标识，可用于初始化等操作
 */
interface StartupComponent {

    fun init(app: Application) {
        LogUtils.d("init component --> $this")
    }

    fun isSync() = true
}