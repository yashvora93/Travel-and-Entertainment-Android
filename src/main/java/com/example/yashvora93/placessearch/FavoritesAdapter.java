package com.example.yashvora93.placessearch;

import android.app.Activity;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
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

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    private List<Place> placeList;
    private Context context;
    private Activity activity;
    public TextView noFavorites;
    public RecyclerView recyclerView;

    public FavoritesAdapter(List<Place> places, Context context) {
        this.placeList = places;
        this.context = context;
        this.activity = (Activity) context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        noFavorites = ((View)parent.getParent()).findViewById(R.id.noFavorites);
        return new ViewHolder(v);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
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

        holder.favIcon.setImageResource(R.drawable.ic_heart_fill_red);

        holder.favIcon.setOnClickListener(view -> remove(place.getPlaceId(), holder.getAdapterPosition(), place.getName()));
    }

    private void remove(String placeId, int item, String name) {
        Favorites f = new Favorites(activity);
        f.removeFavorites(placeId);
        Toast.makeText(activity, name + " was removed from favorites", Toast.LENGTH_LONG).show();
        placeList.remove(item);
        notifyDataSetChanged();
        if(placeList.size() == 0) {
            noFavorites.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
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
            placeName = itemView.findViewById(R.id.placeName);
            placeLocation = itemView.findViewById(R.id.placeLocation);
            icon = itemView.findViewById(R.id.imageIcon);
            favIcon = itemView.findViewById(R.id.favIcon);
            linearLayout = itemView.findViewById(R.id.placeRow);
            place_id = itemView.findViewById(R.id.placeId);
            imageIconURL = itemView.findViewById(R.id.imageIconURL);

            itemView.setOnClickListener(v -> {
                String placeId = ((TextView) v.findViewById(R.id.placeId)).getText().toString();
                GetData gd = new GetData(activity, Details.class, "Fetching place details");
                gd.makeRequest("http://csci571travel-env.us-east-2.elasticbeanstalk.com/get/getPlaceDetails?place_id=" + placeId);
            });
        }
    }

}
