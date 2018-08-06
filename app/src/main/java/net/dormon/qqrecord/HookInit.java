package net.dormon.qqrecord;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static net.dormon.qqrecord.Utils.log;

public class HookInit implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        new CheckActive().handleLoadPackage(lpparam);
        if (!lpparam.packageName.equals(Variants.QQ_PACKAGE_NAME)) {
            return;
        }

        findAndHookMethod(
                "android.app.Activity",
                lpparam.classLoader,
                "onResume",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        log("load pack|Activity：", param.thisObject.getClass().getName());

                    }
                }
        );

        findAndHookMethod(
                "android.support.v4.app.Fragment",
                lpparam.classLoader,
                "onResume",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        log("load pack|Fragment：", param.thisObject.getClass().getName());
                    }
                }
        );

        new MainHook().handleLoadPackage(lpparam);
    }

}


