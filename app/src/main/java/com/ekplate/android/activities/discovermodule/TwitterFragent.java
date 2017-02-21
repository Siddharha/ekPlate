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
public class TwitterFragent extends Fragment implements BackgroundActionInterface {

    private RecyclerView rvSocialFeedList;
    private View rootView;
    private ArrayList<SocialFeedListItem> listItems;
    private SocialFeedItemAdapter socialFeedItemAdapter;
    private ProgressDialog progressDialog;
    private CallServiceAction _serviceAction;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    LinearLayoutManager mLayoutManager;
    ProgressBar pbTTLoader;
    private boolean loading = true;
    JSONObject data = new JSONObject();
    //int flag =0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.twitter_chennel_fragment, container, false);
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
        /*if (listItems.size() == 0) {
            JSONObject jsonObject2 = new JSONObject();
            try {
                jsonObject2.put("id_str", "");
                data.put("data", jsonObject2);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            JSONObject jsonObject2 = new JSONObject();
            String id_str = listItems.get(listItems.size() - 1).getTwitter_id_str();
            try {
                jsonObject2.put("id_str", id_str);
                data.put("data", jsonObject2);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        Log.d("twitterrequestparam",data.toString());*/
        pbTTLoader.setVisibility(View.VISIBLE);
        _serviceAction.requestVersionV2Api(data, "get-twitter-feeds");
       // flag =0;
       // showProgressDialogue();

    }

    private void showProgressDialogue() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
    }

    private void initialize() {
        rvSocialFeedList = (RecyclerView) rootView.findViewById(R.id.rvSocialFeedList);
        mLayoutManager = new LinearLayoutManager(getActivity());
        rvSocialFeedList.setLayoutManager(mLayoutManager);
        listItems = new ArrayList<>();
        pbTTLoader = (ProgressBar)rootView.findViewById(R.id.pbTTLoader);
        _serviceAction = new CallServiceAction(getActivity());
        _serviceAction.actionInterface = this;
        getFeed();
        /*rvSocialFeedList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();
                   *//* if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            Log.d("farman", "Last Item");
                            getFeed();
                            // _serviceAction.requestVersionV2Api(jsonObjectData, "get-facebook-feeds");

                        }
                    }*//*

                    if(mLayoutManager.findLastVisibleItemPosition() == mLayoutManager.getItemCount()-1){
                       // Toast.makeText(getActivity(), "XXXX", Toast.LENGTH_SHORT).show();

                        if(flag == 1){

                            getFeed();
                        }

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
        Log.e("twitter", response.toString());
     //   progressDialog.dismiss();
        pbTTLoader.setVisibility(View.GONE);
        JSONObject jsonErrorObj = response.optJSONObject("errNode");
        try {
            if (jsonErrorObj.getInt("errCode") == 0) {
                JSONArray jsonArrayData = response.optJSONArray("data");
                if (jsonArrayData != null) {
                    for (int i = 0; i < jsonArrayData.length(); i++) {
                        JSONObject jobj = jsonArrayData.optJSONObject(i);
                        SocialFeedListItem socialItem = new SocialFeedListItem();
                        //  socialItem.setHeading(jobj.optString("name"));
                        // socialItem.setImageUrl(jobj.optString("picture"));
                        socialItem.setPostingTime(jobj.optString("created_at"));
                        socialItem.setContent(jobj.optString("text"));
                        //  socialItem.setLink(jobj.optString("link"));
                        socialItem.setSocialType("twitter");
                        socialItem.setTwitter_id_str(jobj.optString("id_str"));
                        listItems.add(socialItem);
                    }
                   // flag =1;
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