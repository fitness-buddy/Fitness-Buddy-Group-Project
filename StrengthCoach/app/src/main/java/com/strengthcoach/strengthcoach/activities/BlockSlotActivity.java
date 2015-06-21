package com.strengthcoach.strengthcoach.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;
import com.strengthcoach.strengthcoach.R;
import com.strengthcoach.strengthcoach.helpers.Constants;
import com.strengthcoach.strengthcoach.models.BlockedSlots;
import com.strengthcoach.strengthcoach.models.SimpleUser;
import com.strengthcoach.strengthcoach.models.Trainer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class BlockSlotActivity extends ActionBarActivity{
    CaldroidFragment caldroidFragment;
    Date currentDate, dateAfterMonth;
    Button bProceedToPayment, bAddToCart;
    Date previousDate = null;
    String userSelectedDate;
    Spinner spSelectSlot;
    String dayOfTheWeek, selectedDate;
    SimpleDateFormat simpleDayFormat = new SimpleDateFormat(Constants.DAY_OF_WEEK_FORMAT);
    SimpleDateFormat simpleDateStrFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
    Date date = new Date();
    ArrayList<String> listOfSlots = new ArrayList<String>();
    ArrayList<Integer> arBookedSlots = new ArrayList<Integer>();
    ArrayList<Integer> arraySlots = new ArrayList<Integer>();
    ArrayList<String> listOfAvailableDays = new ArrayList<String>();
    String name, phoneno;
    BlockedSlots  bSlots ;
    boolean flag;
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_slot);
        // setup Toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        spSelectSlot = (Spinner) findViewById(R.id.spSelectSlot);
        bAddToCart = (Button) findViewById(R.id.bAddToCart);
        bProceedToPayment = (Button)findViewById(R.id.bProceedToPayment);
        name =  getIntent().getStringExtra("etName");
        phoneno =  getIntent().getStringExtra("etPhoneNumber");
        flag=false;

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
        selectedDate = simpleDateStrFormat.format(date);
        userSelectedDate = selectedDate;
        alreadyBookedSlots(Trainer.currentTrainerObjectId, dayOfTheWeek, selectedDate);
        setupListener();



        if (getLoggedInUserId().equals("")) {
            // Start login activity
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, 20);
        }
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
                                caldroidFragment.setBackgroundResourceForDate(R.color.colorPrimary, result);
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
        // spSelectSlot.setOnItemSelectedListener(this);
        bProceedToPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callCartActivity();
            }
        });

        bAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bSlots = new BlockedSlots();
                // need to save data to user model;
                String currentUser;
                if (SimpleUser.currentUserObjectId == null) {
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    currentUser = pref.getString("userId", "");
                } else {
                    currentUser = SimpleUser.currentUserObjectId;
                }
                if (!spSelectSlot.getSelectedItem().toString().equals("Select a slot") && spSelectSlot.getSelectedItem().toString() != null) {
                    ParseObject trainer = ParseObject.createWithoutData("Trainer", Trainer.currentTrainerObjectId);
                    ParseObject user = ParseObject.createWithoutData("SimpleUser", currentUser);
                    bSlots.setTrainerId(trainer);
                    bSlots.setBookedByUserId(user);
                    bSlots.setSlotDate(userSelectedDate);
                    String[] selectedSlot = spSelectSlot.getSelectedItem().toString().split(" ");
                    String slotTime = selectedSlot[0];
                    String finalSelectedSlot = "";
                    if (selectedSlot[1].equals(Constants.AM)) {
                        finalSelectedSlot = slotTime;
                    } else if (selectedSlot[1].equals(Constants.PM)) {
                        if (slotTime.equals("12")) {
                            finalSelectedSlot = slotTime;
                        } else {
                            int intSlot = 12 + Integer.valueOf(slotTime);
                            finalSelectedSlot = Integer.toString(intSlot);
                        }
                    }
                    bSlots.setSlotTime(finalSelectedSlot);
                    bSlots.setStatus(Constants.ADD_TO_CART);
                    bSlots.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d("DEBUG!!!", "Slot Saved Successfully ");

                            } else {
                                Log.d("DEBUG!!!", "Slot Not Saved");
                            }

                        }
                    });
                    bProceedToPayment.setVisibility(View.VISIBLE);
                    spSelectSlot.setSelection(0);
                } else {
                    Toast.makeText(BlockSlotActivity.this, "Select a slot", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void setupCaldroidListener(){
        // Setup listener
        final CaldroidListener listener = new CaldroidListener() {

            @Override
            public void onSelectDate(Date date, View view) {
                // changing the background color of earlier selected date to blue
                if (previousDate != null ){
                    caldroidFragment.setBackgroundResourceForDate(R.color.colorPrimary, previousDate);
                    caldroidFragment.refreshView();
                }
                flag=false;
                // changing the background color of selected date to pink
                caldroidFragment.setBackgroundResourceForDate(R.color.pink, date);
                previousDate = date;
                caldroidFragment.refreshView();
                userSelectedDate = simpleDateStrFormat.format(date);
                alreadyBookedSlots(Trainer.currentTrainerObjectId,simpleDayFormat.format(date),simpleDateStrFormat.format(date));


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
    public void alreadyBookedSlots(final String trainerId, final String sDay, final String sDate) {
        arBookedSlots.clear();
        ParseObject trainer = ParseObject.createWithoutData("Trainer", trainerId);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("BlockedSlots");
        query.selectKeys(Arrays.asList("slot_time"));
        query.include("trainer_id");
        query.whereEqualTo("trainer_id", trainer);
        query.whereEqualTo("slot_date", sDate);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> bookedSlots, com.parse.ParseException e) {
                if (e == null) {
                    for (ParseObject slots : bookedSlots) {
                        int slotTime = Integer.valueOf(slots.getString("slot_time"));
                        arBookedSlots.add(slotTime);
                    }
                } else {
                    Log.d("DEBUG", "Error: " + e.getMessage());
                }
                populateAvailableSlots(trainerId, sDay, sDate);
            }


        });

    }
    public void populateAvailableSlots(final String trainerId, final String day, final String sDate) {
        final ParseObject trainer = ParseObject.createWithoutData("Trainer",trainerId);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("TrainerSlots");
        query.selectKeys(Arrays.asList("start_time", "end_time"));
        query.include("trainer_id");
        query.whereEqualTo("trainer_id", trainer);
        query.whereEqualTo("day", day);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> trainerSlots, com.parse.ParseException e) {
                if (e == null) {
                    listOfSlots.clear();
                    listOfSlots.add(Constants.SELECT_SLOT);
                    for (ParseObject slots : trainerSlots) {
                        int startTimeInt = Integer.valueOf(slots.getString("start_time"));
                        int endTimeInt = Integer.valueOf(slots.getString("end_time"));
                        arraySlots.clear();
                        for (int i = startTimeInt; startTimeInt < endTimeInt; startTimeInt++) {
                            arraySlots.add(startTimeInt);
                        }
                        // find out time slots not in arBookedSlots
                        List<Integer> noBookedSlots = new ArrayList<Integer>(arraySlots);
                        noBookedSlots.removeAll(arBookedSlots);

                        for (int k = 0; k < noBookedSlots.size(); k++) {
                            int intSlotsWithoutBookedSlots = noBookedSlots.get(k);
                            String slotsWithoutBookedSlots;
                            if (intSlotsWithoutBookedSlots <= 11) {
                                slotsWithoutBookedSlots = intSlotsWithoutBookedSlots + " " + Constants.AM;
                            } else if (intSlotsWithoutBookedSlots == 12) {
                                slotsWithoutBookedSlots = intSlotsWithoutBookedSlots + " " + Constants.PM;
                            } else {
                                slotsWithoutBookedSlots = (intSlotsWithoutBookedSlots - 12) + " " + Constants.PM;
                            }
                            listOfSlots.add(slotsWithoutBookedSlots);
                        }
                        try {
                            Date d = simpleDateStrFormat.parse(sDate);
                            caldroidFragment.setBackgroundResourceForDate(R.color.pink, d);
                            previousDate = d;
                            caldroidFragment.refreshView();
                        } catch (java.text.ParseException e1) {
                            e1.printStackTrace();
                        }
                        spSelectSlot.setAdapter(new ArrayAdapter<String>(BlockSlotActivity.this,
                                android.R.layout.simple_spinner_item, listOfSlots));
                    }
                } else {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 20) {
            if(resultCode != RESULT_OK){
                // User didn't login cancel book slot
                Intent returnIntent = new Intent();
                setResult(RESULT_CANCELED, returnIntent);
                finish();
            }
        }
    }

    public void onCartClick(MenuItem item){
        callCartActivity();

    }
    public void callCartActivity() {
        Intent intent = new Intent(BlockSlotActivity.this, CartActivity.class);
        startActivity(intent);
    }

    private String getLoggedInUserId() {
        SharedPreferences pref =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String userId = pref.getString("userId", "");
        return userId;
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
        overridePendingTransition(R.anim.stay_in_place, R.anim.exit_to_bottom);
    }
}