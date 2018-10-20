package com.example.yashvora93.placessearch;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class Favorites {

    private Context context;

    public Favorites(Context context) {
        this.context = context;
    }

    public void addFavorites(Place place) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        JsonObject result = new JsonObject();
        result.add("placeId", new JsonPrimitive(place.getPlaceId()));
        result.add("name", new JsonPrimitive(place.getName()));
        result.add("icon_url", new JsonPrimitive(place.getIcon_url()));
        result.add("vicinity", new JsonPrimitive(place.getVicinity()));
        Gson gson = new Gson();
        String json = gson.toJson(result);
        editor.putString(place.getPlaceId(), json);
        editor.apply();
    }

    public void removeFavorites(String placeId) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(placeId);
        editor.apply();
    }
}
