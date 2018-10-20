package com.example.yashvora93.placessearch;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PhotosTab extends Fragment {


    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList photoList;
    private GeoDataClient mGeoDataClient;
    private TextView noPhotos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.photos, container, false);
        Intent intent = getActivity().getIntent();
        String data = intent.getStringExtra("DATA");
        loadPhotos(rootView, data);
        return rootView;
    }

    private void loadPhotos(View rootView, String data) {
        recyclerView = rootView.findViewById(R.id.photoResults);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);
        noPhotos = rootView.findViewById(R.id.noPhotos);
        photoList = new ArrayList();

        JSONObject obj = null;
        try {
            obj = new JSONObject(data);
            JSONObject results = obj.getJSONObject("result");
            final String place_id = results.has("place_id") ? results.getString("place_id") : "";
            photoList.clear();
            if(place_id != "") {
                final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(place_id);
                photoMetadataResponse.addOnCompleteListener(task -> {
                    PlacePhotoMetadataResponse photos = task.getResult();
                    PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                    for (int i=0; i<photoMetadataBuffer.getCount(); i++) {
                        photoList.add(photoMetadataBuffer.get(i).freeze());
                    }
                    photoMetadataBuffer.release();
                    if(photoList.size() > 0) {
                        adapter = new PhotosAdapter(photoList, getActivity());
                        recyclerView.setAdapter(adapter);
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        noPhotos.setVisibility(View.VISIBLE);
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}