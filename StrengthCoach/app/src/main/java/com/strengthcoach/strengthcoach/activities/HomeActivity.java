package com.strengthcoach.strengthcoach.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.strengthcoach.strengthcoach.R;
import com.strengthcoach.strengthcoach.fragments.TrainersListFragment;
import com.strengthcoach.strengthcoach.models.Gym;
import com.strengthcoach.strengthcoach.models.SimpleUser;
import com.strengthcoach.strengthcoach.models.Trainer;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends AppCompatActivity {
    private TrainersListFragment fragment;
    private final int REQUEST_CODE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        fragment = (TrainersListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);

        String currentUserId = getLoggedInUserId();
        // If the user is already logged in get the user object
        if ((SimpleUser.currentUserObject == null) && (!currentUserId.isEmpty())) {
            ParseQuery<SimpleUser> query = ParseQuery.getQuery("SimpleUser");
            query.whereEqualTo("objectId", currentUserId);
            query.findInBackground(new FindCallback<SimpleUser>() {
                public void done(List<SimpleUser> users, ParseException e) {
                    if (e == null) {
                        // Set the value of global current user object
                        SimpleUser.currentUserObject = users.get(0);
                        populateTrainers();
                    } else {
                        Log.d("DEBUG", "Error: " + e.getMessage());
                    }
                }
            });
        } else {
            populateTrainers();
        }
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
        else if (id == R.id.action_map) {
            Intent intent = new Intent(this, MapActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        }
        else if (id == R.id.action_favorites) {
            populateFavoriteTrainers();
        } else if (id == R.id.action_home) {
            populateTrainers();
        }

        return super.onOptionsItemSelected(item);
    }

    public void launchMap() {
        Intent intent = new Intent(this, MapActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    // Pass the list of trainers to fragment
    public void populateTrainers() {
        final TrainersListFragment finalFragment = fragment;
        ParseQuery<Trainer> query = ParseQuery.getQuery("Trainer");
        query.include("favorited_by");
        query.findInBackground(new FindCallback<Trainer>() {
            public void done(List<Trainer> trainers, ParseException e) {
                if (e == null) {
                    Log.d("DEBUG", "Retrieved " + trainers.size() + " trainers");
                    refreshFragment(trainers);
                } else {
                    Log.d("DEBUG", "Error: " + e.getMessage());
                }
            }
        });
    }

    // Pass the list of trainers to fragment
    public void populateFavoriteTrainers() {
        final TrainersListFragment finalFragment = fragment;
        if (SimpleUser.currentUserObject != null) {
            ParseQuery<Trainer> query = ParseQuery.getQuery("Trainer");
            query.include("favorited_by");
            query.whereEqualTo("favorited_by", SimpleUser.currentUserObject);
            query.findInBackground(new FindCallback<Trainer>() {
                public void done(List<Trainer> trainers, ParseException e) {
                    if (e == null) {
                        Log.d("DEBUG", "Retrieved " + trainers.size() + " trainers");
                        refreshFragment(trainers);
                    } else {
                        Log.d("DEBUG", "Error: " + e.getMessage());
                    }
                }
            });
        } else {
            // Ask the user to sign up
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);
        }
    }

    private String getLoggedInUserId() {
        SharedPreferences pref =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String userId = pref.getString("userId", "");
        return userId;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Get the name of gym from map activity
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            String gymName = data.getExtras().getString("gymName");
            populateTrainersFromGymName(gymName);
        }
    }

    private void populateTrainersFromGymName(final String gymName) {
        final TrainersListFragment trainersListFragment = fragment;
        ParseQuery<Gym> query = ParseQuery.getQuery("Gym");
        query.include("trainers");
        query.whereEqualTo("name", gymName);
        query.findInBackground(new FindCallback<Gym>() {
            public void done(List<Gym> gyms, ParseException e) {
                if (e == null) {
                    Log.d("DEBUG", "Retrieved " + gyms.size() + " gyms");
                    Gym gym = gyms.get(0);
                    Log.d("DEBUG", "Retrieved " + gym.getTrainers().size() + " gyms");
                    Toast.makeText(getBaseContext(), "Loaded trainers from " + gymName, Toast.LENGTH_SHORT).show();
                    ArrayList<Trainer> trainers = gym.getTrainers();
                    refreshFragment(trainers);
                } else {
                    Log.d("DEBUG", "Error: " + e.getMessage());
                }
            }
        });
    }

    public void refreshFragment(List<Trainer> trainers) {
        fragment.setItems(new ArrayList<Trainer>());
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.detach(fragment);
        ft.attach(fragment);
        ft.commit();
        fragment.setItems(trainers);
    }
}
