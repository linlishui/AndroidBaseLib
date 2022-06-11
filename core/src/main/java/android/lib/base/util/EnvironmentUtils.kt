package android.lib.base.util

import android.animation.ValueAnimator
import android.app.ActivityManager
import android.content.Context
import android.lib.base.log.LogUtils
import android.os.Build
import android.os.PowerManager
import android.os.UserManager
import android.provider.Settings
import java.util.*


/**
 *  author : linlishui
 *  time   : 2022/3/14
 *  desc   : 环境检测相关的工具类
 */
object EnvironmentUtils {

    private val TAG = "EnvironmentUtils"

    @JvmField
    val IS_VM_MULTIDEX_CAPABLE: Boolean = isVMMultidexCapable(System.getProperty("java.vm.version"))

    @JvmField
    val CPU_COUNT: Int = Runtime.getRuntime().availableProcessors()

    /**
     * Indicates if the device has a debug build. Should only be used to store additional info or
     * add extra logging and not for changing the app behavior.
     */
    @JvmField
    val IS_DEBUG_DEVICE = Build.TYPE.lowercase(Locale.ROOT).contains("debug") || Build.TYPE.lowercase(Locale.ROOT) == "eng"

    private var sIsAtLeastN = false
    private var sIsAtLeastO = false
    private var sIsAtLeastP = false
    private var sIsAtLeastQ = false
    private var sIsAtLeastR = false
    private var sIsAtLeastS = false

    init {
        val v = getApiVersion()
        sIsAtLeastN = v >= Build.VERSION_CODES.N
        sIsAtLeastO = v >= Build.VERSION_CODES.O
        sIsAtLeastP = v >= Build.VERSION_CODES.P
        sIsAtLeastQ = v >= Build.VERSION_CODES.Q
        sIsAtLeastR = v >= Build.VERSION_CODES.R
        sIsAtLeastS = v >= Build.VERSION_CODES.S

    }

    @JvmStatic
    fun getApiVersion(): Int {
        return Build.VERSION.SDK_INT
    }

    @JvmStatic
    fun isAtLeastN(): Boolean {
        return sIsAtLeastN
    }

    @JvmStatic
    fun isAtLeastO(): Boolean {
        return sIsAtLeastO
    }

    @JvmStatic
    fun isAtLeastP(): Boolean {
        return sIsAtLeastP
    }

    @JvmStatic
    fun isAtLeastQ(): Boolean {
        return sIsAtLeastQ
    }

    @JvmStatic
    fun isAtLeastR(): Boolean {
        return sIsAtLeastR
    }

    @JvmStatic
    fun isAtLeastS(): Boolean {
        return sIsAtLeastS
    }

    private fun isVMMultidexCapable(versionString: String?): Boolean {
        var isMultidexCapable = false
        if (versionString != null) {
            val tokenizer = StringTokenizer(versionString, ".")
            val majorToken = if (tokenizer.hasMoreTokens()) tokenizer.nextToken() else null
            val minorToken = if (tokenizer.hasMoreTokens()) tokenizer.nextToken() else null
            if (majorToken != null && minorToken != null) {
                try {
                    val major = majorToken.toInt()
                    val minor = minorToken.toInt()
                    isMultidexCapable = major > 2 || major == 2 && minor >= 1
                } catch (var7: NumberFormatException) {
                    // no-op
                }
            }
        }
        ThreadUtils.ensureMainThread()
        LogUtils.i(
            TAG,
            "VM with version " + versionString + if (isMultidexCapable) " has multidex support" else " does not have multidex support"
        )
        return isMultidexCapable
    }

    @JvmStatic
    fun areAnimationsEnabled(context: Context): Boolean {
        return if (isAtLeastO()) ValueAnimator.areAnimatorsEnabled() else !context.getSystemService(PowerManager::class.java).isPowerSaveMode
    }

    /**
     * Returns true if Monkey is running.
     */
    @JvmStatic
    fun isMonkeyRunning(): Boolean {
        return ActivityManager.isUserAMonkey()
    }

    @JvmStatic
    fun isBootCompleted(): Boolean {
        return "1" == Utilities.getSystemProperty("sys.boot_completed", "1")
    }

    @JvmStatic
    fun isDevelopersOptionsEnabled(context: Context): Boolean {
        val um = context.getSystemService(Context.USER_SERVICE) as UserManager
        val settingEnabled = Settings.Global.getInt(
            context.contentResolver,
            Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
            if (Build.TYPE == "eng") 1 else 0
        ) != 0
        val hasRestriction = um.hasUserRestriction(
            UserManager.DISALLOW_DEBUGGING_FEATURES
        )
        // final boolean isAdmin = um.isAdminUser();
        //return isAdmin && !hasRestriction && settingEnabled;
        return !hasRestriction && settingEnabled
    }
}