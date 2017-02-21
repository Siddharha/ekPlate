package com.ekplate.android.activities.menumodule;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ekplate.android.R;
import com.ekplate.android.activities.homemodule.HomeResideActivity;
import com.ekplate.android.config.BaseActivity;

public class QuestionSubmitResultActivity extends BaseActivity {

    private TextView toolbarHeaderTextThankYou;
    private TextView tvSuccessTextOne, tvSuccessTextTwo;
    private ImageView ivBackThankYou;
    private LinearLayout llDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_submit_result);
        getSupportActionBar().hide();
        getWindow().setBackgroundDrawable(null);
        setUpToolBar();
        init();
        setListener();
        setUpSuccessText();
    }

    private void setUpToolBar(){
        ivBackThankYou = (ImageView) findViewById(R.id.ivBackThankYou);
        toolbarHeaderTextThankYou = (TextView) findViewById(R.id.toolbarHeaderTextThankYou);
    }

    private void init(){
        tvSuccessTextOne = (TextView) findViewById(R.id.tvSuccessTextOne);
        tvSuccessTextTwo = (TextView) findViewById(R.id.tvSuccessTextTwo);
        llDone = (LinearLayout) findViewById(R.id.llDone);
    }

    private void setListener() {
        ivBackThankYou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        llDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getIntent().getExtras().getString("screenTypeFlag").equalsIgnoreCase("report_problem") ||
                        getIntent().getExtras().getString("screenTypeFlag").equalsIgnoreCase("write_reviews")) {
                    onBackPressed();
                } else {
                    startActivity(new Intent(QuestionSubmitResultActivity.this, HomeResideActivity.class));
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                }
            }
        });
    }

    private void setUpSuccessText(){
        if (getIntent().getExtras().getString("screenTypeFlag").equalsIgnoreCase("faq")){
            tvSuccessTextOne.setText("We will get back to you as soon as we can");
            toolbarHeaderTextThankYou.setText("FAQ");
        }
        else if(getIntent().getExtras().getString("screenTypeFlag").equalsIgnoreCase("suggestion")){
            toolbarHeaderTextThankYou.setText("HELP US IMPROVE");
            tvSuccessTextOne.setText("Thank you for your suggestion, we will try our level best to get back to you as soon as possible!");
        }
        else if(getIntent().getExtras().getString("screenTypeFlag").equalsIgnoreCase("report_problem")){
            toolbarHeaderTextThankYou.setText("REPORT PROBLEM");
            tvSuccessTextOne.setText("Your feedback has been successfully submitted. We will try to resolve this issue as soon as we can.");
        }
        else if(getIntent().getExtras().getString("screenTypeFlag").equalsIgnoreCase("write_reviews")){
            toolbarHeaderTextThankYou.setText("WRITE REVIEWS");
            tvSuccessTextOne.setText("Your review has been submitted successfully.");
        }
        else if(getIntent().getExtras().getString("screenTypeFlag").equalsIgnoreCase("add_vendor")){
            toolbarHeaderTextThankYou.setText("ADD VENDOR");
            tvSuccessTextOne.setText("Thank you for adding your vendor. We will notify you as soon as the stall is live on the app.");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
