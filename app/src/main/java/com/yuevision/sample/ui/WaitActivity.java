package com.yuevision.sample.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.yuevision.sample.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by sjy on 2018/1/4.
 */

public class WaitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_welcome);

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Intent intent1 = new Intent(WaitActivity.this, MainActivity.class);
                startActivity(intent1);
                WaitActivity.this.finish();
            }
        };
        timer.schedule(timerTask, 1000 * 3);
    }
}
