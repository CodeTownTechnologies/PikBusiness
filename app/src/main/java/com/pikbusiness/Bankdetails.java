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
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

public class Bankdetails extends AppCompatActivity {

    @BindView(R.id.acct_no)EditText acctno;
    @BindView(R.id.bank_name)EditText bankname;
    @BindView(R.id.full_name)EditText fullname;
    @BindView(R.id.ifsc_code)EditText ifsc;
    @BindView(R.id.progressBar)ProgressBar progressBar;
    @BindView(R.id.actno_txt)TextView actno_txt;
    @BindView(R.id.fname_txt)TextView fname_txt;
    @BindView(R.id.ifsc_txt)TextView ifsc_txt;
    @BindView(R.id.bname_txt)TextView bname_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bankdetails);
        ButterKnife.bind(this);
        Fabric.with(this, new Crashlytics());
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        acctno.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        bankname.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        fullname.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        ifsc.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));

        actno_txt.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        fname_txt.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        ifsc_txt.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        bname_txt.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        if(checkInternetConenction()){
            getbank_details();
        }

    }

    public void getbank_details(){
        if(ParseUser.getCurrentUser()!=null) {
            progressBar.setVisibility(View.VISIBLE);
            ParseQuery<ParseObject> query = ParseQuery.getQuery("BankDetails");
            // filtering with current user object id
            query.setCachePolicy(ParseQuery.CachePolicy.IGNORE_CACHE);
            query.whereEqualTo("businessObjectId", ParseUser.getCurrentUser().getObjectId());
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> object, ParseException e) {
                    progressBar.setVisibility(View.GONE);
                    if (e == null) {

                        if (object.size() > 0) {
                            for (ParseObject user : object) {
                                bankname.setText(user.getString("bankName"));
                                acctno.setText(user.getString("accountNumber"));
                                fullname.setText(user.getString("fullName"));
                                ifsc.setText(user.getString("ibanNumber"));
                            }
                        } else {
                            Crashlytics.logException(e);
                            // Handle the exception
                        }
                    } else {
                        Crashlytics.logException(e);
//                                    Log.d(TAG, "Error: " + e. getMessage());
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
        Toast toast = Toasty.error(Bankdetails.this,msg, Toast.LENGTH_SHORT);
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
