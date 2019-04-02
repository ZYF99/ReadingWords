package com.zyf.italker.factory.model.api.translate;
import java.util.List;

public class TransModel {
    String word;

    @Override
    public String toString() {
        return "TransModel{" +
                "word='" + word + '\'' +
                '}';
    }

    public TransModel(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
