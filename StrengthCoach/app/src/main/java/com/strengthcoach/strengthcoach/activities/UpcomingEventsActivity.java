package com.strengthcoach.strengthcoach.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.strengthcoach.strengthcoach.R;
import com.strengthcoach.strengthcoach.adapters.CartItemsAdapter;
import com.strengthcoach.strengthcoach.adapters.UpcomingEventsAdapter;
import com.strengthcoach.strengthcoach.helpers.Constants;
import com.strengthcoach.strengthcoach.models.BlockedSlots;
import com.strengthcoach.strengthcoach.models.SimpleUser;
import com.strengthcoach.strengthcoach.models.Trainer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UpcomingEventsActivity extends AppCompatActivity {
    public static ArrayList<BlockedSlots> alSlots;
    public static UpcomingEventsAdapter adSlots;
    public static ListView lvUpcomingEvents;
    Button bBookMoreSlots;
    Date currentDate;
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_events);

        // setup Toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        alSlots = new ArrayList<>();
        lvUpcomingEvents = (ListView) findViewById(R.id.lvUpcomingEvents);
        bBookMoreSlots = (Button) findViewById(R.id.bBookMoreSlots);
        // adding header to the list view starts
        View header = LayoutInflater.from(UpcomingEventsActivity.this).inflate(R.layout.upcoming_events_header, null);
        lvUpcomingEvents.addHeaderView(header);
        adSlots = new UpcomingEventsAdapter(UpcomingEventsActivity.this, alSlots);
        // adding header to the list view ends
        bBookMoreSlots = (Button) findViewById(R.id.bBookMoreSlots);
        lvUpcomingEvents.setAdapter(adSlots);
        populateEvents();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_upcoming_events, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void populateEvents() {

        String currentUser;
        currentDate = Calendar.getInstance().getTime();// get current date
        final SimpleDateFormat simpleDateStrFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
        final String strCurrentDate = simpleDateStrFormat.format(currentDate);
        if (SimpleUser.currentUserObjectId == null) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            currentUser = pref.getString("userId", "");
        } else {
            currentUser = SimpleUser.currentUserObjectId;
        }
        ParseObject trainer = ParseObject.createWithoutData("Trainer", Trainer.currentTrainerObjectId);
        ParseObject user = ParseObject.createWithoutData("SimpleUser", currentUser);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("BlockedSlots");
        query.selectKeys(Arrays.asList("slot_date", "slot_time"));
        query.include("trainer_id");
        query.whereEqualTo("trainer_id", trainer);
        query.whereEqualTo("user_id", user);
        query.whereEqualTo("status", Constants.BOOKED);
        query.orderByAscending("slot_date");
        adSlots.clear();
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> trainerSlots, ParseException e) {
                if (e == null) {
                    for (ParseObject slots : trainerSlots) {
                        BlockedSlots b = new BlockedSlots();
                        b.setSlotDate(slots.getString("slot_date"));
                        b.setSlotTime(slots.getString("slot_time"));
                        try {
                            Date cDate = simpleDateStrFormat.parse(strCurrentDate);
                            Date slotDate = simpleDateStrFormat.parse(slots.getString("slot_date"));
                            if (slotDate.after(cDate) || slotDate.equals(cDate)) {
                                alSlots.add(b);
                            }
                            adSlots.notifyDataSetChanged();
                        } catch (java.text.ParseException e1) {
                            e1.printStackTrace();
                        }
                        adSlots.notifyDataSetChanged();
                    }
                } else {
                    Log.v("DEBUG!!!!!!!!!!!!!", "Error occured >>>>>>>>>>>>>>>>>>         " + e);
                }
            }
        });
        bBookMoreSlots.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UpcomingEventsActivity.this, BlockSlotActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_bottom, R.anim.stay_in_place);
            }
        });
    }
}
