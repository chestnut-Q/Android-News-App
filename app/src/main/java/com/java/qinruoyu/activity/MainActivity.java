package com.java.qinruoyu.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.java.qinruoyu.OfflineNewsManager;
import com.java.qinruoyu.R;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private final String FILENAME = "data.txt";
    private OfflineNewsManager mOfflineNewsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mOfflineNewsManager = OfflineNewsManager.getInstance(getApplicationContext());

        // Opt1: 启动时删除历史记录
//        File dir = getApplicationContext().getFilesDir();
//        File file = new File(dir, FILENAME);
//        if (file.exists())
//            file.delete();

        // Opt2: 从本地读取历史记录
        mOfflineNewsManager.loadList();

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                startActivity(new Intent(getApplicationContext(), MainScreenActivity.class));
                finish();
            }
        };
        timer.schedule(task, 1500);
    }
}