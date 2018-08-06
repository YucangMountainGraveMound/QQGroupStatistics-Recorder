package net.dormon.qqrecord;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

class CheckActive {

    @SuppressWarnings("all")
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        if (!loadPackageParam.packageName.equals(BuildConfig.APPLICATION_ID)) {
            return;
        }
        XposedHelpers.findAndHookMethod(MainActivity.class.getName(), loadPackageParam.classLoader, "isModuleActive", XC_MethodReplacement.returnConstant(true));
    }
}
