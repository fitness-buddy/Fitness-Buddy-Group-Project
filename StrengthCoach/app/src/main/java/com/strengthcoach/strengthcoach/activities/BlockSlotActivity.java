package com.strengthcoach.strengthcoach.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
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
    Button bProceedToPayment;
    Date previousDate = null;
    Date userSelectedDate;
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
    Button bSelectSlot;
    public static int addToCartCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_slot);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.navigationBarColor));
        }

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

        bProceedToPayment = (Button)findViewById(R.id.bProceedToPayment);
        bSelectSlot = (Button) findViewById(R.id.bSelectSlot);

        if(addToCartCount > 0){
            bProceedToPayment.setText(Html.fromHtml("Checkout ("+addToCartCount+")"));
        }

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
            setupCaldroidListener(this);


            FragmentTransaction t = getSupportFragmentManager().beginTransaction();
            t.replace(R.id.flCalendar, caldroidFragment);
            t.commit();
        }

        dayOfTheWeek = simpleDayFormat.format(date);
        selectedDate = simpleDateStrFormat.format(date);
        userSelectedDate = date;
        alreadyBookedSlots(Trainer.currentTrainerObjectId, dayOfTheWeek, selectedDate, this);
        setupListener(this);

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
                                caldroidFragment.setBackgroundResourceForDate(R.color.availableSlotColor, result);
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


    public void setupListener(final Context context){
        bSelectSlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> list = listOfSlots;
                CharSequence[] cs = list.toArray(new CharSequence[list.size()]);
                new MaterialDialog.Builder(context)
                        .title(R.string.book_slot_dialog_title)
                        .items(cs)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                bSlots = new BlockedSlots();
                                // need to save data to user model;
                                String currentUser;
                                if (SimpleUser.currentUserObjectId == null) {
                                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                    currentUser = pref.getString("userId", "");
                                } else {
                                    currentUser = SimpleUser.currentUserObjectId;
                                }
                                ParseObject trainer = ParseObject.createWithoutData("Trainer", Trainer.currentTrainerObjectId);
                                ParseObject user = ParseObject.createWithoutData("SimpleUser", currentUser);
                                bSlots.setTrainerId(trainer);
                                bSlots.setBookedByUserId(user);
                                bSlots.setSlotDate(simpleDateStrFormat.format(userSelectedDate));
                                String[] selectedSlot = text.toString().split(" ");
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
                                            ++addToCartCount;
                                            bProceedToPayment.setText(Html.fromHtml("Checkout ("+addToCartCount+")"));
                                            alreadyBookedSlots(Trainer.currentTrainerObjectId, simpleDayFormat.format(userSelectedDate), simpleDateStrFormat.format(userSelectedDate), context);
                                        } else {
                                            Log.d("DEBUG!!!", "Slot Not Saved");
                                        }
                                    }
                                });
                                bProceedToPayment.setVisibility(View.VISIBLE);
                                Log.v("payment", "Proceed to PAyment " + addToCartCount);
                                if (addToCartCount>0){
                                    bProceedToPayment.setText(Html.fromHtml("CheckOut ("+addToCartCount+")"));
                                }
                                return true;
                            }
                        })
                        .positiveText(R.string.positive_text)
                        .negativeText(R.string.cancel)
                        .show();
            }
        });

        bProceedToPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callCartActivity();
            }
        });
    }

    public void setupCaldroidListener(final Context context){
        // Setup listener
        final CaldroidListener listener = new CaldroidListener() {

            @Override
            public void onSelectDate(Date date, View view) {
                // changing the background color of earlier selected date to blue
                if (previousDate != null ){
                    caldroidFragment.setBackgroundResourceForDate(R.color.availableSlotColor, previousDate);
                    caldroidFragment.refreshView();
                }
                flag=false;
                // changing the background color of selected date to pink
                caldroidFragment.setBackgroundResourceForDate(R.color.selectedSlotColor, date);
                previousDate = date;
                caldroidFragment.refreshView();
                userSelectedDate = date;
                alreadyBookedSlots(Trainer.currentTrainerObjectId,simpleDayFormat.format(date),simpleDateStrFormat.format(date), context);


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
    public void alreadyBookedSlots(final String trainerId, final String sDay, final String sDate, final Context context) {
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
                populateAvailableSlots(trainerId, sDay, sDate, context);
            }
        });

    }
    public void populateAvailableSlots(final String trainerId, final String day, final String sDate, final Context context) {
        final TextView tvSlotsInfo = (TextView) findViewById(R.id.tvSlotsInfo);
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
                        SimpleDateFormat dt = new SimpleDateFormat("MMMM dd");
                        tvSlotsInfo.setText(listOfSlots.size() + " slots available on " + dt.format(new Date(sDate)) + ", " + day);
                        bSelectSlot.setBackground(getResources().getDrawable(R.drawable.primary_blue_button));

                        try {
                            Date d = simpleDateStrFormat.parse(sDate);
                            caldroidFragment.setBackgroundResourceForDate(R.color.selectedSlotColor, d);
                            previousDate = d;
                            caldroidFragment.refreshView();
                        } catch (java.text.ParseException e1) {
                            e1.printStackTrace();
                        }
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
        overridePendingTransition(R.anim.enter_from_bottom, R.anim.stay_in_place);
    }

    private String getLoggedInUserId() {
        SharedPreferences pref =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String userId = pref.getString("userId", "");
        return userId;
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.stay_in_place, R.anim.exit_to_bottom);
    }
}