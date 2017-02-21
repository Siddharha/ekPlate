package com.ekplate.android.adapters.menumodule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ekplate.android.R;
import com.ekplate.android.models.menumodule.FaqAnswerItem;
import com.ekplate.android.models.menumodule.FaqQuestionItem;

import java.util.ArrayList;

/**
 * Created by Rahul on 10/7/2015.
 */
public class FaqListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<FaqQuestionItem> faqQuestionItems;
    private LayoutInflater inflater;

    public FaqListAdapter(Context context, ArrayList<FaqQuestionItem> faqQuestionItems) {
        this.context = context;
        this.faqQuestionItems = faqQuestionItems;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getGroupCount() {
        return faqQuestionItems.size();
    }

    @Override
    public int getChildrenCount(int childPosition) {
        return faqQuestionItems.get(childPosition).getFaqAnswerItems().size();
    }

    @Override
    public FaqQuestionItem getGroup(int parentPosition) {
        return faqQuestionItems.get(parentPosition);
    }

    @Override
    public FaqAnswerItem getChild(int parentPosition, int childPosition) {
        return faqQuestionItems.get(parentPosition).getFaqAnswerItems().get(childPosition);
    }

    @Override
    public long getGroupId(int parentPosition) {
        return parentPosition;
    }

    @Override
    public long getChildId(int parentPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int parentPosition, boolean b, final View view, final ViewGroup viewGroup) {
        View rowView = view;
        if (rowView==null){
            rowView = inflater.inflate(R.layout.faq_question_row_layout, null);
            final QuestionItemHolder itemHolder = new QuestionItemHolder();
            itemHolder.tbFaqQuestion=(TextView) rowView.findViewById(R.id.tbFaqQuestion);
            itemHolder.imgListIndicator = (ImageView)rowView.findViewById(R.id.imgListIndicator);
            rowView.setTag(itemHolder);
        }

        final QuestionItemHolder newItemHolder = (QuestionItemHolder) rowView.getTag();
        newItemHolder.tbFaqQuestion.setText(getGroup(parentPosition).getQuestion());


        return rowView;
    }

    @Override
    public View getChildView(int parentPosition, int childPosition, boolean b, View view, ViewGroup viewGroup) {

        View answerRowView = view;
        if(answerRowView==null){
            answerRowView = inflater.inflate(R.layout.faq_answer_row_layout,null);
            AnswerItemHolder answerItemHolder = new AnswerItemHolder();
            answerItemHolder.tvFaqAnswer = (TextView) answerRowView.findViewById(R.id.tvFaqAnswer);
            answerRowView.setTag(answerItemHolder);
        }

        AnswerItemHolder newAnswerItemHolder = (AnswerItemHolder)answerRowView.getTag();
        newAnswerItemHolder.tvFaqAnswer.setText(getChild(parentPosition, childPosition).getAnswer());

        return answerRowView;
    }

    @Override
    public boolean isChildSelectable(int parentPosition, int childPosition) {
        return false;
    }

    private class QuestionItemHolder {
        TextView tbFaqQuestion;
        ImageView imgListIndicator;
    }

    private class AnswerItemHolder {
        TextView tvFaqAnswer;
    }

}