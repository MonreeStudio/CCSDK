package com.ssjj.cclibrary;

import android.content.Context;
import android.util.Log;

public class CCSDK {

    private CCSDK() {}

    private static class CCSDKHolder {
        private static CCSDK INSTANCE = new CCSDK();
    }

    public static CCSDK getInstance() {
        return CCSDKHolder.INSTANCE;
    }

    public void init(Context context, String supportNumber) {
        CrashHandler.getInstance().init(context);
        CrashHandler.getInstance().setSupportNumber(supportNumber);
        String logTag = "CCTestLog";
        Log.d(logTag, "CrashHandler初始化成功");
    }
}
