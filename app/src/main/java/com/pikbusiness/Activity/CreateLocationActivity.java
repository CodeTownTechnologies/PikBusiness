package com.pikbusiness.Activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hbb20.CountryCodePicker;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.pikbusiness.Loginmodule.Loginscreen;
import com.pikbusiness.Loginmodule.SessionManager;
import com.pikbusiness.R;
import com.pikbusiness.services.Toasty;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

public class CreateLocationActivity extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, OnMapReadyCallback {

    private static final int REQUEST_CODE_CHECK_SETTINGS = 669;
    private static final long LOCATION_UPDATE_INTERVAL = 5000;
    private static final long LOCATION_UPDATE_FASTEST_INTERVAL = 3000;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private GoogleMap mMap;
    String apikey = "";
    private LatLng latLng;
    private boolean isPermission;
    //SessionManager session;
    @BindView(R.id.et_contact_number)
    EditText etContactNumber;
    @BindView(R.id.country_code_picker)
    CountryCodePicker mCountryCodePicker;
    @BindView(R.id.et_location_name)
    EditText etLocationName;
    @BindView(R.id.et_set_pin)
    EditText etSetPin;
    @BindView(R.id.et_re_pin)
    EditText etRePin;
    @BindView(R.id.btn_save_location)
    Button btnSave;
    @BindView(R.id.txt_location)
    TextView txt_location;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private Double latitude = 0.0, longitude = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_location);
        ButterKnife.bind(this);
        Fabric.with(this, new Crashlytics());

    //    session = new SessionManager(this);
        mCountryCodePicker.registerCarrierNumberEditText(etContactNumber);
//        SharedPreferences pref = getApplicationContext().getSharedPreferences("Reg", 0);
//        SharedPreferences.Editor editor = pref.edit();
//        loc_name = pref.getString("locationName", null);
//        phoneno_string = pref.getString("phone", null);
//        setpin_string = pref.getString("tvSetPin", null);
//        repin_string = pref.getString("tvRepin", null);
//        txt_location.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
//        etContactNumber.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
//        etLocationName.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
//        etSetPin.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
//        etRePin.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
//        btnSave.setTypeface(Typer.set(this).getFont(Font.ROBOTO_MEDIUM));
//        ltxt.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
//        setpintxt.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
//        repintxt.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));

//        if(loc_name != null){
//            etLocationName.setText(loc_name);
//        }
//        if(phoneno_string != null){
//            etContactNumber.setText(phoneno_string);
//        }
//        if(setpin_string != null){
//               etSetPin.setText(setpin_string);
//        }
//        if(repin_string != null){
//           etRePin.setText(repin_string);
//        }
//
//        Intent i = getIntent();
//        latitude = i.getDoubleExtra("lat", 0.0);
//        longitude =  i.getDoubleExtra("long",0.0);
        apikey = getApplicationContext().getResources().getString(R.string.apikey);
        if (ParseUser.getCurrentUser() != null) {

        } else {
            ParseUser.logOut();
            Intent l = new Intent(CreateLocationActivity.this, Loginscreen.class);
            startActivity(l);
            finish();
//            overridePendingTransition(R.anim.pull_in_right, R.anim.pull_out_left);
        }

        enableLocationSettings();
        if (checkAndRequestPermissions()) {

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

        }

        //  btnSave.setVisibility(View.VISIBLE);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lname = etLocationName.getText().toString().trim();
                String pno = etContactNumber.getText().toString().trim();
                String pin1 = etSetPin.getText().toString().trim();
                String repin1 = etRePin.getText().toString().trim();

                if (lname.length() == 0) {
                    customToast("Please enter txt_location name");
                } else if (pno.length() == 0) {
                    customToast("Please enter mobile number");
                } else if (!mCountryCodePicker.isValidFullNumber()) {
                    customToast("Enter valid mobile number");
                } else if (pin1.length() == 0) {
                    customToast("Please set pin");
                } else if (repin1.length() == 0) {
                    customToast("Please confirm pin");
                } else if (pin1.length() <= 3) {
                    customToast("Pin must be 4 digits only");
                } else if (!pin1.equals(repin1)) {
                    customToast("You entered pin not matching");
                } else if (latitude != 0.0 && longitude != 0.0) {

                    CreateLocation();
                } else {
                    customToast("Please select the shop location in map");
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
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {

            mMap.setMyLocationEnabled(true);

        }
        if (latitude != 0.0 && longitude != 0.0) {
            int height = 80;
            int width = 60;
            BitmapDrawable bitmapdraw = (BitmapDrawable) ContextCompat.getDrawable(CreateLocationActivity.this, R.drawable.pointer);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
            mMap.clear();
            latLng = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

        mMap.setOnMapClickListener(point -> {

            Intent i = new Intent(CreateLocationActivity.this, MapActivity.class);
            Bundle b = new Bundle();
            b.putDouble("latitude", latitude);
            b.putDouble("longitude", longitude);
            i.putExtras(b);
            startActivityForResult(i, 2);
        });


//        mMap.setOnMapClickListener(point -> {
//
////            session.savesession(etLocationName.getText().toString().trim(), etContactNumber.getText().toString().trim(),
////                    etSetPin.getText().toString().trim(), etRePin.getText().toString().trim());
//            Intent i = new Intent(CreateLocationActivity.this, MapActivity.class);
//            i.putExtra("lat",latitude);
//            i.putExtra("long",longitude);
//            i.putExtra("sts","0");
//            i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//            startActivity(i);
//        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            Bundle b = data.getExtras();
            latitude = b.getDouble("latitude");
            longitude = b.getDouble("longitude");


            if (latitude != 0.0 && longitude != 0.0) {
                int height = 80;
                int width = 60;
                BitmapDrawable bitmapdraw = (BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.pointer);
                Bitmap b2 = bitmapdraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b2, width, height, false);
                mMap.clear();
                latLng = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
            } else {
                int height = 80;
                int width = 60;
                BitmapDrawable bitmapdraw = (BitmapDrawable) ContextCompat.getDrawable
                        (this, R.drawable.pointer);
                Bitmap b1 = bitmapdraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b1, width, height, false);
                mMap.clear();
                latLng = new LatLng(24.466667, 54.366669);
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));

//            }
            }
        }
    }

    @Override
    public void onMyLocationClick(@NonNull Location arg0) {
        int height = 80;
        int width = 60;
        BitmapDrawable bitmapdraw = (BitmapDrawable) ContextCompat.getDrawable
                (CreateLocationActivity.this, R.drawable.pointer);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        mMap.clear();
        latLng = new LatLng(arg0.getLatitude(), arg0.getLongitude());
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(arg0.getLatitude(), arg0.getLongitude()))
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
     //   Toast.makeText(this, "Current txt_location:\n" + txt_location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
//        session.savesession(etLocationName.getText().toString().trim(), etContactNumber.getText().toString().trim(),
//                etSetPin.getText().toString().trim(), etRePin.getText().toString().trim());
//        Intent i = new Intent(CreateLocationActivity.this, MapActivity.class);
//        i.putExtra("lat", latitude);
//        i.putExtra("long", longitude);
//        i.putExtra("sts", "0");
//        i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//        startActivity(i);


        Intent i = new Intent(CreateLocationActivity.this, MapActivity.class);
        Bundle b = new Bundle();
        b.putDouble("latitude", latitude);
        b.putDouble("longitude", longitude);
        i.putExtras(b);
        startActivityForResult(i, 2);

        return false;
    }

    private void CreateLocation() {

        ParseUser user = ParseUser.getCurrentUser();
        String taxId = user.getString("taxId");
        String tax = String.valueOf(user.getNumber("tax"));
     //   session.savesession("", "", "", "");
        progressBar.setVisibility(View.VISIBLE);
        ParseGeoPoint point = new ParseGeoPoint(latitude, longitude);
       // ParseUser user = ParseUser.getCurrentUser();
        ParseObject shop = new ParseObject("ShopLocations");
        shop.put("locationName", etLocationName.getText().toString().trim());
        shop.put("pin", Integer.parseInt(etSetPin.getText().toString().trim()));
        shop.put("location", point);
        shop.put("phoneNo", Long.valueOf(mCountryCodePicker.getFullNumber()));
        shop.put("menu", ParseUser.getCurrentUser());
        shop.put("shopStatus", Integer.parseInt("0"));
        shop.put("business", ParseUser.getCurrentUser());
        shop.put("businessObjectId", user.getObjectId());
        if (taxId != null) {
            shop.put("taxId", taxId);
        }

        try {
            if (!tax.equals("null")) {
                shop.put("tax", Integer.parseInt(tax));
            }
        } catch (NumberFormatException e) {
        }
        shop.saveInBackground();
        shop.saveInBackground(new SaveCallback() {

            @Override
            public void done(ParseException e) {
                // TODO Auto-generated method stub
                progressBar.setVisibility(View.GONE);
                if (e == null) {
                    Intent i = new Intent(CreateLocationActivity.this, DashboardActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(i);
//                    overridePendingTransition(R.anim.pull_in_right, R.anim.pull_out_left);
                    Toast.makeText(CreateLocationActivity.this,
                            "Success your shop location is created", Toast.LENGTH_SHORT).show();
                    // success
                } else {
                    Crashlytics.logException(e);
                    customToast(e.getMessage());
                }
            }
        });
    }

    private void customToast(String msg) {
        Toast toast = Toasty.error(CreateLocationActivity.this, msg, Toast.LENGTH_SHORT);
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
                        .setMessage("This app needs the Location permission, please accept to use txt_location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(CreateLocationActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
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


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // txt_location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mMap.setMyLocationEnabled(true);

                    }

                } else {

                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private boolean checkAndRequestPermissions() {
        int loc = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int loc2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (loc2 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (loc != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    protected boolean enableLocationSettings() {
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
                    isPermission = true;
                    if (ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling

                        return;
                    }

                    mMap.setMyLocationEnabled(true);
                })
                .addOnFailureListener(this, ex -> {
                    isPermission = false;
                    if (ex instanceof ResolvableApiException) {
                        // Location settings are NOT satisfied,  but this can be fixed  by showing the user a dialog.
                        try {

                            // Show the dialog by calling startResolutionForResult(),  and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) ex;
                            resolvable.startResolutionForResult(CreateLocationActivity.this, REQUEST_CODE_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            Crashlytics.logException(sendEx);
                            // Ignore the error.
                        }
                    }
                });
        return isPermission;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
         //   session.savesession("", "", "", "");
            Intent i = new Intent(CreateLocationActivity.this, DashboardActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            return true;
        }
        return (super.onOptionsItemSelected(item));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       // session.savesession("", "", "", "");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
       // session.savesession("", "", "", "");
        Intent i = new Intent(CreateLocationActivity.this, DashboardActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}

