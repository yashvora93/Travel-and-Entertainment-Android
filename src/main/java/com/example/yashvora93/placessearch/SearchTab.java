package com.example.yashvora93.placessearch;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class SearchTab extends Fragment {


    protected GeoDataClient mGeoDataClient;
    private AutoCompleteTextView autoCompleteTextView;
    private PlaceAutocompleteAdapter mAdapter;

    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(34.02996, -118.28092), new LatLng(35.02996, -117.28092));

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.search, container, false);
        autoCompleteTextView = (AutoCompleteTextView) rootView.findViewById(R.id.otherLocationAutoComplete);
        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);
        mAdapter = new PlaceAutocompleteAdapter(getActivity(), mGeoDataClient, BOUNDS_GREATER_SYDNEY, null);
        autoCompleteTextView.setAdapter(mAdapter);
        return rootView;
    }
}
