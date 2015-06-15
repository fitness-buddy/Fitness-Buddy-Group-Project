package com.strengthcoach.strengthcoach.helpers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.parse.ParsePushBroadcastReceiver;
import com.strengthcoach.strengthcoach.R;
import com.strengthcoach.strengthcoach.activities.ChatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

/**
 * Created by varungupta on 6/15/15.
 */

public class PushBroadcastReceiver extends ParsePushBroadcastReceiver {

    public static final String PARSE_DATA_KEY = "com.parse.Data";

    @Override
    protected Notification getNotification(Context context, Intent intent) {
        // deactivate standard notification
        return null;
    }

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        JSONObject data = getDataFromIntent(intent);
        try {
            Intent chatIntent = new Intent(context, ChatActivity.class);
            chatIntent.putExtra("trainerId", data.getString("trainer"));
            chatIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(chatIntent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context, intent);
        JSONObject data = getDataFromIntent(intent);
        // Do something with the data. To create a notification do:

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Bundle extras = intent.getExtras();
        Random random = new Random();
        int contentIntentRequestCode = random.nextInt();
        int deleteIntentRequestCode = random.nextInt();
        String packageName = context.getPackageName();
        Intent contentIntent = new Intent("com.parse.push.intent.OPEN");
        contentIntent.putExtras(extras);
        contentIntent.setPackage(packageName);
        Intent deleteIntent = new Intent("com.parse.push.intent.DELETE");
        deleteIntent.putExtras(extras);
        deleteIntent.setPackage(packageName);
        PendingIntent pContentIntent = PendingIntent.getBroadcast(context, contentIntentRequestCode, contentIntent, 0x8000000);
        PendingIntent pDeleteIntent = PendingIntent.getBroadcast(context, deleteIntentRequestCode, deleteIntent, 0x8000000);


        Bitmap notificationLargeIconBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.app_icon);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.app_icon)
                        .setLargeIcon(notificationLargeIconBitmap)
                        .setContentIntent(pContentIntent)
                        .setDeleteIntent(pDeleteIntent)
                        .setContentTitle("title")
                        .setGroup("999")
                        .setGroupSummary(true)
                        .setContentText("text");


        notificationManager.notify("MyTag", 0, builder.build());

    }

    private JSONObject getDataFromIntent(Intent intent) {
        JSONObject data = null;
        try {
            data = new JSONObject(intent.getExtras().getString(PARSE_DATA_KEY));
        } catch (JSONException e) {
            // Json was not readable...
        }
        return data;
    }
}