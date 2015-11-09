package com.example.tejavelagapudi.checkintentsandfilesharing;

import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcelable;
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
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ChooserDialogFragment.ActivitiesSelectedListener {
    private Intent mRequestFileIntent;
    List<ResolveInfo> mResInfo;

    private static final String TAG = MainActivity.class.getSimpleName();
    private ViewDataBinding mActivityMainBinding;
    Intent mSendIntent;

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

    public void onTextButtonClicked(View view) {
        mSendIntent = new Intent();
        mSendIntent.setAction(Intent.ACTION_SEND);
        mSendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
        mSendIntent.setType("text/plain");
        PackageManager pm = getPackageManager();
        mResInfo = pm.queryIntentActivities(mSendIntent, 0);
        ArrayList<String> activities = new ArrayList<>();
        Log.d(TAG, "Intent activities size->" + mResInfo.size());
        for (int i = 0; i < mResInfo.size(); i++) {
            Log.d(TAG, "activity->" + mResInfo.get(i).activityInfo.toString());
            activities.add(mResInfo.get(i).activityInfo.packageName);
        }

        startChooserDialog(activities);


    }

    private void startChooserDialog(ArrayList<String> activities) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        ChooserDialogFragment chooserDialogFragment = ChooserDialogFragment.newInstance(activities);
        chooserDialogFragment.show(transaction, "dialog");

    }

    @Override
    public void onActivitiesSelected(ArrayList<String> selectedActivities) {
        Log.d(TAG, "choosenActivitiesSize->" + selectedActivities.size());
        List<Intent> intents=new ArrayList<>();
        for (int i = 0; i < selectedActivities.size(); i++) {
            Intent intent = new Intent();
            intent.setPackage(selectedActivities.get(i));
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
            intent.setType("text/plain");
            intents.add(intent);
        }
        Intent chooserIntent=Intent.createChooser(intents.remove(0),getString(R.string.choose_apps));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,intents.toArray(new Parcelable[]{}));
        startActivity(chooserIntent);


    }
}
