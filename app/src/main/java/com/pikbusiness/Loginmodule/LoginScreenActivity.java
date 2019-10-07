package com.pikbusiness.Loginmodule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pikbusiness.Activity.DashboardActivity;
import com.pikbusiness.Activity.OrderListActivityNew;
import com.pikbusiness.OrderListActivity;
import com.pikbusiness.R;
import com.pikbusiness.services.Toasty;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.LoginEvent;
import com.elmargomez.typer.Font;
import com.elmargomez.typer.Typer;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import org.json.JSONArray;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

public class LoginScreenActivity extends AppCompatActivity {

    @BindView(R.id.email_edittext)
    EditText etEmail;
    @BindView(R.id.pwd_edittext)
    EditText etPassword;
    @BindView(R.id.login)
    Button login;
    @BindView(R.id.register)
    TextView register;
    @BindView(R.id.visible_icon)
    ImageView imgVisibleEye;
    @BindView(R.id.forgot)
    TextView forgot;
    @BindView(R.id.loginhead)
    TextView login_htext;
    @BindView(R.id.email_textv)
    TextView email_text;
    @BindView(R.id.pasw_text)
    TextView pasw_txt;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private String approve = "",chk_busiess = "",chk_bank = "";
    SessionManager session;
    Boolean isclicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginscreen);
        //Butterknife injection of ids
        ButterKnife.bind(this);
        Fabric.with(this, new Crashlytics());
        SharedPreferences pref = getApplicationContext().getSharedPreferences("Reg", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        String pin = pref.getString("pin", null);
        if(pin != null){
            if(pin.length()> 1){
                Intent intent = new Intent(LoginScreenActivity.this, OrderListActivityNew.class);
                startActivity(intent);
            }
        }
//        if(ParseUser.getCurrentUser()!= null) {
//
//        }
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        login_htext.setTypeface(Typer.set(this).getFont(Font.ROBOTO_MEDIUM));
        email_text.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        etEmail.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        pasw_txt.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        etPassword.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        login.setTypeface(Typer.set(this).getFont(Font.ROBOTO_MEDIUM));
        register.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        forgot.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        session = new SessionManager(this);
        // For full screen no actionbar
        if (Build.VERSION.SDK_INT >= 21)
        {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

//        Log.d("chk", "onCreate: "+pusharray.toString());
        // Login validation ...
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
               // isValidEmail(email);
               // if (isValidEmail(email)){

                    if(etEmail.getText().toString().length() == 0)
                    {
                        customToast("Please enter email address");
                    }
                    else if (etPassword.getText().toString().length() == 0) {

                        customToast("Please enter password");
                    }
                    else if (password.length() < 4) {
                        customToast("Password should be greater than 4 digits");
                    }
                    else if(etEmail.getText().toString().length()>0 && !isValidEmail(email))
                    {
                        customToast("Please enter valid email address");
                    }
                    else if(checkInternetConnection()){
                        login(v,email,password);
                    }
//                }
//                else{
//                    customToast("Invalid etEmail address");
//                }
            }
        });
        // Forgot etPassword click action
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginScreenActivity.this, Resetpassword.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);            }
        });
        // Register screen redirection action
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginScreenActivity.this,Registerscreen.class);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        // Restrict etPassword action
        imgVisibleEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isclicked){
                    imgVisibleEye.setImageResource(R.drawable.eyehide);
                    etPassword.setInputType(129);
                    isclicked = false;
                }else{
                    imgVisibleEye.setImageResource(R.drawable.visible);
                    etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    isclicked = true;

                }
            }
        });
    }
    // Email validation method
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        SharedPreferences pref = getApplicationContext().getSharedPreferences("Reg", 0); // 0 - for private mode
//        SharedPreferences.Editor editor = pref.edit();
//        String pin = pref.getString("pin", null);
//        if(pin != null){
//            Intent intent = new Intent(LoginScreenActivity.this, OrderListActivity.class);
//            startActivity(intent);
////            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//        }
//    }

    public void checkStatus(){
       if (ParseUser.getCurrentUser() != null) {
           ParseQuery<ParseObject> query = ParseQuery.getQuery("BankDetails");
           query.whereEqualTo("businessObjectId", ParseUser.getCurrentUser().getObjectId());
           query.setCachePolicy(ParseQuery.CachePolicy.IGNORE_CACHE);
           query.findInBackground(new FindCallback<ParseObject>() {
               public void done(List<ParseObject> object, ParseException e) {
                   if (e == null) {
                       if (object.size() > 0) {
                           for (ParseObject user : object) {
                               chk_bank = user.getString("bankName");
                               Log.d("chk", "done: "+chk_bank);
                               if(approve.equals("0")){
                                  {
                                       if(chk_bank == null || chk_bank.isEmpty() || chk_bank.equals("")){
                                           saveinstall();
                                           Intent intent = new Intent(LoginScreenActivity.this, Bank_Details.class);
                                           startActivity(intent);
                                           finish();
                                           overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                                       }else{
                                           saveinstall();
                                           Intent intent = new Intent(LoginScreenActivity.this, DashboardActivity.class);
                                           startActivity(intent);
                                           finish();
                                           overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                       }
                                   }
                               }
                               else if(approve.equals("1")){
                                   if(chk_busiess == null || chk_busiess.isEmpty() || chk_busiess.equals("")){
                                       saveinstall();
                                       Intent intent = new Intent(LoginScreenActivity.this, Business_setup.class);
                                       startActivity(intent);
                                       finish();
                                       overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                   }
                                 else if(chk_bank == null || chk_bank.isEmpty() || chk_bank.equals("")){
                                       saveinstall();
                                       Log.d("chk", "done: "+chk_bank);
                                       Intent intent = new Intent(LoginScreenActivity.this, Bank_Details.class);
                                       startActivity(intent);
                                       finish();
                                       overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                                   }else{
                                       saveinstall();
                                       Intent intent = new Intent(LoginScreenActivity.this, DashboardActivity.class);
                                       startActivity(intent);
                                       finish();
                                       overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                   }
                               }
                               else {
                                   Toast.makeText(LoginScreenActivity.this, "Please contact support", Toast.LENGTH_SHORT).show();
                               }
                           }
                       } else {
                           saveinstall();
                           Intent intent = new Intent(LoginScreenActivity.this, Bank_Details.class);
                           startActivity(intent);
                           finish();
                           overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                           // andle the exception
                       }
                   } else {

                       Crashlytics.logException(e);
//                       Log.d("bnk", "Error: " + e.getMessage());
                   }
               }
           });
       }
   }
   public void saveinstall(){
       Answers.getInstance().logLogin(new LoginEvent()
//               .putMethod(ParseUser.getCurrentUser().getObjectId())
//               .putSuccess(true)
               .putCustomAttribute("User objectid", ParseUser.getCurrentUser().getObjectId())
               .putCustomAttribute("Email", etEmail.getText().toString()));
       ParseInstallation installation = ParseInstallation.getCurrentInstallation();
       JSONArray pusharray = new JSONArray();
       pusharray.put("business_"+ParseUser.getCurrentUser().getObjectId());
       pusharray.put("AllBusiness");
       installation.put("channels",pusharray);
       installation.saveInBackground();

   }
    public void login(View view,String email,String pass) {

      progressBar.setVisibility(View.VISIBLE);
        ParseUser.logInInBackground(email, pass, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                progressBar.setVisibility(View.GONE);
                if (parseUser != null) {

                    session.createLoginSession(email,pass);
                    Crashlytics.setUserIdentifier(email);
                    Crashlytics.setUserEmail(email);
                    Crashlytics.setUserName(email);

                    ParseQuery<ParseUser> userQueryu=ParseUser.getQuery();
                    userQueryu.whereEqualTo("objectId",ParseUser.getCurrentUser().getObjectId());
                    userQueryu.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> userList, ParseException e) {
                            if (e == null) {
                                //Retrieve user data
                                if (userList.size() > 0) {
                                    for (ParseUser user : userList) {
                                        chk_busiess =  user.getString("Business_name");
                                        approve = String.valueOf(user.getInt("accountStatus"));
                                        if(chk_busiess == null || chk_busiess.isEmpty() || chk_busiess.equals("")){
                                            saveinstall();
                                            Intent intent = new Intent(LoginScreenActivity.this, Business_setup.class);
                                            startActivity(intent);
                                            finish();
                                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                        }else{
//                                            Log.d("elsebnk", "done:chk bank ");
                                            checkStatus();
                                        }
                                    }
                                } else {
//                                    Log.d("chk", "done: "+e.getMessage());
                                    // Handle the exception
                                }

                            }else{
                                Crashlytics.log(Log.ERROR, "Login", "error caught!");
                                Crashlytics.logException(e);
//                                Log.d("chk", "done: "+e.getMessage());
                            }
                        }
                    });


                } else {
                    Crashlytics.log(Log.ERROR, "Login", "error caught!");
                    Crashlytics.logException(e);
                    customToast(e.getMessage());
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    // Checking Internet connection
    private boolean checkInternetConnection() {
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
        Toast toast = Toasty.error(LoginScreenActivity.this,msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 230);
        toast.show();
    }
}
