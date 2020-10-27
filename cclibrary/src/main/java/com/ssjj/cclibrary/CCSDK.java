package com.ssjj.cclibrary;

import android.content.Context;
import android.util.Log;

import com.tencent.bugly.crashreport.CrashReport;

public class CCSDK {

    private CCSDK() {}

    private static class MSCCSDKHolder {
        private static CCSDK INSTANCE = new CCSDK();
    }

    public static CCSDK getInstance() {
        return MSCCSDKHolder.INSTANCE;
    }

    public void init(Context context, String buglyAppId, String supportNumber) {
        CrashHandler.getInstance().init(context);
        CrashHandler.getInstance().setSupportNumber(supportNumber);
        String logTag = "CCTestLog";
        Log.d(logTag, "CrashHandler初始化成功");
        CrashReport.initCrashReport(context, buglyAppId, false);
        Log.d(logTag, "Bugly初始化成功");
    }
}
