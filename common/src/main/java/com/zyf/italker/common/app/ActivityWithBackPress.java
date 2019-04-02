package com.zyf.italker.common.app;

import android.app.Activity;
import android.view.KeyEvent;

public class ActivityWithBackPress extends Activity {
    private long clickTime = 0; // 第一次点击的时间

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 是否触发按键为back键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        } else { // 如果不是back键正常响应
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onBackPressed() {
        exit();
    }

    private void exit() {
        if ((System.currentTimeMillis() - clickTime) > 2000) {
            Application.showToast("再按一次退出");
            clickTime = System.currentTimeMillis();
        } else {
            this.finish();
        }
    }
}

