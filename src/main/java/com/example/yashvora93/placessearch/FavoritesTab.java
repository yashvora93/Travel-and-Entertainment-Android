package com.example.yashvora93.placessearch;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class FavoritesTab extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList<Place> placeList;
    private TextView noFavorites;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.favorites, container, false);
        loadFavorites(rootView);
        return rootView;
    }

    private void loadFavorites(View rootView) {
        recyclerView = rootView.findViewById(R.id.favoritesRow);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        noFavorites = rootView.findViewById(R.id.noFavorites);
        placeList = new ArrayList();

        SharedPreferences pref =  PreferenceManager.getDefaultSharedPreferences(getActivity());
        Map<String, String> map = (Map<String, String>) pref.getAll();
        if(map.size() > 0) {
            for (Map.Entry<String,String> entry : map.entrySet()) {
                JSONObject result = null;
                try {
                    result = new JSONObject(entry.getValue());
                    placeList.add(new Place(result.getString("name"),
                            result.getString("vicinity"),
                            result.getString("icon_url"),
                            result.getString("placeId")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            adapter = new FavoritesAdapter(placeList, getActivity());
            recyclerView.setAdapter(adapter);
        } else {
            recyclerView.setVisibility(View.GONE);
            noFavorites.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavorites(getView());
        //getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

}
