package com.example.tejavelagapudi.checkintentsandfilesharing;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tejavelagapudi.checkintentsandfilesharing.databinding.ActivityMainBinding;

import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private Intent mRequestFileIntent;

    private static final String TAG = MainActivity.class.getSimpleName();
    private ViewDataBinding mActivityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

    }

    /*
        This is the listener for calculator button
        It will fire an intent to open calculator app
        checkIntent method will validate if the intent resolves or not
        so that the app does not crash if there is no app that can handle the intent

     */
    public void onCalButtonClicked(View view) {
        Intent i = new Intent();
        i.setClassName("com.android.calculator2",
                "com.android.calculator2.Calculator");
        checkIntent("calculator", i);
    }


    /*
        Listener for opening a browser same as the calculator button listener
     */

    public void onChromeButtonClicked(View view) {
        String url = "https://www.google.com";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        checkIntent("chrome", i);

    }

    /*
        This is the listener for sending an implicit intent to get image files
         for the apps who ever are wiling to share image files(ex gallery app) and also
         the server app we created

     */

    public void onFileShareButtonClicked(View view) {
        setContentView(R.layout.activity_main);

        mRequestFileIntent = new Intent(Intent.ACTION_PICK);
        mRequestFileIntent.setType("image/png");
        ComponentName componentName = mRequestFileIntent.resolveActivity(getPackageManager());
        if (componentName != null) {
            try {
                startActivityForResult(mRequestFileIntent, 0);
            } catch (ActivityNotFoundException ex) {
                Log.d(TAG, "share intent is caught in the exception");
                Log.d(TAG, "componentName->" + componentName.flattenToString());
            }

        }

    }

    public void checkIntent(String appName, Intent i) {
        ComponentName componentName = i.resolveActivity(getPackageManager());

        if (componentName == null) {
            Log.d(TAG, appName + " intent cannot be sent componentName is null");
        } else {
            try {
                startActivity(i);
            } catch (ActivityNotFoundException ex) {
                Log.d(TAG, appName + " intent cannot be sent exception caught");
                Log.d(TAG, "componentName->" + componentName.flattenToString());
            }
        }
    }


    /*
        This is where you get the result from the server app which will be a content uri for
        the image or the file.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If the selection didn't work
        if (resultCode != RESULT_OK) {
            Log.d(TAG, "RESULT CODE IS NOT RESULT OK");
            // Exit without doing anything else
            return;
        } else {
            // Get the file's content URI from the incoming Intent
            Uri returnUri = data.getData();
            Log.d(TAG, returnUri.getPath());
            ImageView imageView = (ImageView) findViewById(R.id.image_view);
            imageView.setImageURI(returnUri);
        }
    }
}
