package com.example.yashvora93.placessearch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Places;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class Details extends AppCompatActivity {

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private ImageView favIcon, shareIcon;
    private Activity mCurrentActivity;

    public Activity getCurrentActivity(){
        return mCurrentActivity;
    }

    public void setCurrentActivity(Activity mCurrentActivity){
        this.mCurrentActivity = mCurrentActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.detailsContainer);
        setupViewPager(mViewPager);
        setCurrentActivity(this);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_details);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        Intent intent = getIntent();
        String data = intent.getStringExtra("DATA");
        loadDetails(data, toolbar);

        setupTabIcons(tabLayout);
        setTabDivider(tabLayout);

    }

    private void loadDetails(String data, Toolbar toolbar) {
        JSONObject obj = null;
        try {
            obj = new JSONObject(data);
            JSONObject results = obj.getJSONObject("result");
            setUpToolbar(toolbar, results);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void setUpToolbar(Toolbar toolbar, JSONObject result) {
        try {
            toolbar.setTitle(result.getString("name"));
            handleFavorites(result);
            handleTwitter(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        Details.ViewPagerAdapter adapter = new Details.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new InfoTab(), "Info");
        adapter.addFrag(new PhotosTab(), "Photos");
        adapter.addFrag(new MapTab(), "Map");
        adapter.addFrag(new ReviewsTab(), "Reviews");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void setupTabIcons(TabLayout tabLayout) {

        LinearLayout tabLinearLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab_padding, null);
        TextView infoTab = (TextView) tabLinearLayout.findViewById(R.id.tab_with_padding);
        infoTab.setText(" Info");
        infoTab.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_info_outline, 0, 0, 0);
        tabLayout.getTabAt(0).setCustomView(infoTab);

        tabLinearLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab_padding, null);
        TextView photosTab = (TextView) tabLinearLayout.findViewById(R.id.tab_with_padding);
        photosTab.setText(" Photos");
        photosTab.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_photos, 0, 0, 0);
        tabLayout.getTabAt(1).setCustomView(photosTab);

        tabLinearLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab_padding, null);
        TextView mapTab = (TextView) tabLinearLayout.findViewById(R.id.tab_with_padding);
        mapTab.setText(" Map");
        mapTab.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_maps, 0, 0, 0);
        tabLayout.getTabAt(2).setCustomView(mapTab);

        tabLinearLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab_padding, null);
        TextView reviewsTab = (TextView) tabLinearLayout.findViewById(R.id.tab_with_padding);
        reviewsTab.setText(" Reviews");
        reviewsTab.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_reviews, 0, 0, 0);
        tabLayout.getTabAt(3).setCustomView(reviewsTab);
    }

    public void setTabDivider(TabLayout tabLayout) {
        LinearLayout linearLayout = (LinearLayout)tabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.WHITE);
        drawable.setSize(1, 1);
        linearLayout.setDividerPadding(10);
        linearLayout.setDividerDrawable(drawable);
    }

    private void handleFavorites(JSONObject result) {
        try {
            final String placeId = result.has("place_id") ? result.getString("place_id") : "";
            final String name = result.has("name") ? result.getString("name") : "";
            final String vicinity = result.has("vicinity") ? result.getString("vicinity") : "";
            final String icon = result.has("icon") ? result.getString("icon") : "";


            SharedPreferences pref =  PreferenceManager.getDefaultSharedPreferences(mCurrentActivity);
            String place = pref.getString(placeId, "");

            favIcon = mCurrentActivity.findViewById(R.id.favorite);
            if(place != "") {
                favIcon.setImageResource(R.drawable.ic_heart_fill_white);
            } else {
                favIcon.setImageResource(R.drawable.ic_heart_outline_white);
            }

            favIcon.setOnClickListener(v -> {
                Favorites f = new Favorites(mCurrentActivity);
                if(favIcon.getDrawable().getConstantState() != getCurrentActivity().getResources().getDrawable(R.drawable.ic_heart_fill_white).getConstantState()) {
                    favIcon.setImageResource(R.drawable.ic_heart_fill_white);
                    Place p = new Place(name,
                            vicinity,
                            icon,
                            placeId);
                    f.addFavorites(p);
                    Toast.makeText(mCurrentActivity, name + " was added to favorites", Toast.LENGTH_LONG).show();
                } else {
                    favIcon.setImageResource(R.drawable.ic_heart_outline_white);
                    f.removeFavorites(placeId);
                    Toast.makeText(mCurrentActivity, name + " was removed from favorites", Toast.LENGTH_LONG).show();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handleTwitter(JSONObject result) {
        try {
            String name = result.has("name") ? result.getString("name") : "";
            String formatted_address = result.has("formatted_address") ? result.getString("formatted_address") : "";
            String website = result.has("website") ? result.getString("website") : "";
            String url = result.has("url") ? result.getString("url") : "";

            final String tweet = "Check out " + (name.equals("") ? "" : name) + " located at " + (formatted_address.equals("") ? "" : formatted_address) + ". Website: " + (website.equals("") ? url.equals("") ? "" : url : website);

            shareIcon = mCurrentActivity.findViewById(R.id.share);
            shareIcon.setOnClickListener(v -> {
                Uri uri = Uri.parse("https://twitter.com/intent/tweet?hashtags=TravelAndEntertainmentSearch&text=" + tweet);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                getCurrentActivity().startActivity(intent);
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
