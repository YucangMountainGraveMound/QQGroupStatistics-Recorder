package net.dormon.qqrecord;

import de.robv.android.xposed.XposedBridge;

class Utils {

    @SuppressWarnings("all")
    public static void log(String tag, String content) {
        XposedBridge.log("DORMON_QQ_RECORDER|" + tag + "|" + content);
    }

}
