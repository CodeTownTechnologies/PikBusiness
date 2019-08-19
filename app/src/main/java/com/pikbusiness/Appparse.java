package com.pikbusiness;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.SaveCallback;

import androidx.multidex.MultiDexApplication;

public class Appparse extends MultiDexApplication {

    public static final String CHANNEL_NAME = "AllBusiness";
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.appid))
                // if defined
                .clientKey(getString(R.string.clientkey))
                .server(getString(R.string.serverurl))
                .build()
        );
        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParsePush.subscribeInBackground(CHANNEL_NAME, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
//                    Log.d("chk", "successfully subscribed to the broadcast channel.");
                }
                else {
//                    Log.e("chk", "failed to subscribe for push", e);
                }
            }
        });

    }

}
