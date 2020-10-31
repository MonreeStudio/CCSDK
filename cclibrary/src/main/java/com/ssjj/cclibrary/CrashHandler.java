package com.ssjj.cclibrary;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import android.os.Looper;
import android.util.Log;

import java.util.List;

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private Thread.UncaughtExceptionHandler mDefaultUncaughtExceptionHandler;
    private Context context;
    private String supportNumber;   //客服QQ号码
    private final String logTag = "CCTestLog";

    public static CrashHandler getInstance() {
        return CrashHandlerHolder.INSTANCE;
    }

    public void init(Context context) {
        this.context = context;
        mDefaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        JNI.init();
        Log.d(logTag, "JNI初始化成功");
    }

    @Override
    public void uncaughtException(Thread thread, final Throwable throwable) {
        Intent intent = new Intent(context, CrashActivity.class);
        intent.putExtra("Throwable", throwable);
        intent.putExtra("Progress", android.os.Process.myPid());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        boolean isMainThread = Looper.getMainLooper().getThread().getId() == thread.getId();
        Log.d(logTag, "当前线程是否为主线程：" + isMainThread);
        if (isMainThread) {
            mDefaultUncaughtExceptionHandler.uncaughtException(thread, throwable);
        }
        else {
            ((CrashActivity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDefaultUncaughtExceptionHandler.uncaughtException(Thread.currentThread(), throwable);
                }
            });
        }
        Log.d(logTag, "捕获成功");
        exitAPP();
    }

    private void exitAPP() {
        ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.AppTask> appTaskList = activityManager.getAppTasks();
        for (ActivityManager.AppTask appTask : appTaskList) {
            appTask.finishAndRemoveTask();
        }
    }

    public String getSupportNumber() {
        return supportNumber;
    }

    public void setSupportNumber(String supportNumber) {
        this.supportNumber = supportNumber;
    }

    private static class CrashHandlerHolder {
        @SuppressLint("StaticFieldLeak")
        static final CrashHandler INSTANCE = new CrashHandler();
    }

    private CrashHandler() { }
}

