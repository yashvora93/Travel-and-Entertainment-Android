package com.example.yashvora93.placessearch;

import android.app.Activity;
import android.app.ProgressDialog;

public class Loader {
    ProgressDialog progressDialog;

    public Loader(Activity activity, String message) {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(message); // Setting Message
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
    }

    public void start() {
        progressDialog.show(); // Display Progress Dialog
        progressDialog.setCancelable(false);
    }

    public void stop() {
        progressDialog.dismiss();
    }

}
