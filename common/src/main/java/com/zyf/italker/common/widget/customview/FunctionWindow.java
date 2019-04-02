package com.zyf.italker.common.widget.customview;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.zyf.italker.common.R;


public class FunctionWindow extends BasePopWindow implements View.OnClickListener {
    View view;
    TextView txt_addWord;
    TextView txt_setCount;
    FunctionListener functionListener;

    public FunctionWindow(Context context, FunctionListener functionListener) {
        super(context);
        this.functionListener = functionListener;
    }

    @Override
    public View getViewId(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.pop_functionwindow, null);
        txt_addWord = view.findViewById(R.id.txt_addword);
        txt_setCount = view.findViewById(R.id.txt_setcount);
        txt_addWord.setOnClickListener(this);
        txt_setCount.setOnClickListener(this);
        return view;
    }

    @Override
    public void initWindow() {
        super.initWindow();
        DisplayMetrics d = context.getResources().getDisplayMetrics();
        this.setWidth((int) (d.widthPixels * 0.4));

    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.txt_addword){
            functionListener.onFunctionAddWord();
        }else if (view.getId()==R.id.txt_setcount){
            functionListener.onFunctionSetCount();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        functionListener.onDismiss();
    }

    //抛给view层的监听接口
    public interface FunctionListener {
        //点击功能弹窗中的添加单词
        void onFunctionAddWord();

        //点击功能弹窗中的设置次数
        void onFunctionSetCount();

        //功能弹窗的dismiss
        void onDismiss();
    }
}
