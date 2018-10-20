package com.example.yashvora93.placessearch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchResults extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList<Place> placeList;
    private Button nextBtn, previousBtn;
    private Activity mCurrentActivity = null;
    private int currentPage = 0;
    private String next_token = "";
    private LinearLayout layout, navControl;
    private TextView noResults;

    public Activity getCurrentActivity(){
        return mCurrentActivity;
    }

    public void setCurrentActivity(Activity mCurrentActivity){
        this.mCurrentActivity = mCurrentActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> startActivity(new Intent(getApplicationContext(),Home.class)));

        setCurrentActivity(this);

        Intent intent = getIntent();
        String data = intent.getStringExtra("DATA");

        recyclerView = findViewById(R.id.placeSearchResults);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        noResults = findViewById(R.id.noResults);
        navControl = findViewById(R.id.navControl);

        placeList = new ArrayList<>();
        loadData(data);

    }

    private void loadData(String data) {
        try {
            JSONObject obj = new JSONObject(data);
            JSONArray arr = obj.getJSONArray("results");
            placeList.clear();
            if(arr.length() > 0) {
                for(int i=0; i<arr.length(); i++) {
                    JSONObject curr = arr.getJSONObject(i);
                    Place place = new Place(
                            curr.getString("name"),
                            curr.getString("vicinity"),
                            curr.getString("icon"),
                            curr.getString("place_id"));
                    placeList.add(place);
                }
                ++currentPage;
                adapter = new PlaceSearchAdapter(placeList, getCurrentActivity());
                recyclerView.setAdapter(adapter);
                PlaceResults.setMap(currentPage, (ArrayList<Place>) placeList.clone());
                loadNextPage(obj);
                loadPreviousPage();
            } else {
                recyclerView.setVisibility(View.GONE);
                navControl.setVisibility(View.GONE);
                noResults.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            Toast.makeText(this, "Data corrupted", Toast.LENGTH_LONG);
        }
    }

    private void createRecyclerView(ArrayList<Place> places) {
        placeList.clear();
        for(int i=0; i<places.size(); i++) {
            Place curr = places.get(i);
            Place place = new Place(curr.getName(),
                    curr.getVicinity(),
                    curr.getIcon_url(),
                    curr.getPlaceId());
            placeList.add(place);
            adapter = new PlaceSearchAdapter(placeList, getCurrentActivity());
            recyclerView.setAdapter(adapter);
        }
    }

    private void loadNextPage(final JSONObject obj) throws JSONException {
        final boolean hasNextToken = obj.has("next_page_token");
        next_token = hasNextToken ? obj.getString("next_page_token") : "";

        nextBtn = findViewById(R.id.next);
        final int totalPages = PlaceResults.getSize();

        toggleStates();

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!next_token.equals("") && totalPages != 3 && currentPage == totalPages) {
                    nextBtn.setEnabled(true);

                    String url ="http://csci571travel-env.us-east-2.elasticbeanstalk.com/get/getNextPlaces?token=";
                    url += next_token;

                    RequestQueue queue = Volley.newRequestQueue(getCurrentActivity());
                    final Loader l = new Loader(getCurrentActivity(), "Fetching next page");
                    l.start();

                    // Request a string response from the provided URL.
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    l.stop();
                                    loadData(response);
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            l.stop();
                            Toast.makeText(getCurrentActivity(), "Failed to get results", Toast.LENGTH_LONG).show();
                        }
                    });

                    queue.add(stringRequest);

                } else {
                    ++currentPage;
                    createRecyclerView(PlaceResults.getMap().get(currentPage));
                    toggleStates();
                }
            }
        });
    }

    private void loadPreviousPage() {
        previousBtn = findViewById(R.id.previous);

        if(currentPage > 1) {
            previousBtn.setEnabled(true);
        } else {
            previousBtn.setEnabled(false);
        }

        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                --currentPage;
                createRecyclerView(PlaceResults.getMap().get(currentPage));
                toggleStates();
            }
        });
    }

    private void toggleStates() {
        int totalPages = PlaceResults.getSize();

        previousBtn = findViewById(R.id.previous);
        if(currentPage > 1) {
            previousBtn.setEnabled(true);
        } else {
            previousBtn.setEnabled(false);
        }

        nextBtn = findViewById(R.id.next);
        if(totalPages == currentPage && next_token.equals("")) {
            nextBtn.setEnabled(false);
        } else {
            nextBtn.setEnabled(true);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if(placeList.size() > 0)
            adapter.notifyDataSetChanged();
    }

}