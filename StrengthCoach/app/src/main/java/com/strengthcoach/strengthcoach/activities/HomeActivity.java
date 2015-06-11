package com.strengthcoach.strengthcoach.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.strengthcoach.strengthcoach.R;
import com.strengthcoach.strengthcoach.fragments.TrainersListFragment;
import com.strengthcoach.strengthcoach.models.Trainer;

import java.util.List;


public class HomeActivity extends ActionBarActivity {
    private TrainersListFragment fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        fragment = (TrainersListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        populateTrainers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            String appLinkUrl, previewImageUrl;

            appLinkUrl = "https://github.com/varungu/Android-Bootcamp";
            previewImageUrl = "http://www.ajayengineeringworks.com/Adminpanel/product_images/06c3757555a99c26ee8f3e8bebdaba0c.jpg";

            if (AppInviteDialog.canShow()) {
                AppInviteContent content = new AppInviteContent.Builder()
                        .setApplinkUrl(appLinkUrl)
                        .setPreviewImageUrl(previewImageUrl)
                        .build();
                AppInviteDialog.show(this, content);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    // Pass the list of trainers to fragment
    private void populateTrainers() {
        final TrainersListFragment finalFragment = fragment;
        ParseQuery<Trainer> query = ParseQuery.getQuery("Trainer");
        query.findInBackground(new FindCallback<Trainer>() {
            public void done(List<Trainer> trainers, ParseException e) {
                if (e == null) {
                    Log.d("DEBUG", "Retrieved " + trainers.size() + " trainers");
                    finalFragment.setItems(trainers);
                } else {
                    Log.d("DEBUG", "Error: " + e.getMessage());
                }
            }
        });
    }
}
