package com.example.yashvora93.placessearch;

import android.content.Intent;
import android.opengl.Visibility;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class InfoTab extends Fragment {

    private TextView addressTextView, phoneNumberTextView, priceLevelTextView, googlePageTextView, websiteTextView;
    private RelativeLayout addressLayout, phoneNumberLayout, priceLayout, ratingLayout, googlePageLayout, websiteLayout;
    private RatingBar ratingBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.info, container, false);
        Intent intent = getActivity().getIntent();
        String data = intent.getStringExtra("DATA");
        loadInfo(rootView, data);
        return rootView;
    }

    private void showField(TextView textView, RelativeLayout layout, String text) {
        if(text != "") {
            textView.setText(text);
        } else {
            layout.setVisibility(View.GONE);
        }
    }

    private void loadInfo(View view, String data) {
        addressTextView = view.findViewById(R.id.addressValue);
        addressLayout = view.findViewById(R.id.addressLayout);
        phoneNumberTextView = view.findViewById(R.id.phoneValue);
        phoneNumberLayout = view.findViewById(R.id.phoneLayout);
        priceLevelTextView = view.findViewById(R.id.priceValue);
        priceLayout = view.findViewById(R.id.priceLayout);
        ratingBar = view.findViewById(R.id.ratingValue);
        ratingLayout = view.findViewById(R.id.ratingsLayout);
        googlePageTextView = view.findViewById(R.id.googlePageValue);
        googlePageLayout = view.findViewById(R.id.googlePageLayout);
        websiteTextView = view.findViewById(R.id.websiteValue);
        websiteLayout = view.findViewById(R.id.websiteLayout);
        try {
            JSONObject obj = new JSONObject(data);
            JSONObject results = obj.getJSONObject("result");
            String address = results.has("vicinity") ? results.getString("vicinity") : "";
            String phoneNumber = results.has("formatted_phone_number") ? results.getString("formatted_phone_number") : "";
            String priceLevel = results.has("price_level") ? results.getString("price_level") : "";
            String ratings = results.has("rating") ? results.getString("rating") : "";
            String googlePage = results.has("url") ? results.getString("url") : "";
            String website = results.has("website") ? results.getString("website") : "";
            showField(addressTextView, addressLayout, address);
            showField(phoneNumberTextView, phoneNumberLayout, phoneNumber);
            String price = "";
            if(priceLevel != "") {
                for (int i=0; i<Integer.parseInt(priceLevel); i++) {
                    price += "$";
                }
            }
            showField(priceLevelTextView, priceLayout, price);
            if(ratings != "") {
                Float rating = Float.parseFloat(ratings);
                ratingBar.setRating(rating);
            } else {
                ratingLayout.setVisibility(View.GONE);
            }
            showField(googlePageTextView, googlePageLayout, googlePage);
            showField(websiteTextView, websiteLayout, website);
            Linkify.addLinks(phoneNumberTextView, Linkify.ALL);
            Linkify.addLinks(googlePageTextView, Linkify.ALL);
            Linkify.addLinks(websiteTextView, Linkify.ALL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}