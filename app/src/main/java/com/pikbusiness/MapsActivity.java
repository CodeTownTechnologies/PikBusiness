package com.pikbusiness;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng latLng;
    private static final int REQUEST_CODE_CHECK_SETTINGS = 669;
    private static final long LOCATION_UPDATE_INTERVAL = 5000;
    private static final long LOCATION_UPDATE_FASTEST_INTERVAL = 3000;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private boolean isPermission;
    private Double l1 = 0.0,l2 = 0.0;
    String sts = "",id;
    @BindView(R.id.save)
    Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        Fabric.with(this, new Crashlytics());
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Intent i = getIntent();
        l1 = i.getDoubleExtra("lat", 0.0);
        l2 =  i.getDoubleExtra("long",0.0);
        sts = getIntent().getStringExtra("sts");
        id = getIntent().getStringExtra("id");
        save.setTypeface(Typer.set(this).getFont(Font.ROBOTO_MEDIUM));
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        enableLocationSettings();
        if(checkAndRequestPermissions()){

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sts = getIntent().getStringExtra("sts");

                if(l1 != 0.0 && l2 != 0.0){
                    if(sts.equals("0")){
                        Intent i = new Intent(MapsActivity.this,Createloctaion.class);
                        i.putExtra("lat",l1);
                        i.putExtra("long",l2);
//                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }else if(sts.equals("1")){
                        Intent i = new Intent(MapsActivity.this, EditLocationActivity.class);
                        Bundle b = new Bundle();
                        b.putDouble("latt", l1);
                        b.putDouble("logg", l2);
                        i.putExtras(b);
                        i.putExtra("mp","2");
                        i.putExtra("id",id);
//                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }

                }else{
                    Toast.makeText(MapsActivity.this, "select your shop location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public void onBackPressed() {

        if(l1 != 0.0 && l2 != 0.0){
            if(sts.equals("0")){
                Intent i = new Intent(MapsActivity.this,Createloctaion.class);
                i.putExtra("lat",l1);
                i.putExtra("long",l2);
//                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }else if(sts.equals("1")){
                Intent i = new Intent(MapsActivity.this, EditLocationActivity.class);
                Bundle b = new Bundle();
                b.putDouble("latt", l1);
                b.putDouble("logg", l2);
                i.putExtras(b);
                i.putExtra("mp","2");
                i.putExtra("id",id);
//                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
            super.onBackPressed();
        }else{
            Toast.makeText(MapsActivity.this, "select your shop location", Toast.LENGTH_SHORT).show();
        }
//        overridePendingTransition(R.anim.pull_in_right, R.anim.pull_out_left);
    }
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            if(l1 != 0.0 && l2 != 0.0){
                if(sts.equals("0")){
                    Intent i = new Intent(MapsActivity.this,Createloctaion.class);
                    i.putExtra("lat",l1);
                    i.putExtra("long",l2);
//                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }else{
                    Intent i = new Intent(MapsActivity.this, EditLocationActivity.class);
                    Bundle b = new Bundle();
                    b.putDouble("latt", l1);
                    b.putDouble("logg", l2);
                    i.putExtras(b);
                    i.putExtra("mp","2");
                    i.putExtra("id",id);
//                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
                super.onBackPressed();
            }else{
                Toast.makeText(MapsActivity.this, "select your shop location", Toast.LENGTH_SHORT).show();
            }

            return true;
        }
        return (super.onOptionsItemSelected(item));
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
        if(l1 != 0.0 && l2 != 0.0){
            int height = 80;
            int width = 60;
            BitmapDrawable bitmapdraw=(BitmapDrawable)ContextCompat.getDrawable(this,R.drawable.pointer);
            Bitmap b=bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
            mMap.clear();
            latLng = new LatLng(l1, l2);
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15.0f));
        }
        else{
            int height = 80;
            int width = 60;
                        BitmapDrawable bitmapdraw=(BitmapDrawable)ContextCompat.getDrawable
                                (this,R.drawable.pointer);
                        Bitmap b=bitmapdraw.getBitmap();
                        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                        mMap.clear();
                        latLng = new LatLng(24.466667, 54.366669);
                        mMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15.0f));

//            }
        }
        mMap.setOnMapClickListener(point -> {
            int height = 80;
            int width = 60;
            BitmapDrawable bitmapdraw=(BitmapDrawable)ContextCompat.getDrawable(MapsActivity.this,R.drawable.pointer);
            Bitmap b=bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
            mMap.clear();
            latLng = new LatLng(point.latitude, point.longitude);
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(point.latitude, point.longitude))
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15.0f));
          l1 = point.latitude;
          l2 = point.longitude;
        });

    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this,
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
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);

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
                            resolvable.startResolutionForResult(MapsActivity.this, REQUEST_CODE_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            Crashlytics.logException(sendEx);
                            // Ignore the error.
                        }
                    }
                });
        return isPermission;
    }
    private  boolean checkAndRequestPermissions() {
        int loc = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int loc2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (loc2 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (loc != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (!listPermissionsNeeded.isEmpty())
        {
            ActivityCompat.requestPermissions(this,listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
}
