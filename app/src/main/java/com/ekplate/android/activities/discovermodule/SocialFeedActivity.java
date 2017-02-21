package com.ekplate.android.activities.discovermodule;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ekplate.android.R;
import com.ekplate.android.adapters.discovermodule.SocialFeedItemAdapter;
import com.ekplate.android.appinterfaces.BackgroundActionInterface;
import com.ekplate.android.config.BaseActivity;
import com.ekplate.android.models.discovermodule.SocialFeedListItem;
import com.ekplate.android.utils.CallServiceAction;
import com.ekplate.android.utils.NetworkConnectionCheck;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.gson.JsonObject;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnProfileListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.services.concurrency.internal.DefaultRetryPolicy;

public class SocialFeedActivity extends BaseActivity implements BackgroundActionInterface{

    private Toolbar tbSocialFeed;
    private TextView toolbarHeaderText;
    private RecyclerView rvSocialFeedList;
    private ArrayList<SocialFeedListItem> listItems;
    private SocialFeedItemAdapter socialFeedItemAdapter;

   // private SimpleFacebook mSimpleFacebook;
   // private Profile.Properties properties;
    private NetworkConnectionCheck _connection;
    private CallServiceAction _serviceAction;
    private ProgressDialog progressDialog;
    private String channelFeedAPI,feedType;

    List headlines;
    List links;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_feed);
        getWindow().setBackgroundDrawable(null);
        getSupportActionBar().hide();
        initialize();
        setUpToolbar();
    }

    private void getChannelType() {
        Intent intent = getIntent();
        int channelType = intent.getIntExtra("mediaChannel",0);

        switch (channelType)
        {
            case 1:
                channelFeedAPI = "get-facebook-feeds";
                feedType = "facebook";
                break;
            case 2:
                channelFeedAPI = "get-twitter-feeds";
                feedType = "twitter";
                break;
            case 3:
                channelFeedAPI = "get-instagram-feeds";
                feedType = "instagram";
                break;
        }


    }

    private void getFeed() {
        if(_connection.isNetworkAvailable()){
            _serviceAction = new CallServiceAction(SocialFeedActivity.this);
            _serviceAction.actionInterface = this;
            _serviceAction.requestVersionV2Api(null, /*"get-social-feeds"*/ channelFeedAPI);
            showProgressDialogue();

        }else{
            _connection.getNetworkActiveAlert().show();
        }

    }


 /*   @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mSimpleFacebook.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }*/

    private void initialize(){
        rvSocialFeedList = (RecyclerView) findViewById(R.id.rvSocialFeedList);
        rvSocialFeedList.setLayoutManager(new LinearLayoutManager(this));
        listItems = new ArrayList<>();
        _connection = new NetworkConnectionCheck(SocialFeedActivity.this);
        _serviceAction = new CallServiceAction(SocialFeedActivity.this);
        _serviceAction.actionInterface = this;

        /*properties = new Profile.Properties.Builder()
                .add(Profile.Properties.EMAIL)
                .add(Profile.Properties.ID)
                .add(Profile.Properties.FIRST_NAME)
                .add(Profile.Properties.LAST_NAME)
                .add(Profile.Properties.PICTURE)
                .build();*/
    }

    private void setUpToolbar(){
        toolbarHeaderText = (TextView) findViewById(R.id.toolbarHeaderText);
        tbSocialFeed = (Toolbar) findViewById(R.id.tbSocialFeed);
       // tbSocialFeed.inflateMenu(R.menu.menu_social_feed);
        tbSocialFeed.setNavigationIcon(R.drawable.ic_action_back);
        toolbarHeaderText.setText("SOCIAL FEED");
        tbSocialFeed.setBackground(getResources().getDrawable(R.drawable.toolbar_bg));
        tbSocialFeed.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getChannelType();
        getFeed();
    }

    //region Extra
   /* OnLoginListener fbLoginListener = new OnLoginListener() {
        @Override
        public void onLogin(String s, List<Permission> permissions, List<Permission> permissions2) {
            mSimpleFacebook.getProfile(properties, new OnProfileListener() {
                @Override
                public void onComplete(Profile response) {
                    super.onComplete(response);

                    FacebookSdk.sdkInitialize(getApplicationContext());
                    AccessToken accessToken = AccessToken.getCurrentAccessToken();

                    GraphRequest request = GraphRequest.newGraphPathRequest(
                            AccessToken.getCurrentAccessToken(),
                            "/ekplate2014/feed",
                            new GraphRequest.Callback() {
                                @Override
                                public void onCompleted(GraphResponse response) {
                                    Log.v("response", response.toString());
                                    //loadSocialFeed(response);
                                }
                            });
                    request.executeAsync();
                }

                @Override
                public void onException(Throwable throwable) {
                    super.onException(throwable);
                }

                @Override
                public void onFail(String reason) {
                    super.onFail(reason);
                }

                @Override
                public void onThinking() {
                    super.onThinking();
                }
            });
        }

        @Override
        public void onCancel() {
        }

        @Override
        public void onException(Throwable throwable) {

        }

        @Override
        public void onFail(String s) {

        }
    };*/

   /* private void loadSocialFeed(GraphResponse response){
        try{
            JSONObject jsonResponse  = response.getJSONObject();
            JSONArray dataArray = jsonResponse.getJSONArray("data");
            for(int i=0; i<dataArray.length(); i++){
                JSONObject dataObject = dataArray.getJSONObject(i);
                SocialFeedListItem _item = new SocialFeedListItem();
                _item.setId(dataObject.getString("id"));
                _item.setHeading(dataObject.getString("message"));
                _item.setPostingTime(dataObject.getString("created_time"));
                listItems.add(_item);
            }
            socialFeedItemAdapter = new SocialFeedItemAdapter(SocialFeedActivity.this, listItems);
            rvSocialFeedList.setAdapter(socialFeedItemAdapter);
            socialFeedItemAdapter.notifyDataSetChanged();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }*/
//endregion
    @Override
    public void onStarted() {

    }

    @Override
    public void onCompleted(JSONObject response) {

        //{"data":{"data":[{"id":"648091205317681_902581203202012","from":{"name":"ekPlate","category":"Food\/Beverages","id":"648091205317681"},"message":"Is Jhal Muri London's Most Favourite Street Food ? #ekplate #ekplatejhalmuri The everybody love love jhal muri express\nhttp:\/\/www.ekplate.com\/blog\/2016\/07\/10\/8685\/","message_tags":{"77":[{"id":"145796928806531","name":"The everybody love love jhal muri express","type":"page","offset":77,"length":41}]},"picture":"https:\/\/external.xx.fbcdn.net\/safe_image.php?d=AQBfpKs2eAfhbauv&w=130&h=130&url=https%3A%2F%2Fwww.facebook.com%2Fads%2Fimage%2F%3Fd%3DAQKIPVNQ7b65chewRFXQoxz0SgVnNW8AqE22hfP41OQFcE0pEzO4sMDxX5nqijxaODfGi6R2ieztfHpTGU6xxlOj1ErdYhsWoMP3_JDaPsiu862MID69REvVKPUrv4t3XFlOXk2r1fWcEgKUwB7T1OCg&cfs=1","link":"http:\/\/www.ekplate.com\/blog\/2016\/07\/10\/8685\/","name":"Is Jhal Muri London's Most Favourite Street Food ? #ekplate #ekplatejhalmuri - ek plate","caption":"ekplate.com","description":"Jhal muri is a popular street food snack of Kolkata. Jhal means spices and muri are puffed rice. A long time ago, Ek plate visited Kolkata and ek plate still remember this mouthwatering snack. This puffed rice is tossed with potato, cucumber, tomatoes. A special blend of spices including tamarind pu...","icon":"https:\/\/www.facebook.com\/images\/icons\/post.gif","privacy":{"value":"","description":"","friends":"","allow":"","deny":""},"type":"link","status_type":"shared_story","created_time":"2016-07-10T12:48:54+0000","updated_time":"2016-07-12T23:52:09+0000","shares":{"count":1},"is_hidden":false,"is_expired":false,"likes":{"data":[{"id":"158574354563027","name":"Ankita Brahma"},{"id":"1656163321373132","name":"Urmila Dey"},{"id":"525711620963194","name":"Suhana Keshari"},{"id":"150881068653239","name":"Aish Sharma"},{"id":"572321386306167","name":"Aditya Deb"},{"id":"817623498373535","name":"Jiten Deuri"},{"id":"1715388362061567","name":"Pratik Kundu"},{"id":"306810296321357","name":"Shahnaz Mansoori"},{"id":"301707213509299","name":"Shrabani Das"},{"id":"168803213536979","name":"Mansoor RockZz"},{"id":"1644448589214569","name":"Sany Dey"},{"id":"1737128993207062","name":"Riddham Modi Modi"},{"id":"261878170854430","name":"S Gunjan Seenam"},{"id":"2053767131515633","name":"Jāy Štær"},{"id":"1761020957517312","name":"Anamika Singh"},{"id":"313880462285803","name":"Rakesh Mundel"},{"id":"254550438264867","name":"Dipender Panwer Panwer"},{"id":"1010772529038185","name":"Viral Solanki"},{"id":"142549606171537","name":"Aradhya Roy"},{"id":"1665393133784393","name":"Ajay Machhi"},{"id":"267306683640209","name":"राणिचा बादशाह सुरज भगत"},{"id":"284776518580116","name":"Riddhi Patel"},{"id":"324335217897654","name":"Monalisha Borah"},{"id":"1658928944432464","name":"Melwyn Stewart"},{"id":"1679952202327693","name":"Nittu Nittu"}],"paging":{"cursors":{"before":"MTU4NTc0MzU0NTYzMDI3","after":"MTY3OTk1MjIwMjMyNzY5MwZDZD"},"next":"https:\/\/graph.facebook.com\/v2.2\/648091205317681_902581203202012\/likes?access_token=1523994814544561%7CAuLX5X8BTFIjBJG4DBQfpI9X_fE&limit=25&after=MTY3OTk1MjIwMjMyNzY5MwZDZD"}},"comments":{"data":[{"created_time":"2016-07-12T23:52:09+0000","from":{"name":"Amita Hembram","id":"274088089619358"},"message":"Yesss\nEast or west\nJhalmuri is the best #amibangalisobsomoye","can_remove":false,"like_count":1,"user_likes":false,"id":"902581203202012_904067283053404"},{"created_time":"2016-07-12T15:18:35+0000","from":{"name":"Shekhar Debnath","id":"235197200206679"},"message":"nice","can_remove":false,"like_count":1,"user_likes":false,"id":"902581203202012_903812869745512"},{"created_time":"2016-07-12T15:18:28+0000","from":{"name":"Shekhar Debnath","id":"235197200206679"},"message":"nicr","can_remove":false,"like_count":0,"user_likes":false,"id":"902581203202012_903812789745520"}],"paging":{"cursors":{"before":"MwZDZD","after":"MQZDZD"}}}},{"id":"648091205317681_901824676610998","from":{"name":"ekPlate","category":"Food\/Beverages","id":"648091205317681"},"message":"Learn How To Make Your
        progressDialog.dismiss();

        Log.e("FacebookFeedRes", response.toString());

            JSONObject jsonErrorObj = response.optJSONObject("errNode");
        try {
            if(jsonErrorObj.getInt("errCode")== 0){
                JSONArray jsonArrayData = response.optJSONArray("data");

if(feedType.equals("facebook"))
{

    listItems.clear();
    //JSONObject jsonObjectData = response.optJSONObject("data");
    if(jsonArrayData!=null)
    {
        for(int i =0;i<jsonArrayData.length();i++) {
            JSONObject jobj = jsonArrayData.optJSONObject(i);
            SocialFeedListItem socialItem = new SocialFeedListItem();
            socialItem.setHeading(jobj.optString("name"));
            socialItem.setImageUrl(jobj.optString("picture"));
            socialItem.setPostingTime(jobj.optString("created_time"));
            socialItem.setContent(jobj.optString("message"));
            socialItem.setLink(jobj.optString("link"));
            socialItem.setSocialType(feedType);
            listItems.add(socialItem);

        }
    }

}else if(feedType.equals("twitter")){
    listItems.clear();
    if(jsonArrayData!=null)
    {
        for(int i =0;i<jsonArrayData.length();i++) {
            JSONObject jobj = jsonArrayData.optJSONObject(i);
            SocialFeedListItem socialItem = new SocialFeedListItem();
          //  socialItem.setHeading(jobj.optString("name"));
           // socialItem.setImageUrl(jobj.optString("picture"));
            socialItem.setPostingTime(jobj.optString("created_at"));
            socialItem.setContent(jobj.optString("text"));
          //  socialItem.setLink(jobj.optString("link"));
            socialItem.setSocialType(feedType);
            listItems.add(socialItem);
        }
    }
}else {
    listItems.clear();
    if(jsonArrayData!=null)
    {
        for(int i =0;i<jsonArrayData.length();i++) {
            JSONObject jobj = jsonArrayData.optJSONObject(i);
            SocialFeedListItem socialItem = new SocialFeedListItem();
            //  socialItem.setHeading(jobj.optString("name"));

            socialItem.setPostingTime(jobj.optString("created_time"));

              socialItem.setLink(jobj.optString("link"));
            JSONObject jsImages = jobj.optJSONObject("images");
            JSONObject jsThumbnail = jsImages.optJSONObject("thumbnail");
             socialItem.setImageUrl(jsThumbnail.optString("url"));

            JSONObject jsCaption = jobj.optJSONObject("caption");
            socialItem.setContent(jsCaption.optString("text"));
            socialItem.setSocialType(feedType);
            listItems.add(socialItem);
        }
    }
}

                }else {
                Toast.makeText(this,jsonErrorObj.getString("errMsg").toString(),Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        socialFeedItemAdapter = new SocialFeedItemAdapter(SocialFeedActivity.this, listItems);
        rvSocialFeedList.setAdapter(socialFeedItemAdapter);
        socialFeedItemAdapter.notifyDataSetChanged();
    }

    private void showProgressDialogue(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
    }
}
