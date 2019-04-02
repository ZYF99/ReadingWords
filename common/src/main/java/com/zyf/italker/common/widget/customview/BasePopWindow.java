package com.zyf.italker.common.widget.customview;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupWindow;

import com.zyf.italker.common.R;

import java.security.PublicKey;

/**
 * 弹窗视图
 */
public abstract class BasePopWindow extends PopupWindow {
    public Context context;

    public BasePopWindow(Context context) {
        super(context);
        this.context = context;
        initalize();
    }

    //得到弹窗中的控件，返回整个布局的id
    public abstract View getViewId(LayoutInflater inflater);

    private void initalize() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = getViewId(inflater);
        setContentView(view);
        initWindow();
    }

    public void initWindow() {
        DisplayMetrics d = context.getResources().getDisplayMetrics();

        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.update();
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x00000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        backgroundAlpha((Activity) context, 0.5f);//0.0-1.0

        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha((Activity) context, 1f);
            }
        });
    }

    //设置添加屏幕的背景透明度
    public void backgroundAlpha(Activity context, float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }

    public void showAtBottom(View view) {
        //弹窗位置设置
        setAnimationStyle(R.style.pop_animation);
        showAtLocation(view, Gravity.BOTTOM,view.getWidth(),view.getHeight());//有偏差
        //showAsDropDown(view, Math.abs((view.getWidth() - getWidth()) / 2),  -Math.abs((view.getHeight() - getHeight())));

    }

    public void showAtCenter(View view) {
        //弹窗位置设置
        setAnimationStyle(R.style.pop_animation);
        showAtLocation(view, Gravity.CENTER, 10, 110);//有偏差

    }

    @Override
    public void dismiss() {
        super.dismiss();

    }







}
