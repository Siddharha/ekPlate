package com.ekplate.android.activities.discovermodule;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ekplate.android.R;
import com.ekplate.android.activities.vendormodule.VendorsActivity;
import com.ekplate.android.adapters.discovermodule.RssFeedItemAdapter;
import com.ekplate.android.config.BaseActivity;
import com.ekplate.android.models.discovermodule.RssFeedListItem;
import com.ekplate.android.utils.NetworkConnectionCheck;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Avishek on 7/11/2016.
 */
public class VendorStoriesActivity extends BaseActivity {

    private Toolbar tbRssFeed;
    private TextView toolbarHeaderText;
    private RecyclerView rvRssFeedList;
    private ArrayList<RssFeedListItem> listItems;
    private RssFeedItemAdapter rssFeedItemAdapter;
    private String title;
    private ProgressDialog progressDialog;
    private ProgressBar pbLoader;
    private LinearLayoutManager layoutManager;
    int visibleItemCount,totalItemCount,pastVisiblesItems,paginatorMultiplyer = 0,previousTotal = 0;
    boolean loading = true;
    private int pageCount = 0;
    private NetworkConnectionCheck _connection_check;

    List headlines;
    List links;
    List content;
    List Date;
    List Media;
    int flag = 0;
    //ProgressDialog prDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rss_feed);
        //getWindow().setBackgroundDrawable(null);
        getSupportActionBar().hide();
        initialize();
        setUpToolbar();
        new StoriesActivityAsync().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void initializeRssFeed() {
        loadPageProgress();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        headlines = new ArrayList();
        links = new ArrayList();
        content = new ArrayList();
        Date = new ArrayList();
        Media = new ArrayList();

        try {
            // URL url = new URL("http://www.india.com/feed/");
            URL url = new URL(getIntent().getStringExtra("link"));
            Log.e("urlXML",url.toString());
            title = getIntent().getStringExtra("title");
            toolbarHeaderText.setText(title);
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser xpp = factory.newPullParser();

            // We will get the XML from an input stream
            xpp.setInput(getInputStream(url), "UTF_8");

            boolean insideItem = false;

            // Returns the type of current event: START_TAG, END_TAG, etc..
            int eventType = xpp.getEventType();



            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {

                    String s = xpp.getAttributeValue(null,"language");
                    if(xpp.getName().equalsIgnoreCase("totalcount")){
                        pageCount = Integer.parseInt(xpp.nextText());
                        Log.e("total_page_count",String.valueOf(pageCount));
                    }

                    if (xpp.getName().equalsIgnoreCase("item")) {
                        insideItem = true;
                    } else if (xpp.getName().equalsIgnoreCase("title")) {
                        if (insideItem)
                            headlines.add(xpp.nextText()); //extract the headline
                    } else if (xpp.getName().equalsIgnoreCase("link")) {
                        if (insideItem)
                            links.add(xpp.nextText()); //extract the link of article
                    } else if (xpp.getName().equalsIgnoreCase("description")) {
                        if (insideItem)
                            content.add(xpp.nextText());
                    } else if (xpp.getName().equalsIgnoreCase("pubDate")) {
                        if (insideItem)
                            Date.add(xpp.nextText());
                    } else if (xpp.getName().equalsIgnoreCase("media")) {
                        if (insideItem){
                            Media.add(xpp.getAttributeValue(null,"url"));
                        }

                    }
                    }else if(eventType== XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")){
                    insideItem=false;
                }

                eventType = xpp.next(); //move to next element
            }

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

    }

    private void loadPageProgress() {
        runOnUiThread(new Runnable() {
            public void run() {
                try{
                    progressDialog.setMessage("Loading...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }catch (WindowManager.BadTokenException e){
                    e.printStackTrace();
                }

            }
        });

    }

    public InputStream getInputStream(URL url) {
        try {
            return url.openConnection().getInputStream();
        } catch (IOException e) {
            return null;
        }
    }


    private void initialize(){
        pbLoader = (ProgressBar)findViewById(R.id.pbLoader);
        rvRssFeedList = (RecyclerView) findViewById(R.id.rvRssFeedList);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvRssFeedList.setLayoutManager(layoutManager);
       /* prDialog = ProgressDialog.show(VendorStoriesActivity.this, null, "loading, please wait...");*/
        listItems = new ArrayList<>();
        _connection_check = new NetworkConnectionCheck(this);
        progressDialog = new ProgressDialog(this);
    }

    private void setUpToolbar(){
        toolbarHeaderText = (TextView) findViewById(R.id.toolbarHeaderText);
        tbRssFeed = (Toolbar) findViewById(R.id.tbRssFeed);
       // tbRssFeed.inflateMenu(R.menu.menu_social_feed);
        tbRssFeed.setNavigationIcon(R.drawable.ic_action_back);
        tbRssFeed.setBackground(getResources().getDrawable(R.drawable.toolbar_bg));
        tbRssFeed.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        rvRssFeedList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(dy > 0) //check for scroll down
                {

                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();
                    if(layoutManager.findLastVisibleItemPosition() == layoutManager.getItemCount()-1){
                       // Toast.makeText(VendorStoriesActivity.this, "XXXX", Toast.LENGTH_SHORT).show();
                       if(flag == 1){

                           if(listItems.size()< pageCount-1){
                               new PagerAsync().execute();
                           }

                       }

                    }
                   /* if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            Toast.makeText(VendorStoriesActivity.this, "You Scrolled Down!!", Toast.LENGTH_SHORT).show();

                            if (paginatorMultiplyer <= 1) {
                                paginatorMultiplyer++;
                            }
                            new PagerAsync().execute();
                            // _serviceAction.requestVersionV2Api(jsonObjectData, "get-facebook-feeds");

                        }
                    }*/
                }
            }

        });
    }

    private void requestPaginationFeed() {
        try {
            flag =0;
            // URL url = new URL("http://www.india.com/feed/");
            paginatorMultiplyer++;
            URL url = new URL(getIntent().getStringExtra("link")+"&start="+(20*paginatorMultiplyer));
            Log.e("next_url_xml",url.toString());
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser xpp = factory.newPullParser();

            // We will get the XML from an input stream
            xpp.setInput(getInputStream(url), "UTF_8");

            boolean insideItem = false;

            // Returns the type of current event: START_TAG, END_TAG, etc..
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {

                    if (xpp.getName().equalsIgnoreCase("item")) {
                        insideItem = true;
                    } else if (xpp.getName().equalsIgnoreCase("title")) {
                        if (insideItem)
                            headlines.add(xpp.nextText()); //extract the headline
                    } else if (xpp.getName().equalsIgnoreCase("link")) {
                        if (insideItem)
                            links.add(xpp.nextText()); //extract the link of article
                    } else if (xpp.getName().equalsIgnoreCase("description")) {
                        if (insideItem)
                            content.add(xpp.nextText());
                    } else if (xpp.getName().equalsIgnoreCase("pubDate")) {
                        if (insideItem)
                            Date.add(xpp.nextText());
                    } else if (xpp.getName().equalsIgnoreCase("media")) {
                        if (insideItem){
                            Media.add(xpp.getAttributeValue(null,"url"));
                        }

                    }

                }else if(eventType== XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")){
                    insideItem=false;
                }

                eventType = xpp.next(); //move to next element
            }

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void loadRssFeed(){
        try{
            int prevSize=listItems.size();
            for(int i=prevSize; i<headlines.size(); i++){
                RssFeedListItem _item = new RssFeedListItem();
                _item.setHeading(headlines.get(i).toString());
                _item.setLink(links.get(i).toString());
                _item.setContent(content.get(i).toString());
                _item.setPostingTime(Date.get(i).toString());
                if(Media.get(i)!=null){
                    _item.setImageUrl(Media.get(i).toString());
                }else {
                    _item.setImageUrl("");
                }

                listItems.add(_item);
            }
            flag =1;
            //prDialog.dismiss();
            if(listItems.size() > 20){
                rssFeedItemAdapter.notifyDataSetChanged();
            }else{
                rssFeedItemAdapter = new RssFeedItemAdapter(VendorStoriesActivity.this, listItems);
                rvRssFeedList.setAdapter(rssFeedItemAdapter);
            }


        }
        catch (Exception e){
            e.printStackTrace();
        }
        try{
            progressDialog.dismiss();
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }

        //rssFeedItemAdapter.notifyDataSetChanged();
       // rvRssFeedList.refreshDrawableState();
    }

    /*private void loadRssFeedFirstTime(){

        try{
            for(int i=0; i<5; i++){
                RssFeedListItem _item = new RssFeedListItem();
                _item.setHeading(headlines.get(i).toString());
                _item.setLink(links.get(i).toString());
                _item.setContent(content.get(i).toString());
                _item.setPostingTime(Date.get(i).toString());
                if(Media.get(i)!=null){
                    _item.setImageUrl(Media.get(i).toString());
                }else {
                    _item.setImageUrl("");
                }
                listItems.add(_item);
            }
           // prDialog.dismiss();
            rssFeedItemAdapter = new RssFeedItemAdapter(VendorStoriesActivity.this, listItems);
            rvRssFeedList.setAdapter(rssFeedItemAdapter);
           // rssFeedItemAdapter.notifyDataSetChanged();
            //rvRssFeedList.refreshDrawableState();

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }*/

    private class StoriesActivityAsync extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            if(_connection_check.isNetworkAvailable()){
                initializeRssFeed();
            }else{
                try{
                    _connection_check.getNetworkActiveAlert().show();
                }catch (WindowManager.BadTokenException e){
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //loadRssFeedFirstTime();
            loadRssFeed();
            //rssFeedItemAdapter.notifyDataSetChanged();
            getWindow().getDecorView().invalidate();

        }
    }

    private class PagerAsync extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... params) {
            if(_connection_check.isNetworkAvailable()){
                    requestPaginationFeed();

            }else{
                try{
                   _connection_check.getNetworkActiveAlert().show();
                }catch(WindowManager.BadTokenException e){
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try{
                pbLoader.setVisibility(View.VISIBLE);
            }catch (WindowManager.BadTokenException e){
                e.printStackTrace();
            }

            //loading = true;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //loading = false;
            loadRssFeed();
            getWindow().getDecorView().invalidate();
            try{
                pbLoader.setVisibility(View.GONE);
            }catch (IllegalArgumentException e){
                e.printStackTrace();
            }

        }
    }
}

