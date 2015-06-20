package com.strengthcoach.strengthcoach.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.strengthcoach.strengthcoach.R;
import com.strengthcoach.strengthcoach.dialog.ErrorDialogFragment;
import com.strengthcoach.strengthcoach.dialog.ProgressDialogFragment;
import com.strengthcoach.strengthcoach.helpers.Constants;
import com.strengthcoach.strengthcoach.models.SimpleUser;
import com.strengthcoach.strengthcoach.models.Trainer;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Customer;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

public class PaymentActivity extends AppCompatActivity {

    private String cardNumber = null;
    private String cardHidden = "xxxx xxxx xxxx";
    private int edit = 0;
    Button bSubmit;
    EditText etExpiry, etCCNumber;
    private ProgressDialogFragment progressFragment;
    SimpleUser currentUser = new SimpleUser();
    String cUser;
    Customer customer, editCustomer;
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        // setup Toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.app_icon);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        progressFragment = ProgressDialogFragment.newInstance(R.string.progressMessage);
        etCCNumber = (EditText) findViewById(R.id.etCCNumber);
        etExpiry = (EditText) findViewById(R.id.etExpiry);
        bSubmit = (Button) findViewById(R.id.bSubmit);
        com.stripe.Stripe.apiKey = Constants.STRIPE_SECRET_KEY;
        if (SimpleUser.currentUserObjectId == null){
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            cUser = pref.getString("userId","n");
        }else {
            cUser = SimpleUser.currentUserObjectId;
        }
        getCurrentUser(cUser);
        final String cust_id = currentUser.getTokenId();
        if (cust_id != null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        customer = Customer.retrieve(cust_id);
                    } catch (AuthenticationException |
                            InvalidRequestException |
                            APIConnectionException |
                            CardException | APIException e) {
                        Log.e("PaymentActivity", "Error: " + e.getMessage());
                    }
                    return null;
                }

                protected void onPostExecute(Void result) {
                    if (customer != null) {
                        etCCNumber.setText(cardHidden + currentUser.get(Constants.last4Key));
                        etExpiry.setText(currentUser.getString(Constants.expDateKey));
                        edit = 1;
                        editCustomer = customer;
                    }
                }
            }.execute();
        }
        setupViewListeners();
    }

    private void setupViewListeners() {
        etCCNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (before <= s.length()) {
                    if ((s.length() == 4) ||
                            (s.length() == 9) ||
                            (s.length() == 14)) {
                        etCCNumber.setText(etCCNumber.getText().toString() + " ");
                        etCCNumber.setSelection(etCCNumber.getText().toString().length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etExpiry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                SimpleDateFormat formatter = new SimpleDateFormat("MM/yy", Locale.US);
                Calendar expiryDateDate = Calendar.getInstance();
                    try {
                        expiryDateDate.setTime(formatter.parse(input));
                    } catch (java.text.ParseException e) {

                    if (s.length() == 2) {
                        int month = Integer.parseInt(input);
                        if (month > 0 && month <= 12) {
                            etExpiry.setText(etExpiry.getText().toString() + " / ");
                            etExpiry.setSelection(etExpiry.getText().toString().length());
                        }
                    }  else if (s.length() == 1) {
                        int month = Integer.parseInt(input);
                        if (month > 1) {
                            etExpiry.setText("0" + etExpiry.getText().toString() + "/");
                            etExpiry.setSelection(etExpiry.getText().toString().length());
                        }
                    } else if (s.length() == 7) {
                        if (etCCNumber.getText().toString().length() == 19) {
                            InputMethodManager imm = (InputMethodManager)getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(etCCNumber.getWindowToken(), 0);
                        }
                    }
                }
            }
        });

        bSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cardNumber == null) {
                    cardNumber = "";
                    String[] cardNumberParts = etCCNumber.getText().toString().split(" ");
                    for (int i = 0; i < cardNumberParts.length; i++) {
                        cardNumber = cardNumber + cardNumberParts[i];
                    }
                }

                int expMonth = Integer.parseInt(etExpiry.getText().toString().split("/")[0].trim());
                int expYear = Integer.parseInt(etExpiry.getText().toString().split("/")[1].trim());

                final Card card = new Card(
                        cardNumber,
                        expMonth,
                        expYear, null);

                boolean validation = card.validateCard();
                if (validation) {
                    startProgress();
                    new Stripe().createToken(
                            card,
                            Constants.STRIPE_PUBLISHABLE_KEY,
                            new TokenCallback() {
                                public void onSuccess(Token token) {
                                    com.stripe.Stripe.apiKey = Constants.STRIPE_SECRET_KEY;
                                    //Customer Parameters HashMap
                                    final Map<String, Object> customerParams = new HashMap();
                                    customerParams.put("description", "Customer for " +
                                            currentUser.getName());
                                    customerParams.put("card", token.getId());

                                    new AsyncTask<Void, Void, Void>() {
                                        Customer cust;

                                        @Override
                                        protected Void doInBackground(Void... params) {
                                            try {
                                                if (edit == 0) {
                                                    cust = Customer.create(customerParams);
                                                } else {
                                                    cust = editCustomer;
                                                    editCustomer.update(customerParams);
                                                }
                                            } catch (com.stripe.exception.AuthenticationException |
                                                    InvalidRequestException |
                                                    APIConnectionException |
                                                    CardException | APIException e) {
                                                Log.e("PaymentActivity", "Error: " + e.getMessage());
                                            }
                                            return null;
                                        }

                                        protected void onPostExecute(Void result) {
                                            Toast.makeText(PaymentActivity.this, "Payment Successful", Toast.LENGTH_SHORT);
                                            if (cust != null) {
                                                ParseQuery<ParseObject> query = ParseQuery.getQuery("SimpleUser");
                                                query.whereEqualTo("objectId", cUser);
                                                query.getFirstInBackground(new GetCallback<ParseObject>() {
                                                    public void done(ParseObject trainerSlots, com.parse.ParseException e) {
                                                        if (e == null) {
                                                            trainerSlots.put(Constants.tokenIdKey, cust.getId());
                                                            currentUser.setTokenId(cust.getId());
                                                            trainerSlots.put(Constants.cardTypeKey, card.getType());
                                                            trainerSlots.put(Constants.last4Key, card.getLast4());
                                                            trainerSlots.put(Constants.expDateKey, card.getExpMonth() + " / " + card.getExpYear());
                                                            trainerSlots.saveInBackground(new SaveCallback() {
                                                                @Override
                                                                public void done(com.parse.ParseException
                                                                                         e) {
                                                                    if (e != null) {
                                                                        Toast.makeText(PaymentActivity.this,
                                                                                getResources().getString(
                                                                                        R.string.not_saved),
                                                                                Toast.LENGTH_SHORT).show();
                                                                        Log.e("Payment Activity", "Card not saved! " +
                                                                                e.getMessage());
                                                                    } else {
                                                                        final Intent intent;
                                                                        intent = new Intent(PaymentActivity.this, UpcomingEventsActivity.class);
                                                                        intent.putExtra("trainerId", Trainer.currentTrainerObjectId);
                                                                        PaymentActivity.this.startActivity(intent);

                                                                    }
                                                                }
                                                            });
                                                        } else {
                                                            Log.d("DEBUG", "Error: " + e.getMessage());
                                                        }
                                                    }
                                                });
                                            }
                                                ParseObject trainer = ParseObject.createWithoutData("Trainer", Trainer.currentTrainerObjectId);
                                                ParseObject user = ParseObject.createWithoutData("SimpleUser", cUser);
                                                ParseQuery<ParseObject> paidSlots = ParseQuery.getQuery("BlockedSlots");
                                                paidSlots.selectKeys(Arrays.asList("objectId"));
                                                paidSlots.include("trainer_id");
                                                paidSlots.whereEqualTo("trainer_id", trainer);
                                                paidSlots.whereEqualTo("user_id", user);
                                                paidSlots.whereEqualTo("status", Constants.ADD_TO_CART);
                                                paidSlots.findInBackground(new FindCallback<ParseObject>() {
                                                    public void done(List<ParseObject> trainerSlots, ParseException e) {
                                                        if (e == null) {
                                                            for (ParseObject slots : trainerSlots) {
                                                                slots.put("status",Constants.BOOKED);
                                                                slots.saveInBackground();
                                                            }
                                                        }
                                                    }
                                                });
                                            }
                                    }.execute();
                                    finishProgress();
                                }
                                public void onError(Exception error) {
                                    handleError(error.getLocalizedMessage());
                                    finishProgress();
                                }
                            });

                } else if (!card.validateNumber()) {
                    handleError(Constants.INVALID_CREDITCARD_NUMBER);
                } else if (!card.validateExpiryDate()) {
                    handleError(Constants.INVALID_EXPIRYDATE);
                } else if (!card.validateCVC()) {
                    handleError(Constants.INVALID_CVC);
                } else {
                    handleError(Constants.INVALID_CREDITCARD_DETAILS);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_payment, menu);
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

    private void startProgress() {
        progressFragment.show(getSupportFragmentManager(), "progress");
    }

    private void finishProgress() {
        progressFragment.dismiss();
    }
    private void handleError(String error) {
              DialogFragment fragment = ErrorDialogFragment.newInstance(R.string.validationErrors, error);
              fragment.show(getSupportFragmentManager(), "error");
         }
    public void getCurrentUser(String userId){

    }

    public void onScanClick(View v) {
        Intent scanIntent = new Intent(this, CardIOActivity.class);

        // customize these values to suit your needs.
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false

        // hides the manual entry button
        // if set, developers should provide their own manual entry mechanism in the app
        scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, false); // default: false

        // matches the theme of your application
        scanIntent.putExtra(CardIOActivity.EXTRA_KEEP_APPLICATION_THEME, false); // default: false

        // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
        startActivityForResult(scanIntent, Constants.SCAN_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String resultStr;
        if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
            CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

            // Never log a raw card number. Avoid displaying it, but if necessary use getFormattedCardNumber()
            resultStr = scanResult.getRedactedCardNumber();
            cardNumber = scanResult.getFormattedCardNumber();

            etCCNumber.setText(resultStr);

            // Do something with the raw number, e.g.:
            // myService.setCardNumber( scanResult.cardNumber );

            if (scanResult.isExpiryValid()) {
                resultStr = scanResult.expiryMonth + "/" + scanResult.expiryYear;
                etExpiry.setText(resultStr);
            }
        } else {
            etCCNumber.setText(Constants.CC);
            etExpiry.setText(Constants.EXPIRY_DATE);
        }
    }
}
