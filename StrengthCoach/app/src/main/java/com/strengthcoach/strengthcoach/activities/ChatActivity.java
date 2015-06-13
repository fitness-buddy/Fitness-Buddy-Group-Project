package com.strengthcoach.strengthcoach.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseQuery;
import com.strengthcoach.strengthcoach.R;
import com.strengthcoach.strengthcoach.adapters.ChatItemAdapter;
import com.strengthcoach.strengthcoach.models.Message;
import com.strengthcoach.strengthcoach.models.SimpleUser;
import com.strengthcoach.strengthcoach.models.Trainer;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends ActionBarActivity {

    Trainer m_trainer;
    ArrayList<Message> messages;
    ChatItemAdapter messagesAdapter;

    EditText etMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messages = new ArrayList<>();
        messagesAdapter = new ChatItemAdapter(this, messages);
        ListView lvMessages = (ListView) findViewById(R.id.lvMessages);
        lvMessages.setAdapter(messagesAdapter);

        etMessage = (EditText) findViewById(R.id.etMessage);

        // Get the m_trainer object from parse and setup the view
        String trainerId = getIntent().getStringExtra("trainerId");
        ParseQuery<Trainer> query = ParseQuery.getQuery("Trainer");
        query.whereEqualTo("objectId", trainerId);
        query.findInBackground(new FindCallback<Trainer>() {
            @Override
            public void done(List<Trainer> list, com.parse.ParseException e) {
                Log.d("DEBUG", ((Trainer) list.get(0)).getName());
                m_trainer = list.get(0);
                messagesAdapter.setTrainer(m_trainer);
            }
        });
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
        message.setFromObjectId(SimpleUser.currentUserObjectId);
        message.setToObjectId(m_trainer.getObjectId());
        message.setText(etMessage.getText().toString());
        message.saveInBackground();
        messagesAdapter.add(message);

        addFakeMessageFromTrainer();
    }

    private void addFakeMessageFromTrainer() {
        Message message = new Message();
        message.setToObjectId(SimpleUser.currentUserObjectId);
        message.setFromObjectId(m_trainer.getObjectId());
        message.setText("Hello. I would love to work with you");
        message.saveInBackground();
        messagesAdapter.add(message);
    }
}
