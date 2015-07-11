package com.strengthcoach.strengthcoach.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.strengthcoach.strengthcoach.R;
import com.strengthcoach.strengthcoach.helpers.Constants;
import com.strengthcoach.strengthcoach.helpers.Utils;
import com.strengthcoach.strengthcoach.models.SimpleUser;

public class PhoneNoVerificationActivity extends ActionBarActivity {

    EditText etVerificationCode;
    String name, phoneno, verifyCode;
    Button bVerify;
    SimpleUser simpleUser;
    Button regenerateCode;
    Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_no_verification);
        // setup Toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        setupViews();
        name =  getIntent().getStringExtra("etName");
        phoneno =  getIntent().getStringExtra("etPhoneNumber");
        verifyCode =  getIntent().getStringExtra("verifyCode");
    }

    public void setupViews(){
        etVerificationCode = (EditText) findViewById(R.id.etVerificationCode);
        bVerify = (Button) findViewById(R.id.bVerify);

        bVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (verifyCode.equals(etVerificationCode.getText().toString())) {
                ParseQuery<SimpleUser> query = ParseQuery.getQuery("SimpleUser");
                query.whereEqualTo("phone_number", phoneno);
                query.getFirstInBackground(new GetCallback<SimpleUser>() {
                    public void done(SimpleUser user, ParseException e) {
                        if (user == null) {
                            simpleUser = new SimpleUser();
                            // need to save data to user model;
                            simpleUser.setPhoneNumber(phoneno);
                            simpleUser.setName(name);
                            simpleUser.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Log.d("DEBUG!!!", "inside if of user ");
                                        //saved successfully
                                        getCurrentUserId(phoneno);
                                    } else {
                                        Log.d("DEBUG!!!", "User Not Found");
                                    }

                                }
                            });
                        } else {
                            getCurrentUserId(phoneno);
                        }
                    }
                });
            }
            }
        });
    }

    public void callRegenerateCode(View v){
        regenerateCode = (Button) findViewById(R.id.bRegenerateCode);
        // Create a new user and signup
        String strRegeneratedCode = Utils.generateRandomCode();

        // Send SMS with verify code starts here
        SmsManager smsManager = SmsManager.getDefault();
        String smsMessage = strRegeneratedCode + " " + Constants.VERIFICATION_SMS_TEXT;
        smsManager.sendTextMessage(phoneno, null, smsMessage, null, null);

        verifyCode = strRegeneratedCode;
        Log.v("Regenerated Code","Regenerated Code  new  ************************************************************* "+verifyCode);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_phone_no_verification, menu);
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

    private void getCurrentUserId(String phoneNumber) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("SimpleUser");
        query.whereEqualTo("phone_number", phoneNumber);
        query.getFirstInBackground(new GetCallback<ParseObject>(){
            public void done(ParseObject object, ParseException e) {
            if (e == null) {
                String userId = object.getObjectId().toString();
                String currentUserObjId = userId;
                SimpleUser.currentUserObjectId = currentUserObjId;
                // Write the userId in shared pref if the user successfully signed up
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor edit = pref.edit();
                edit.putString("userId", userId);
                edit.commit();

                Intent returnIntent = new Intent();
                setResult(RESULT_OK, returnIntent);
                finish();
                overridePendingTransition(R.anim.stay_in_place, R.anim.exit_to_bottom);
            } else {
                Log.d("DEBUG", "Error: " + e.getMessage());
            }
            }

        });
    }

}