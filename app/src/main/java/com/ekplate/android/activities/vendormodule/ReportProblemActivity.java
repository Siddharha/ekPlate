package com.ekplate.android.activities.vendormodule;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ekplate.android.R;
import com.ekplate.android.activities.menumodule.QuestionSubmitResultActivity;
import com.ekplate.android.appinterfaces.BackgroundActionInterface;
import com.ekplate.android.config.BaseActivity;
import com.ekplate.android.utils.CallServiceAction;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.NetworkConnectionCheck;
import com.ekplate.android.utils.Pref;

import org.json.JSONObject;

public class ReportProblemActivity extends BaseActivity implements BackgroundActionInterface {

    private Toolbar tbReportProblem;
    private TextView toolbarHeaderText;
    private ImageView ivPhoneNoSelect, ivAddressSelect, ivOutletClosedSelect, ivMenuIncorrectSelect,
            ivImageNotAppropriate;
    private RelativeLayout rlPhoneNoReportProblem, rlAddressReportProblem, rlOutletClosed, rlMenuIncorrect,
            rlImageNotAppropriate;
    private EditText etDetailedReport;
    private Button btnSubmitReport;
    private boolean phoneNoSelectFlag = false, addressSelectFlag = false, outletClosedSelectFlag = false,
            menuIncorrectSelectFlag = false, imageNotAppropriateFlag = false;
    private Pref _pref;
    private NetworkConnectionCheck _connection;
    private CallServiceAction _serviceAction;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_problem);
        getSupportActionBar().hide();
        getWindow().setBackgroundDrawable(null);
        setUpToolBar();
        initialize();
        onClick();
    }

    private void initialize(){
        ivPhoneNoSelect = (ImageView) findViewById(R.id.ivPhoneNoSelect);
        ivAddressSelect = (ImageView) findViewById(R.id.ivAddressSelect);
        ivOutletClosedSelect = (ImageView) findViewById(R.id.ivOutletClosedSelect);
        ivMenuIncorrectSelect = (ImageView) findViewById(R.id.ivMenuIncorrectSelect);
        ivImageNotAppropriate = (ImageView) findViewById(R.id.ivImageNotAppropriate);
        rlPhoneNoReportProblem = (RelativeLayout) findViewById(R.id.rlPhoneNoReportProblem);
        rlAddressReportProblem = (RelativeLayout) findViewById(R.id.rlAddressReportProblem);
        rlOutletClosed = (RelativeLayout) findViewById(R.id.rlOutletClosed);
        rlMenuIncorrect = (RelativeLayout) findViewById(R.id.rlMenuIncorrect);
        rlImageNotAppropriate = (RelativeLayout) findViewById(R.id.rlImageNotAppropriate);
        etDetailedReport = (EditText) findViewById(R.id.etDetailedReport);
        btnSubmitReport = (Button) findViewById(R.id.btnSubmitReport);
        _pref = new Pref(this);
        _connection = new NetworkConnectionCheck(this);
        _serviceAction = new CallServiceAction(this);
        _serviceAction.actionInterface = this;
    }

    private void setUpToolBar(){
        tbReportProblem = (Toolbar) findViewById(R.id.tbReportProblem);
        toolbarHeaderText = (TextView) findViewById(R.id.toolbarHeaderText);
        toolbarHeaderText.setText("REPORT PROBLEM");
        tbReportProblem.setBackground(getResources().getDrawable(R.drawable.toolbar_bg));
        tbReportProblem.setNavigationIcon(R.drawable.ic_action_back);
        tbReportProblem.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void onClick(){
        ivPhoneNoSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!phoneNoSelectFlag){
                    phoneNoSelectFlag = true;
                    ivPhoneNoSelect.setSelected(true);
                } else {
                    phoneNoSelectFlag = false;
                    ivPhoneNoSelect.setSelected(false);
                }
            }
        });

        rlPhoneNoReportProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!phoneNoSelectFlag){
                    phoneNoSelectFlag = true;
                    ivPhoneNoSelect.setSelected(true);
                } else {
                    phoneNoSelectFlag = false;
                    ivPhoneNoSelect.setSelected(false);
                }
            }
        });

        ivAddressSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!addressSelectFlag) {
                    addressSelectFlag = true;
                    ivAddressSelect.setSelected(true);
                } else {
                    addressSelectFlag = false;
                    ivAddressSelect.setSelected(false);
                }
            }
        });

        rlAddressReportProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!addressSelectFlag) {
                    addressSelectFlag = true;
                    ivAddressSelect.setSelected(true);
                } else {
                    addressSelectFlag = false;
                    ivAddressSelect.setSelected(false);
                }
            }
        });

        ivOutletClosedSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!outletClosedSelectFlag) {
                    outletClosedSelectFlag = true;
                    ivOutletClosedSelect.setSelected(true);
                } else {
                    outletClosedSelectFlag = false;
                    ivOutletClosedSelect.setSelected(false);
                }
            }
        });

        rlOutletClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!outletClosedSelectFlag) {
                    outletClosedSelectFlag = true;
                    ivOutletClosedSelect.setSelected(true);
                } else {
                    outletClosedSelectFlag = false;
                    ivOutletClosedSelect.setSelected(false);
                }
            }
        });


        ivMenuIncorrectSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!menuIncorrectSelectFlag) {
                    menuIncorrectSelectFlag = true;
                    ivMenuIncorrectSelect.setSelected(true);
                } else {
                    menuIncorrectSelectFlag = false;
                    ivMenuIncorrectSelect.setSelected(false);
                }
            }
        });

        rlMenuIncorrect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!menuIncorrectSelectFlag) {
                    menuIncorrectSelectFlag = true;
                    ivMenuIncorrectSelect.setSelected(true);
                } else {
                    menuIncorrectSelectFlag = false;
                    ivMenuIncorrectSelect.setSelected(false);
                }
            }
        });

        ivImageNotAppropriate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!imageNotAppropriateFlag) {
                    imageNotAppropriateFlag = true;
                    ivImageNotAppropriate.setSelected(true);
                } else {
                    imageNotAppropriateFlag = false;
                    ivImageNotAppropriate.setSelected(false);
                }
            }
        });

        rlImageNotAppropriate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!imageNotAppropriateFlag) {
                    imageNotAppropriateFlag = true;
                    ivImageNotAppropriate.setSelected(true);
                } else {
                    imageNotAppropriateFlag = false;
                    ivImageNotAppropriate.setSelected(false);
                }
            }
        });

        btnSubmitReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_connection.isNetworkAvailable()) {
                    setUpProgressDialog();
                    setInputParamForReportProblem(phoneNoSelectFlag, addressSelectFlag, outletClosedSelectFlag,
                            menuIncorrectSelectFlag, imageNotAppropriateFlag, etDetailedReport.getText().toString());
                } else {
                    _connection.getNetworkActiveAlert().show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onCompleted(JSONObject response) {
        try {
            progressDialog.dismiss();
            JSONObject jsonObjError = response.getJSONObject("errNode");
            if (jsonObjError.getInt("errCode") == 0) {
                JSONObject jsonObjData = response.getJSONObject("data");
                if (jsonObjData.getBoolean("success")) {
                    Intent intent = new Intent(ReportProblemActivity.this, QuestionSubmitResultActivity.class);
                    intent.putExtra("screenTypeFlag", "report_problem");
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    Toast.makeText(this, jsonObjData.getString("msg"), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, jsonObjError.getString("errMsg"), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setInputParamForReportProblem(boolean isPhoneNo, boolean isAddress, boolean isOutLetClosed,
                                               boolean isMenuIncorrectOrOutDated, boolean isImageNotAppropriate,
                                               String detailReport){
        try {
            JSONObject parentJsonObj = new JSONObject();
            JSONObject childJsonObj = new JSONObject();
            childJsonObj.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));
            /////////// Will Be Changed Here ///////////
            /*if (getIntent().getExtras().getString("routFrom").equalsIgnoreCase("vendorDetails")) {
                childJsonObj.put("vendorId", "vendor-" + String.valueOf(getIntent().getExtras().getInt("vendorId")));
            } else {
                childJsonObj.put("vendorId", "vendor-" + String.valueOf(getIntent().getExtras().getInt("vendorId")));
            }*/
            //////////////// End  //////////////////
            childJsonObj.put("vendorId", String.valueOf(getIntent().getExtras().getInt("vendorId")));
            childJsonObj.put("isPhNo", isPhoneNo);
            childJsonObj.put("isAddress", isAddress);
            childJsonObj.put("isOutLetclosed", isOutLetClosed);
            childJsonObj.put("isMenuIncorrectOrOutDated", isMenuIncorrectOrOutDated);
            childJsonObj.put("isImageProper", isImageNotAppropriate);
            childJsonObj.put("imageId", isImageNotAppropriate);
            childJsonObj.put("imgType", "1");
            childJsonObj.put("detailReport", detailReport);
            parentJsonObj.put("data", childJsonObj);
            Log.e("input data", parentJsonObj.toString());
            _serviceAction.requestVersionApi(parentJsonObj, "report-problem");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setUpProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
}
