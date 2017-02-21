package com.ekplate.android.activities.menumodule;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ekplate.android.R;
import com.ekplate.android.adapters.menumodule.FaqListAdapter;
import com.ekplate.android.appinterfaces.BackgroundActionInterface;
import com.ekplate.android.config.BaseActivity;
import com.ekplate.android.models.menumodule.FaqAnswerItem;
import com.ekplate.android.models.menumodule.FaqQuestionItem;
import com.ekplate.android.utils.CallServiceAction;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.NetworkConnectionCheck;
import com.ekplate.android.utils.Pref;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FaqActivity extends BaseActivity implements BackgroundActionInterface {

    private Toolbar tbFaq;
    private TextView toolbarHeaderText;
    private EditText etUserQuestionFaq;
    private LinearLayout llUserQuestionSubmitBtn;
    private ExpandableListView exlvFaqList;
    private ArrayList<FaqQuestionItem> faqQuestionItems;
    private FaqListAdapter faqListAdapter;
    private Pref _pref;
    private NetworkConnectionCheck _connection;
    private CallServiceAction _serviceAction;
    private LinearLayout llFaqContainer, llFaqProgressBarContainer, llErrorThreeFaq;
    private int userQuestionFlag = 0;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        getSupportActionBar().hide();
        getWindow().setBackgroundDrawable(null);
        initialize();
        setUpToolBar();
        setUpFaqList();
        onClick();
    }

    private void setUpToolBar(){
        tbFaq = (Toolbar) findViewById(R.id.tbFaq);
        toolbarHeaderText = (TextView) findViewById(R.id.toolbarHeaderText);
        toolbarHeaderText.setText("FAQ");
        tbFaq.setBackground(getResources().getDrawable(R.drawable.toolbar_bg));
        tbFaq.setNavigationIcon(R.drawable.ic_action_back);
        tbFaq.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void initialize(){
        llFaqContainer = (LinearLayout) findViewById(R.id.llFaqContainer);
        llFaqProgressBarContainer = (LinearLayout) findViewById(R.id.llFaqProgressBarContainer);
        llErrorThreeFaq = (LinearLayout) findViewById(R.id.llErrorThreeFaq);
        exlvFaqList = (ExpandableListView) findViewById(R.id.exlvFaqList);
        etUserQuestionFaq = (EditText) findViewById(R.id.etUserQuestionFaq);;
        llUserQuestionSubmitBtn = (LinearLayout) findViewById(R.id.llUserQuestionSubmitBtn);
        faqQuestionItems = new ArrayList<FaqQuestionItem>();
        faqListAdapter = new FaqListAdapter(this, faqQuestionItems);
        _pref = new Pref(this);
        _connection = new NetworkConnectionCheck(this);
        _serviceAction = new CallServiceAction(this);
        _serviceAction.actionInterface = FaqActivity.this;
    }

    private void onClick(){
        llUserQuestionSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etUserQuestionFaq.getText().toString().equals("")) {
                    Toast.makeText(FaqActivity.this, "Please give you question", Toast.LENGTH_LONG).show();
                } else {
                    userQuestionFlag = 1;
                    setUpProgressDialog();
                    setInputParamForUserQuestion(etUserQuestionFaq.getText().toString());
                }
            }
        });
    }

    private void setUpFaqList(){
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            exlvFaqList.setIndicatorBounds(width - GetPixelFromDips(50),
                    width - GetPixelFromDips(50));
             exlvFaqList.setIndicatorBoundsRelative(width
                    - GetPixelFromDips(50), width - GetPixelFromDips(50));
        }
        exlvFaqList.setAdapter(faqListAdapter);
        exlvFaqList.setDivider(null);
        if(_connection.isNetworkAvailable()) {
            setInputParamForFaqList();
        } else {
            llFaqProgressBarContainer.setVisibility(View.GONE);
            llErrorThreeFaq.setVisibility(View.VISIBLE);
        }
    }

    private void setInputParamForFaqList(){
        try {
            JSONObject parentJsonObj = new JSONObject();
            JSONObject childJsonObj = new JSONObject();
            childJsonObj.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));
            parentJsonObj.put("data", childJsonObj);
            Log.e("input data", parentJsonObj.toString());
            _serviceAction.requestVersionApi(parentJsonObj, "faq-list");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setInputParamForUserQuestion(String question){
        try {
            JSONObject parentJsonObj = new JSONObject();
            JSONObject childJsonObj = new JSONObject();
            childJsonObj.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));
            childJsonObj.put("askQuestion", question);
            parentJsonObj.put("data", childJsonObj);
            Log.e("input data", parentJsonObj.toString());
            _serviceAction.requestVersionApi(parentJsonObj, "ask-question-by-user");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public int GetPixelFromDips(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    private void setUpProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait..");
        progressDialog.show();
    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onCompleted(JSONObject response) {
        if(userQuestionFlag == 0) {
            try {
                JSONObject errorNodeJsonObj = response.getJSONObject("errNode");
                if (errorNodeJsonObj.getInt("errCode") == 0) {
                    JSONObject dataJsonObject = response.getJSONObject("data");
                    JSONArray faqListJsonArray = dataJsonObject.getJSONArray("FaqList");
                    for (int i = 0; i < faqListJsonArray.length(); i++) {
                        JSONObject faqListItemJsonObj = faqListJsonArray.getJSONObject(i);
                        FaqQuestionItem _questionItem = new FaqQuestionItem();
                        ArrayList<FaqAnswerItem> faqAnswerItems = new ArrayList<FaqAnswerItem>();
                        FaqAnswerItem _answerItem = new FaqAnswerItem();
                        _answerItem.setId(i);
                        _answerItem.setAnswer(faqListItemJsonObj.getString("faqDetails"));
                        faqAnswerItems.add(_answerItem);
                        _questionItem.setId(i);
                        _questionItem.setQuestion(faqListItemJsonObj.getString("faqTitle"));
                        _questionItem.setFaqAnswerItems(faqAnswerItems);

                        faqQuestionItems.add(_questionItem);
                    }

                    faqListAdapter.notifyDataSetChanged();
                    llFaqContainer.setVisibility(View.VISIBLE);
                    llFaqProgressBarContainer.setVisibility(View.GONE);
                } else {
                    llFaqProgressBarContainer.setVisibility(View.GONE);
                    llErrorThreeFaq.setVisibility(View.VISIBLE);
                    Toast.makeText(FaqActivity.this, errorNodeJsonObj.getString("errMsg"), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                JSONObject errorNodeJsonObj = response.getJSONObject("errNode");
                if (errorNodeJsonObj.getInt("errCode") == 0) {
                    JSONObject dataJsonObject = response.getJSONObject("data");
                    if (dataJsonObject.getBoolean("success")){
                        etUserQuestionFaq.setText("");
                        progressDialog.dismiss();
                        Intent intent = new Intent(FaqActivity.this, QuestionSubmitResultActivity.class);
                        intent.putExtra("screenTypeFlag", "faq");
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    } else {
                        Toast.makeText(FaqActivity.this, dataJsonObject.getString("msg"), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(FaqActivity.this, errorNodeJsonObj.getString("errMsg"), Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
