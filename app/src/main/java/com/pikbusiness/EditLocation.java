package com.pikbusiness;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pikbusiness.Loginmodule.Loginscreen;
import com.pikbusiness.Loginmodule.SessionManager;
import com.pikbusiness.services.Toasty;
import com.crashlytics.android.Crashlytics;
import com.elmargomez.typer.Font;
import com.elmargomez.typer.Typer;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;

public class EditLocation extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_CODE_CHECK_SETTINGS = 669;
    private static final long LOCATION_UPDATE_INTERVAL = 5000;
    private static final long LOCATION_UPDATE_FASTEST_INTERVAL = 3000;
    private GoogleMap mMap;
    String apikey = "",mp ="0",loc_string,phoneno_string,setpin_string,repin_string;
    @BindView(R.id.loc) EditText locname;
    @BindView(R.id.setpin) EditText setpin;
    @BindView(R.id.phno) EditText phno;
    @BindView(R.id.repin) EditText repin;
    @BindView(R.id.ltxt) TextView ltxt;
    @BindView(R.id.phtxt) TextView phtxt;
    @BindView(R.id.changetxt) TextView changetxt;
    @BindView(R.id.reptxt) TextView reptxt;
    @BindView(R.id.progressBar)ProgressBar progressBar;
    @BindView(R.id.save) Button save;
    Double latitude,longitude;
    String lname,idd,pin,shopstatus,phoneno,tax;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_location);
       ButterKnife.bind(this);
        session = new SessionManager(this);
        apikey = getApplicationContext().getResources().getString(R.string.apikey);
        save.setTypeface(Typer.set(this).getFont(Font.ROBOTO_MEDIUM));
        ltxt.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        locname.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        setpin.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        repin.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        changetxt.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        reptxt.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        phtxt.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        lname = getIntent().getStringExtra("lname");
        pin = getIntent().getStringExtra("pin");
        idd = getIntent().getStringExtra("id");
        tax = getIntent().getStringExtra("tax");
        shopstatus = getIntent().getStringExtra("shopStatus");
        phoneno = getIntent().getStringExtra("phoneNo");
        locname.setText(lname);
        phno.setText(phoneno);
        repin.setText(pin);
        setpin.setText(pin);

        Intent t = new Intent();
         mp = getIntent().getStringExtra("mp");
         if(mp != null){
             Bundle b = getIntent().getExtras();
           latitude = b.getDouble("latt");
             longitude = b.getDouble("logg");
           SharedPreferences pref = getApplicationContext().getSharedPreferences("Reg", 0);
        SharedPreferences.Editor editor = pref.edit();
        loc_string = pref.getString("lname", null);
        phoneno_string = pref.getString("phone", null);
        setpin_string = pref.getString("setpin", null);
        repin_string = pref.getString("repin", null);
        if(loc_string != null){
            locname.setText(loc_string);
        }
        if(phoneno_string != null){
            phno.setText(phoneno_string);
        }
        if(setpin_string != null){
            setpin.setText(setpin_string);
        }
        if(repin_string != null){
            repin.setText(repin_string);
        }

         }
        else{
             latitude = t.getDoubleExtra("latelse", 0.0);
            latitude = Double.valueOf(getIntent().getStringExtra("lat"));
            longitude = Double.valueOf(getIntent().getStringExtra("log"));
        }

                if(ParseUser.getCurrentUser()!= null) {

                }else{
                    ParseUser.logOut();
                    Intent i = new Intent(EditLocation.this,Loginscreen.class);
                    startActivity(i);
                    finish();
                }
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);

                enableLocationSettings();
                mapFragment.getMapAsync(this);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String lname = locname.getText().toString().trim();
                        String pno = phno.getText().toString().trim();
                        String pin1 = setpin.getText().toString().trim();
                        String repin_string = repin.getText().toString().trim();
                        if (lname.length() == 0) {
                            customToast("Please enter location name");
                        } else if (pno.length() == 0) {
                            customToast("Please enter mobile number");
                        } else if (!isValidPhoneNumber(pno)) {
                            customToast("Enter valid mobile number");
                        } else if (pin1.length() == 0) {
                            customToast("Please set pin");
                        } else if (repin_string.length() == 0) {
                            customToast("Please confirm pin");
                        }
                        else if(pin1.length() <= 3){
                            customToast("Pin must be 4 digits only");
                        }
                        else if(pin1.length() == 4){

                            if (pin1.equals(repin_string)) {

                                if(latitude != null && longitude != null){

                                    editLocation();
                                }else{
                                    customToast("Please select the shop location in maps");
                                }
                            }else{
                                customToast("You entered pin not matching");
                            }

                        }else{
                            customToast("Pin must be 4 digits only");
                        }
                    }
                });

            }

            @Override
            public void onPause() {
                super.onPause();

                //stop location updates when Activity is no longer active
            }

            private boolean isValidPhoneNumber(CharSequence phoneNumber) {
                if (!TextUtils.isEmpty(phoneNumber)) {
                    return Patterns.PHONE.matcher(phoneNumber).matches();
                }
                return false;
            }
            // Mobile number validation method

            protected void enableLocationSettings() {
                LocationRequest locationRequest = LocationRequest.create()
                        .setInterval(LOCATION_UPDATE_INTERVAL)
                        .setFastestInterval(LOCATION_UPDATE_FASTEST_INTERVAL)
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                        .addLocationRequest(locationRequest);
                LocationServices
                        .getSettingsClient(this)
                        .checkLocationSettings(builder.build())
                        .addOnSuccessListener(this, (LocationSettingsResponse response) -> {
                            // startUpdatingLocation(...);
                            if (ActivityCompat.checkSelfPermission(this,
                                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling

                                return;
                            }

                            mMap.setMyLocationEnabled(true);
                        })
                        .addOnFailureListener(this, ex -> {

                            if (ex instanceof ResolvableApiException) {
                                // Location settings are NOT satisfied,  but this can be fixed  by showing the user a dialog.
                                try {
                                    // Show the dialog by calling startResolutionForResult(),  and check the result in onActivityResult().
                                    ResolvableApiException resolvable = (ResolvableApiException) ex;
                                    resolvable.startResolutionForResult(EditLocation.this, REQUEST_CODE_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sendEx) {
                                    Crashlytics.logException(sendEx);
                                    // Ignore the error.
                                }
                            }
                        });

            }

            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        //Location Permission already granted
                        mMap.setMyLocationEnabled(true);
                        LatLng shop = new LatLng(latitude, longitude);
                        mMap.addMarker(new MarkerOptions().position(shop)
                                .title(lname));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(shop));
                    } else {
                        //Request Location Permission
                        checkLocationPermission();
                    }
                } else {

                    mMap.setMyLocationEnabled(true);
                }
                mMap.setOnMapClickListener(point -> {

                    session.savesession(locname.getText().toString().trim(),phno.getText().toString().trim(),
                            setpin.getText().toString().trim(),repin.getText().toString().trim());
                    Intent i = new Intent(EditLocation.this,MapsActivity.class);
                    i.putExtra("lat",latitude);
                    i.putExtra("long",longitude);
                    i.putExtra("id",idd);
                    i.putExtra("sts","1");
                    startActivity(i);
                });
//            if(latitude != null && longitude != null){
//                getAddress(this,latitude,longitude);

            }
            private  void editLocation(){

                session.savesession("","", "","");

                ParseUser userr = ParseUser.getCurrentUser();
                String taxid =  userr.getString("taxId");
                String tax = String.valueOf(userr.getNumber("tax"));

                ParseQuery<ParseObject> query = ParseQuery.getQuery("ShopLocations");
                query.whereEqualTo("business",ParseUser.getCurrentUser().getObjectId());
                query.setCachePolicy(ParseQuery.CachePolicy.IGNORE_CACHE);
                query.getInBackground(idd, new GetCallback<ParseObject>() {
                    public void done(ParseObject shop, ParseException e) {
                        progressBar.setVisibility(View.GONE);
                        if (e == null) {
                            // Now let's update it with some new data.
                            shop.put("locationName", locname.getText().toString().trim());
                            shop.put("pin", Integer.parseInt(setpin.getText().toString()) );
                            ParseGeoPoint point = new ParseGeoPoint(latitude, longitude);
                            shop.put("locationName", locname.getText().toString().trim());
                            shop.put("pin", Integer.parseInt(setpin.getText().toString().trim()) );
                            shop.put("phoneNo", Long.valueOf(phno.getText().toString().trim()));
                            shop.put("location", point);
                            if(taxid != null){
                                shop.put("taxId",taxid);
                            }
                            try {
                                if(!tax.equals("null")){
                                    shop.put("tax",Integer.parseInt(tax));
                                }
                            }
                            catch (NumberFormatException ignored)
                            { }
                            shop.put("shopStatus", Integer.parseInt("0"));
                            shop.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    progressBar.setVisibility(View.VISIBLE);
                                    if (e == null) {
                                        // Saved successfully.
                                        Intent i = new Intent(EditLocation.this,Dashboard.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        startActivity(i);
//                                        overridePendingTransition(R.anim.pull_in_right, R.anim.pull_out_left);
                                        Toast.makeText(EditLocation.this,
                                                "Success your shop location is updated", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Crashlytics.logException(e);
                                        customToast(e.getMessage());
                                    }
                                }
                            });
                        } else {
                            Crashlytics.logException(e);
                            e.printStackTrace();
                        }
                    }
                });
            }

            private void customToast(String msg) {
                Toast toast = Toasty.error(EditLocation.this, msg, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 230);
                toast.show();
            }

            private void checkLocationPermission() {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {

                        new AlertDialog.Builder(this)
                                .setTitle("Location Permission Needed")
                                .setMessage("This app needs the Location permission, please accept to use location functionality")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //Prompt the user once explanation has been shown
                               ActivityCompat.requestPermissions(EditLocation.this,
                                                new String[]{
                                    Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                                    }
                                })
                                .create()
                                .show();


                    } else {
                        // No explanation needed, we can request the permission.
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSIONS_REQUEST_LOCATION);
                    }
                }
            }

            public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

            @Override
            public void onRequestPermissionsResult(int requestCode,
            String permissions[], int[] grantResults) {
                switch (requestCode) {
                    case MY_PERMISSIONS_REQUEST_LOCATION: {
                        // If request is cancelled, the result arrays are empty.
                        if (grantResults.length > 0
                                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                            // permission was granted, yay! Do the
                            // location-related task you need to do.
                            if (ContextCompat.checkSelfPermission(this,
                                    Manifest.permission.ACCESS_FINE_LOCATION)
                                    == PackageManager.PERMISSION_GRANTED) {
//                                mMap.setMyLocationEnabled(true);
                            }

                        } else {

                            // permission denied, boo! Disable the
                            // functionality that depends on this permission.
                            Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                        }
                        return;
                    }
                }
            }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        session.savesession("","", "","");
    }
            public boolean onOptionsItemSelected(MenuItem item) {

                if (item.getItemId() == android.R.id.home) {
                    session.savesession("","", "","");
                    Intent i = new Intent(EditLocation.this,Dashboard.class);
                    startActivity(i);
                    finish();
//                    overridePendingTransition(R.anim.pull_in_right, R.anim.pull_out_left);
                    return true;
                }
                return (super.onOptionsItemSelected(item));
            }

            @Override
            public void onBackPressed() {
                super.onBackPressed();
                session.savesession("","", "","");
                Intent i = new Intent(EditLocation.this,Dashboard.class);
                startActivity(i);
                finish();
//                overridePendingTransition(R.anim.pull_in_right, R.anim.pull_out_left);
            }
        }

