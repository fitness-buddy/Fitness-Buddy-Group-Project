package com.strengthcoach.strengthcoach.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;
import com.strengthcoach.strengthcoach.R;
import com.strengthcoach.strengthcoach.adapters.ChatItemAdapter;
import com.strengthcoach.strengthcoach.models.ChatNotification;
import com.strengthcoach.strengthcoach.models.ChatPerson;
import com.strengthcoach.strengthcoach.models.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatActivity extends ActionBarActivity {

    ChatPerson m_me;
    ChatPerson m_other;
    ArrayList<Message> m_messages;
    ChatItemAdapter messagesAdapter;
    ListView lvMessages;
    EditText etMessage;
    Toolbar mToolbar;
    TextView tvTrainerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // setup Toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        m_me = (ChatPerson)getIntent().getSerializableExtra("me");
        m_other = (ChatPerson)getIntent().getSerializableExtra("other");

        ImageView ivToolbarImage = (ImageView) mToolbar.findViewById(R.id.ivToolbarImage);
        if (m_other.imageUrl != null && !m_other.imageUrl.equals("")) {
            Picasso.with(this).load(m_other.imageUrl).into(ivToolbarImage);
        }
        else {
            ivToolbarImage.setImageResource(R.drawable.default_profile_image);
        }

        // Set the title of toolbar
        tvTrainerName = (TextView) mToolbar.findViewById(R.id.tvTrainerName);
        tvTrainerName.setText(m_other.name);

        m_messages = new ArrayList<>();
        messagesAdapter = new ChatItemAdapter(this, m_messages, m_me, m_other);
        lvMessages = (ListView) findViewById(R.id.lvMessages);
        lvMessages.setAdapter(messagesAdapter);

        etMessage = (EditText) findViewById(R.id.etMessage);

        addNewMessages();

        final Handler handler = new Handler();
        final int delay = 2000; //milliseconds
        handler.postDelayed(new Runnable() {
            public void run() {
                addNewMessages();
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
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

    public void onSendClicked(View view) {
        Message message = new Message();
        message.setFromObjectId(m_me.objectId);
        message.setToObjectId(m_other.objectId);
        message.setText(etMessage.getText().toString());
        message.saveInBackground();
        messagesAdapter.add(message);
        lvMessages.setSelection(lvMessages.getCount() - 1);

        try {
            ChatNotification notification = new ChatNotification();
            notification.from = m_me;
            notification.to = m_other;
            notification.text = etMessage.getText().toString();
            String jsonString = new Gson().toJson(notification);
            JSONObject jsonObject = new JSONObject(jsonString);

            ParseQuery pushQuery = ParseInstallation.getQuery();
            pushQuery.whereEqualTo("channels", "");

            ParsePush push = new ParsePush();
            push.setQuery(pushQuery);
            push.setData(jsonObject);
            push.sendInBackground();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        etMessage.setText("");
    }

    private void addNewMessages() {
        String[] objectIds = {m_me.objectId, m_other.objectId};
        ParseQuery<Message> query = ParseQuery.getQuery("Message");
        query.whereContainedIn("fromObjectId", Arrays.asList(objectIds));
        query.whereContainedIn("toObjectId", Arrays.asList(objectIds));
        query.orderByAscending("createdAt");
        query.findInBackground(new FindCallback<Message>() {
            public void done(List<Message> messages, ParseException e) {
                if (e == null) {
                    // Set the value of global current user object
                    for (int i = 0; i < messages.size(); i++) {
                        Message message = messages.get(i);
                        if (!m_messages.contains(message)) {
                            messagesAdapter.add(message);
                            lvMessages.setSelection(lvMessages.getCount() - 1);
                        }
                    }
                } else {
                    Log.d("DEBUG", "Error: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
        overridePendingTransition(R.anim.stay_in_place, R.anim.exit_to_bottom);
    }
}