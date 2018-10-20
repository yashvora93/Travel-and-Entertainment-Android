package com.example.yashvora93.placessearch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class  ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    private Context context;
    private Activity activity;
    private ArrayList<Reviews> reviewList;

    public ReviewsAdapter(ArrayList<Reviews> reviewList, Context context) {
        this.reviewList = reviewList;
        this.context = context;
        this.activity = (Activity) context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Reviews review = reviewList.get(position);
        if(!review.getProfile_photo_url().equals("")) {
            Picasso.with(context)
                    .load(review.getProfile_photo_url())
                    .into(holder.authorImage);
        } else {
            holder.authorImage.setImageResource(0);
        }

        holder.authorName.setText(review.getAuthor_name());

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate= formatter.format(new Date(review.getReviewDate() * 1000));
        holder.reviewDate.setText(strDate);

        holder.reviewText.setText(review.getReviewText());

        Float rate = Float.valueOf(review.getRatings());
        holder.ratings.setRating(rate);

        holder.authorUrl.setText(review.getAuthor_url());
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView authorImage;
        public TextView authorName, reviewDate, reviewText, authorUrl;
        public RatingBar ratings;
        public LinearLayout linearLayout;

        public ViewHolder(final View itemView) {
            super(itemView);
            authorImage = itemView.findViewById(R.id.personImage);
            authorName = itemView.findViewById(R.id.personName);
            ratings = itemView.findViewById(R.id.reviewRatingsBar);
            reviewDate = itemView.findViewById(R.id.reviewDate);
            reviewText = itemView.findViewById(R.id.reviewText);
            authorUrl = itemView.findViewById(R.id.authorlURL);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.reviewRow);

            itemView.setOnClickListener(v -> {
                String authorUrl = ((TextView)v.findViewById(R.id.authorlURL)).getText().toString();
                if(!authorUrl.equals("")) {
                    Uri uri = Uri.parse(authorUrl);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    activity.startActivity(intent);
                }
            });
        }
    }
}

