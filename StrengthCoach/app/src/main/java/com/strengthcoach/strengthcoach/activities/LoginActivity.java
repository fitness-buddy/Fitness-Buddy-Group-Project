package com.strengthcoach.strengthcoach.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseUser;
import com.strengthcoach.strengthcoach.R;
import com.strengthcoach.strengthcoach.helpers.Constants;
import com.strengthcoach.strengthcoach.helpers.Utils;

public class LoginActivity extends ActionBarActivity {
    EditText etName, etPhoneNumber;
    Button bLogin;
    ParseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    private void setupViews(){
        etName = (EditText) findViewById(R.id.etLoginName);
        etPhoneNumber = (EditText) findViewById(R.id.etLoginPhoneNumber);
        etPhoneNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        bLogin = (Button) findViewById(R.id.bLogin);
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a new user and signup
                String verifyCode = Utils.generateRandomCode();

                // Send SMS with verify code starts here
                SmsManager smsManager = SmsManager.getDefault();
                String smsMessage = verifyCode + " " + Constants.VERIFICATION_SMS_TEXT;
                smsManager.sendTextMessage(etPhoneNumber.getText().toString(), null, smsMessage, null, null);
                // Send SMS with verify code ends here

                Intent intent = new Intent(LoginActivity.this, PhoneNoVerificationActivity.class);
                intent.putExtra("etName", etName.getText().toString());
                intent.putExtra("etPhoneNumber", etPhoneNumber.getText().toString());
                intent.putExtra("verifyCode", verifyCode);
                Log.v("verifyCode", "verifyCode-=============================== " + verifyCode);
                startActivityForResult(intent, 30);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 30) {
            Intent returnIntent = new Intent();
            setResult(resultCode, returnIntent);
            finish();
        }
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