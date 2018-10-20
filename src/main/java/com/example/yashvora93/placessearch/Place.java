package com.example.yashvora93.placessearch;

import android.support.v7.widget.RecyclerView;

public class Place {
    private String name;
    private String icon_url;
    private String vicinity;
    private String placeId;
    RecyclerView data;

    public Place(String name, String vicinity, String icon, String placeId) {
        this.name = name;
        this.vicinity = vicinity;
        this.icon_url = icon;
        this.setPlaceId(placeId);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String location) {
        this.vicinity = location;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
}
