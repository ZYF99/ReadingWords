package com.zyf.italker.factory.service.main;


import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import com.zyf.italker.common.Common;
import com.zyf.italker.common.app.Application;
import com.zyf.italker.factory.SharedPreferencesUtil;
import com.zyf.italker.factory.model.WordCardWithCheck;
import com.zyf.italker.factory.service.ServiceWithBind;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


/**
 * 主界面用于按列表朗读的service
 */

public class MainService extends ServiceWithBind implements MediaPlayer.OnCompletionListener {

    String TAG = "MainService";
    static MediaPlayer player = new MediaPlayer(); //音频播放器
    static ServiceListener serviceListener; //本服务的监听器（就是Presenter）

    public static final int LIST_ADD = -1;
    List<WordCardWithCheck> wordCards;
    Integer currentPlayWord = 0;
    //文字转语音
    private TextToSpeech textToSpeech;


    public void initPlayer() {

        player.reset();

        player.setOnCompletionListener(this);
    }

    public void initTts() {

        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }

        //文字转语音tts的设置
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    // 设置音调,1.0是常规
                    textToSpeech.setPitch(0f);
                    //设定语速 ，默认1.0正常语速
                    textToSpeech.setSpeechRate(1.0f);
                    int result = textToSpeech.setLanguage(Locale.CHINA);
                    if (result != TextToSpeech.LANG_COUNTRY_AVAILABLE
                            && result != TextToSpeech.LANG_AVAILABLE) {
                        Application.showToast("TTS暂时不支持这种语音的朗读！");
                    }
                }
            }
        });

        //中文朗读监听
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String s) {

            }

            //中文朗读完成的回调
            @Override
            public void onDone(String s) {
                //告诉Presenter中英文都播放完毕
                serviceListener.OnSoundCompletion();
            }

            @Override
            public void onError(String s) {

            }
        });
    }

    //播放完成一条英语的回调
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");
        //读中文
        textToSpeech.speak(wordCards.get(currentPlayWord).getChinese(), TextToSpeech.QUEUE_FLUSH, map);

    }


    //client 可以通过Binder获取Service实例
    public class MyBinder extends Binder {
        public MainService getService(ServiceListener serviceListener) {

            MainService.serviceListener = serviceListener;
            currentPlayWord = (int) SharedPreferencesUtil.getData("currentPlayPosition", currentPlayWord);
            return MainService.this;
        }
    }

    //通过binder实现调用者client与Service之间的通信
    private MainService.MyBinder binder = new MainService.MyBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    //播放上一条
    public void playLast() {
        //当有上一条时
        if ((currentPlayWord - 1) >= 0) {
            realPlay(currentPlayWord - 1);

        }
    }

    //播放当前保存的条目
    public void playCurrent() {
        if (wordCards.size() > 0) {
            if (currentPlayWord <= wordCards.size() - 1) {
                realPlay(currentPlayWord);

            } else {
                realPlay(wordCards.size() - 1);

            }
        }
    }

    //播放下一条
    public void playNext() {
        //当有下一条时
        if ((currentPlayWord + 1) < wordCards.size()) {
            realPlay(currentPlayWord + 1);

        }

    }

    //真实播放 填入哪条就播哪条
    public void realPlay(final Integer newPosition) {

        //TODO 媒体播放
        try {
            initPlayer();
            initTts();
            player.setDataSource(Common.Constance.YOUDAO_READ_URL + wordCards.get(newPosition).getEnglish());
            player.prepare();
            serviceListener.onPlayingChanged(currentPlayWord, newPosition);
            player.start();
            currentPlayWord = newPosition;
            SharedPreferencesUtil.putData("currentPlayPosition", currentPlayWord);
            serviceListener.onPlayingStateChange();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //暂停
    public void pause() {
        initPlayer();
        initTts();
        serviceListener.onPlayingStateChange();
    }

    //向Presenter提供获得是否在播放
    public boolean isPlay() {
        if (player != null) {
            return player.isPlaying() || textToSpeech.isSpeaking();
        } else {
            return false;
        }

    }

    //向Presenter提供播放的位置
    public int getCurrentPosition() {
        return currentPlayWord;
    }


    /**
     * 更新主单词卡列表
     *
     * @param reducePosition 减少的单项位置 -1为增加 其余为位置
     */
    public void updateList(List<WordCardWithCheck> wordCards, int reducePosition) {
        this.wordCards = wordCards;
        if (reducePosition != LIST_ADD) {

            //列表项减少了
            if (currentPlayWord < reducePosition) {
                //当前播放单项位置在删除位之前，无操作
            } else if (currentPlayWord > reducePosition) {
                //当前播放单项位置在删除位之后，当前位置前移一位
                currentPlayWord--;
            } else {
                //当前播放单项位置等于删除位，直接暂停播放
                serviceListener.onServiceCurrentDelete();
                //如果删除的不是最后一个人，播放更新后的
                if (!(reducePosition >= wordCards.size())) {
                    playCurrent();
                }

            }
        }
    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "服务要关了");
        if (player != null) {
            player.stop();
            player.release();
        }
        super.onDestroy();

        // 重启
        Intent intent = new Intent(getApplicationContext(), MainService.class);
        startService(intent);
    }

    public interface ServiceListener {


        void OnSoundCompletion();

        void onPlayingStateChange();

        void onPlayingChanged(int oldPosition, int newPosition);

        void onServiceCurrentDelete();
    }


}
