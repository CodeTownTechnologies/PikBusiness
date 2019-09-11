package com.pikbusiness.services;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.pikbusiness.Activity.DashboardActivity;

import org.json.JSONException;
import org.json.JSONObject;

// Hack note: Javadoc smashes the last two paragraphs together without the <p> tags.
@SuppressWarnings("unused")
public class ParsePushBroadcastReceiver extends com.parse.ParsePushBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {

            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
//            Log.d("chk barod", json.toString());
            final String notificationTitle = json.getString("title");
            final String notificationContent = json.getString("alert");
            final String uri = json.getString("uri");

            Intent resultIntent = null;
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            resultIntent = new Intent(context, DashboardActivity.class);
            stackBuilder.addParentStack(DashboardActivity.class);

            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        } catch (JSONException e) {
            Log.d("er", e.getMessage());
        }

    }
}
