package com.example.yashvora93.placessearch;

import android.app.Activity;
//import android.content.Intent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
//import android.text.Editable;
//import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//import com.android.volley.Request;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.google.android.gms.common.api.Status;
//import com.google.android.gms.location.places.Place;
//import com.google.android.gms.location.places.ui.PlaceAutocomplete;
//import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
//import com.google.android.gms.location.places.ui.PlaceSelectionListener;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.io.UnsupportedEncodingException;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {


    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private TextView keywordErrTextView, otherLocationErrTextView;
    private EditText keywordText, distanceText;
    private RadioButton currLocation, otherLocation;
    private AutoCompleteTextView placeAutoComplete;
    private Spinner categorySpinner;
    private Activity mCurrentActivity = null;

    private Double lat, lon;
    private UserLocation ul;

    public Activity getCurrentActivity(){
        return mCurrentActivity;
    }

    public void setCurrentActivity(Activity mCurrentActivity){
        this.mCurrentActivity = mCurrentActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        ul = new UserLocation(this);

        setupTabIcons(tabLayout);
        setCurrentActivity(this);
        setTabDivider(tabLayout);
    }

    private void loadElements() {
        keywordErrTextView = findViewById(R.id.errKeywordTextView);
        otherLocationErrTextView = findViewById(R.id.errOtherLocationTextView);
        keywordText = findViewById(R.id.keywordText);
        distanceText = findViewById(R.id.distanceText);
        categorySpinner = findViewById(R.id.category);
        currLocation = findViewById(R.id.currentLocationRadio);
        otherLocation = findViewById(R.id.otherLocationRadio);
        placeAutoComplete = findViewById(R.id.otherLocationAutoComplete);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new SearchTab(), "Search");
        adapter.addFrag(new FavoritesTab(), "Favorites");
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

        LinearLayout tabLinearLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        TextView searchTab = (TextView) tabLinearLayout.findViewById(R.id.tab);
        searchTab.setText(" Search");
        searchTab.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search, 0, 0, 0);
        tabLayout.getTabAt(0).setCustomView(searchTab);

        tabLinearLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        TextView favoritesTab = (TextView) tabLinearLayout.findViewById(R.id.tab);
        favoritesTab.setText(" Favorites");
        favoritesTab.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_fill_white, 0, 0, 0);
        tabLayout.getTabAt(1).setCustomView(favoritesTab);
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

    public void setAutoComplete(View view) {
        loadElements();
        placeAutoComplete.setEnabled(otherLocation.isChecked());
        if(otherLocation.isChecked()) {
            placeAutoComplete.requestFocus();
        }
    }

    public void  validateForm(View view) {
        loadElements();
        boolean isError = false;

        if(keywordText.getText().toString().trim().equals("")) {
            keywordErrTextView.setVisibility(View.VISIBLE);
            isError = true;
        } else {
            keywordErrTextView.setVisibility(View.GONE);
        }

        if(otherLocation.isChecked() && placeAutoComplete.getText().toString().trim().equals("")) {
            otherLocationErrTextView.setVisibility(View.VISIBLE);
            isError = true;
        }  else {
            otherLocationErrTextView.setVisibility(View.GONE);
        }

        if(isError) {
            Toast.makeText(getCurrentActivity(), "Please fix all fields with errors", Toast.LENGTH_LONG).show();
        } else {
            lat = ul.getLatitude();
            lon = ul.getLongitude();

            String url ="http://csci571travel-env.us-east-2.elasticbeanstalk.com/get/getPlaces?keyword=";
            url += keywordText.getText() + "&category=" + categorySpinner.getSelectedItem().toString().toLowerCase().replace(" ", "_") + "&distance=";
            url += distanceText.getText() + "&locationType=" + (otherLocation.isChecked() ? "disLoc" : "currLoc") + "&latitude=";
            url += lat + "&longitude=" + lon + "&disLocation=" + placeAutoComplete.getText();

            PlaceResults.clear();

            GetData gd = new GetData(getCurrentActivity(), SearchResults.class, "Fetching Results");
            gd.makeRequest(url);
        }

    }

    public void clearForm(View view) {
        loadElements();
        keywordErrTextView.setVisibility(View.GONE);
        otherLocationErrTextView.setVisibility(View.GONE);
        keywordText.setText("");
        placeAutoComplete.setText("");
        distanceText.setText("");
        categorySpinner.setSelection(0);
        currLocation.setChecked(true);
        placeAutoComplete.setEnabled(false);
    }

}
