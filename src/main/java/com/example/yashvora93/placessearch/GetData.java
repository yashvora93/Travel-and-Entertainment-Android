package com.example.yashvora93.placessearch;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class GetData extends Application {
    private RequestQueue queue;
    private static GetData instance;
    private Activity activity;
    private Class nextClass;
    private String message;

    public GetData() {
    }

    public GetData(Activity activity, Class nextClass, String message) {
        this.activity = activity;
        this.nextClass = nextClass;
        this.message = message;
        if(instance == null) {
            instance = new GetData();
        }
    }

    public static synchronized GetData getInstance() {
        return instance;
    }

    public RequestQueue getReqQueue() {
        if (queue == null) {
            queue = Volley.newRequestQueue(activity);
        }

        return queue;
    }

    public <T> void addToReqQueue(Request<T> req, String tag) {

        getReqQueue().add(req);
    }

    public <T> void addToReqQueue(Request<T> req) {

        getReqQueue().add(req);
    }

    public void cancelPendingReq(Object tag) {
        if (queue != null) {
            queue.cancelAll(tag);
        }
    }

    public void makeRequest(String url) {

        final Loader l = new Loader(activity, message);
        l.start();

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        l.stop();
                        Intent intent = new Intent(activity, nextClass);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        intent.putExtra("DATA", response);
                        activity.startActivity(intent);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        l.stop();
                        Log.v("Err:", error.getMessage());
                        Toast.makeText(activity, "Failed to get results", Toast.LENGTH_LONG).show();
                    }
        });

        addToReqQueue(stringRequest);
    }

}
