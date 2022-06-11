package android.lib.base.util;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.icu.text.NumberFormat;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.Message;
import android.os.TransactionTooLargeException;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Interpolator;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Various utilities shared amongst the App's classes.
 */
public final class Utilities {

    private static final String TAG = "Utilities";

    private static final Pattern sTrimPattern =
            Pattern.compile("^[\\s|\\p{javaSpaceChar}]*(.*)[\\s|\\p{javaSpaceChar}]*$");

    private static final int[] sLoc0 = new int[2];
    private static final int[] sLoc1 = new int[2];

    /**
     * Set on a motion event do disallow any gestures and only handle touch.
     * See {@link MotionEvent#setEdgeFlags(int)}.
     */
    public static final int FLAG_NO_GESTURES = 1 << 9;

    public static boolean shouldDisableGestures(MotionEvent ev) {
        return (ev.getEdgeFlags() & FLAG_NO_GESTURES) == FLAG_NO_GESTURES;
    }

    /**
     * Sets {@param out} to be same as {@param in} by rounding individual values
     */
    public static void roundArray(float[] in, int[] out) {
        for (int i = 0; i < in.length; i++) {
            out[i] = Math.round(in[i]);
        }
    }

    /**
     * Formats a double from 0.0..100.0 with an option to round
     **/
    public static String formatPercentage(double percentage, boolean round) {
        final int localPercentage = round ? Math.round((float) percentage) : (int) percentage;
        return formatPercentage(localPercentage);
    }

    /**
     * Formats the ratio of amount/total as a percentage.
     */
    public static String formatPercentage(long amount, long total) {
        return formatPercentage(((double) amount) / total);
    }

    /**
     * Formats an integer from 0..100 as a percentage.
     */
    public static String formatPercentage(int percentage) {
        return formatPercentage(((double) percentage) / 100.0);
    }

    /**
     * Formats a double from 0.0..1.0 as a percentage.
     */
    public static String formatPercentage(double percentage) {
        return NumberFormat.getPercentInstance().format(percentage);
    }

    public static void offsetPoints(float[] points, float offsetX, float offsetY) {
        for (int i = 0; i < points.length; i += 2) {
            points[i] += offsetX;
            points[i + 1] += offsetY;
        }
    }

    /**
     * Utility method to determine whether the given point, in local coordinates,
     * is inside the view, where the area of the view is expanded by the slop factor.
     * This method is called while processing touch-move events to determine if the event
     * is still within the view.
     */
    public static boolean pointInView(View v, float localX, float localY, float slop) {
        return localX >= -slop && localY >= -slop && localX < (v.getWidth() + slop) &&
                localY < (v.getHeight() + slop);
    }

    public static int[] getCenterDeltaInScreenSpace(View v0, View v1) {
        v0.getLocationInWindow(sLoc0);
        v1.getLocationInWindow(sLoc1);

        sLoc0[0] += (v0.getMeasuredWidth() * v0.getScaleX()) / 2;
        sLoc0[1] += (v0.getMeasuredHeight() * v0.getScaleY()) / 2;
        sLoc1[0] += (v1.getMeasuredWidth() * v1.getScaleX()) / 2;
        sLoc1[1] += (v1.getMeasuredHeight() * v1.getScaleY()) / 2;
        return new int[]{sLoc1[0] - sLoc0[0], sLoc1[1] - sLoc0[1]};
    }

    public static void scaleRectFAboutCenter(RectF r, float scale) {
        if (scale != 1.0f) {
            float cx = r.centerX();
            float cy = r.centerY();
            r.offset(-cx, -cy);
            r.left = r.left * scale;
            r.top = r.top * scale;
            r.right = r.right * scale;
            r.bottom = r.bottom * scale;
            r.offset(cx, cy);
        }
    }

    public static void scaleRectAboutCenter(Rect r, float scale) {
        if (scale != 1.0f) {
            int cx = r.centerX();
            int cy = r.centerY();
            r.offset(-cx, -cy);
            scaleRect(r, scale);
            r.offset(cx, cy);
        }
    }

    public static void scaleRect(Rect r, float scale) {
        if (scale != 1.0f) {
            r.left = (int) (r.left * scale + 0.5f);
            r.top = (int) (r.top * scale + 0.5f);
            r.right = (int) (r.right * scale + 0.5f);
            r.bottom = (int) (r.bottom * scale + 0.5f);
        }
    }

    public static void insetRect(Rect r, Rect insets) {
        r.left = Math.min(r.right, r.left + insets.left);
        r.top = Math.min(r.bottom, r.top + insets.top);
        r.right = Math.max(r.left, r.right - insets.right);
        r.bottom = Math.max(r.top, r.bottom - insets.bottom);
    }

    /**
     * Maps t from one range to another range.
     *
     * @param t       The value to map.
     * @param fromMin The lower bound of the range that t is being mapped from.
     * @param fromMax The upper bound of the range that t is being mapped from.
     * @param toMin   The lower bound of the range that t is being mapped to.
     * @param toMax   The upper bound of the range that t is being mapped to.
     * @return The mapped value of t.
     */
    public static float mapToRange(float t, float fromMin, float fromMax, float toMin, float toMax,
                                   Interpolator interpolator) {
        if (fromMin == fromMax || toMin == toMax) {
            Log.e(TAG, "mapToRange: range has 0 length");
            return toMin;
        }
        float progress = getProgress(t, fromMin, fromMax);
        return mapRange(interpolator.getInterpolation(progress), toMin, toMax);
    }

    public static float getProgress(float current, float min, float max) {
        return Math.abs(current - min) / Math.abs(max - min);
    }

    public static float mapRange(float value, float min, float max) {
        return min + (value * (max - min));
    }

    public static boolean isSystemApp(Context context, Intent intent) {
        PackageManager pm = context.getPackageManager();
        ComponentName cn = intent.getComponent();
        String packageName = null;
        if (cn == null) {
            ResolveInfo info = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if ((info != null) && (info.activityInfo != null)) {
                packageName = info.activityInfo.packageName;
            }
        } else {
            packageName = cn.getPackageName();
        }
        if (packageName != null) {
            try {
                PackageInfo info = pm.getPackageInfo(packageName, 0);
                return (info != null) && (info.applicationInfo != null) &&
                        ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
            } catch (NameNotFoundException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    /*
     * Finds a system apk which had a broadcast receiver listening to a particular action.
     * @param action intent action used to find the apk
     * @return a pair of apk package name and the resources.
     */
    static Pair<String, Resources> findSystemApk(String action, PackageManager pm) {
        final Intent intent = new Intent(action);
        for (ResolveInfo info : pm.queryBroadcastReceivers(intent, 0)) {
            if (info.activityInfo != null &&
                    (info.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                final String packageName = info.activityInfo.packageName;
                try {
                    final Resources res = pm.getResourcesForApplication(packageName);
                    return Pair.create(packageName, res);
                } catch (NameNotFoundException e) {
                    Log.w(TAG, "Failed to find resources for " + packageName);
                }
            }
        }
        return null;
    }

    /**
     * Trims the string, removing all whitespace at the beginning and end of the string.
     * Non-breaking whitespaces are also removed.
     */
    public static String trim(CharSequence s) {
        if (s == null) {
            return null;
        }

        // Just strip any sequence of whitespace or java space characters from the beginning and end
        Matcher m = sTrimPattern.matcher(s);
        return m.replaceAll("$1");
    }

    /**
     * Calculates the height of a given string at a specific text size.
     */
    public static int calculateTextHeight(float textSizePx) {
        Paint p = new Paint();
        p.setTextSize(textSizePx);
        Paint.FontMetrics fm = p.getFontMetrics();
        return (int) Math.ceil(fm.bottom - fm.top);
    }

    /**
     * Convenience println with multiple args.
     */
    public static void println(String key, Object... args) {
        StringBuilder b = new StringBuilder();
        b.append(key);
        b.append(": ");
        boolean isFirstArgument = true;
        for (Object arg : args) {
            if (isFirstArgument) {
                isFirstArgument = false;
            } else {
                b.append(", ");
            }
            b.append(arg);
        }
        System.out.println(b.toString());
    }

    public static boolean isRtl(Resources res) {
        return res.getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }

    public static String getSystemProperty(String property, String defaultValue) {
        try {
            Class clazz = Class.forName("android.os.SystemProperties");
            Method getter = clazz.getDeclaredMethod("get", String.class);
            String value = (String) getter.invoke(null, property);
            if (!TextUtils.isEmpty(value)) {
                return value;
            }
        } catch (Exception e) {
            Log.d(TAG, "Unable to read system properties");
        }
        return defaultValue;
    }

    /**
     * Ensures that a value is within given bounds. Specifically:
     * If value is less than lowerBound, return lowerBound; else if value is greater than upperBound,
     * return upperBound; else return value unchanged.
     */
    public static int boundToRange(int value, int lowerBound, int upperBound) {
        return Math.max(lowerBound, Math.min(value, upperBound));
    }

    /**
     * @see #boundToRange(int, int, int).
     */
    public static float boundToRange(float value, float lowerBound, float upperBound) {
        return Math.max(lowerBound, Math.min(value, upperBound));
    }

    /**
     * @see #boundToRange(int, int, int).
     */
    public static long boundToRange(long value, long lowerBound, long upperBound) {
        return Math.max(lowerBound, Math.min(value, upperBound));
    }

    public static void closeSilently(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                // no-op
            }
        }
    }

    public static boolean isBinderSizeError(Exception e) {
        return e.getCause() instanceof TransactionTooLargeException
                || e.getCause() instanceof DeadObjectException;
    }

    /**
     * Utility method to post a runnable on the handler, skipping the synchronization barriers.
     */
    public static void postAsyncCallback(Handler handler, Runnable callback) {
        Message msg = Message.obtain(handler, callback);
        msg.setAsynchronous(true);
        handler.sendMessage(msg);
    }

    /**
     * Parses a string encoded using {@link #getPointString(int, int)}
     */
    public static Point parsePoint(String point) {
        String[] split = point.split(",");
        return new Point(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }

    /**
     * Encodes a point to string to that it can be persisted atomically.
     */
    public static String getPointString(int x, int y) {
        return String.format(Locale.ENGLISH, "%d,%d", x, y);
    }

    public static void unregisterReceiverSafely(Context context, BroadcastReceiver receiver) {
        try {
            context.unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            // no-op
        }
    }

    public static int[] getIntArrayFromString(String tokenized) {
        StringTokenizer tokenizer = new StringTokenizer(tokenized, ",");
        int[] array = new int[tokenizer.countTokens()];
        int count = 0;
        while (tokenizer.hasMoreTokens()) {
            array[count] = Integer.parseInt(tokenizer.nextToken());
            count++;
        }
        return array;
    }

    public static String getStringFromIntArray(int[] array) {
        StringBuilder str = new StringBuilder();
        for (int value : array) {
            str.append(value).append(",");
        }
        return str.toString();
    }

    public static float squaredHypot(float x, float y) {
        return x * x + y * y;
    }

    public static float squaredTouchSlop(Context context) {
        float slop = ViewConfiguration.get(context).getScaledTouchSlop();
        return slop * slop;
    }

}