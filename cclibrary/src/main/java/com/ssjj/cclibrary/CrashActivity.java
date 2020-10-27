package com.ssjj.cclibrary;

import android.annotation.SuppressLint;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class CrashActivity extends AppCompatActivity {
    private final String logTag = "CCTestLog";
    private final String activityName = "CrashActivity";

    private TextView tv_exception;
    private TextView tv_help;
    private Button btn_close;
    private Button btn_restart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash);
        Log.d(logTag, activityName + "->生命周期onCreate");
        initTextView();
        initButton();
        Throwable throwable = (Throwable) getIntent().getSerializableExtra("Throwable");
        throwable.printStackTrace();
        initContent(throwable);
    }

    private void initButton() {
        btn_close = findViewById(R.id.btn_close);
        btn_restart = findViewById(R.id.btn_restart);

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.os.Process.killProcess(android.os.Process.myPid());
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