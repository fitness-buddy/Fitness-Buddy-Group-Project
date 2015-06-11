package com.strengthcoach.strengthcoach.activities;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;
import com.strengthcoach.strengthcoach.R;
import com.strengthcoach.strengthcoach.helpers.Constants;
import com.strengthcoach.strengthcoach.models.SimpleUser;
import com.strengthcoach.strengthcoach.models.Trainer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class BlockSlotActivity extends ActionBarActivity {
    CaldroidFragment caldroidFragment;
    Date currentDate, dateAfterMonth;
    Button bProceedToPayment;
    Spinner spSelectSlot;
    String dayOfTheWeek;
    SimpleDateFormat simpleDayFormat = new SimpleDateFormat(Constants.DAY_OF_WEEK_FORMAT);
    Date date = new Date();
    ArrayList<String> listOfSlots = new ArrayList<String>();
    ArrayList<String> listOfAvailableDays = new ArrayList<String>();
    String name, phoneno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_slot);
        spSelectSlot = (Spinner) findViewById(R.id.spSelectSlot);
        bProceedToPayment = (Button)findViewById(R.id.bProceedToPayment);
        name =  getIntent().getStringExtra("etName");
        phoneno =  getIntent().getStringExtra("etPhoneNumber");

        if (savedInstanceState == null) {
            caldroidFragment = new CaldroidFragment();
            Bundle args = new Bundle();
            Calendar cal = Calendar.getInstance();
            args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
            args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
            args.putBoolean(CaldroidFragment.SQUARE_TEXT_VIEW_CELL, false);
            caldroidFragment.setArguments(args);
            currentDate = Calendar.getInstance().getTime();// get current date
            // adding one month to current date
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, 1);
            dateAfterMonth = calendar.getTime();
            caldroidFragment.setMinDate(currentDate); // disable dates prior to current date
            caldroidFragment.setMaxDate(dateAfterMonth);// disable dates after a month from current date
            getDaysBetweenDates(currentDate, dateAfterMonth, Trainer.currentTrainerObjectId);
            setupCaldroidListener();


            FragmentTransaction t = getSupportFragmentManager().beginTransaction();
            t.replace(R.id.flCalendar, caldroidFragment);
            t.commit();
        }

        dayOfTheWeek = simpleDayFormat.format(date);
        populateAvailableSlots(Trainer.currentTrainerObjectId, "a", dayOfTheWeek);
        setupListener();
    }

    public void getDaysBetweenDates(final Date startdate, final Date enddate, String trainerId) {
        ParseObject trainer = ParseObject.createWithoutData("Trainer", trainerId);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("TrainerSlots");
        query.selectKeys(Arrays.asList("day"));
        query.include("trainer_id");
        query.whereEqualTo("trainer_id", trainer);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> trainerSlots, com.parse.ParseException e) {
                if (e == null) {
                    listOfAvailableDays.clear();
                    for (ParseObject slots : trainerSlots) {
                        String availableDay = slots.getString("day");
                        listOfAvailableDays.add(availableDay);
                    }


                    ArrayList<Date> unAvailableDates = new ArrayList<Date>();
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(startdate);

                    while (calendar.getTime().before(enddate)) {

                        Date result = calendar.getTime();
                        for (int i = 0; i < listOfAvailableDays.size(); i++) {
                            if (listOfAvailableDays.contains(simpleDayFormat.format(result))) {
                                caldroidFragment.setBackgroundResourceForDate(R.color.caldroid_holo_blue_light, result);
                                caldroidFragment.setTextColorForDate(R.color.white, result);
                            } else {
                                unAvailableDates.add(result);
                            }
                        }
                        calendar.add(Calendar.DATE, 1);
                    }
                    caldroidFragment.setDisableDates(unAvailableDates);
                    caldroidFragment.refreshView();

                } else {
                    Log.d("DEBUG", "Error: " + e.getMessage());
                }
            }
        });


    }


    public void setupListener(){
        bProceedToPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(BlockSlotActivity.this, PaymentActivity.class);
                intent.putExtra("etPhoneNumber", phoneno);
                startActivity(intent);
            }
        });

    }
    public void setupCaldroidListener(){
        // Setup listener
        final CaldroidListener listener = new CaldroidListener() {

            @Override
            public void onSelectDate(Date date, View view) {
               String  month = (String) android.text.format.DateFormat.format("MM", date); //06
                String year = (String) android.text.format.DateFormat.format("yyyy", date); //2013
                String day = (String) android.text.format.DateFormat.format("dd", date); //20

                populateAvailableSlots(Trainer.currentTrainerObjectId,"a",simpleDayFormat.format(date));

            }

            @Override
            public void onChangeMonth(int month, int year) {

            }

            @Override
            public void onLongClickDate(Date date, View view) {

            }

            @Override
            public void onCaldroidViewCreated() {
                if (caldroidFragment.getLeftArrowButton() != null) {
                    /*Toast.makeText(getApplicationContext(),
                            "Caldroid view is created", Toast.LENGTH_SHORT)
                            .show();*/
                }
            }

        };
        caldroidFragment.setCaldroidListener(listener);

    }
    public void populateAvailableSlots(String trainerId, String status, String day) {
        ParseObject trainer = ParseObject.createWithoutData("Trainer",trainerId);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("TrainerSlots");
        query.selectKeys(Arrays.asList("start_time", "end_time"));
        query.include("trainer_id");
        query.whereEqualTo("trainer_id", trainer);
        query.whereEqualTo("status", status);
        query.whereEqualTo("day", day);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> trainerSlots, com.parse.ParseException e) {
                if (e == null) {
                    listOfSlots.clear();
                    listOfSlots.add(Constants.SELECT_SLOT);
                    for(ParseObject slots: trainerSlots){
                        int startTimeInt = Integer.valueOf(slots.getString("start_time"));
                        int endTimeInt = Integer.valueOf(slots.getString("end_time"));

                        for (int i = startTimeInt; startTimeInt < endTimeInt; startTimeInt++)
                        {
                           String timeRange = startTimeInt + "-" + (startTimeInt+1) ;
                           listOfSlots.add(timeRange);
                        }
                        spSelectSlot.setAdapter(new ArrayAdapter<String>(BlockSlotActivity.this,
                                android.R.layout.simple_spinner_item, listOfSlots));
                    } }else {
                    Log.d("DEBUG", "Error: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_block_slot, menu);
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
}
