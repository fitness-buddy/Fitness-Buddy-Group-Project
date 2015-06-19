package com.strengthcoach.strengthcoach.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.SendCallback;
import com.strengthcoach.strengthcoach.R;
import com.strengthcoach.strengthcoach.adapters.ChatItemAdapter;
import com.strengthcoach.strengthcoach.models.ChatNotification;
import com.strengthcoach.strengthcoach.models.ChatPerson;
import com.strengthcoach.strengthcoach.models.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChatActivity extends ActionBarActivity {

    ChatPerson m_me;
    ChatPerson m_other;

    ArrayList<Message> messages;
    ChatItemAdapter messagesAdapter;

    EditText etMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        m_me = (ChatPerson)getIntent().getSerializableExtra("me");
        m_other = (ChatPerson)getIntent().getSerializableExtra("other");

        messages = new ArrayList<>();
        messagesAdapter = new ChatItemAdapter(this, messages, m_me, m_other);
        ListView lvMessages = (ListView) findViewById(R.id.lvMessages);
        lvMessages.setAdapter(messagesAdapter);

        String text = getIntent().getStringExtra("text");
        if (text != null && text != "") {
            Message message = new Message();
            message.setFromObjectId(m_other.objectId);
            message.setToObjectId(m_me.objectId);
            message.setText(text);

            messagesAdapter.add(message);
        }
        etMessage = (EditText) findViewById(R.id.etMessage);
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
            push.sendInBackground(new SendCallback() {
                @Override
                public void done(ParseException e) {
                    Toast.makeText(getBaseContext(), "sent", Toast.LENGTH_SHORT);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

        etMessage.setText("");
        // TODO: Remove fake message once trainer view is in place.
        //addFakeMessageFromTrainer();
    }

    /*
    private void addFakeMessageFromTrainer() {
        Message message = new Message();
        message.setToObjectId(currentUserId);
        message.setFromObjectId(m_trainer.getObjectId());
        message.setText("Hello. I would love to work with you");
        message.saveInBackground();
        messagesAdapter.add(message);
    }
    */
}
