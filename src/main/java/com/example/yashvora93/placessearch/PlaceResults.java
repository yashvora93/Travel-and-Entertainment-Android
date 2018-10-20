package com.example.yashvora93.placessearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlaceResults {

    private static Map<Integer, ArrayList<Place>> map = new HashMap();

    public static Map<Integer, ArrayList<Place>> getMap() {
        return map;
    }

    public static void setMap(Integer key, ArrayList<Place> placeList) {
        map.put(key, placeList);
    }

    public static int getSize() {
        return map.size();
    }

    public static void clear() {
        map = new HashMap();
    }
}
