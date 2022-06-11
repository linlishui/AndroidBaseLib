package android.lib.base

import android.view.View
import androidx.appcompat.app.AppCompatActivity

/**
 * author : linlishui
 * time   : 2021/08/18
 * desc   : 基础的Activity类
 */
open class BaseActivity : AppCompatActivity() {

    val decorView: View
        get() = window.decorView
}