package com.zyf.italker.factory.model;

public class WordCardWithCheck extends WordCard {


    boolean isChecked;

    boolean isPlaying;

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public WordCardWithCheck(String english, String chinese) {
        this(english, chinese, false,false);
    }

    public WordCardWithCheck(String english, String chinese, boolean isChecked,boolean isPlaying) {
        super(english, chinese);

        this.isChecked = isChecked;
        this.isPlaying = isPlaying;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

}
