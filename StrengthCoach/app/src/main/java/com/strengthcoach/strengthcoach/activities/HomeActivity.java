package com.strengthcoach.strengthcoach.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.strengthcoach.strengthcoach.R;
import com.strengthcoach.strengthcoach.fragments.TrainersListFragment;
import com.strengthcoach.strengthcoach.models.Gym;
import com.strengthcoach.strengthcoach.models.SimpleUser;
import com.strengthcoach.strengthcoach.models.Trainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class HomeActivity extends AppCompatActivity  {
    public static Trainer markedFavorite;
    private final int LOAD_TRAINERS_FOR_GYM = 20;
    private final int LOGIN_FOR_FAVORITES = 122;
    private final int LOGIN_FOR_MARKING_FAVORITES = 117;
    private TrainersListFragment fragment;
    MenuItem miActionProgressItem;
    ProgressWheel progressWheel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.navigationBarColor));
        }
        // Later refactor this logic and move to nav drawer
//        SignOut();

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
        // Store instance of the menu item containing progress
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        // Extract the action-view from the menu item
        ProgressBar v =  (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            String appLinkUrl, previewImageUrl;
//
//            appLinkUrl = "https://github.com/varungu/Android-Bootcamp";
//            previewImageUrl = "http://www.ajayengineeringworks.com/Adminpanel/product_images/06c3757555a99c26ee8f3e8bebdaba0c.jpg";
//
//            if (AppInviteDialog.canShow()) {
//                AppInviteContent content = new AppInviteContent.Builder()
//                        .setApplinkUrl(appLinkUrl)
//                        .setPreviewImageUrl(previewImageUrl)
//                        .build();
//                AppInviteDialog.show(this, content);
//            }
//        } else if (id == R.id.action_map) {
//            launchMap();
//        }

        if (id == R.id.action_map) {
            launchMap();
        }
        if(id == R.id.miActionProgress){
            showProgressBar();
        }


        return super.onOptionsItemSelected(item);
    }

    public void launchMap() {
        Intent intent = new Intent(this, MapActivity.class);
        startActivityForResult(intent, LOAD_TRAINERS_FOR_GYM);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    // Pass the list of trainers to fragment
    public void populateTrainers() {
        final TrainersListFragment finalFragment = fragment;
        ParseQuery<Trainer> query = ParseQuery.getQuery("Trainer");
        query.include("favorited_by");
        query.orderByDescending("name");
        query.findInBackground(new FindCallback<Trainer>() {
            public void done(List<Trainer> trainers, ParseException e) {
                if (e == null) {
                    hideProgressBar();
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
        String currentUserId = getLoggedInUserId();

        // If userId is found; user has already signed up
        if (!currentUserId.equals("")) {

            ParseQuery<SimpleUser> query = ParseQuery.getQuery("SimpleUser");
            query.whereEqualTo("objectId", currentUserId);
            query.getFirstInBackground(new GetCallback<SimpleUser>() {
                @Override
                public void done(SimpleUser simpleUser, ParseException e) {
                    SimpleUser.currentUserObject = simpleUser;
                    showFavorites();
                }
            });
        } else {
            // Ask the user to sign up
            launchLoginActivity(LOGIN_FOR_FAVORITES);
        }
        hideProgressBar();
    }

    public void launchLoginActivity(final int IDENTIFIER) {
        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
        startActivityForResult(intent, IDENTIFIER);
        overridePendingTransition(R.anim.enter_from_bottom, R.anim.stay_in_place);
    }

    // No need to update the fav icon because it is already done through the adapter
    public void handleFavorite() {
        ParseQuery<SimpleUser> query = ParseQuery.getQuery("SimpleUser");
        query.whereEqualTo("objectId", SimpleUser.currentUserObjectId);
        query.getFirstInBackground(new GetCallback<SimpleUser>() {
            @Override
            public void done(SimpleUser simpleUser, ParseException e) {
                SimpleUser.currentUserObject = simpleUser;
                markedFavorite.getFavoritedBy().add(SimpleUser.currentUserObject);
                markedFavorite.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.d("DEBUG", "Successfully marked favorite trainer");
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public void showFavorites() {
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
    }

    public String getLoggedInUserId() {
        SharedPreferences pref =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String userId = pref.getString("userId", "");
        return userId;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case LOAD_TRAINERS_FOR_GYM:
                    // Get the name of gym from map activity
                    String gymName = data.getExtras().getString("gymName");
                    populateTrainersFromGymName(gymName);
                    break;

                case LOGIN_FOR_FAVORITES:
                    populateFavoriteTrainers();
                    break;

                case LOGIN_FOR_MARKING_FAVORITES:
                    handleFavorite();
            }
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
                    Collections.sort(trainers, new Comparator<Trainer>(){
                        public int compare(Trainer o1, Trainer o2){
                            if(o1.getName() == o2.getName())
                                return 0;
                            return o1.getName().compareTo(o2.getName());
                        }
                    });
                    refreshFragment(trainers);

                } else {
                    Log.d("DEBUG", "Error: " + e.getMessage());
                }
                hideProgressBar();
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
        hideProgressBar();
    }

    public void SignOut() {
        if (SimpleUser.currentUserObject != null)
        {
            {
                // Delete favorites
                ParseQuery<Trainer> query = ParseQuery.getQuery("Trainer");
                query.include("favorited_by");
                query.whereEqualTo("favorited_by", SimpleUser.currentUserObject);
                query.findInBackground(new FindCallback<Trainer>() {
                    public void done(List<Trainer> trainers, ParseException e) {
                        if (e == null) {
                            for (int i = 0; i < trainers.size(); i++) {
                                Trainer t = trainers.get(i);
                                t.getFavoritedBy().remove(SimpleUser.currentUserObject);
                                t.saveInBackground();
                            }
                        } else {
                            Log.d("DEBUG", "Error: " + e.getMessage());
                        }
                    }
                });
            }

            {
                // Delete items in cart
                ParseQuery<ParseObject> query = ParseQuery.getQuery("BlockedSlots");
                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> slots, ParseException e) {
                        if (e == null) {
                            for (int i = 0; i < slots.size(); i++) {
                                ParseObject slot = slots.get(i);
                                slot.deleteInBackground();
                            }
                        } else {
                            Log.d("DEBUG", "Error: " + e.getMessage());
                        }
                    }
                });
            }

            {
                // Delete messages
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Message");
                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> slots, ParseException e) {
                        if (e == null) {
                            for (int i = 0; i < slots.size(); i++) {
                                ParseObject slot = slots.get(i);
                                slot.deleteInBackground();
                            }
                        } else {
                            Log.d("DEBUG", "Error: " + e.getMessage());
                        }
                    }
                });
            }
        }

        // Sign-out
        SimpleUser.currentUserObject.deleteInBackground();
        SimpleUser.currentUserObject = null;
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor edit = pref.edit();
        edit.clear();
        edit.commit();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        // Extract the action-view from the menu item
        ProgressBar v =  (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);
        // Return to finish
        return super.onPrepareOptionsMenu(menu);
    }
    public void showProgressBar() {
        // Show progress item
        miActionProgressItem.setVisible(true);
    }

    public void hideProgressBar() {
        // Hide progress item
        miActionProgressItem.setVisible(false);
    }


}
