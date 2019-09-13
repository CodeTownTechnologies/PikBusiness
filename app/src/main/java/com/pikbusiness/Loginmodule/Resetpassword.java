package com.pikbusiness.Loginmodule;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.pikbusiness.R;
import com.pikbusiness.services.Toasty;
import com.crashlytics.android.Crashlytics;
import com.elmargomez.typer.Font;
import com.elmargomez.typer.Typer;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

public class Resetpassword extends AppCompatActivity {

    @BindView(R.id.email_edittext)EditText email;
    @BindView(R.id.hidelinear)LinearLayout hide_layout;
    @BindView(R.id.submit)Button submit;
    @BindView(R.id.msg)TextView msg;
    @BindView(R.id.progressBar)ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpassword);
        ButterKnife.bind(this);
        Fabric.with(this, new Crashlytics());
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        hide_layout.setVisibility(View.VISIBLE);
        msg.setVisibility(View.GONE);
        submit.setTypeface(Typer.set(this).getFont(Font.ROBOTO_MEDIUM));
        msg.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        email.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email2 = email.getText().toString().trim();

                isValidEmail(email2);
                if (isValidEmail(email2)){
                   checkInternetConenction();
                    ResetPass();
                }else{
                    customToast("Invalid etEmail address");

                }
            }
        });
    }
    public  void ResetPass(){

        progressBar.setVisibility(View.VISIBLE);
        ParseUser.requestPasswordResetInBackground(email.getText().toString().trim(), new RequestPasswordResetCallback() {
            public void done(ParseException e) {
               progressBar.setVisibility(View.GONE);
                if (e == null) {
                    hide_layout.setVisibility(View.GONE);
                    msg.setVisibility(View.VISIBLE);
                    Toast toast = Toasty.success(Resetpassword.this,"Success reset etPassword link sent to your etEmail address", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 230);
                    toast.show();
                    // An etEmail was successfully sent with reset instructions.
                } else {
//                    Crashlytics.log(Log.ERROR, "resetpass ", "error caught!");
                    Crashlytics.logException(e);
                    hide_layout.setVisibility(View.VISIBLE);
                    msg.setVisibility(View.GONE);
                    // Something went wrong. Look at the ParseException to see what's up.
                }
            }
        });
    }
     public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
       finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            return true;
        }
        return (super.onOptionsItemSelected(item));
    }
    // Checking Internet connection
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
    // custom toast for showing errors
    private void customToast(String msg){
        Toast toast = Toasty.error(Resetpassword.this,msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 230);
        toast.show();
    }
}
