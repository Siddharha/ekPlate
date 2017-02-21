package com.ekplate.android.activities.menumodule;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ekplate.android.R;
import com.ekplate.android.adapters.menumodule.NotificationAdapter;
import com.ekplate.android.appinterfaces.BackgroundActionInterface;
import com.ekplate.android.config.BaseActivity;
import com.ekplate.android.models.menumodule.NotificationItem;
import com.ekplate.android.utils.CallServiceAction;
import com.ekplate.android.utils.ConstantClass;
import com.ekplate.android.utils.NetworkConnectionCheck;
import com.ekplate.android.utils.Pref;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class NotificationActivity extends BaseActivity
        implements BackgroundActionInterface {

    private Toolbar tbNotification;
    private TextView toolbarHeaderText;
    private LinearLayout llNotificationProgressBarContainer,llNotificationListContainer, llErrorThreeNotification;
    private RecyclerView rcNotificationList;
    private ArrayList<NotificationItem> notificationItems;
    private NotificationAdapter adapter;
    private Pref _pref;
    private NetworkConnectionCheck _connection;
    private CallServiceAction _serviceAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        getSupportActionBar().hide();
        getWindow().setBackgroundDrawable(null);
        initialize();
        setUpToolBar();
        setUpNotificationList();
    }

    private void initialize(){
        llNotificationProgressBarContainer = (LinearLayout) findViewById(R.id.llNotificationProgressBarContainer);
        llErrorThreeNotification = (LinearLayout) findViewById(R.id.llErrorThreeNotification);
        llNotificationListContainer = (LinearLayout) findViewById(R.id.llNotificationListContainer);
        rcNotificationList = (RecyclerView) findViewById(R.id.rcNotificationList);
        rcNotificationList.setLayoutManager(new LinearLayoutManager(this));
        notificationItems = new ArrayList<NotificationItem>();
        adapter = new NotificationAdapter(this, notificationItems);
        _pref = new Pref(this);
        _connection = new NetworkConnectionCheck(this);
        _serviceAction = new CallServiceAction(this);
        _serviceAction.actionInterface = NotificationActivity.this;
    }

    private void setUpToolBar(){
        tbNotification = (Toolbar) findViewById(R.id.tbNotification);
        toolbarHeaderText = (TextView) findViewById(R.id.toolbarHeaderText);
        toolbarHeaderText.setText("Notification");
        tbNotification.setBackground(getResources().getDrawable(R.drawable.toolbar_bg));
        tbNotification.setNavigationIcon(R.drawable.ic_action_back);
        tbNotification.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void setUpNotificationList(){
        rcNotificationList.setAdapter(adapter);
        if(_connection.isNetworkAvailable()) {
            setInputParamForNotificationList();
        } else {
            llNotificationProgressBarContainer.setVisibility(View.GONE);
            llErrorThreeNotification.setVisibility(View.VISIBLE);
        }
    }

    /*private void loadItems(){
        for (int i=1; i<=10; i++){
            NotificationItem _item = new NotificationItem();
            _item.setId(i);
            notificationItems.add(_item);
        }
        adapter.notifyDataSetChanged();
    }*/

    private void setInputParamForNotificationList(){
        try {
            JSONObject parentJsonObj = new JSONObject();
            JSONObject childJsonObj = new JSONObject();
            childJsonObj.put("accessToken", _pref.getSession(ConstantClass.ACCESS_TOKEN));
            parentJsonObj.put("data", childJsonObj);
            Log.e("input data", parentJsonObj.toString());
            _serviceAction.requestVersionApi(parentJsonObj, "get-notifications");
        } catch (Exception e){
            e.printStackTrace();
        }
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
        Log.e("notice response", response.toString());
        try {
            JSONObject errorNodeJsonObj = response.getJSONObject("errNode");
            if (errorNodeJsonObj.getInt("errCode") == 0) {
                JSONObject dataJsonObject = response.getJSONObject("data");
                JSONArray notificationListJsonArray = dataJsonObject.getJSONArray("notificationItem");
                for (int i = 0; i < notificationListJsonArray.length(); i++) {
                    JSONObject notificationItemJsonObj = notificationListJsonArray.getJSONObject(i);
                    NotificationItem _item = new NotificationItem();
                    _item.setId(notificationItemJsonObj.getInt("id"));
                    _item.setImageUrl(notificationItemJsonObj.getString("imageUrl"));
                    _item.setHeading(notificationItemJsonObj.getString("notificationHeading"));
                    _item.setBodyText(notificationItemJsonObj.getString("notificationBody"));
                    _item.setTime(notificationItemJsonObj.getString("notificationDate"));
                    notificationItems.add(_item);
                }
                adapter.notifyDataSetChanged();
                llNotificationListContainer.setVisibility(View.VISIBLE);
                llNotificationProgressBarContainer.setVisibility(View.GONE);
            } else {
                llNotificationProgressBarContainer.setVisibility(View.GONE);
                llErrorThreeNotification.setVisibility(View.VISIBLE);
                Toast.makeText(NotificationActivity.this, errorNodeJsonObj.getString("errMsg"), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
