
package net.side5.httploader;

import android.util.Log;

public class Trace {
    private final static String TAG = "HTTPLoader";

    public static boolean DEBUG_ENABLE = false;

    private final static int DEBUG_LEVEL_NONE = 0;
    private final static int DEBUG_LEVEL_1 = 1;
    private final static int DEBUG_LEVEL_2 = 2;
    private final static int DEBUG_LEVEL_3 = 3;
    private final static int DEBUG_LEVEL_4 = 4;
    private final static int DEBUG_LEVEL_5 = 5;

    private final static int DEBUG_LEVEL = DEBUG_LEVEL_5;//BaseActivity.isUserModeBuild() ? DEBUG_LEVEL_3 : DEBUG_LEVEL_5;

    public static void e(String str) {
        if (DEBUG_ENABLE && DEBUG_LEVEL > DEBUG_LEVEL_NONE) {
            Exception e = new Exception();
            StackTraceElement element = e.getStackTrace()[1];
            StackTraceElement element1 = e.getStackTrace()[2];
            Log.e(TAG, String.format("[%s : %s / %s : %s] %s", element.getFileName(), element.getLineNumber(), element1.getFileName(), element1.getLineNumber(), str));
        }
    }

    public static void w(String str) {
        if (DEBUG_ENABLE && DEBUG_LEVEL > DEBUG_LEVEL_1) {
            Exception e = new Exception();
            StackTraceElement element = e.getStackTrace()[1];
            Log.w(TAG, String.format("[%s : %s] %s", element.getFileName(), element.getLineNumber(), str));
        }
    }

    public static void i(String str) {
        if (DEBUG_ENABLE && DEBUG_LEVEL > DEBUG_LEVEL_2) {
            Exception e = new Exception();
            StackTraceElement element = e.getStackTrace()[1];
            Log.i(TAG, String.format("[%s : %s] %s", element.getFileName(), element.getLineNumber(), str));
        }
    }

    public static void d(String str) {
        if (DEBUG_ENABLE && DEBUG_LEVEL > DEBUG_LEVEL_3) {
            Exception e = new Exception();
            StackTraceElement element = e.getStackTrace()[1];
            Log.d(TAG, String.format("[%s : %s] %s", element.getFileName(), element.getLineNumber(), str));
        }
    }

    public static void v(String str) {
        if (DEBUG_ENABLE && DEBUG_LEVEL > DEBUG_LEVEL_4) {
            Exception e = new Exception();
            StackTraceElement element = e.getStackTrace()[1];
            Log.v(TAG, String.format("[%s : %s] %s", element.getFileName(), element.getLineNumber(), str));
        }
    }
}
