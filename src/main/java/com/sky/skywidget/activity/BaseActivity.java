package com.sky.skywidget.activity;

import android.app.Activity;
import android.view.View;
import android.widget.Toast;

/**
 * Created by sky on 16/11/25.
 */

public class BaseActivity extends Activity {
    protected <E extends View> E $(int id) {
        return (E) findViewById(id);
    }

    protected void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    protected void showToast(int str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}
