package com.example.yashvora93.placessearch;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MapTab extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String destination, destLat, destLng;
    private String origin, originLat, originLng;
    private GeoApiContext geoApiContext;
    private Spinner mode;
    private GeoDataClient mGeoDataClient;
    private PlaceAutocompleteAdapter mAdapter;
    private AutoCompleteTextView fromLocationAutoText;

    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(34.02996, -118.28092), new LatLng(35.02996, -117.28092));

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_maps_directions, container, false);

        fromLocationAutoText = rootView.findViewById(R.id.fromAutoCompleteTextView);
        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);
        mAdapter = new PlaceAutocompleteAdapter(getActivity(), mGeoDataClient, BOUNDS_GREATER_SYDNEY, null);
        fromLocationAutoText.setAdapter(mAdapter);

        initializeDirections(rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Intent dataIntent = getActivity().getIntent();
        String data = dataIntent.getStringExtra("DATA");

        mMap = googleMap;

        JSONObject obj = null;
        try {
            obj = new JSONObject(data);
            JSONObject results = obj.getJSONObject("result");
            destination = results.has("name") ? results.getString("name") :  "";
            results = results.has("geometry") ? results.getJSONObject("geometry") : new JSONObject();
            results = results.has("location") ? results.getJSONObject("location") : new JSONObject();
            destLat = results.has("lat") ? results.getString("lat") : "";
            destLng = results.has("lng") ? results.getString("lng") : "";
            if(destLat != "" && destLng != "") {
                LatLng place = new LatLng(Float.parseFloat(destLat), Float.parseFloat(destLng));
                mMap.addMarker(new MarkerOptions().position(place).title(destination)).showInfoWindow();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(place.latitude, place.longitude), 15.0f));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initializeDirections(View rootView) {
        fromLocationAutoText = rootView.findViewById(R.id.fromAutoCompleteTextView);
        mode = rootView.findViewById(R.id.travelModeSpinner);

        fromLocationAutoText.setOnItemClickListener((parent, view, position, id) -> {
            String placeId = mAdapter.getItem(position).getPlaceId().toString();
            origin = mAdapter.getItem(position).getPrimaryText(new StyleSpan(Typeface.NORMAL)).toString();

            RequestQueue queue = Volley.newRequestQueue(getActivity());
            String url ="http://csci571travel-env.us-east-2.elasticbeanstalk.com/get/getPlaceDetails?place_id=" + placeId;
            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    response -> {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONObject result = obj.has("result") ? obj.getJSONObject("result") : null;
                            if(result != null) {
                                result = result.has("geometry") ? result.getJSONObject("geometry") : null;
                                if(result != null) {
                                    result = result.has("location") ? result.getJSONObject("location") : null;
                                    originLat = result.getString("lat");
                                    originLng = result.getString("lng");
                                    getDirections(mode.getSelectedItem().toString().toUpperCase());
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> {
                    });

            queue.add(stringRequest);
        });

        mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!fromLocationAutoText.getText().toString().equals("")) {
                    getDirections(mode.getSelectedItem().toString().toUpperCase());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private GeoApiContext getGeoApiContext() {
        geoApiContext = new GeoApiContext();
        return geoApiContext.setQueryRateLimit(3)
                .setApiKey(getString(R.string.google_directions_key))
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);
    }

    private void getDirections(String mode) {
        try {
            if(originLat != null && originLng != null) {
                DirectionsResult result = DirectionsApi.newRequest(getGeoApiContext())
                        .mode(TravelMode.valueOf(mode))
                        .origin(new com.google.maps.model.LatLng(Float.parseFloat(originLat), Float.parseFloat(originLng)))
                        .destination(new com.google.maps.model.LatLng(Float.parseFloat(destLat), Float.parseFloat(destLng)))
                        .await();
                addMarkersToMap(result, mMap);
                addPolyline(result, mMap);
            }
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addMarkersToMap(DirectionsResult results, GoogleMap mMap) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[0].legs[0].startLocation.lat,results.routes[0].legs[0].startLocation.lng)).
                title(origin)).showInfoWindow();
        mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(destLat),Double.parseDouble(destLng))).
                title(destination));
    }

    private void addPolyline(DirectionsResult results, GoogleMap mMap) {
        List<LatLng> decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline.getEncodedPath());
        mMap.addPolyline(new PolylineOptions().addAll(decodedPath).color(Color.BLUE).width(15));

        LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();
        for (LatLng point : decodedPath) {
            boundsBuilder.include(point);
        }
        LatLngBounds bounds = boundsBuilder.build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds,  150);
        mMap.moveCamera(cameraUpdate);
    }
}