package com.zyf.italker.factory.presenter.main;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import com.zyf.italker.common.app.Application;
import com.zyf.italker.common.widget.customview.AddWordWindow;
import com.zyf.italker.common.widget.customview.FunctionWindow;
import com.zyf.italker.factory.R;
import com.zyf.italker.factory.SharedPreferencesUtil;
import com.zyf.italker.factory.data.MainDataSource;
import com.zyf.italker.factory.model.WordCardWithCheck;
import com.zyf.italker.factory.model.api.translate.TransModel;
import com.zyf.italker.factory.model.api.translate.TransRspModel;
import com.zyf.italker.factory.nethelper.NetHelper;
import com.zyf.italker.factory.contract.MainContract;
import com.zyf.italker.factory.service.main.MainService;
import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;
import java.util.ArrayList;
import java.util.List;
import static android.content.Context.BIND_AUTO_CREATE;

public class MainPresenter implements MainContract.Presenter, MainDataSource.Callback<TransRspModel>, AddWordWindow.AddWindowListener, FunctionWindow.FunctionListener,
        MainService.ServiceListener {
    private final static int FUNCTION_ROTATE_START = 1;
    private final static int FUNCTION_ROTATE_END = 2;

    private static final String TAG = "MainPresenter";
    private Context context;
    private MainContract.View mainView;//此Presenter的View层
    private List<WordCardWithCheck> mainWordList = new ArrayList<>();//单词卡列表
    private boolean showBox = false;    //删除盒子是否显示
    private MainService mainService; //用于播放音频的服务
    private boolean isBind = false; //是否绑定了服务
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            isBind = true;
            MainService.MyBinder myBinder = (MainService.MyBinder) binder;
            mainService = myBinder.getService(MainPresenter.this);  //获得服务的引用
            mainService.updateList(mainWordList, MainService.LIST_ADD);  //在服务中刷新列表
            Log.d(TAG, "新连接");
            mainService.initPlayer();    //初始化service的mediaplayer
            mainService.initTts();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBind = false;
            Log.d(TAG, "断开连接");
        }
    };//服务与Presenter的连接
    private int count_current = 1;//读当前单词的总次数

    public MainPresenter(MainContract.View mainView, Context context) {
        this.mainView = mainView;
        this.context = context;
    }

    //翻译栏接收翻译成功
    @Override
    public void onTransLoaded(final TransRspModel transRspModel) {
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                StringBuilder transPage = new StringBuilder();
                for (List<TransRspModel.TranslateResultBean> list : transRspModel.getTranslateResult()) {
                    for (TransRspModel.TranslateResultBean bean : list) {
                        transPage.append(bean.getTgt());
                    }
                }
                mainView.setText(transPage.toString());
            }
        });
    }

    //翻译栏接收翻译失败
    @Override
    public void onTransAvailable() {
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                Application.showToast(R.string.trans_failed);
            }
        });
    }

    //暴露给View层的翻译栏翻译方法
    @Override
    public void translate(TransModel transModel) {
        mainView.showTransLoading();
        if (transModel.getWord().length() >= 1) {
            NetHelper.translateInToolBar(transModel, this);
        } else {
            mainView.setText(Application.getInstance().getString(R.string.pleaseInput));
        }
    }


    //****************************列表相关****************************************

    //初始化主单词卡列表数据
    @Override
    public void initMainWords() {
        mainWordList = SharedPreferencesUtil.getListData("mainWords", WordCardWithCheck.class);
        mainView.getRecyclerAdapter().replace(mainWordList);
        onPlayingChanged(0, (int) SharedPreferencesUtil.getData("currentPlayPosition", 0));
    }

    //单词条的点击
    @Override
    public void onCellClick(WordCardWithCheck wordCardWithCheck) {
        if (showBox) {
            //有删除盒子，不能播放 将删除盒子后面变值
            wordCardWithCheck.setChecked(!wordCardWithCheck.isChecked());
            mainView.getRecyclerAdapter().notifyDataSetChanged();
        } else {
            //没有删除盒子 可以播放
            int oldPosition = mainService.getCurrentPosition();
            int newPosition = mainWordList.indexOf(wordCardWithCheck);
            if (mainService.isPlay()) {
                if (mainService.getCurrentPosition() == newPosition) {
                    //点击的是正在播放的
                    mainService.pause();

                } else {
                    mainService.pause();
                    onPlayingChanged(oldPosition, newPosition);
                    mainService.realPlay(newPosition);


                }

            } else {
                onPlayingChanged(oldPosition, newPosition);
                mainService.realPlay(newPosition);

            }
        }


    }

    //删除选中项
    @Override
    public void deleteWords() {
        List<WordCardWithCheck> newMainList = new ArrayList<>();
        for (WordCardWithCheck wordCardWithCheck : mainWordList) {
            if (!wordCardWithCheck.isChecked()) {
                newMainList.add(wordCardWithCheck);
            } else {
                Log.d(TAG, "删除后的暂停播放: " + wordCardWithCheck.toString());
                mainService.updateList(newMainList, mainWordList.indexOf(wordCardWithCheck));
                // mainView.changePlayImage();
            }
        }
        if (newMainList.size() != mainWordList.size()) {
            mainWordList = newMainList;
            SharedPreferencesUtil.putListData("mainWords", mainWordList);

            showBox = !showBox;
            mainView.getRecyclerAdapter().replace(mainWordList);
            mainView.hideDel();
        } else {
            Application.showToast(R.string.noSelected);
        }


    }

    @Override
    public void onServiceCurrentDelete() {
        mainService.pause();
    }

    //显示或隐藏全部删除盒子
    @Override
    public void showOrHideDelBox() {
        showBox = !showBox;
        for (WordCardWithCheck wordCardWithCheck : mainWordList) {
            wordCardWithCheck.setChecked(false);
        }
        mainView.getRecyclerAdapter().notifyDataSetChanged();

        if (showBox) {
            mainView.showDel();
        } else {
            mainView.hideDel();
        }

    }

    //提供给MainActivity得到是否显示删除的方法
    public boolean getShowBox() {
        return showBox;
    }


    //***********************************功能弹窗相关****************************************

    @Override
    public void onFunctionClick() {
        mainView.startAnim(FUNCTION_ROTATE_START);
        mainView.showFunctionWindow();
    }

    //功能弹窗的添加单词点击回调
    @Override
    public void onFunctionAddWord() {
        mainView.hideFunctionWindow();
        mainView.showeAddWindow();
    }

    //功能弹窗的设置次数点击回调
    @Override
    public void onFunctionSetCount() {

    }

    //功能弹窗消失时的回调
    @Override
    public void onDismiss() {
        mainView.startAnim(FUNCTION_ROTATE_END);
    }


    //************************************添加弹窗相关****************************************


    //添加窗口翻译接收翻译成功
    @Override
    public void onAddWindowTransLoaded(final TransRspModel transRspModel) {
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                StringBuilder chinese = new StringBuilder();
                StringBuilder transPage = new StringBuilder();
                Log.d(TAG, "res :" + transRspModel.toString());
                for (List<TransRspModel.TranslateResultBean> list : transRspModel.getTranslateResult()) {
                    for (TransRspModel.TranslateResultBean bean : list) {
                        chinese.append(bean.getSrc());
                        transPage.append(bean.getTgt());
                    }
                }
                //得到翻译回来的对象
                WordCardWithCheck wordCard = new WordCardWithCheck(chinese.toString(), transPage.toString());
                mainWordList.add(wordCard);
                SharedPreferencesUtil.putListData("mainWords", mainWordList);
                mainView.getRecyclerAdapter().add(wordCard);
                //通知service数据有变更 变更类型为添加
                mainService.updateList(mainWordList, MainService.LIST_ADD);
                mainView.hideAddWindow();
            }
        });
    }

    //添加窗口翻译接收翻译失败
    @Override
    public void onAddWindowTransAvailable() {
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                mainView.hideAddWindowLoading();
                Application.showToast(R.string.AddCannotTrans);
            }
        });
    }

    //弹窗添加单词按钮的响应
    @Override
    public void addWord() {
        String english = mainView.getWindowInputEnglish();
        //翻译添加的单词
        mainView.showAddWindowLoading();
        NetHelper.translateInAdd(new TransModel(english), this);
    }

    //添加单词弹窗消失的回调
    @Override
    public void onAddWindowDismiss() {
        mainView.hideAddWindow();
    }


    //*****************************底部控制栏****************************************

    //last按钮点击事件
    @Override
    public void playLast() {
        mainService.playLast();
    }

    //play按钮点击事件
    @Override
    public void onPlayClick() {
        if (mainService.isPlay()) {
            //状态为正在播放
            mainService.pause();
        } else {
            //状态为暂停
            mainService.playCurrent();
        }
    }

    //next按钮点击事件
    @Override
    public void playNext() {
        mainService.playNext();
    }


    //*****************************服务相关****************************************


    //开启服务
    @Override
    public void startService() {
        if (!isBind) {
            Log.d(TAG, "未绑定");
            context.bindService(new Intent(context, MainService.class), conn, BIND_AUTO_CREATE);
        } else {
            Log.d(TAG, "已绑定");
        }
    }

    //一条单词卡播放完毕的回调
    @Override
    public void OnSoundCompletion() {

        if (count_current <= 20) {
            //mainService.pause();
            mainService.playCurrent();
            count_current++;
        } else {
            mainService.pause();
            count_current = 1;
            playNext();
        }

    }


    //播放状态有变化
    @Override
    public void onPlayingStateChange() {
        if (mainService.isPlay()) {
            mainView.showBtnPause();
        } else {
            mainView.showBtnPlay();
        }
    }

    //当有播放的位置变化时，Cell变色
    @Override
    public void onPlayingChanged(final int oldPosition, final int newPosition) {
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                if (oldPosition != newPosition || newPosition == 0 && mainWordList.size() > 0) {
                    mainWordList.get(oldPosition).setPlaying(false);
                    mainWordList.get(newPosition).setPlaying(true);
                    mainView.getRecyclerAdapter().notifyDataSetChanged();
                        mainView.scrollToPosition(newPosition);
                }
            }
        });

    }

    //随活动的生命周期destroy
    @Override
    public void onDestroy() {
        if (isBind)
            context.unbindService(conn);
    }


}
