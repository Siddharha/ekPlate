package com.ekplate.android.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ekplate.android.appinterfaces.BackgroundActionInterface;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rahul on 9/28/2015.
 */
public class CallServiceAction {
    private Context context;
    public BackgroundActionInterface actionInterface;
    private JSONObject errorNodeMain;

    public CallServiceAction(Context context){
        this.context = context;
    }

    public void requestVersionApi(JSONObject jsonObjParams, String apiUrl){

        Log.d("RequestParam",jsonObjParams.toString());
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.getCache().clear();
        JsonObjectRequest myRequest = new JsonObjectRequest(
                Request.Method.POST,
                ConstantClass.BASE_URL + apiUrl,
                jsonObjParams,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response",response.toString());
                        actionInterface.onCompleted(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            errorNodeMain = new JSONObject();
                            JSONObject jsonErrNode = new JSONObject();
                            jsonErrNode.put("errCode", "-1");
                            jsonErrNode.put("errMsg", "Error in server connection.");
                            errorNodeMain.put("errNode", jsonErrNode);
                            actionInterface.onCompleted(errorNodeMain);
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-agent", "Custom-Agent 1.0");
                actionInterface.onStarted();
                return headers;
            }
        };

        myRequest.setRetryPolicy(new DefaultRetryPolicy(
                99999,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(myRequest);
    }

    public void requestVersionV2Api(JSONObject jsonObjParams, String apiUrl){
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.getCache().clear();
        JsonObjectRequest myRequest = new JsonObjectRequest(
                Request.Method.POST,
                ConstantClass.BASE_URL_V2 + apiUrl,
                jsonObjParams,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        actionInterface.onCompleted(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            errorNodeMain = new JSONObject();
                            JSONObject jsonErrNode = new JSONObject();
                            jsonErrNode.put("errCode", "-1");
                            jsonErrNode.put("errMsg", "Error in server connection.");
                            errorNodeMain.put("errNode", jsonErrNode);
                            actionInterface.onCompleted(errorNodeMain);
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-agent", "Custom-Agent 1.0");
                actionInterface.onStarted();
                return headers;
            }
        };

        myRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(myRequest);
    }
}
