package com.strengthcoach.strengthcoach.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_no_verification);
        setupViews();
        name =  getIntent().getStringExtra("etName");
        phoneno =  getIntent().getStringExtra("etPhoneNumber");
        verifyCode =  getIntent().getStringExtra("verifyCode");



    }

    public void setupViews(){
        etVerificationCode = (EditText) findViewById(R.id.etVerificationCode);
        bVerify = (Button) findViewById(R.id.bVerify);
        regenerateCode = (Button) findViewById(R.id.bRegenerateCode);
        bVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyCode.equals(etVerificationCode.getText().toString())) {
                    simpleUser = new SimpleUser();
                    // need to save data to user model;
                    simpleUser.setPhoneNumber(phoneno);
                    simpleUser.setName(name);
                    simpleUser.saveInBackground(new SaveCallback()
                    {
                        @Override
                        public void done(ParseException e) {
                            if(e==null){
                                Log.d("DEBUG!!!","inside if of user ");
                                //saved successfully
                                getCurrentUserId(phoneno);
                            } else{
                                Log.d("DEBUG!!!","User Not Found");
                            }

                        }
                    });
                    Intent intent = new Intent(PhoneNoVerificationActivity.this, BlockSlotActivity.class);
                    intent.putExtra("etPhoneNumber", phoneno);
                    startActivity(intent);
                }
            }
        });
        regenerateCode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String verifyCode = Utils.generateRandomCode();

                // Send SMS with verify code starts here
                SmsManager smsManager = SmsManager.getDefault();
                String smsMessage = verifyCode +" "+ Constants.VERIFICATION_SMS_TEXT ;
                smsManager.sendTextMessage(phoneno, null, smsMessage, null, null);
                // Send SMS with verify code ends here

                Intent intent = new Intent(PhoneNoVerificationActivity.this, PhoneNoVerificationActivity.class);
                intent.putExtra("etName", name);
                intent.putExtra("etPhoneNumber", phoneno);
                intent.putExtra("verifyCode", verifyCode);
                startActivity(intent);

            }

        });
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
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(PhoneNoVerificationActivity.this);
                    SharedPreferences.Editor edit = pref.edit();
                    edit.putString("userId", userId);
                    edit.commit();
                } else {
                    Log.d("DEBUG", "Error: " + e.getMessage());
                }
            }

        });
    }

}