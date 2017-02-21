package com.ekplate.android.activities.menumodule;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract.Contacts;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import com.ekplate.android.R;
import com.ekplate.android.config.BaseActivity;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.Pref;

import java.io.IOException;

public class DevOptionActivity extends BaseActivity {

    private Toolbar tbHelpToImprove;
    private TextView toolbarHeaderText;
    private CardView btnChangeUrl,btnLogPrint;
    private Switch swLog;
    private Pref _pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_option);
        getSupportActionBar().hide();
        getWindow().setBackgroundDrawable(null);
        setUpToolBar();
        initialize();
        getState();
        onClick();
    }

    private void getState() {
            swLog.setChecked(_pref.getLogTrackStatus());
    }

    private void initialize() {
        btnChangeUrl = (CardView)findViewById(R.id.btnChangeUrl);
        btnLogPrint = (CardView)findViewById(R.id.btnLogPrint);
        swLog = (Switch)findViewById(R.id.swLog);
        _pref = new Pref(this);
    }

    private void onClick() {
        btnChangeUrl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                changeUrlPopup();
            }
        });

        btnLogPrint.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setLogTracker();
            }
        });

        swLog.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setLogTracker();
            }
        });
    }

    private void changeUrlPopup() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("CHANGE URL");
        alertDialog.setMessage("Enter Base URL");
        final EditText input = new EditText(this);

        input.setHint("Ex. "+ConstantClass.BASE_URL_DEF);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                ConstantClass.BASE_URL = input.getText().toString();
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        alertDialog.show();
    }

    private void setLogTracker() {

        if(swLog.isChecked())
        {
            swLog.setChecked(false);
            _pref.saveLogTrackStatus(false);
        }else {
            swLog.setChecked(true);
            _pref.saveLogTrackStatus(true);
        }


        //Toast.makeText(DevOptionActivity.this, "Not Implemented Yet!", Toast.LENGTH_SHORT).show();
    }


    private void setUpToolBar(){
        tbHelpToImprove = (Toolbar) findViewById(R.id.tbHelpToImprove);
        toolbarHeaderText = (TextView) findViewById(R.id.toolbarHeaderText);
        toolbarHeaderText.setText("DEVELOPERS MODE");
        tbHelpToImprove.setBackground(getResources().getDrawable(R.drawable.toolbar_bg));
        tbHelpToImprove.setNavigationIcon(R.drawable.ic_action_back);
        tbHelpToImprove.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }



}
