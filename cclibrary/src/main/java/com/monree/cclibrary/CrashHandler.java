package com.monree.cclibrary;

import android.content.Context;
import android.content.Intent;

import android.os.Looper;
import android.util.Log;

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private Thread.UncaughtExceptionHandler mDefaultUncaughtExceptionHandler;
    private Context context;
    private String supportNumber;

    public static CrashHandler getInstance() {
        return CrashHandlerHolder.INSTANCE;
    }

    public void init(Context context) {
        Log.d("CrashReportInfo", "开始初始化");
        this.context = context;
        mDefaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        Log.d("CrashReportInfo", "JNI初始化");
        JNI.init();
        Log.d("CrashReportInfo", "初始化成功");
    }

    @Override
    public void uncaughtException(Thread thread, final Throwable throwable) {
        Log.d("CrashReportInfo", "异常捕获");
        Intent intent = new Intent(context, CrashActivity.class);
        intent.putExtra("Throwable", throwable);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        Log.d("CrashReportInfo", "异常捕获2");
        boolean isMainThread = Looper.getMainLooper().getThread().getId() == thread.getId();
        Log.d("CrashReportInfo", "是否为主线程：" + isMainThread);
        if (isMainThread) {
            mDefaultUncaughtExceptionHandler.uncaughtException(thread, throwable);
            Log.d("CrashReportInfo", "异常捕获3");
        }
        else {
            ((CrashActivity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDefaultUncaughtExceptionHandler.uncaughtException(Thread.currentThread(), throwable);
                    Log.d("CrashReportInfo", "异常捕获3");
                }
            });
        }
    }

    public String getSupportNumber() {
        return supportNumber;
    }

    public void setSupportNumber(String supportNumber) {
        this.supportNumber = supportNumber;
    }

    private static class CrashHandlerHolder {
        static final CrashHandler INSTANCE = new CrashHandler();
    }

    private CrashHandler() { }
}

