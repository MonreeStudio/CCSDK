package com.monree.cclibrary;

import android.content.Context;
import android.util.Log;

import com.tencent.bugly.crashreport.CrashReport;

public class MSCCSDK {
    private MSCCSDK() {}

    private static class MSCCSDKHolder {
        private static MSCCSDK INSTANCE = new MSCCSDK();
    }

    public static MSCCSDK getInstance() {
        return MSCCSDKHolder.INSTANCE;
    }

    public void init(Context context, String buglyAppId, String supportNumber) {
        CrashHandler.getInstance().init(context);
        CrashHandler.getInstance().setSupportNumber(supportNumber);
        Log.d("MSCCSDK", "CrashHandler初始化");
        CrashReport.initCrashReport(context, buglyAppId, false);
        Log.d("MSCCSDK", "Bugly初始化");
    }
}
