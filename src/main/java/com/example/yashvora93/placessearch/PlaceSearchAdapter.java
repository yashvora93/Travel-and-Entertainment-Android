package com.example.yashvora93.placessearch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;


import java.util.List;

public class PlaceSearchAdapter extends RecyclerView.Adapter<PlaceSearchAdapter.ViewHolder> {

    public static final String KEY_NAME = "name";
    public static final String KEY_VICINITY = "vicinty";
    public static final String KEY_ICON = "icon";

    private List<Place> placeList;
    private Context context;
    private Activity activity;

    public PlaceSearchAdapter(List<Place> places, Context context) {
        this.placeList = places;
        this.context = context;
        this.activity = (Activity) context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Place place = placeList.get(position);
        holder.placeName.setText(place.getName());
        holder.placeLocation.setText(place.getVicinity());
        holder.place_id.setText(place.getPlaceId());
        holder.imageIconURL.setText(place.getIcon_url());

        Picasso.with(context)
                .load(place.getIcon_url())
                .into(holder.icon);

        String placeId = place.getPlaceId();
        SharedPreferences pref =  PreferenceManager.getDefaultSharedPreferences(activity);
        String favPlace = pref.getString(placeId, "");
        if(favPlace != "") {
            holder.favIcon.setImageResource(R.drawable.ic_heart_fill_red);
        } else {
            holder.favIcon.setImageResource(R.drawable.ic_heart_outline_black);
        }

    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView placeName, placeLocation, imageIconURL;
        public ImageView icon;
        public ImageView favIcon;
        public TextView place_id;
        public LinearLayout linearLayout;

        public ViewHolder(final View itemView) {
            super(itemView);
            placeName = (TextView) itemView.findViewById(R.id.placeName);
            placeLocation = (TextView) itemView.findViewById(R.id.placeLocation);
            icon = (ImageView) itemView.findViewById(R.id.imageIcon);
            favIcon = (ImageView) itemView.findViewById(R.id.favIcon);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.placeRow);
            place_id = (TextView) itemView.findViewById(R.id.placeId);
            imageIconURL = (TextView) itemView.findViewById(R.id.imageIconURL);


            itemView.setOnClickListener(v -> {
                String placeId = ((TextView) v.findViewById(R.id.placeId)).getText().toString();
                GetData gd = new GetData(activity, Details.class, "Fetching place details");
                gd.makeRequest("http://csci571travel-env.us-east-2.elasticbeanstalk.com/get/getPlaceDetails?place_id=" + placeId);
            });

            favIcon.setOnClickListener(v -> {
                Favorites f = new Favorites(activity);
                if(favIcon.getDrawable().getConstantState() != activity.getResources().getDrawable(R.drawable.ic_heart_fill_red).getConstantState()) {
                    favIcon.setImageResource(R.drawable.ic_heart_fill_red);
                    Place p = new Place(placeName.getText().toString(),
                            placeLocation.getText().toString(),
                            imageIconURL.getText().toString(),
                            place_id.getText().toString());
                    f.addFavorites(p);
                    Toast.makeText(activity, placeName.getText() + " was added to favorites", Toast.LENGTH_LONG).show();
                } else {
                    favIcon.setImageResource(R.drawable.ic_heart_outline_black);
                    f.removeFavorites(place_id.getText().toString());
                    Toast.makeText(activity, placeName.getText() + " was removed from favorites", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
