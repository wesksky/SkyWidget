package com.sky.skywidget.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.sky.skywidget.R;
import com.sky.skywidget.widget.TimerCount;

public class TimerCountActivity extends BaseActivity {
    TimerCount timerCount1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_count);
        initView();
        initTimer1();
    }

    private void initView() {
        timerCount1 = $(R.id.timer_count1);
    }

    private void initTimer1() {
        timerCount1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                timerCount1.start();
            }
        });
        timerCount1.setTime(10);
        timerCount1.setTimerText(new TimerCount.ITimerText() {
            @Override
            public String getTimerText(int time) {
                // 默认mm:ss
                // 该接口可自定义显示方式
                long hour = time / 3600;
                long min = time % 3600 / 60;
                long sec = time % 60;
                String hourStr, minStr, secStr;
                hourStr = hour < 10 ? "0" + hour : String.valueOf(hour);
                minStr = min < 10 ? "0" + min : String.valueOf(min);
                secStr = sec < 10 ? "0" + sec : String.valueOf(sec);
                return hourStr + ":" + minStr + ":" + secStr;
            }
        });

        timerCount1.setOnTimerCountEnd(new TimerCount.OnTimerCountEnd() {
            @Override
            public void onTimerCountEnd() {
                Toast.makeText(TimerCountActivity.this, "计时结束", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
