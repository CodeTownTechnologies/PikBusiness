package com.pikbusiness.Loginmodule;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.pikbusiness.Dashboard;
import com.pikbusiness.R;
import com.pikbusiness.services.Toasty;
import com.crashlytics.android.Crashlytics;
import com.elmargomez.typer.Font;
import com.elmargomez.typer.Typer;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

public class Bank_Details extends AppCompatActivity {

    @BindView(R.id.acct_no)EditText account_number;
    @BindView(R.id.bank_name)EditText bank_name;
    @BindView(R.id.full_name)EditText full_name;
    @BindView(R.id.ifsc_code)EditText ifsc_code;
    @BindView(R.id.done)Button next_button;
    @BindView(R.id.progressBar)ProgressBar progressBar;
    @BindView(R.id.bank_htxt)TextView bank_htxt;
    @BindView(R.id.actno_txt)TextView actno_txt;
    @BindView(R.id.fname_txt)TextView fname_txt;
    @BindView(R.id.ifsc_txt)TextView ifsc_txt;
    @BindView(R.id.bname_txt)TextView bname_txt;
    AlertDialog alertDialog;
    String approv_status = "0";
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank__details);
        ButterKnife.bind(this);
        Fabric.with(this, new Crashlytics());
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        session = new SessionManager(this);
        bank_htxt.setTypeface(Typer.set(this).getFont(Font.ROBOTO_MEDIUM));
        account_number.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        bank_name.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        full_name.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        ifsc_code.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));

        actno_txt.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        fname_txt.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        ifsc_txt.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        bname_txt.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        next_button.setTypeface(Typer.set(this).getFont(Font.ROBOTO_MEDIUM));
         ifsc_code.setAllCaps(true);
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
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("chk");
            if(value.equals("1")){
                success();
            }
            //The key argument here must match that used in the other activity
        }
        String alert = getIntent().getStringExtra("chk");
        if (alert != null){
            if(alert.equals("1")){
                success();
            }
        }
        account_number.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 2){
                    account_number.setTextColor(ContextCompat.getColor(Bank_Details.this, R.color.purple));
//                   account_number.setTextColor(ContextCompat.getColor(this,getResources().getColor(R.color.blue)));
                }else{
                    account_number.setTextColor(ContextCompat.getColor(Bank_Details.this, R.color.white));

                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void afterTextChanged(Editable s) {

            }
        });
        next_button.setOnClickListener(v -> {


            String name = full_name.getText().toString().trim();
            String acctno1 = account_number.getText().toString().trim();
            String bnkname = bank_name.getText().toString().trim();
            String ifsc1 = ifsc_code.getText().toString().trim();

            if(full_name.getText().toString().trim().length() == 0){
                customToast("Please enter full name");
            }
            else if(acctno1.length() == 0){
                customToast("Please enter account number");
            }
            else if(bnkname.length() == 0){
                customToast("Please enter bank name");
            }
            else if(ifsc1.length() == 0){
                customToast("Please enter IBAN number");
            }
            else if (checkInternetConenction()) {

                    UploadBankdetails();
                }
        });
    }

    private  void UploadBankdetails(){

      progressBar.setVisibility(View.VISIBLE);

        ParseUser user = ParseUser.getCurrentUser();

        ParseObject bank = new ParseObject("BankDetails");
        bank.put("fullName", full_name.getText().toString());
        bank.put("accountNumber",account_number.getText().toString() );
        bank.put("bankName",bank_name.getText().toString());
        bank.put("business",ParseUser.getCurrentUser());
        bank.put("ibanNumber",ifsc_code.getText().toString());
        bank.put("businessObjectId",ParseUser.getCurrentUser().getObjectId());
//        bank.saveInBackground();
        bank.saveInBackground(new SaveCallback() {

            @Override
            public void done(ParseException e) {
                // TODO Auto-generated method stub
               progressBar.setVisibility(View.GONE);
                if (e == null) {
                    approve();
//                    success();
                    // success
                } else {
                    Crashlytics.log(Log.ERROR, "BAnkd detail entering", "error caught!");
                    Crashlytics.logException(e);
                   customToast(e.getMessage());
                }
            }

        });

    }
    public void approve(){
        if(ParseUser.getCurrentUser()!=null) {
            ParseQuery<ParseUser> userQueryu=ParseUser.getQuery();
            userQueryu.setCachePolicy(ParseQuery.CachePolicy.IGNORE_CACHE);
            userQueryu.whereEqualTo("objectId",ParseUser.getCurrentUser().getObjectId());
            userQueryu.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> userList, ParseException e) {
                    if (e == null) {
                        //Retrieve user data
                        if (userList.size() > 0) {
                            for (ParseUser user : userList) {

                                approv_status = String.valueOf(user.getInt("accountStatus"));
                                if(approv_status.equals("0")){
                                   success();
                                    HashMap<String, String> params = new HashMap<>();
                                    params.put("busName", user.getString("Business_name"));
                                    params.put("busPhno", String.valueOf(user.getNumber("phoneNumber")));
                                    params.put("busObjId",ParseUser.getCurrentUser().getObjectId());
                                    params.put("busEmail", user.getEmail());
                                    ParseFile shoplicence = ParseUser.getCurrentUser().
                                            getParseFile("ShopLicense");
                                    ParseFile ownerlicence = ParseUser.getCurrentUser().
                                            getParseFile("ownerLicense");
                                    if(shoplicence != null){
                                        params.put("busShopLicense", shoplicence.getUrl());
                                    }
                                     if(ownerlicence != null){
                                         params.put("busOwnLicense", ownerlicence.getUrl());
                                     }

                                    ParseCloud.callFunctionInBackground("emailAtReg",
                                            params,(FunctionCallback<Map<String,
                                                    List<ParseObject>>>) (object, re) -> {
                                        if (re == null) {
                                            Log.d("chk", "done:ank emailat reg success ");
                                        }
                                        else{
                                            Log.d("chk", "done:error emailatreg"+re.getMessage());
                                        }
                                    });
                                }else if(approv_status.equals("1")){
                                    Intent intent = new Intent(Bank_Details.this, Dashboard.class);
                                    startActivity(intent);
                                    finish();
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                }else{
                                    Toast.makeText(Bank_Details.this, "Please contact support", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Crashlytics.log(Log.ERROR, "BAnkd detail entering", "error caught!");
                            Crashlytics.logException(e);
                            // Handle the exception
                        }

                    }else{
                        Crashlytics.log(Log.ERROR, "BAnkd detail entering", "error caught!");
                        Crashlytics.logException(e);
                    }
                }
            });
        }
    }
    private boolean validateIban(String iban) {

        int IBAN_MIN_SIZE = 15;
        int IBAN_MAX_SIZE = 34;
        long IBAN_MAX = 999999999;
        long IBAN_MODULUS = 97;

        String trimmed = iban.trim();

        if (trimmed.length() < IBAN_MIN_SIZE || trimmed.length() > IBAN_MAX_SIZE) {
            return false;
        }

        String reformat = trimmed.substring(4) + trimmed.substring(0, 4);
        long total = 0;

        for (int i = 0; i < reformat.length(); i++) {

            int charValue = Character.getNumericValue(reformat.charAt(i));

            if (charValue < 0 || charValue > 35) {
                return false;
            }

            total = (charValue > 9 ? total * 100 : total * 10) + charValue;

            if (total > IBAN_MAX) {
                total = (total % IBAN_MODULUS);
            }
        }

        return (total % IBAN_MODULUS) == 1;
    }
    public void success() {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.waitverify, null);

        TextView txt = dialogView.findViewById(R.id.msg);
        Button login = dialogView.findViewById(R.id.login);
        txt.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                session.logoutUser();
           Intent i = new Intent(Bank_Details.this,Loginscreen.class);
           i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
           startActivity(i);
           finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
           alertDialog.dismiss();
//           overridePendingTransition(R.anim.pull_in_left, R.anim.pull_out_right);
            }
        });

        dialogBuilder.setView(dialogView);
        alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i= new Intent(Bank_Details.this,Loginscreen.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
        Toast toast = Toasty.error(Bank_Details.this,msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 230);
        toast.show();
    }
}
