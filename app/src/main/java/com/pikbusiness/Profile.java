package com.pikbusiness;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pikbusiness.services.Toasty;
import com.crashlytics.android.Crashlytics;
import com.elmargomez.typer.Font;
import com.elmargomez.typer.Typer;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

public class Profile extends AppCompatActivity {

    @BindView(R.id.business)EditText business;
    @BindView(R.id.phonenumber)EditText phonenumber;
    @BindView(R.id.progressBar)ProgressBar pDialog;
    @BindView(R.id.bustxt) TextView bustxt;
    @BindView(R.id.phtxt) TextView phtxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        Fabric.with(this, new Crashlytics());
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        phtxt.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        business.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        phonenumber.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        bustxt.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));

        if(checkInternetConenction()){
            getprofile();
        }
    }
  public void getprofile() {
      if(ParseUser.getCurrentUser()!=null) {

          pDialog.setVisibility(View.VISIBLE);
          ParseQuery<ParseUser> userQueryu = ParseUser.getQuery();
          userQueryu.setCachePolicy(ParseQuery.CachePolicy.IGNORE_CACHE);
          userQueryu.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
          userQueryu.findInBackground(new FindCallback<ParseUser>() {
              @Override
              public void done(List<ParseUser> userList, ParseException e) {
                  pDialog.setVisibility(View.GONE);
                  if (e == null) {

                      //Retrieve user data
                      if (userList.size() > 0) {
                          for (ParseUser user : userList) {
                              business.setText(user.getString("Business_name"));
                             phonenumber.setText(String.valueOf(user.getInt("phoneNumber")));
                          }
                      } else {
                          Crashlytics.logException(e);
                          // Handle the exception
                      }

                  }else{
                      Crashlytics.logException(e);
                  }
              }
          });
      }
    }
    private boolean checkInternetConenction() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // test for connection
        if (cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            customToast("Please check the internet connection");
//            Log.v("spalsh", "Internet Connection Not Present");
            return false;
        }
    }
    private void customToast(String msg){
        Toast toast = Toasty.error(Profile.this,msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 230);
        toast.show();
    }
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            return true;
        }
        return (super.onOptionsItemSelected(item));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
