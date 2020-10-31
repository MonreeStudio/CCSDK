package com.ssjj.cclibrary;

import android.annotation.SuppressLint;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;


public class CrashActivity extends AppCompatActivity {
    private String logTag = "CCTestLog";
    private String activityName = "CrashActivity";
    private int preProgressId;

    private boolean hasSent;

    private TextView tv_exception;
    private TextView tv_help;
    private Button btn_close;
    private Button btn_restart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash);
        Log.d(logTag, activityName + "->生命周期onCreate");
        Log.d(logTag, "所在进程ID：" + android.os.Process.myPid());
        initTextView();
        initButton();
        Throwable throwable = (Throwable) getIntent().getSerializableExtra("Throwable");
        throwable.printStackTrace();
        preProgressId = getIntent().getIntExtra("Progress", -1);
        initContent(throwable);
        postDataBySocket(throwable.toString() + Arrays.toString(throwable.getStackTrace()));
    }

    private void initButton() {
        btn_close = findViewById(R.id.btn_close);
        btn_restart = findViewById(R.id.btn_restart);

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.os.Process.killProcess(android.os.Process.myPid());
                if(preProgressId != -1)
                    android.os.Process.killProcess(preProgressId);
            }
        });
        btn_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(getApplication().getPackageName());
                assert LaunchIntent != null;
                LaunchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(LaunchIntent);
            }
        });
    }

    private void initTextView() {
        tv_exception = findViewById(R.id.tv_exception);
        tv_help = findViewById(R.id.tv_help);
    }

    @SuppressLint("SetTextI18n")
    private void initContent(Throwable e) {
        if(e.toString().contains(":"))
            tv_exception.setText(getString(R.string.dialog_content1) + e.toString().substring(0, e.toString().indexOf(":")) + "\n" + getString(R.string.dialog_tip));
        else
            tv_exception.setText(getString(R.string.dialog_content1) + e.toString() + "\n" + getString(R.string.dialog_tip));
        tv_help.setText(getString(R.string.dialog_customer_service_tip) + CrashHandler.getInstance().getSupportNumber());
    }

    private void postDataBySocket(final String stackTrace) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    if(!hasSent) {
                        Log.d("CrashReportInfo", "尝试通过Socket上传日志");
                        try {
                            Socket socket = new Socket("8.129.27.96", 8888);
                            PrintWriter out=new PrintWriter(socket.getOutputStream());
                            out.write("CCSDK崩溃异常信息：" + stackTrace);
                            out.flush();
                            Log.d("CrashReportInfo", "日志上传成功");
                            socket.close();
                            hasSent = true;
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            Log.d("CrashReportInfo", "上传失败，发生异常：" + e);
                        }
                    }
                }

            }
        }).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(logTag, activityName + "->生命周期onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(logTag, activityName + "->生命周期onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(logTag, activityName + "->生命周期onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(logTag, activityName + "->生命周期onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(logTag, activityName + "->生命周期onDestroy");
    }

}