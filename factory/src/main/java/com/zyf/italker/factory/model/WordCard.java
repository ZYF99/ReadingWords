package com.zyf.italker.factory.model;

public class WordCard {
    String english;
    String chinese;

    @Override
    public String toString() {
        return "WordCard{" +
                "english='" + english + '\'' +
                ", chinese='" + chinese + '\'' +
                '}';
    }

    public WordCard(String english, String chinese) {
        this.english = english;
        this.chinese = chinese;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public String getChinese() {
        return chinese;
    }

    public void setChinese(String chinese) {
        this.chinese = chinese;
    }
}
