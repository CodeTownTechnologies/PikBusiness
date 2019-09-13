package com.pikbusiness;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.elmargomez.typer.Font;
import com.elmargomez.typer.Typer;
import com.mukesh.OtpView;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.pikbusiness.Loginmodule.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EnterPinActivity extends AppCompatActivity {

    @BindView(R.id.login)
    Button login;
    @BindView(R.id.txt)
    TextView txt;
    JSONObject reusl;
    private String pin;
    String chkres;
    SessionManager session;
    private OtpView otpView;
    JSONArray pusharray;
    String objectId;
    String otpPin;
    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_pin_layout);
        ButterKnife.bind(this);

        session = new SessionManager(this);
        otpView = findViewById(R.id.otp_view);

        otpView.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        txt.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        login.setTypeface(Typer.set(this).getFont(Font.ROBOTO_MEDIUM));

        pin = getIntent().getStringExtra("pin");
        objectId = getIntent().getStringExtra("objectId");
        Bundle b = getIntent().getExtras();
        latitude = b.getDouble("latitude");
        longitude = b.getDouble("longitude");

//      new Getorderslist().execute();
        pusharray = new JSONArray();
        pusharray.put("business_" + ParseUser.getCurrentUser().getObjectId());
        pusharray.put("shop_" + objectId);
        login.setOnClickListener(v -> {
            otpPin = otpView.getText().toString();
            if (otpView.getText().toString().isEmpty()) {
                Toast.makeText(EnterPinActivity.this, "Please enter shop password", Toast.LENGTH_SHORT).show();
            } else if (pin.equals(otpPin)) {
                String locationName = getIntent().getStringExtra("locationName");
                String businessName = getIntent().getStringExtra("businessName");
                //String objectId = getIntent().getStringExtra("objectId");
                String shopStatus = getIntent().getStringExtra("shopStatus");
                if (ParseUser.getCurrentUser() != null) {
                    ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                    JSONArray channelsArray = installation.getJSONArray("channels");
                    if (installation.getJSONArray("channels") != null) {
                        if (installation.getJSONArray("channels").length() > 0) {

                            channelsArray.put("shop_" + objectId);
                            installation.put("channels", channelsArray);
                            installation.saveInBackground();
//                            Log.d("chk", "onCreate:11 "+ary);

                        } else {
                            installation.put("channels", pusharray);
                            installation.saveInBackground();
//                            Log.d("chk", "onCreate:22 "+ary);
                        }
                    } else {
                        installation.put("channels", pusharray);
                        installation.saveInBackground();
                    }
                } else {
                    session.logoutUser();
                }
                Intent i = new Intent(EnterPinActivity.this, OrderListActivity.class);
                i.putExtra("locationName", locationName);
                i.putExtra("businessName", businessName);
                i.putExtra("objectId", objectId);
                Bundle bundle = new Bundle();
                bundle.putDouble("latitude", latitude);
                bundle.putDouble("longitude", longitude);
                i.putExtras(bundle);
                i.putExtra("shopStatus", shopStatus);
                i.putExtra("pin", pin);
                i.putExtra("phoneNo", getIntent().getStringExtra("phoneNo"));
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
                //session.Pinlogin(otpPin, lname, bname, id1, shopstatus,
                //      getIntent().getStringExtra("lat"),
                //     getIntent().getStringExtra("log"));
//                    Toast.makeText(EnterPinActivity.this, "Success", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(EnterPinActivity.this, "Incorrect pin" + otpPin, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            finish();
            return true;
        }
        return (super.onOptionsItemSelected(item));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
