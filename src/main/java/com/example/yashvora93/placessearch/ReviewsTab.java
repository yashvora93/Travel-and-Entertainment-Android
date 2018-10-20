package com.example.yashvora93.placessearch;

import android.content.Intent;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

public class ReviewsTab extends Fragment {

    private ArrayList<Reviews> reviewList;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private Spinner reviewType, reviewOrder;
    String latitude, longitude, name, city, state, address1, country, currentReview;
    private TextView noReviews;

    private HashMap<String, ArrayList<Reviews>> reviewTypesMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.reviews, container, false);
        Intent intent = getActivity().getIntent();
        String data = intent.getStringExtra("DATA");
        loadReviews(rootView, data);
        return rootView;
    }

    private void loadReviews(View rootView, String data) {

        recyclerView = rootView.findViewById(R.id.review);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        reviewTypesMap = new HashMap<>();
        reviewType = rootView.findViewById(R.id.reviewTypeSpinner);
        reviewOrder = rootView.findViewById(R.id.reviewOrderSpinner);
        noReviews = rootView.findViewById(R.id.noReviews);

        try {
            JSONObject result = new JSONObject(data);
            result = result.getJSONObject("result");
            JSONObject location = result.getJSONObject("geometry").getJSONObject("location");
            latitude = location.getString("lat");
            longitude = location.getString("lng");
            name = result.getString("name");
            city = state = address1 = "";
            String addr = result.getString("adr_address");
            String arr[] = addr.split("</span>");
            for(String component :  arr) {
                if(component.indexOf("locality") > -1) {
                    city = component.split("\"locality\">")[1];
                }
                if(component.indexOf("region") > -1) {
                    state = component.split("\"region\">")[1];
                }
                if(component.indexOf("street-address") > -1) {
                    address1 = component.split("\"street-address\">")[1];
                }
            }
            country = "US";
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject obj = new JSONObject(data);
            JSONObject results = obj.getJSONObject("result");
            JSONArray arr = results.has("reviews") ? results.getJSONArray("reviews") : null;
            currentReview = "Google";
            if(arr != null) {
                createGoogleReviews(arr);
            } else {
                reviewTypesMap.put("Google", null);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        reviewType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                handleReviews(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        reviewOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortReviews(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        loadYelpReview();
    }

    private void loadYelpReview() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url ="http://csci571travel-env.us-east-2.elasticbeanstalk.com/get/getYelpReviews?latitude=" + latitude + "&longitude=" +longitude;
        url += "&name=" + name + "&city=" + city + "&state=" + state + "&address1=" + address1 + "&country=" + country;
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray arr = new JSONArray(response);
                            if(arr.length() > 0)
                                createYelpReviews(arr);
                            else {
                                reviewTypesMap.put("Yelp", null);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        queue.add(stringRequest);
    }

    private void sortReviews(final int position) {
        ArrayList<Reviews> sortReviewsData = reviewTypesMap.get(currentReview);
        if(sortReviewsData != null) {
            ArrayList<Reviews> clonedReviews = (ArrayList<Reviews>) sortReviewsData.clone();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                clonedReviews.sort((o1, o2) -> {
                    if(position == 1 || position == 2) {
                        int ratings = o1.compareToRatings(o2, position);
                        if (ratings != 0) {
                            return ratings;
                        }
                    } else if(position == 3 || position == 4){
                        Long date = o1.compareToDate(o2, position);
                        if (date != 0) {
                            return  Integer.parseInt(String.valueOf(date));
                        }
                    }
                    return 0;
                });
                adapter = new ReviewsAdapter(clonedReviews, getActivity());
                recyclerView.setAdapter(adapter);
            }
        }
    }

    private void createGoogleReviews(JSONArray arr) {
        reviewList = new ArrayList();
        try {
            if(arr.length() > 0) {
                for(int i=0; i<arr.length(); i++) {
                    JSONObject curr = null;
                    curr = arr.getJSONObject(i);
                    Reviews review = new Reviews(
                            curr.has("author_url") ? curr.getString("author_url") : "",
                            curr.has("profile_photo_url") ? curr.getString("profile_photo_url") : "",
                            curr.has("author_name") ? curr.getString("author_name") : "",
                            curr.has("rating") ? Integer.parseInt(curr.getString("rating")) : 0,
                            curr.has("time") ? Long.parseLong(curr.getString("time")) : 0,
                            curr.has("text") ? curr.getString("text") : "");
                    reviewList.add(review);
                    reviewTypesMap.put("Google", reviewList);
                }
                adapter = new ReviewsAdapter(reviewList, getActivity());
                recyclerView.setAdapter(adapter);
            } else {
                noReviews.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void createYelpReviews(JSONArray arr) {
        reviewList = new ArrayList();
        try {
            if(arr.length() > 0) {
                for(int i=0; i<arr.length(); i++) {
                    JSONObject curr = null;
                    curr = arr.getJSONObject(i);
                    SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    long milliseconds = 0;
                    try {
                        if(curr.has("time_created")) {
                            Date d = f.parse(curr.getString("time_created"));
                            milliseconds = d.getTime() / 1000;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Reviews review = new Reviews(
                            curr.has("url") ? curr.getString("url") : "",
                            curr.has("user") ? curr.getJSONObject("user").has("image_url") ? curr.getJSONObject("user").getString("image_url") : "" : "",
                            curr.has("user") ? curr.getJSONObject("user").has("name") ? curr.getJSONObject("user").getString("name") : "" : "",
                            curr.has("rating") ? Integer.parseInt(curr.getString("rating")) : 0,
                            milliseconds,
                            curr.has("text") ? curr.getString("text") : "");
                    reviewList.add(review);
                    reviewTypesMap.put("Yelp", reviewList);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handleReviews(int reviewType) {
        currentReview = reviewType == 0 ? "Google" : "Yelp";
        ArrayList<Reviews> reviews = reviewTypesMap.get(currentReview);
        if(reviews != null) {
            adapter = new ReviewsAdapter(reviews, getActivity());
            recyclerView.setAdapter(adapter);
            sortReviews(reviewOrder.getSelectedItemPosition());
            noReviews.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            noReviews.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }
}