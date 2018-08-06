package net.dormon.qqrecord;

import android.app.Application;
import android.content.Context;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static net.dormon.qqrecord.Utils.log;

public class MainHook implements IXposedHookLoadPackage {

    private XC_LoadPackage.LoadPackageParam lpparam;

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam)
            throws Throwable {

        this.lpparam = loadPackageParam;

        findAndHookMethod(
                "com.tencent.common.app.QFixApplicationImpl",
                lpparam.classLoader,
                "isAndroidNPatchEnable",
                XC_MethodReplacement.returnConstant(false)
        );

        findAndHookMethod(
                Application.class.getName(),
                loadPackageParam.classLoader,
                "attach",
                Context.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        final ClassLoader classLoader = ((Context) param.args[0]).getClassLoader();
                        startHook(classLoader);
                    }
                }
        );
    }

    private void startHook(ClassLoader classLoader) {
        if (classLoader == null) {
            log("start hook|", getClass().toString() + "dormon:Can't get ClassLoader!");
            return;
        }

        new Hook().hook(classLoader);
    }
}
