package com.strengthcoach.strengthcoach.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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


import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.strengthcoach.strengthcoach.R;
import com.strengthcoach.strengthcoach.dialog.ErrorDialogFragment;
import com.strengthcoach.strengthcoach.dialog.ProgressDialogFragment;
import com.strengthcoach.strengthcoach.helpers.Constants;
import com.strengthcoach.strengthcoach.models.SimpleUser;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PaymentActivity extends ActionBarActivity {

    private String cardNumber = null;
    private String cardHidden = "xxxx xxxx xxxx";
    private int edit = 0;
    Button bSubmit;
    EditText etExpiry, etCCNumber;
    private ProgressDialogFragment progressFragment;
    SimpleUser currentUser = new SimpleUser();
    Customer customer, editCustomer;
    String phoneno, objectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        progressFragment = ProgressDialogFragment.newInstance(R.string.progressMessage);
        phoneno = getIntent().getStringExtra("etPhoneNumber");
        etCCNumber = (EditText) findViewById(R.id.etCCNumber);
        etExpiry = (EditText) findViewById(R.id.etExpiry);
        bSubmit = (Button) findViewById(R.id.bSubmit);
        com.stripe.Stripe.apiKey = Constants.STRIPE_SECRET_KEY;
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
                                    final Map<String, Object> customerParamsFinal = customerParams;

                                    new AsyncTask<Void, Void, Void>() {
                                        Customer cust;

                                        @Override
                                        protected Void doInBackground(Void... params) {
                                            try {
                                                if (edit == 0) {
                                                    cust = Customer.create(customerParamsFinal);
                                                } else {
                                                    cust = editCustomer;
                                                    editCustomer.update(customerParamsFinal);
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
                                            if (cust != null) {
                                                ParseQuery<ParseObject> query = ParseQuery.getQuery("SimpleUser");
                                                query.whereEqualTo("objectId", SimpleUser.currentUserObjectId);
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
                                                                            intent =  new Intent(PaymentActivity.this, TrainerDetailsActivity.class);
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


}
