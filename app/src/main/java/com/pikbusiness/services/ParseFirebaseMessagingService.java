package com.pikbusiness.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import com.pikbusiness.Activity.DashboardActivity;
import com.pikbusiness.OrderListActivity;
import com.pikbusiness.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.parse.PushRouter;
import com.parse.fcm.ParseFCM;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Map;

import androidx.core.app.NotificationCompat;

public class ParseFirebaseMessagingService extends FirebaseMessagingService {


    public static final String NOTIFICATION_CHANNEL_ID = "1";
    NotificationManager notificationManager;

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        ParseFCM.register(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> params = remoteMessage.getData();

        String pushId = remoteMessage.getData().get("push_id");
        String timestamp = remoteMessage.getData().get("time");
        String dataString = remoteMessage.getData().get("data");
        String channel = remoteMessage.getData().get("channel");
        SharedPreferences pref = getApplicationContext().getSharedPreferences("Reg", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
       String pin = pref.getString("pin", null);

        JSONObject data = null;
        if (dataString != null) {
            try {
                data = new JSONObject(dataString);
                JSONObject aps = data.getJSONObject("aps");
                String sound = aps.getString("sound");
                JSONObject alertt = aps.getJSONObject("alert");
                String title = alertt.getString("title");
                String body = alertt.getString("body");

                if(pin != null){
                    Intent intent = new Intent(this, OrderListActivity.class);
                    PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(),
                            0,intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    DisplayNotif(title,body,contentIntent,sound);
                }else{
                    Intent intent = new Intent(this, DashboardActivity.class);
                    PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0,intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    DisplayNotif(title,body,contentIntent,sound);
                }
//                Log.e("chk data", data.toString() + "");
            } catch (JSONException e) {
//                Log.d(  "Error", e.getMessage() + "");
//                PLog.d("parse", "Ignoring push because of JSON exception while processing: " + dataString, e);
                return;
            }
        }
//        soundhorn();
        PushRouter.getInstance().handlePush(pushId, timestamp, channel, data);

    }
    public void soundhorn(String nm){
        try
        {
            if(nm.equals("imHere.mp3")){
                Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                        getApplicationContext().getPackageName() + "/raw/im_here");
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), alarmSound);
                r.play();
            }else{
                Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                        getApplicationContext().getPackageName() + "/raw/pushring");
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), alarmSound);
                r.play();
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @SuppressLint("WrongConstant")
    public void DisplayNotif(String title, String message, PendingIntent intent,String sound) {

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String idd = "Pik Business";
        CharSequence name = "business";
        int importance = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            importance = NotificationManager.IMPORTANCE_DEFAULT;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        NotificationChannel notificationChannel = new NotificationChannel(idd, name,importance);
        long[] pattern = {0, 100, 1000, 300, 200, 100, 500, 200, 100};
        Vibrator vibrator = (Vibrator)this.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1));
        notificationChannel.enableLights(true);
        notificationChannel.setShowBadge(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.enableVibration(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        soundhorn(sound);
        notificationManager.createNotificationChannel(notificationChannel);
    }
        int id = (int) System.currentTimeMillis();
        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(title))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setDefaults(Notification.DEFAULT_LIGHTS)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.small52)
                .setBadgeIconType(R.mipmap.ic_launcher)
                .setTicker(title)
                .setLargeIcon(BitmapFactory.decodeResource
                        (ParseFirebaseMessagingService.this.getResources(), R.mipmap.ic_launcher))
                .setContentTitle(title)
                .setColorized(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(intent)
                .setContentText(message)
                .setChannelId(idd)
                .setContentInfo("");

        soundhorn(sound);
        notificationManager.notify(id, notificationBuilder.build());

    }
}
