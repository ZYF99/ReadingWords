package com.zyf.italker.retrofit;


import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.zyf.italker.common.app.ActivityWithBackPress;
import com.zyf.italker.common.app.Application;
import com.zyf.italker.common.widget.customview.AddWordWindow;
import com.zyf.italker.common.widget.customview.FunctionWindow;
import com.zyf.italker.common.widget.recycler.RecyclerAdapter;
import com.zyf.italker.factory.model.WordCardWithCheck;
import com.zyf.italker.factory.contract.MainContract;
import com.zyf.italker.factory.model.api.translate.TransModel;
import com.zyf.italker.factory.presenter.main.MainPresenter;
import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class MainActivity extends ActivityWithBackPress implements MainContract.View {

    //主活动的控制器
    MainPresenter mainPresenter;
    //添加单词弹窗
    private AddWordWindow addWordWindow;
    //功能气泡弹窗
    private FunctionWindow functionWindow;

    @BindView(R.id.trans_word)
    TextView txt_trans;

    @BindView(R.id.input_word)
    EditText input_word;

    @BindView(R.id.nav_function)
    ImageView btn_function;

    @BindView(R.id.list_word_main)
    RecyclerView list_word_main;

    @BindView(R.id.nav_last)
    ImageView btn_navLast;

    @BindView(R.id.nav_play)
    ImageView btn_navPlay;

    @BindView(R.id.nav_next)
    ImageView btn_navNext;

    @BindView(R.id.nav)
    ConstraintLayout nav;

    @BindView(R.id.btn_del)
    ConstraintLayout btn_del;

    //适配器，WordCard，单词卡 带中文和英文
    private RecyclerAdapter<WordCardWithCheck> mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //设置recyclerview的适配器
        list_word_main.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RecyclerAdapter<WordCardWithCheck>() {
            @Override
            protected int getItemViewType(int position, WordCardWithCheck wordCard) {
                return R.layout.cell_word_main;
            }

            @Override
            protected ViewHolder<WordCardWithCheck> onCreatViewHolder(View root, int viewType) {
                return new MainActivity.ViewHolder(root);
            }
        };
        list_word_main.setAdapter(mAdapter);
        //设置MainActivity的Presenter
        mainPresenter = new MainPresenter(this, this);
        //委托presenter开启服务
        mainPresenter.startService();
        //初始化列表数据
        mainPresenter.initMainWords();
        //翻译输入栏的监听器
        TextWatcher watcher = new MTextWatcher();
        input_word.addTextChangedListener(watcher);
    }

    class MTextWatcher implements TextWatcher {

        MTextWatcher() {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            mainPresenter.translate(new TransModel(getWord()));
        }

    }


    //设置翻译后的文字
    @Override
    public void setText(String transWord) {
        txt_trans.setText(transWord);
    }

    //设置界面翻译loading
    @Override
    public void showTransLoading() {
        txt_trans.setText(Application.getInstance().getString(R.string.transLoading));
    }

    //翻译栏得到需要翻译的文字
    @Override
    public String getWord() {
        return input_word.getText().toString();
    }


    //*************************************列表******************************************

    //recyclerview滑动到指定位置
    @Override
    public void scrollToPosition(int position) {
        list_word_main.smoothScrollToPosition(position);
    }

    //向外部提供得到Adapter的方法
    @Override
    public RecyclerAdapter<WordCardWithCheck> getRecyclerAdapter() {
        return mAdapter;
    }


    //用于RecyclerView的适配的。内部ViewHolder类
    class ViewHolder extends RecyclerAdapter.ViewHolder<WordCardWithCheck> {
        @BindView(R.id.cell_main_english)
        TextView mEnglish;
        @BindView(R.id.cell_main_chinese)
        TextView mChinese;
        @BindView(R.id.checkbox_del)
        ImageView checkbox_del;
        @BindView(R.id.cell_word_main)
        ConstraintLayout cell;

        ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(WordCardWithCheck wordCard) {
            mEnglish.setText(wordCard.getEnglish());
            mChinese.setText(wordCard.getChinese());

            //判断是否显示勾选盒子
            if (mainPresenter.getShowBox()) {
                checkbox_del.setVisibility(View.VISIBLE);
            } else {
                checkbox_del.setVisibility(View.GONE);
            }

            //是否勾选判断盒子
            if (wordCard.isChecked()) {
                checkbox_del.setImageResource(R.drawable.sel_check);
            } else {
                checkbox_del.setImageResource(R.drawable.sel_nor);
            }

            //判断是否将Cell置为蓝色
            if (wordCard.isPlaying()) {
                cell.setBackground(getResources().getDrawable(R.drawable.cell_playing));
            } else {
                cell.setBackground(getResources().getDrawable(R.drawable.cell_nor));
            }

        }

        @OnClick(R.id.cell_word_main)
        void onCellClick() {
            mainPresenter.onCellClick(mData);
        }

        @OnLongClick(R.id.cell_word_main)
        boolean onCellLongClick() {
            mainPresenter.showOrHideDelBox();
            return true;
        }

        @OnClick(R.id.checkbox_del)
        void onDelCheckBoxClick() {
            if (mData.isChecked()) {
                //选中状态变为未选中状态
                checkbox_del.setImageResource(R.drawable.sel_nor);
                mData.setChecked(false);
            } else {
                //未选中状态变为选中状态
                checkbox_del.setImageResource(R.drawable.sel_check);
                mData.setChecked(true);
            }
        }

    }


    //***********************************功能弹窗******************************************

    @Override
    //功能键的动画
    public void startAnim(int animType) {
        Animation rotateAnim;
        switch (animType) {
            case 1:
                rotateAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_start);
                break;
            case 2:
                rotateAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_end);
                break;
            default:
                rotateAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_start);
                break;
        }
        rotateAnim.setFillAfter(true);
        LinearInterpolator lin = new LinearInterpolator();
        rotateAnim.setInterpolator(lin);
        btn_function.startAnimation(rotateAnim);
    }

    @Override
    public void hideFunctionWindow() {
        functionWindow.dismiss();
    }

    @Override
    public void showFunctionWindow() {
        functionWindow = new FunctionWindow(this, mainPresenter);
        functionWindow.showAtBottom(nav);
    }


    //***********************************添加弹窗******************************************

    //显示弹窗加词loading
    @Override
    public void showAddWindowLoading() {
        addWordWindow.showAddLoading();
    }

    //隐藏弹窗加词loading
    @Override
    public void hideAddWindowLoading() {
        addWordWindow.hideAddLoading();
    }

    //获取window中的English
    @Override
    public String getWindowInputEnglish() {
        return addWordWindow.getInputEnglish();
    }

    //显示添加单词弹窗
    @Override
    public void showeAddWindow() {
        //创建添加词条的弹窗
        addWordWindow = new AddWordWindow(this, mainPresenter);
        addWordWindow.showAtCenter(txt_trans);
    }

    //关闭添加单词弹窗
    @Override
    public void hideAddWindow() {
        addWordWindow.hideWindow();
    }


    //**********************************底部控制栏****************************************

    //last按钮点击事件
    @OnClick(R.id.nav_last)
    void onLastClick() {
        mainPresenter.playLast();
    }

    //play按钮点击事件
    @OnClick(R.id.nav_play)
    void onPlayClick() {
        mainPresenter.onPlayClick();
    }

    //next按钮点击事件
    @OnClick(R.id.nav_next)
    void onNextClick() {
        mainPresenter.playNext();
    }


    //功能键点击事件
    @OnClick(R.id.nav_function)
    void onFunctionClick() {
        mainPresenter.onFunctionClick();
    }


    //删除按钮点击事件
    @OnClick(R.id.btn_del)
    void onDeleteClick() {
        mainPresenter.deleteWords();
    }

    //更换play按钮的图标为播放
    @Override
    public void showBtnPlay() {
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                btn_navPlay.setImageResource(R.drawable.ic_play);
            }
        });

    }

    //更换play按钮的图标为暂停
    @Override
    public void showBtnPause() {
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                btn_navPlay.setImageResource(R.drawable.ic_pause);
            }
        });

    }

    //显示删除
    @Override
    public void showDel() {
        btn_del.setVisibility(View.VISIBLE);
    }

    //隐藏删除
    @Override
    public void hideDel() {
        btn_del.setVisibility(View.GONE);
    }


    //隐藏软键盘
    private void showOrHideSoftInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainPresenter.onDestroy();
    }
}
