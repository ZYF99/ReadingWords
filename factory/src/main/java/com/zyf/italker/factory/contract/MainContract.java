package com.zyf.italker.factory.contract;

import com.zyf.italker.common.widget.recycler.RecyclerAdapter;
import com.zyf.italker.factory.model.WordCardWithCheck;
import com.zyf.italker.factory.model.api.translate.TransModel;

public interface MainContract {

    interface Presenter {
        void translate(TransModel transModel);

        void initMainWords();

        void playLast();

        void onPlayClick();

        void playNext();

        void showOrHideDelBox();

        void startService();

        void deleteWords();

        void onCellClick(WordCardWithCheck wordCardWithCheck);

        void onDestroy();

        void onFunctionClick();

    }

    interface View {
        void setText(String transWord);

        void showTransLoading();

        void showAddWindowLoading();

        void hideAddWindowLoading();

        void showeAddWindow();

        void hideAddWindow();

        String getWord();

        String getWindowInputEnglish();

        RecyclerAdapter<WordCardWithCheck> getRecyclerAdapter();

        void showBtnPlay();

        void showBtnPause();

        void showDel();

        void hideDel();

        void scrollToPosition(int position);

        void startAnim(int type);

        void hideFunctionWindow();

        void showFunctionWindow();

    }

}
