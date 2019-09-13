package com.pikbusiness.Activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.crashlytics.android.Crashlytics;
import com.elmargomez.typer.Font;
import com.elmargomez.typer.Typer;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.pikbusiness.Adapters.ShopLocationAdapter;
import com.pikbusiness.Bankdetails;
import com.pikbusiness.BuildConfig;
import com.pikbusiness.Editmenu.EditMenuTabsActivity;
import com.pikbusiness.Loginmodule.SessionManager;
import com.pikbusiness.OrderListActivity;
import com.pikbusiness.Profile;
import com.pikbusiness.R;
import com.pikbusiness.model.Response.Business;
import com.pikbusiness.model.Response.BusinessEstimatedData;
import com.pikbusiness.model.Response.EstimatedData;
import com.pikbusiness.model.Response.Location;
import com.pikbusiness.services.Alertservice;
import com.pikbusiness.services.Toasty;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;
import static android.text.Html.fromHtml;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    @BindView(R.id.recycler_view)
    RecyclerView mShopRecyclerView;
    @BindView(R.id.nolocation)
    LinearLayout nolocation;
    @BindView(R.id.createbtn)
    Button create;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    String accountStatus = "", pik, appversion;
    SessionManager session;
    AlertDialog alertDialog;
    Boolean firstTimeLogin;
    AlertDialog alertDialog1;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    private FirebaseAnalytics mFirebaseAnalytics;
    private List<EstimatedData> estimateDataList = new ArrayList<>();
    private ShopLocationAdapter mAdapter;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);
        Fabric.with(this, new Crashlytics());
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        session = new SessionManager(this);
        mContext = DashboardActivity.this;

        mAdapter = new ShopLocationAdapter(mContext, estimateDataList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mShopRecyclerView.setLayoutManager(mLayoutManager);
        mShopRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mShopRecyclerView.setAdapter(mAdapter);

        if (checkInternetConnection()) {
            checkData();
            addListItems();
        }

        mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorPrimary));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (checkInternetConnection()) {
                    addListItems();
                }
            }
        });
        stopService(new Intent(DashboardActivity.this, Alertservice.class));
      //  updateApp(this);
        checkUpdate();
        SharedPreferences pref = getApplicationContext().getSharedPreferences("Reg", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        String pin = pref.getString("pin", null);
        if (pin != null) {
            if (pin.equals("")) {

            } else {
                if (pin.length() > 0) {
                    Intent intent = new Intent(DashboardActivity.this, OrderListActivity.class);
                    startActivity(intent);
                }
            }
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        checkAndRequestPermissions();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    @SuppressWarnings("deprecation")
    public static Spanned getBoldString(String textNotBoldFirst, String textToBold, String textNotBoldLast) {
        String resultant = null;

        resultant = textNotBoldFirst + " " + "<b>" + textToBold + "</b>" + " " + textNotBoldLast;
        return fromHtml(resultant);

    }

    public void checkData() {
        if (ParseUser.getCurrentUser() != null) {
            estimateDataList.clear();
            ParseQuery<ParseUser> userQueryu = ParseUser.getQuery();
            userQueryu.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
            userQueryu.setCachePolicy(ParseQuery.CachePolicy.IGNORE_CACHE);
            userQueryu.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> userList, ParseException e) {
                    if (e == null) {
                        //Retrieve user data
                        if (userList.size() > 0) {
                            for (ParseUser user : userList) {

                                accountStatus = String.valueOf(user.getInt("accountStatus"));
                                firstTimeLogin = user.getBoolean("firstTimeLogin");
                                firstTimeLoginCheck();
                            }
                        } else {
//                            Crashlytics.log(Log.ERROR, "DashboardActivity", "error caught!");
                            Crashlytics.logException(e);
                            // Handle the exception
                        }

                    } else {
//                        Crashlytics.log(Log.ERROR, "DashboardActivity", "error caught!");
                        Crashlytics.logException(e);
                    }
                }
            });
        } else {

//            ParseUser.logOut();
            session.logoutUser();
        }
    }

    public void firstTimeLoginCheck() {
        if (firstTimeLogin != null) {
            if (firstTimeLogin) {
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("business_name", ParseUser.getCurrentUser().getString("Business_name"));
                params.put("business_objectid", ParseUser.getCurrentUser().getObjectId());
                ParseCloud.callFunctionInBackground("pikPercentage", params, new FunctionCallback<Double>() {
                    public void done(Double result, ParseException e) {
                        if (e == null) {
                            ParseUser user = ParseUser.getCurrentUser();
                            user.put("pikPercentage", result);
                            user.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {

                                    if (e == null) {
//                                        Log.d("chk", "done: update");
                                    } else {
                                        Crashlytics.log(Log.ERROR, "Dasboard", "error caught!");
                                        Crashlytics.logException(e);
                                    }
                                }
                            });
                        }
                    }

                });
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage(getBoldString("Setup your ",
                        "menu", "items"));
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setPositiveButton("CONTINUE",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                                ParseUser user = ParseUser.getCurrentUser();
                                Boolean tr = false;
                                user.put("firstTimeLogin", tr);
                                user.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {

                                        if (e == null) {
//                                        Log.d("chk", "done: update");
                                        } else {
                                            Crashlytics.log(Log.ERROR, "Dasboard", "error caught!");
                                            Crashlytics.logException(e);
                                        }
                                    }
                                });
                                Intent i = new Intent(DashboardActivity.this, EditMenuTabsActivity.class);
                                i.putExtra("one", "1");
                                startActivity(i);
//                            alertDialog1.dismiss();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTypeface(Typer.set(this).getFont(Font.ROBOTO_MEDIUM));
                TextView msgTxt = alertDialog.findViewById(android.R.id.message);
                msgTxt.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));

            }
        }

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        try {
            trimCache(this);
            // Toast.makeText(this,"onDestroy " ,Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // The directory is now empty so delete it
        return dir.delete();
    }

    public void addListItems() {
        if (ParseUser.getCurrentUser() != null) {
            estimateDataList.clear();
            ParseQuery<ParseObject> query = ParseQuery.getQuery("ShopLocations");
            query.whereEqualTo("menu", ParseUser.getCurrentUser());
            query.whereEqualTo("business", ParseUser.getCurrentUser());
            query.include("business");
            query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> object, ParseException e) {
                    if (e == null) {
                        if (mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                        if (object.size() > 0) {

                            for (ParseObject user : object) {

                                String sh = String.valueOf(user.getNumber("shopStatus"));
                                if (sh.equals("2")) {

                                } else {
                                    EstimatedData estimatedData = new EstimatedData();
                                    estimatedData.setLocationName(user.getString("locationName"));
                                    estimatedData.setObjectId(user.getObjectId());
                                    estimatedData.setPin(user.getInt("pin"));
                                    estimatedData.setTax(user.getInt("tax"));
                                    estimatedData.setPhoneNo(user.getInt("phoneNo"));
                                    estimatedData.setShopStatus(user.getInt("shopStatus"));
                                    ParseObject pr = user.getParseObject("business");
                                    estimatedData.setApproveStatus(accountStatus);
                                    Business business = new Business();
                                    BusinessEstimatedData businessEstimateData = new BusinessEstimatedData();
                                    businessEstimateData.setBusinessName(pr.getString("Business_name"));
                                    business.setBusinessEstimatedData(businessEstimateData);
                                    estimatedData.setBusiness(business);
                                    ParseGeoPoint loc = user.getParseGeoPoint("location");
                                    if (loc != null) {
                                        Location location = new Location();
                                        location.setLatitude(loc.getLatitude());
                                        location.setLongitude(loc.getLongitude());
                                        estimatedData.setLocation(location);
                                    }

                                    Bundle bundle = new Bundle();
                                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, user.getObjectId());
                                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, user.getString("locationName"));
                                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, pr.getString("Business_name"));
                                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                                    estimateDataList.add(estimatedData);
                                }
                            }
                            mAdapter.notifyDataSetChanged();


                        } else {
                            Crashlytics.log(Log.ERROR, "DashboardActivity", "error caught!");
                            Crashlytics.logException(e);
                            // Handle the exception
                        }
                    } else {
                        if (e.getMessage().equals("Invalid session token")) {
                            Toast.makeText(DashboardActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            session.logoutUser();
                        }
                        Crashlytics.log(Log.ERROR, "parse", "NPE caught!");
                        Crashlytics.logException(e);
                    }
                }
            });
        } else {
//        ParseUser.logOut();
            session.logoutUser();
        }

    }

    public void checkUpdate() {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Autoupdate");
        query.whereEqualTo("platform", "Android");
        query.whereEqualTo("bundleIdentifier", "com.pikbusiness");
        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            appversion = pInfo.versionName;
//            Log.d("chk", "checkUpdate:ver "+version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> object, ParseException e) {
                if (e == null) {

                    if (object.size() > 0) {
                        for (ParseObject user : object) {

//                            HashMap<String, String> map = new HashMap<String, String>();
                            String newversion = user.getString("newVersion");
                            Number priority = user.getNumber("priority");

                            if (appversion.equals(newversion)) {

                            } else {
                                if (priority.intValue() > 2) {
                                    updatePopUp();
                                }
                            }

//                            Log.d("chk", "done: "+newversion+priority);
                        }

                    } else {
//                        Crashlytics.log(Log.ERROR, "DashboardActivity", "error caught!");
//                        Crashlytics.logException(e);
                        // Handle the exception
                    }
                } else {
                    if (e.getMessage().equals("Invalid session token")) {
                        Toast.makeText(DashboardActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        session.logoutUser();
                    }
//                    Crashlytics.log(Log.ERROR, "parse", "NPE caught!");
//                    Crashlytics.logException(e);
                }
            }
        });
    }

    public void updatePopUp() {

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.popupupdate, null);
        AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
        alertbox.setView(layout);

//        TextView txt = layout.findViewById(R.objectId.msg);
        Button update = layout.findViewById(R.id.update);

        alertbox.setCancelable(false);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                alertDialog.dismiss();
                final String appPackageName = getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?objectId=" + appPackageName)));
                } catch (ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?objectId=" + appPackageName)));
                }
            }
        });

        alertDialog = alertbox.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

    }

    private boolean checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // test for connection
        if (cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            customToast("Please check the internet connection");
//            Log.d("spalsh", "Internet Connection Not Present");
            return false;
        }
    }

    private void customToast(String msg) {
        Toast toast = Toasty.error(DashboardActivity.this, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 230);
        toast.show();
    }

    public static void updateApp(final Activity act) {
        final String appPackageName = BuildConfig.APPLICATION_ID;
        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        builder
                .setTitle("")
                .setMessage("Coffeepik Business latest update")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            act.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?objectId=" + appPackageName)));
                        } catch (ActivityNotFoundException anfe) {
                            act.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?objectId=" + appPackageName)));
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    protected void exitByBackKey() {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage("Are you sure you want to exit")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        finishAffinity();
                        finish();

                    }
                });
        dialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            // do something when the button is clicked
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });

        AlertDialog alert = dialogBuilder.create();
        alert.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();


    }

    private boolean checkAndRequestPermissions() {
        int loc = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int loc2 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int storage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (loc2 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (loc != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            exitByBackKey();
//            overridePendingTransition(R.anim.pull_in_right, R.anim.pull_out_left);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.createloc) {

            Intent i = new Intent(DashboardActivity.this, CreateLocationActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.profile) {
            Intent i = new Intent(DashboardActivity.this, Profile.class);
            startActivity(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else if (id == R.id.bank) {
            Intent i = new Intent(DashboardActivity.this, Bankdetails.class);
            startActivity(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else if (id == R.id.reports) {
            Toastyy();

        } else if (id == R.id.editmenu) {
            Intent i = new Intent(DashboardActivity.this, EditMenuTabsActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else if (id == R.id.transactions) {
            Toastyy();
        } else if (id == R.id.settings) {
            Toastyy();
        } else if (id == R.id.how) {
            Toastyy();

        } else if (id == R.id.support) {
            Toastyy();
        } else if (id == R.id.logout) {
//            ParseUser.logOut();
            session.logoutUser();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return false;
    }

    public void Toastyy() {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.toastpopup, null);

        TextView txt = dialogView.findViewById(R.id.msg);
        Button login = dialogView.findViewById(R.id.ok);
        txt.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        login.setTypeface(Typer.set(this).getFont(Font.ROBOTO_MEDIUM));
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog1.dismiss();
            }
        });

        dialogBuilder.setView(dialogView);
        alertDialog1 = dialogBuilder.create();
        alertDialog1.setCancelable(false);
        alertDialog1.show();
    }
}
