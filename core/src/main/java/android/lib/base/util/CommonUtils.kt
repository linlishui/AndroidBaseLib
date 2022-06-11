package android.lib.base.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.ContextThemeWrapper


/**
 *  author : linlishui
 *  time   : 2021/4/21
 *  desc   : 常用的工具类，各个模块使用频率较高的方法
 */

object CommonUtils {

    @JvmStatic
    fun <T : Activity> findActivity(context: Context): T {
        return when (context) {
            is Activity -> context as T
            is ContextThemeWrapper -> findActivity((context as ContextWrapper).baseContext)
            else -> throw IllegalArgumentException("Cannot find Activity in parent tree")
        }
    }
}