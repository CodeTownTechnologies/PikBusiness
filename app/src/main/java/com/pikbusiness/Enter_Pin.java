package com.pikbusiness;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pikbusiness.Loginmodule.SessionManager;
import com.elmargomez.typer.Font;
import com.elmargomez.typer.Typer;
import com.mukesh.OtpView;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class Enter_Pin extends AppCompatActivity {

    @BindView(R.id.login) Button login;
    @BindView(R.id.txt)TextView txt;
    JSONObject reusl;
    private String pin;
    String chkres;
    SessionManager session;
    private OtpView otpView;
    JSONArray pusharray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter__pin);
        ButterKnife.bind(this);

        session = new SessionManager(this);
        otpView = findViewById(R.id.otp_view);

        otpView.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
       txt.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
       login.setTypeface(Typer.set(this).getFont(Font.ROBOTO_MEDIUM));

        pin = getIntent().getStringExtra("pin");
        String id = getIntent().getStringExtra("objectId");
//      new Getorderslist().execute();
         pusharray = new JSONArray();
        pusharray.put("business_"+ParseUser.getCurrentUser().getObjectId());
        pusharray.put("shop_"+id);
        login.setOnClickListener(v -> {
            String totpin = otpView.getText().toString();
            if(otpView.getText().toString().isEmpty()){
                Toast.makeText(Enter_Pin.this, "Please enter shop password", Toast.LENGTH_SHORT).show();
            }
            else if(pin.equals(totpin)){
                String lname = getIntent().getStringExtra("locationName");
                String bname = getIntent().getStringExtra("bname");
                String id1 = getIntent().getStringExtra("objectId");
                String shopstatus = getIntent().getStringExtra("shopStatus");
                if(ParseUser.getCurrentUser()!=null) {

                    ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                    JSONArray ary = installation.getJSONArray("channels");
                    if( installation.getJSONArray("channels") != null) {
                        if (installation.getJSONArray("channels").length() > 0) {

                            ary.put("shop_"+id);
                            installation.put("channels",ary);
                            installation.saveInBackground();
//                            Log.d("chk", "onCreate:11 "+ary);

                        }else {
                            installation.put("channels",pusharray);
                            installation.saveInBackground();
//                            Log.d("chk", "onCreate:22 "+ary);
                        }
                    }else {
                        installation.put("channels",pusharray);
                        installation.saveInBackground();
                    }
                }else {
                    session.logoutUser();
                }
                Intent i = new Intent(Enter_Pin.this,Orderslist.class);
                i.putExtra("locationName",lname);
                i.putExtra("bname",bname);
                i.putExtra("objectId", id1);
                i.putExtra("lat",getIntent().getStringExtra("lat"));
                i.putExtra("log",getIntent().getStringExtra("log"));
                i.putExtra("shopStatus",shopstatus);
                i.putExtra("pin",getIntent().getStringExtra("pin"));
                i.putExtra("phoneNo",getIntent().getStringExtra("phoneNo"));
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
                session.Pinlogin(totpin,lname,bname, id1,shopstatus,
                        getIntent().getStringExtra("lat"),
                        getIntent().getStringExtra("log"));
//                    Toast.makeText(Enter_Pin.this, "Success", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(Enter_Pin.this, "Incorrect pin "+totpin, Toast.LENGTH_SHORT).show();
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
//    private class Getorderslist extends AsyncTask<Void, Void, List<ParseObject>> {
//
//        List<ParseObject> object11;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//        }
//
//        @Override
//        protected List<ParseObject> doInBackground(Void... params) {
//
//            // Create the array
////            if(ParseUser.getCurrentUser()!=null) {
//            try {
//
//                ParseQuery<ParseObject> query = ParseQuery.getQuery("Orders");
//                ParseObject obj = ParseObject.createWithoutData("ShopLocations", "gKEClDs3cw");
//                query.whereEqualTo("shop", obj);
//                query.setCachePolicy(ParseQuery.CachePolicy.IGNORE_CACHE);
//                query.orderByDescending("updatedAt");
//                object11 = query.find();
//
//            } catch (ParseException e) {
//                Log.d("Error", e.getMessage());
//                e.printStackTrace();
//            }
////            }
////            else {
////                Toast.makeText(Orderslist.this, "Invalid session", Toast.LENGTH_SHORT).show();
////                session.logoutUser();
////                finish();
////            }
//            return object11;
//        }
//
//        @Override
//        protected void onPostExecute(List<ParseObject> result) {
//            Log.d("chk", "onPostExecute: "+result);
//        }
//    }
    private JSONObject parseObjectToJson(ParseObject parseObject) throws com.parse.ParseException, JSONException {
        JSONObject jsonObject = new JSONObject();
        parseObject.fetchIfNeeded();
        Set<String> keys = parseObject.keySet();
        for (String key : keys) {
            Object objectValue = parseObject.get(key);
            if (objectValue instanceof ParseObject) {
                jsonObject.put(key, parseObjectToJson(parseObject.getParseObject(key)));
                // keep in mind about "pointer" to it self, will gain stackoverlow
            } else if (objectValue instanceof ParseRelation) {
                // handle relation
            } else {
                jsonObject.put(key, objectValue.toString());
            }
        }
//        Log.d("chk", "parseObjectToJson: "+jsonObject.toString());
        return jsonObject;
    }
}
