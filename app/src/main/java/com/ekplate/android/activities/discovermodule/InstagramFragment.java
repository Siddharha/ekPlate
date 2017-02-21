package com.ekplate.android.activities.discovermodule;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ekplate.android.R;
import com.ekplate.android.adapters.discovermodule.SocialFeedItemAdapter;
import com.ekplate.android.appinterfaces.BackgroundActionInterface;
import com.ekplate.android.models.discovermodule.SocialFeedListItem;
import com.ekplate.android.utils.CallServiceAction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Avishek on 10/13/2016.
 */
public class InstagramFragment extends Fragment implements BackgroundActionInterface {

    private RecyclerView rvSocialFeedList;
    private View rootView;
    private ArrayList<SocialFeedListItem> listItems;
    private SocialFeedItemAdapter socialFeedItemAdapter;
    private ProgressDialog progressDialog;
    private CallServiceAction _serviceAction;
    String nextUrlString="";
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    LinearLayoutManager mLayoutManager;
    private ProgressBar pbINLoader;
    private boolean loading = true;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.instagram_chennel_fragment, container, false);
        this.rootView = rootView;
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialize();
        onClick();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void getFeed() {
        pbINLoader.setVisibility(View.VISIBLE);
        _serviceAction.requestVersionV2Api(null, "get-instagram-feeds");
      //  showProgressDialogue();

    }

    private void showProgressDialogue() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
    }

    private void initialize() {
        rvSocialFeedList = (RecyclerView)rootView.findViewById(R.id.rvSocialFeedList);
        mLayoutManager=new LinearLayoutManager(getActivity());
        rvSocialFeedList.setLayoutManager(mLayoutManager);
        listItems = new ArrayList<>();
        _serviceAction = new CallServiceAction(getActivity());
        _serviceAction.actionInterface = this;
        pbINLoader = (ProgressBar)rootView.findViewById(R.id.pbINLoader);
        getFeed();
        /*rvSocialFeedList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();
                  *//*  if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            Log.d("farman-twitter", "Last Item");
                          //  getFeed();
                            showProgressDialogue();
                            _serviceAction.requestVersionV2Api(null, nextUrlString);

                        }
                    }*//*

                    if(mLayoutManager.findLastVisibleItemPosition() == mLayoutManager.getItemCount()-1){
                      //  Toast.makeText(getActivity(), "XXXX", Toast.LENGTH_SHORT).show();
                        getFeed();
                    }
                }
            }
        });*/
    }


    private void onClick() {
    }

    private void displayList() {



        //ada = new AllTaskAdapter( arrayListAll,R.layout.all_task_cell_layout,getContext());
        // rlAllTasks.setAdapter(allAdapter);

       /* if(arrayListAll.isEmpty())
        {
            tvNotask.setVisibility(View.VISIBLE);
            rlAllTasks.setVisibility(View.INVISIBLE);
        }else {
            tvNotask.setVisibility(View.GONE);
            rlAllTasks.setVisibility(View.VISIBLE);
        }*/



    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onCompleted(JSONObject response) {
        Log.e("FacebookFeedRes", response.toString());
        //progressDialog.dismiss();
        pbINLoader.setVisibility(View.GONE);
        JSONObject jsonErrorObj = response.optJSONObject("errNode");
        try {
            if(jsonErrorObj.getInt("errCode")== 0){
                JSONArray jsonArrayData = response.optJSONArray("data");
               nextUrlString=response.getJSONObject("pagination").getString("next_url");
                Log.d("farman-next-url",nextUrlString);
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
                        if(!jobj.isNull("caption")){
                            JSONObject jsCaption = jobj.optJSONObject("caption");
                            socialItem.setContent(jsCaption.optString("text"));
                        }
                        socialItem.setSocialType("instagram");
                        listItems.add(socialItem);
                    }
                }


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        socialFeedItemAdapter = new SocialFeedItemAdapter(getActivity(), listItems);
        rvSocialFeedList.setAdapter(socialFeedItemAdapter);
        socialFeedItemAdapter.notifyDataSetChanged();
    }
}