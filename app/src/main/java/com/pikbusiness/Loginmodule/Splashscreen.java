package com.pikbusiness.Loginmodule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.appcompat.app.AppCompatActivity;
import io.fabric.sdk.android.Fabric;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.pikbusiness.Activity.DashboardActivity;
import com.pikbusiness.Orderslist;
import com.pikbusiness.R;
import com.pikbusiness.services.Toasty;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class Splashscreen extends AppCompatActivity {

    String email = "",pin = "",chk_busiess = "",chk_bank = "";
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        Fabric.with(this, new Crashlytics());
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        session = new SessionManager(this);
//        Log.d("chk", "parsechk:ceate ");
        SharedPreferences pref = getApplicationContext().getSharedPreferences("Reg", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        pin = pref.getString("pin", null);
//        Log.d("chk", "parsechk:ceate "+pin);
        email = pref.getString("EMAIL",null);

        if (Build.VERSION.SDK_INT >= 21)
        {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

         parsechk();
    }

   public void parsechk(){

       if(ParseUser.getCurrentUser()!=null) {
           ParseQuery<ParseObject> query = ParseQuery.getQuery("BankDetails");
           query.whereEqualTo("businessObjectId", ParseUser.getCurrentUser().getObjectId());
           query.findInBackground(new FindCallback<ParseObject>() {
               public void done(List<ParseObject> object, ParseException e) {
                   if (e == null) {

                       if (object.size() > 0) {
                           for (ParseObject user : object) {
                               chk_bank = user.getString("bankName");
//                               Log.d("chk", "done: "+chk_bank);
//                               check();
                           }
                       } else {

                           // Handle the exception
                       }
                   } else {
                       Crashlytics.log(Log.ERROR, "splash", "error caught!");
                       Crashlytics.logException(e);
                   }
               }
           });

           ParseQuery<ParseUser> userQueryu=ParseUser.getQuery();
           userQueryu.whereEqualTo("objectId",ParseUser.getCurrentUser().getObjectId());
           userQueryu.findInBackground(new FindCallback<ParseUser>() {
               @Override
               public void done(List<ParseUser> userList, ParseException e) {
                   if (e == null) {
                       //Retrieve user data
                       if (userList.size() > 0) {
                           for (ParseUser user : userList) {
//                               check();
                               chk_busiess =  user.getString("Business_name");
                           }
                       } else {
                           // Handle the exception
                       }

                   }else{
                       Crashlytics.log(Log.ERROR, "splash bus", "error caught!");
                       Crashlytics.logException(e);
                   }
               }
           });
           check();
       }
       else{
           check();
       }
   }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    public void check() {
        new CountDownTimer(1000, 1000) {
            @Override
            public void onFinish()

            {
                 if(checkInternetConenction()){

                     if(ParseUser.getCurrentUser()!=null) {

                         String aprv = String.valueOf(ParseUser.getCurrentUser().getNumber("accountStatus"));
                         if (aprv != null) {
                             if (aprv.equals("1")) {
                             if (pin != null) {
                                 if (pin.length() > 0) {
                                     Intent intent = new Intent(Splashscreen.this, Orderslist.class);
                                     startActivity(intent);
                                     overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                 } else {
                                     Intent intent = new Intent(Splashscreen.this, DashboardActivity.class);
                                     startActivity(intent);
                                     overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                 }
                             } else {
                                 Intent intent = new Intent(Splashscreen.this, DashboardActivity.class);
                                 startActivity(intent);
                                 overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                             }
                         } else if (aprv.equals("0")) {
                             if (chk_busiess == null || chk_busiess.isEmpty() || chk_busiess.equals("")) {
                                 Intent intent = new Intent(Splashscreen.this, Loginscreen.class);
                                 startActivity(intent);
                                 overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                                 Log.d("chk", "onFinish: 1");
                             } else {
                                 if (chk_bank == null || chk_bank.isEmpty() || chk_bank.equals("")) {
//                                     Log.d("chk", "onFinish: 2");
                                     Intent intent = new Intent(Splashscreen.this, Loginscreen.class);
                                     startActivity(intent);
                                     overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                 } else if (pin != null) {
                                     if (pin.length() > 0) {
//                                         Log.d("chk", "onFinish:else111 "+pin);
                                         Intent intent = new Intent(Splashscreen.this, Orderslist.class);
                                         startActivity(intent);
                                         overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                     } else {
                                         Intent intent = new Intent(Splashscreen.this, DashboardActivity.class);
                                         startActivity(intent);
                                         overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                     }
                                 } else {
                                     Intent intent = new Intent(Splashscreen.this, DashboardActivity.class);
                                     startActivity(intent);
                                     overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                 }
                             }
                         }
                     }else{
                             Intent i = new Intent(Splashscreen.this,Loginscreen.class);
                             startActivity(i);
                             overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                         }
                     }
                     else{
                        if(email != null){
                            if (pin != null){
                                if(pin.length() > 0 ){
//                                    Log.d("chk", "onFinish:else "+pin);
                                    Intent intent = new Intent(Splashscreen.this, Orderslist.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                }else{
                                    Intent intent = new Intent(Splashscreen.this, DashboardActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                }
                            }else{
                                Intent intent = new Intent(Splashscreen.this, DashboardActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            }
                        }else{
                            Intent i = new Intent(Splashscreen.this,Loginscreen.class);
                            startActivity(i);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        }

                     }
                 }else{
                     if(email != null){
                         if (pin != null){
                             if(pin.length() >0 ){
                                 Log.d("chk", "onFinish:else111222 "+pin);
                                 Intent intent = new Intent(Splashscreen.this, Orderslist.class);
                                 startActivity(intent);
                                 overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                             }
                             else {
                                 Intent intent = new Intent(Splashscreen.this, DashboardActivity.class);
                                 startActivity(intent);
                                 overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                             }
                         }else{

                             Intent intent = new Intent(Splashscreen.this, DashboardActivity.class);
                             startActivity(intent);
                             overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                         }
                     }else{

//                         Log.d("chk", "no internet: ");
                         Intent i = new Intent(Splashscreen.this,Loginscreen.class);
                         startActivity(i);
                         overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                     }
                 }
            }

            @Override
            public void onTick(long millisUntilFinished) {

            }
        }.start();

    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
       parsechk();
    }
    private boolean checkInternetConenction() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // test for connection
        if (cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            customToast("Please check the internet connection");
//            Log.d("spalsh", "Internet Connection Not Present");
            return false;
        }
    }

    private void customToast(String msg){
        Toast toast = Toasty.error(Splashscreen.this,msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 230);
        toast.show();
    }
}
