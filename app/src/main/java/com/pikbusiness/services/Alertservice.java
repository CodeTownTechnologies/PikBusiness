package com.pikbusiness.services;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import androidx.legacy.content.WakefulBroadcastReceiver;

public class Alertservice extends IntentService {

    public static final int notify = 3000;  //interval between two services(Here Service run every 3 seconds)
    private Handler mHandler = new Handler();   //run on another Thread to avoid crash
    private Timer mTimer = null;    //timer handling
    MediaPlayer mp;
    public static final String CHANNEL_ID = "ServiceChannel";
//    Ringtone r;
    // Must create a default constructor
    public Alertservice() {
        // Used to name the worker thread, important only for debugging.
        super("service");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        WakefulBroadcastReceiver.completeWakefulIntent(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mp.stop();
        stopSelf();
        mTimer.cancel();
//        r.stop();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    public void onStart(Intent intent,int startid){

//        int delay = 1000; // delay for 1 sec.
//        int period = 3000; // repeat every 3 sec.
//        Timer timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask()
//        {
//            public void run()
//            {
//                mp.start();
////               soundhorn();
////                Log.d("chk", "onStart: ");
//            }
//        }, delay, period);
//        Log.d(tag, "On start");

    }


@Override
    public void onCreate() {
        super.onCreate();

    if (mTimer != null)
        // Cancel if already existed
        mTimer.cancel();
    else
        mTimer = new Timer();
    //recreate new

    mTimer.scheduleAtFixedRate(new TimeDisplay(), 10000, notify);   //Schedule task
        try
        {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                    getApplicationContext().getPackageName() + "/raw/pushring");
//            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), alarmSound);
//            r.play();

            mp = new MediaPlayer();
            mp = MediaPlayer.create(this, alarmSound);
            mp.setLooping(false);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        // if you override onCreate(), make sure to call super().
    }
    //class TimeDisplay for handling task
    class TimeDisplay extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mp.start();
                    Log.d("service is ","running");
                }
            });
        }
    }
}