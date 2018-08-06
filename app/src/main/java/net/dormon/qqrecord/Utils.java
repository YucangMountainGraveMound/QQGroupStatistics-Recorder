package net.dormon.qqrecord;

import java.io.File;
import java.io.FileOutputStream;

import de.robv.android.xposed.XposedBridge;

public class Utils {

    public static final String PACKAGE_NAME = Utils.class.getPackage().getName();
    private static final String dataDir = "/data/system/dormonqqrecorder/";

    @SuppressWarnings("all")
    public static void log(String tag, String content) {
        XposedBridge.log("DORMON_QQ_RECORDER|" + tag + "|" + content);
    }

    public static void saveData(String fileName, String content) {
        ensureDir();
        FileOutputStream fos = null;
        try {
            if (fileIsExists(dataDir + fileName)) {
                File file = new File(dataDir, fileName);
                fos = new FileOutputStream(file);
                byte[] buffer = content.getBytes();
                fos.write(buffer);
                fos.close();
            } else {
                log("dormon:file|", "exists!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void ensureDir() {
        File file = new File(dataDir);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    public static boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
