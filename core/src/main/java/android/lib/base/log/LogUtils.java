package android.lib.base.log;

import android.text.TextUtils;
import android.util.Log;

/**
 *  author : linlishui
 *  time   : 2020/9/30
 *  desc   : 日志工具类
 */
public class LogUtils {

    private static String DEFAULT_TAG = "AndroidBaseLib";

    public static void setDefaultTag(String tag) {
        if (!TextUtils.isEmpty(tag)) {
            DEFAULT_TAG = tag;
        }
    }

    public static void d(final String msg) {
        println(Log.DEBUG, "", msg);
    }

    public static void d(final String tag, final String msg) {
        println(Log.DEBUG, tag, msg);
    }

    public static void i(final String tag, final String msg) {
        println(Log.INFO, tag, msg);
    }

    public static void w(final String tag, final String msg) {
        println(Log.WARN, tag, msg);
    }

    public static void w(final String tag, final String msg, final Throwable tr) {
        println(Log.WARN, tag, msg);
        println(Log.WARN, tag, Log.getStackTraceString(tr));
    }

    public static void e(final String tag, final String msg) {
        println(Log.ERROR, tag, msg);
    }

    public static void e(final String tag, final String msg, final Throwable tr) {
        println(Log.ERROR, tag, msg);
        println(Log.ERROR, tag, Log.getStackTraceString(tr));
    }

    public static void logThreadName(String tag) {
        println(Log.DEBUG, tag, "Current Thread Name=" + Thread.currentThread().getName());
    }

    public static void showStackTrace() {
        d(Log.getStackTraceString(new Throwable()));
    }

    /**
     * Low-level logging call.
     *
     * @param level The priority/type of this log message
     * @param tag   Used to identify the source of a log message.  It usually identifies
     *              the class or activity where the log call occurs.
     * @param msg   The message you would like logged.
     */
    private static void println(final int level, final String tag, final String msg) {
        if (TextUtils.isEmpty(tag)) {
            Log.println(level, DEFAULT_TAG, msg);
        } else {
            Log.println(level, DEFAULT_TAG, tag + " # " + msg);
        }

        /*if (level >= android.util.Log.DEBUG) {
            // to do something
        }*/
    }

    /**
     * Checks to see whether or not a log for the specified tag is loggable at the specified level.
     * See {@link Log#isLoggable(String, int)} for more discussion.
     */
    public static boolean isLoggable(final String tag, final int level) {
        return Log.isLoggable(tag, level);
    }
}
