package com.ekplate.android.models.menumodule;

import java.util.ArrayList;

/**
 * Created by Rahul on 10/7/2015.
 */
public class FaqQuestionItem {
    int id;
    String question;
    ArrayList<FaqAnswerItem> faqAnswerItems;

    public ArrayList<FaqAnswerItem> getFaqAnswerItems() {
        return faqAnswerItems;
    }

    public void setFaqAnswerItems(ArrayList<FaqAnswerItem> faqAnswerItems) {
        this.faqAnswerItems = faqAnswerItems;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
