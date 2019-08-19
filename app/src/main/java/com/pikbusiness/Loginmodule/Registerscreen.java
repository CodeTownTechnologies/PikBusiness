package com.pikbusiness.Loginmodule;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.pikbusiness.R;
import com.pikbusiness.services.Toasty;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.SignUpEvent;
import com.elmargomez.typer.Font;
import com.elmargomez.typer.Typer;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

public class Registerscreen extends AppCompatActivity {

    @BindView(R.id.email_edittext)EditText email;
    @BindView(R.id.pwd_edittext)EditText password;
    @BindView(R.id.register)Button register;
    @BindView(R.id.repwd_edittext)EditText repassword;
    @BindView(R.id.visible_icon)ImageView password_eye;
    @BindView(R.id.visible_icon2)ImageView repassword_eye;
    @BindView(R.id.check)CheckBox terms_checkbox;
    @BindView(R.id.terms)TextView terms;
    @BindView(R.id.reg_htxt)TextView reg_htxt;
    @BindView(R.id.email_txtv)TextView email_txtv;
    @BindView(R.id.pws_txt)TextView pwd_txtv;
    @BindView(R.id.repws_txt)TextView repwd_txt;
    @BindView(R.id.progressBar)ProgressBar progressBar;
     SessionManager sessionManager;

    Boolean isonclicked = false,isonclicked2 = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registerscreen);
        Fabric.with(this, new Crashlytics());
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        ButterKnife.bind(this);
        sessionManager = new SessionManager(this);
        reg_htxt.setTypeface(Typer.set(this).getFont(Font.ROBOTO_MEDIUM));
        email.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        password.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        register.setTypeface(Typer.set(this).getFont(Font.ROBOTO_MEDIUM));
        repassword.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));

        email_txtv.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        pwd_txtv.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        repwd_txt.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        terms.setPaintFlags(terms.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        // For full screen no actionbar
        if(Build.VERSION.SDK_INT >= 21)
        {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        // Restrict password action
        password_eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isonclicked){
                    password_eye.setImageResource(R.drawable.eyehide);
                    password.setInputType(129);
                    isonclicked = false;
                }else{
                    password_eye.setImageResource(R.drawable.visible);
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    isonclicked = true;
                }
            }
        });

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Registerscreen.this,Terms.class);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        // Restrict password action
        repassword_eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isonclicked2){
                    repassword_eye.setImageResource(R.drawable.eyehide);
                    repassword.setInputType(129);
                    isonclicked2 = false;
                }else{
                    repassword_eye.setImageResource(R.drawable.visible);
                    repassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    isonclicked2 = true;
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email2 = email.getText().toString().trim();
                String pass1 = password.getText().toString().trim();
                isValidEmail(email2);
                if (isValidEmail(email2)){

                    if (password.getText().toString().length() == 0) {
                        customToast("Please enter password");
                    }
                    else if (password.getText().toString().length() < 4) {
                        customToast("Password should be greater than 4 digits");
                    }
                    else if(repassword.getText().toString().length() == 0){
                        customToast("Please enter re password");
                    }
                    else if (!password.getText().toString().equals(repassword.getText().toString())) {
                        customToast("Confirm password should be match the password");
                    }
                    else if(terms_checkbox.isChecked()){
                          if(checkInternetConenction()){
                            signup(v);
                        }
                    }else{
                        customToast("Please accept the terms & conditions");
                    }
                }else{
                    customToast("Invalid email address");
                }
            }
        });
    }
    public void signup(View view) {

      progressBar.setVisibility(View.VISIBLE);

        ParseUser user = new ParseUser();
        user.setUsername(email.getText().toString().trim());
        user.setEmail(email.getText().toString().trim());
        user.put("accountStatus",0);
        Boolean tr = true;
        user.put("firstTimeLogin",tr);
        user.put("defaultPrice",tr);
        user.setPassword(password.getText().toString());
//            user.put("name", email.getText().toString().trim());
        user.signUpInBackground(e -> {
          progressBar.setVisibility(View.GONE);
            if (e == null) {

                Intent intent = new Intent(Registerscreen.this, Business_setup.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
          // Event trigrer
                Answers.getInstance().logSignUp(new SignUpEvent()
//                        .putMethod(user.getObjectId())
//                        .putSuccess(true)
                        .putCustomAttribute("User objectid", ParseUser.getCurrentUser().getObjectId())
                        .putCustomAttribute("Email", user.getEmail()));

                sessionManager.createLoginSession(email.getText().toString().trim(),password.getText().toString());
            }
            else {
                sessionManager.logoutUser();
                Crashlytics.logException(e);
                customToast(e.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
//        Intent i = new Intent(getApplicationContext(), Loginscreen.class);
//        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);    }

    // Email validation method
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
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
    private void customToast(String msg){
        Toast toast = Toasty.error(Registerscreen.this,msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 230);
        toast.show();
    }
}
