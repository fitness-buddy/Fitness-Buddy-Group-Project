package com.strengthcoach.strengthcoach.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.dexafree.materialList.controller.RecyclerItemClickListener;
import com.dexafree.materialList.model.CardItemView;
import com.dexafree.materialList.view.MaterialListView;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.strengthcoach.strengthcoach.R;
import com.strengthcoach.strengthcoach.models.Trainer;
import com.strengthcoach.strengthcoach.views.CustomCard;

import java.util.List;


public class HomeActivity extends ActionBarActivity {
    private static final String TAG = HomeActivity.class.getName();
    private static String sUserId;
    MaterialListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mListView = (MaterialListView) findViewById(R.id.material_listview);
        mListView.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(CardItemView cardItemView, int i) {
                // TODO: Pass a trainer here
                Intent intent = new Intent(getBaseContext(), RecyclerViewActivity.class);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(CardItemView cardItemView, int i) {

            }
        });
        populateTrainers();
    }

    private void populateTrainers() {
        ParseQuery<Trainer> query = ParseQuery.getQuery("Trainer");
        query.findInBackground(new FindCallback<Trainer>() {
            public void done(List<Trainer> trainers, ParseException e) {
                if (e == null) {
                    Log.d("DEBUG", "Retrieved " + trainers.size() + " trainers");
                    createCards(trainers);
                } else {
                    Log.d("DEBUG", "Error: " + e.getMessage());
                }
            }
        });
    }

    private void createCards(List<Trainer> trainers) {
        for (Trainer trainer : trainers) {
            CustomCard card = new CustomCard(this, trainer);
//            card.setDescription(trainer.getName());
//            card.setTitle(trainer.getPriceFormatted());
//            card.setDrawable(R.drawable.ic_launcher);
            mListView.add(card);
        }
    }

    // Get the userId from the cached currentUser object
    private void startWithCurrentUser() {
        sUserId = ParseUser.getCurrentUser().getObjectId();
    }

    // Create an anonymous user using ParseAnonymousUtils and set sUserId
    private void login() {
        ParseAnonymousUtils.logIn(new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.d(TAG, "Anonymous login failed: " + e.toString());
                } else {
                    startWithCurrentUser();
                }
            }
        });
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
            // TODO: Open this activity on itemclicked event.
            // TODO: Pass a trainer here
            Intent intent = new Intent(this, TrainerDetailsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
