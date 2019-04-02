package com.zyf.italker.common.widget.customview;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.zyf.italker.common.R;
import com.zyf.italker.common.app.Application;

import net.qiujuer.genius.ui.widget.Loading;


public class AddWordWindow extends BasePopWindow implements View.OnClickListener {
    ClearEditText input_english;
    Button btn_addword;
    Button btn_closewindow;
    Loading loading;
    AddWindowListener addWindowListener;

    public AddWordWindow(Context context, AddWindowListener addWindowListener) {
        super(context);
        this.addWindowListener = addWindowListener;
    }

    @Override
    public void initWindow() {
        super.initWindow();
        DisplayMetrics d = context.getResources().getDisplayMetrics();
        this.setWidth((int) (d.widthPixels * 0.6));
    }

    @Override
    public View getViewId(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.pop_addword, null);
        input_english = view.findViewById(R.id.addword_english);
        btn_addword = view.findViewById(R.id.btn_addword);
        btn_closewindow = view.findViewById(R.id.btn_closewindow);
        loading = view.findViewById(R.id.loading);
        btn_closewindow.setOnClickListener(this);
        btn_addword.setOnClickListener(this);
        return view;
    }

    public String getInputEnglish() {
        return input_english.getText().toString();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_addword) {
            if (input_english.getText().toString().length() >= 1) {
                addWindowListener.addWord();
            } else {
                Application.showToast(R.string.pleaseInputWord);
            }

        } else if (view.getId() == R.id.btn_closewindow) {
            addWindowListener.onAddWindowDismiss();
        }
    }

    //显示添加时的加载
    public void showAddLoading() {
        btn_addword.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.VISIBLE);
        loading.start();
    }

    //隐藏添加时的加载
    public void hideAddLoading() {
        btn_addword.setVisibility(View.VISIBLE);
        loading.setVisibility(View.INVISIBLE);
        loading.stop();

    }


    //关闭弹窗
    public void hideWindow() {
        super.dismiss();
    }

    //禁用本类的dismiss方法
    @Override
    public void dismiss() {
        //super.dismiss();
    }

    //抛给view层的点击接口
    public interface AddWindowListener {
        void addWord();

        void onAddWindowDismiss();


    }



}
