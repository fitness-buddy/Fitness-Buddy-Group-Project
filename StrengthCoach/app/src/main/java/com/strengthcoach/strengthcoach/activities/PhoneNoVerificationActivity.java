package com.strengthcoach.strengthcoach.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.strengthcoach.strengthcoach.R;
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
                Log.v("inside onlcik", "inside onlcick");
                if (verifyCode.equals(etVerificationCode.getText().toString())) {
                    Log.v("inside verify code", "inside verifycode");
                    simpleUser = new SimpleUser();
                    // need to save data to user model;
                    simpleUser.setPhoneNumber(phoneno);
                    simpleUser.setName(name);
                    simpleUser.saveInBackground();
                }
            }
        });
        regenerateCode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.v("inside onlcik", "inside onlcick");
                String verifyCode = Utils.generateRandomCode();
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
}
