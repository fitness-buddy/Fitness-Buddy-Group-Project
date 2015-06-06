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

import com.parse.ParseUser;
import com.strengthcoach.strengthcoach.R;

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
        bLogin = (Button) findViewById(R.id.bLogin);
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do something here
                ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser == null) {
                    // Create a new user and signup
                    String verifyCode = generateRandomCode();
                    Intent intent = new Intent(LoginActivity.this, PhoneNoVerificationActivity.class);
                    intent.putExtra("etName",etName.getText().toString());
                    intent.putExtra("etPhoneNumber",etPhoneNumber.getText().toString());
                    intent.putExtra("verifyCode",verifyCode);
                    startActivity(intent);
                } else {
                    user = currentUser;
                    /*PFQuery *query = [PFUser query];
                    [query whereKey:@"username" equalTo:@"actualUsername"];
                    PFUser *user = (PFUser *)[query getFirstObject];*/
                }

            }
        });
    }

    public String generateRandomCode() {
        //generate a 4 digit integer
        int randomCode = (int) (Math.random() * 9000) + 1000;
        Log.v("Login", "generated code " + String.valueOf(randomCode));
        return String.valueOf(randomCode);
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
