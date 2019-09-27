package com.pikbusiness.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;
import com.elmargomez.typer.Font;
import com.elmargomez.typer.Typer;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.pikbusiness.Adapters.OrderListAdapter;
import com.pikbusiness.Editmenu.EditMenuTabsActivity;
import com.pikbusiness.Loginmodule.SessionManager;
import com.pikbusiness.R;
import com.pikbusiness.model.Response.Business;
import com.pikbusiness.model.Response.BusinessEstimatedData;
import com.pikbusiness.model.Response.EstimatedData;
import com.pikbusiness.model.Response.Location;
import com.pikbusiness.model.Response.OrderItem;
import com.pikbusiness.model.Response.Orders;
import com.pikbusiness.model.Response.State;
import com.pikbusiness.services.Alertservice;
import com.pikbusiness.services.Toasty;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

public class OrderListActivityNew extends AppCompatActivity {


    @BindView(R.id.shopname)
    TextView tvShopName;
    @BindView(R.id.shoploc)
    TextView tvShopLocation;
    @BindView(R.id.txt_switch)
    TextView tvShopStatus;
    @BindView(R.id.toggle_switch)
    Switch toggleSwitch;
    @BindView(R.id.more)
    LinearLayout moreLayout;

    private String loginPassword;
    private DialogPlus dialog;
    private SessionManager session;
    private List<Orders> newOrderList;
    private List<Orders> progressList;
    private List<Orders> readyList;
    private Handler mHandler;

    private AlertDialog alertDialog;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.ll_progressBar)
    LinearLayout ll_progressBar;

    private String latitude, longitude;
    private String locationName;
    private String pin;
    private String objectId;
    private String shopStatus;
    private Number phoneNo;
    private String businessName;
    private Context mContext;
    @BindView(R.id.ordExpandableList)
    ExpandableListView orderExpandableList;
    private OrderListAdapter orderAdapter;
    List<String> listDataHeader;
    HashMap<String, List<Orders>> listDataChild;
    private List<OrderItem> orderItemList;
    TimerTask timerTask;
    Timer updateTimer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_layout);
        ButterKnife.bind(this);
        Fabric.with(this, new Crashlytics());
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.setCurrentScreen(OrderListActivityNew.this, "OrderListActivity", "ss");
        session = new SessionManager(this);
        mContext = OrderListActivityNew.this;

        orderItemList = new ArrayList<>();

        this.mHandler = new Handler();
        SharedPreferences pref = getApplicationContext().getSharedPreferences("Reg", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        loginPassword = pref.getString("Pass", null);
        locationName = pref.getString("locationName", null);
        String pinn = pref.getString("pin", null);
        if (pinn != null) {
            if (pinn.equals("")) {
                editor.clear();
                editor.apply();
                session.logoutUser();
            }
        } else {
            editor.clear();
            editor.apply();
            session.logoutUser();
        }
        if (locationName != null) {
            locationName = pref.getString("locationName", null);
            businessName = pref.getString("businessName", null);
            objectId = pref.getString("objectId", null);
            latitude = pref.getString("lat", null);
            longitude = pref.getString("log", null);
            pin = pref.getString("pin", null);
            shopStatus = pref.getString("shopstatus", null);
//            phoneNo = getIntent().getStringExtra("phoneNo");
        }

        start();

        tvShopName.setText(businessName);
        tvShopLocation.setText(locationName);
        if (shopStatus != null) {
            if (shopStatus.equals("0")) {
                toggleSwitch.setChecked(false);
                tvShopStatus.setText("Offline");
            } else if (shopStatus.equals("1")) {
                toggleSwitch.setChecked(true);
                tvShopStatus.setText("Online");
            } else if (shopStatus.equals("2")) {
                toggleSwitch.setChecked(false);
                tvShopStatus.setText("Disabled");
            }
        }

//        m_Runnable.run();

        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<Orders>>();


        // Adding child data
        listDataHeader.add("NEW ORDER");
        listDataHeader.add("IN PROGRESS");
        listDataHeader.add("READY FOR PICK UP");

        if (checkInternetConnection()) {
            ll_progressBar.setVisibility(View.VISIBLE);
            orderExpandableList.setVisibility(View.GONE);
            initiateData();
            new getOrderList().execute();
        }


        orderAdapter = new OrderListAdapter(this, listDataHeader, listDataChild, orderExpandableList);
        orderExpandableList.setAdapter(orderAdapter);

        toggleSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d("chk", "done:shopsts "+shopsts);
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Orders");
                ParseObject obj = ParseObject.createWithoutData("ShopLocations", objectId);
                query.whereEqualTo("shop", obj);
                query.whereEqualTo("isPaid", true);
                query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
                query.orderByDescending("updatedAt");
                query.findInBackground((object, e) -> {
                    if (e == null) {
                        if (toggleSwitch.isChecked()) {

                            tvShopStatus.setText("Online");
                            onOff(objectId, 1, "You will receive orders now");
                            restart();
                        } else {
                            stop();
                            if (object.size() > 0) {
                                restart();
                                offlinePopup();
                                toggleSwitch.setChecked(true);
                                tvShopStatus.setText("Online");
                            } else {

                                tvShopStatus.setText("Offline");
                                onOff(objectId, 0, "You will not receive orders");
                                stop();

                            }
                        }

                    }
                });
            }
        });


        moreLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = DialogPlus.newDialog(OrderListActivityNew.this)
                        .setContentHolder(new ViewHolder(R.layout.bottomoptions))
                        .setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(DialogPlus dialog, Object item, View view, int position) {

                            }
                        })
                        .setExpanded(false)
                        .create();

                View view = dialog.getHolderView();
                LinearLayout editop = view.findViewById(R.id.editoption);
                LinearLayout how = view.findViewById(R.id.how);
                LinearLayout support = view.findViewById(R.id.support);
                LinearLayout logout = view.findViewById(R.id.logout);
                LinearLayout exit = view.findViewById(R.id.exit);
                editop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Intent e = new Intent(OrderListActivityNew.this, EditMenuTabsActivity.class);
                        e.putExtra("locationName", locationName);
                        e.putExtra("objectId", objectId);
                        e.putExtra("lat", latitude);
                        e.putExtra("log", longitude);
                        e.putExtra("pin", getIntent().getStringExtra("pin"));
                        e.putExtra("shopStatus", shopStatus);
                        e.putExtra("phoneNo", getIntent().getStringExtra("phoneNo"));
                        startActivity(e);
                        dialog.dismiss();
                    }
                });

                exit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finishAffinity();
                        finish();
                    }
                });
                how.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                support.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                logout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Log.d("chk", "done:shopsts "+shopsts);
                        alertMessage(v);
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });


    }

    public void start() {
        mHandler.postDelayed(m_Runnable, 5000);
    }

    public void stop() {
        mHandler.removeCallbacks(m_Runnable);
    }

    public void restart() {
        mHandler.removeCallbacks(m_Runnable);
        mHandler.postDelayed(m_Runnable, 5000);
    }


    public void onOff(String id, int sts, String msg) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ShopLocations");
        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.whereEqualTo("business", ParseUser.getCurrentUser().getObjectId());
        query.getInBackground(id, new GetCallback<ParseObject>() {
            public void done(ParseObject shop, ParseException e) {
                if (e == null) {
                    // Now let's update it with some new data.
                    shop.put("shopStatus", sts);
                    shop.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {

                            if (e == null) {
                                Toast.makeText(OrderListActivityNew.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(m_Runnable);

    }


    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void saveinstall() {
        if (ParseUser.getCurrentUser() != null) {

            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
            JSONArray ary = installation.getJSONArray("channels");
            if (installation.getJSONArray("channels") != null) {
                if (installation.getJSONArray("channels").length() > 0) {
                    try {

                        for (int i = 0; i < ary.length(); i++) {
                            String st = ary.getString(i);

                            if ((st.equals("shop_" + objectId))) {
                                ary.remove(i);
                                installation.put("channels", ary);
                                installation.saveInBackground();

                            }
                        }
                        installation.put("channels", ary);
                        installation.saveInBackground();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

        } else {
            Log.d("chk", "done:no user ");
            session.logoutUser();
        }

        Intent i = new Intent(OrderListActivityNew.this, DashboardActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
    }


    public void alertMessage(View view) {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.passwordpopup, null);
        AlertDialog.Builder alertBox = new AlertDialog.Builder(view.getRootView().getContext());
        alertBox.setView(layout);
        Button submit = layout.findViewById(R.id.adsubmit);
        EditText etPassword = layout.findViewById(R.id.adpass);
        TextView txtTagLine = layout.findViewById(R.id.entertxt);
        submit.setTypeface(Typer.set(this).getFont(Font.ROBOTO_MEDIUM));
        etPassword.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        txtTagLine.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        alertBox.setCancelable(true);
        AlertDialog alertDialog = alertBox.create();
        alertDialog.setCancelable(true);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etPassword.getText().toString().trim() != null) {

                    if (loginPassword.equals(etPassword.getText().toString().trim())) {
                        stopService(new Intent(OrderListActivityNew.this, Alertservice.class));
                        session.Pinlogin("", "", "", "", "", "", "");
                        saveinstall();
                        alertDialog.dismiss();

                    } else {
                        Toast.makeText(OrderListActivityNew.this, "Invalid password", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(OrderListActivityNew.this, "Enter password", Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertDialog.show();

    }

    @Override
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

        finishAffinity();
        finish();
//        System.exit(0);
    }

    private boolean checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // test for connection
        if (cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            customToast("Please check the internet connection");
            return false;
        }
    }

    private void customToast(String msg) {
        Toast toast = Toasty.error(OrderListActivityNew.this, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 230);
        toast.show();
    }

    private final Runnable m_Runnable = new Runnable() {
        public void run() {
//            Log.d("chk", "running : ");
//            if (checkInternetConnection()) {
////
//                ParseQuery<ParseObject> query = ParseQuery.getQuery("Orders");
//                ParseObject obj = ParseObject.createWithoutData("ShopLocations", objectId);
//                query.whereEqualTo("shop", obj);
//                query.whereEqualTo("orderStatus", 0);
//                query.whereEqualTo("isPaid", true);
//                query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
//                query.orderByDescending("updatedAt");
//                query.findInBackground((object, e) -> {
//                    if (e == null) {
//
//                        if (object.size() > 0) {
//
//                            new getOrderList().execute();
//                        }
//                    }
//                });
//            }
//            OrderListActivityNew.this.mHandler.postDelayed(m_Runnable, 5000);
        }

    };

    @Override
    protected void onStop() {
        super.onStop();
        mHandler.removeCallbacks(m_Runnable);
    }


    private void initiateData() {
        if (ParseUser.getCurrentUser() != null) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("ShopLocations");
            query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
            query.getInBackground(objectId, new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {

                        shopStatus = String.valueOf(object.getNumber("shopStatus"));

                        if (shopStatus != null) {
                            if (shopStatus.equals("0")) {
                                stop();
                                stopService(new Intent(OrderListActivityNew.this, Alertservice.class));
                                toggleSwitch.setChecked(false);
                                tvShopStatus.setText("Offline");
                            } else if (shopStatus.equals("1")) {
                                toggleSwitch.setChecked(true);
                                tvShopStatus.setText("Online");
                            } else if (shopStatus.equals("2")) {
                                stop();
                                stopService(new Intent(OrderListActivityNew.this, Alertservice.class));
                                toggleSwitch.setChecked(false);
                                tvShopStatus.setText("Disabled");
                            }

                        }
                    } else {
                        if (e.getMessage().equals("Invalid session token")) {
                            Toast.makeText(OrderListActivityNew.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            session.logoutUser();
                        }
                        Crashlytics.logException(e);
                        // something went wrong
                    }
                }
            });
        } else {
            Log.d("chk", "done:no user ");
            session.logoutUser();
        }

    }

    public void offlinePopup() {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.toastpopup, null);

        TextView txt = dialogView.findViewById(R.id.msg);
        Button login = dialogView.findViewById(R.id.ok);
        TextView txt1 = dialogView.findViewById(R.id.txt);
        txt.setText("Orders Pending!");
        txt1.setText("Please complete all orders");
        txt.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        txt1.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        login.setTypeface(Typer.set(this).getFont(Font.ROBOTO_MEDIUM));
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
            }
        });

        dialogBuilder.setView(dialogView);
        alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(true);
        alertDialog.show();
    }

    public void removeItem(int childPosition, int groupPosition, Orders childData) {

        if (groupPosition == 0) {
            newOrderList.remove(childData);
        } else if (groupPosition == 1) {
            progressList.remove(childData);
        } else if (groupPosition == 2) {
            readyList.remove(childData);
        }

        listDataChild.put(listDataHeader.get(0), newOrderList);
        listDataChild.put(listDataHeader.get(1), progressList);
        listDataChild.put(listDataHeader.get(2), readyList);

        orderAdapter.notifyDataSetChanged();
    }


    public void changeStatus(int childPosition, int groupPosition, Orders childData) {


        if (checkInternetConnection()) {
            ll_progressBar.setVisibility(View.VISIBLE);
            orderExpandableList.setVisibility(View.GONE);
            initiateData();
            new getOrderList().execute();
            orderAdapter = new OrderListAdapter(this, listDataHeader, listDataChild, orderExpandableList);
            orderExpandableList.setAdapter(orderAdapter);

        }

    }


    private class getOrderList extends AsyncTask<Void, Void, List<ParseObject>> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            newOrderList = new ArrayList<>();
            progressList = new ArrayList<>();
            readyList = new ArrayList<>();
        }

        @Override
        protected List<ParseObject> doInBackground(Void... params) {

            List<ParseObject> object = null;

            try {

                ParseQuery<ParseObject> query = ParseQuery.getQuery("Orders");
                ParseObject obj = ParseObject.createWithoutData("ShopLocations", objectId);
                query.whereEqualTo("shop", obj);
                query.whereEqualTo("isPaid", true);
                query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
                query.orderByDescending("updatedAt");
                object = query.find();

            } catch (ParseException e) {
                Log.d("Error", e.getMessage());
                e.printStackTrace();
            }

            return object;
        }

        @Override
        protected void onPostExecute(List<ParseObject> result) {

            if (result != null) {

                if (result.size() > 0) {

                    for (ParseObject user : result) {

                        String orderStatus = String.valueOf(user.getNumber("orderStatus"));

                        if (orderStatus.equals("0")) {
                            Intent serviceIntent = new Intent(OrderListActivityNew.this, Alertservice.class);
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                                startForegroundService(serviceIntent);
                            } else {
                                startService(serviceIntent);
                            }
                            startService(new Intent(OrderListActivityNew.this, Alertservice.class));
                            Orders order = new Orders();
                            EstimatedData estimatedData = new EstimatedData();
                            estimatedData.setNotes(user.getString("notes"));
                            estimatedData.setTotalCost(user.getNumber("totalCost").doubleValue());
                            estimatedData.setTranRef(user.getString("tranRef"));
                            estimatedData.setSubTotal(user.getNumber("subTotal").doubleValue());
                            estimatedData.setOrderStatus(user.getNumber("orderStatus").intValue());
                            estimatedData.setTaxId(user.getString("taxId"));
                            estimatedData.setObjectId(user.getObjectId());
                            estimatedData.setDiscountAmount(user.getString("discountAmount"));
                            estimatedData.setOfferEnanbled(user.getBoolean("offerEnabled"));
                            State state = new State();
                            state.setCreatedAt(user.getCreatedAt().toString());
                            order.setState(state);
                            ParseObject customer = user.getParseObject("user");
                            ParseObject shop = user.getParseObject("shop");
                            ParseObject offer = user.getParseObject("offerDetails");

                            try {
                                if (offer != null) {
                                    estimatedData.setOfferObjectId(offer.fetchIfNeeded().getObjectId());
                                }
                                estimatedData.setCustomerName(customer.fetchIfNeeded().getString("name"));
                                estimatedData.setCarDetails(customer.fetchIfNeeded().getString("carDetails"));
                                estimatedData.setShopLocationName(shop.fetchIfNeeded().getString("locationName"));
                                estimatedData.setLocationId(shop.fetchIfNeeded().getObjectId());
                                estimatedData.setShopPhoneNo(shop.fetchIfNeeded().getNumber("phoneNo"));
                                phoneNo = customer.fetchIfNeeded().getNumber("phoneNumber");
                                estimatedData.setCustomerPhoneNumber(customer.fetchIfNeeded().getNumber("phoneNumber"));
                                ParseGeoPoint point = customer.getParseGeoPoint("liveLocation");
                                if (point != null) {
                                    Location location = new Location();
                                    location.setUserlatitude(point.getLatitude());
                                    location.setUserlongitude(point.getLongitude());
                                    estimatedData.setUserLocation(location);
                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            estimatedData.setUserId(user.getString("userString"));
                            estimatedData.setIsPaid(user.getBoolean("isPaid"));
                            if (user.getNumber("refundCost") != null) {
                                estimatedData.setRefundCost(user.getNumber("refundCost"));

                            }
//                            estimatedData.setRefundCost(user.getNumber("refundCost").doubleValue());
                            estimatedData.setCancelledBy(user.getString("cancelledBy"));
                            estimatedData.setRefund(user.getString("refund"));
                            estimatedData.setRefund(user.getString("cancelNote"));
                            estimatedData.setPin(Integer.parseInt(pin));
                            estimatedData.setShopStatus(Integer.parseInt(shopStatus));
                            estimatedData.setPhoneNo(phoneNo);
                            estimatedData.setTax(user.getNumber("tax").intValue());
                            estimatedData.setOrder(user.getString("order"));
                            estimatedData.setCurrency(user.getString("currency"));
                            estimatedData.setCancelNote(user.getString("cancelNote"));
                            if (user.getNumber("totalTime") != null) {
                                estimatedData.setTotalTime(user.getNumber("totalTime"));
                            }
                            estimatedData.setCreatedDateAt(user.getCreatedAt().toString());
                            //       estimatedData.setTime(user.getJSONArray("time").toString());
                            estimatedData.setTime(String.valueOf(user.getJSONArray("time")));

                            Business business = new Business();
                            BusinessEstimatedData businessEstimateData = new BusinessEstimatedData();
                            businessEstimateData.setBusinessName(user.getString("Business_name"));
                            business.setBusinessEstimatedData(businessEstimateData);
                            estimatedData.setBusiness(business);
                            Location location = new Location();
                            if (latitude != null) {
                                if (latitude.length() > 0) {
                                    location.setLatitude(Double.valueOf(latitude));
                                    location.setLongitude(Double.valueOf(longitude));
                                    estimatedData.setLocation(location);
                                }
                            }

                            estimatedData.setButtonStatus("Start Order");
                            order.setEstimatedData(estimatedData);
                            newOrderList.add(order);
                        } else if (orderStatus.equals("1")) {
                            Orders order = new Orders();
                            EstimatedData estimatedData = new EstimatedData();
                            estimatedData.setNotes(user.getString("notes"));
                            estimatedData.setTotalCost(user.getNumber("totalCost"));
                            estimatedData.setTranRef(user.getString("tranRef"));
                            estimatedData.setSubTotal(user.getNumber("subTotal"));
                            estimatedData.setOrderStatus(user.getNumber("orderStatus").intValue());
                            estimatedData.setTaxId(user.getString("taxId"));
                            estimatedData.setObjectId(user.getObjectId());
                            //   estimatedData.setObjectId(objectId);
                            estimatedData.setDiscountAmount(user.getString("discountAmount"));
                            estimatedData.setOfferEnanbled(user.getBoolean("offerEnabled"));
                            State state = new State();
                            state.setCreatedAt(user.getCreatedAt().toString());
                            order.setState(state);
                            ParseObject customer = user.getParseObject("user");
                            ParseObject shop = user.getParseObject("shop");
                            ParseObject offer = user.getParseObject("offerDetails");

                            try {
                                if (offer != null) {
                                    estimatedData.setOfferObjectId(offer.fetchIfNeeded().getObjectId());
                                }
                                estimatedData.setCustomerName(customer.fetchIfNeeded().getString("name"));
                                estimatedData.setCarDetails(customer.fetchIfNeeded().getString("carDetails"));
                                estimatedData.setShopLocationName(shop.fetchIfNeeded().getString("locationName"));
                                estimatedData.setLocationId(shop.fetchIfNeeded().getObjectId());
                                estimatedData.setShopPhoneNo(shop.fetchIfNeeded().getNumber("phoneNo"));
                                estimatedData.setCustomerPhoneNumber(customer.fetchIfNeeded().getNumber("phoneNumber"));
                                phoneNo = customer.fetchIfNeeded().getNumber("phoneNumber");
                                ParseGeoPoint point = customer.getParseGeoPoint("liveLocation");
                                if (point != null) {
                                    Location location = new Location();
                                    location.setUserlatitude(point.getLatitude());
                                    location.setUserlongitude(point.getLongitude());
                                    estimatedData.setUserLocation(location);
                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            estimatedData.setUserId(user.getString("userString"));
                            estimatedData.setIsPaid(user.getBoolean("isPaid"));
                            if (user.getNumber("refundCost") != null) {
                                estimatedData.setRefundCost(user.getNumber("refundCost").doubleValue());

                            }
                            estimatedData.setCancelledBy(user.getString("cancelledBy"));
                            estimatedData.setRefund(user.getString("refund"));
                            estimatedData.setRefund(user.getString("cancelNote"));
                            estimatedData.setPin(Integer.parseInt(pin));
                            estimatedData.setShopStatus(Integer.parseInt(shopStatus));
                            estimatedData.setPhoneNo(phoneNo);
                            estimatedData.setTax(user.getNumber("tax").intValue());
                            Log.d("chk", "onPostExecute: " + user.getNumber("tax"));

                            estimatedData.setOrder(user.getString("order"));
                            estimatedData.setCurrency(user.getString("currency"));
                            estimatedData.setCancelNote(user.getString("cancelNote"));
                            estimatedData.setTotalTime(user.getNumber("totalTime").doubleValue());
                            estimatedData.setCreatedDateAt(user.getCreatedAt().toString());
                            estimatedData.setTime(String.valueOf(user.getJSONArray("time")));
                            //  estimatedData.setTime(user.getJSONArray("time").toString());

                            Business business = new Business();
                            BusinessEstimatedData businessEstimateData = new BusinessEstimatedData();
                            businessEstimateData.setBusinessName(user.getString("Business_name"));
                            business.setBusinessEstimatedData(businessEstimateData);
                            estimatedData.setBusiness(business);
                            Location location = new Location();
                            if (latitude != null) {
                                if (latitude.length() > 0) {
                                    location.setLatitude(Double.valueOf(latitude));
                                    location.setLongitude(Double.valueOf(longitude));
                                    estimatedData.setLocation(location);
                                }
                            }
                            estimatedData.setButtonStatus("Ready");
                            order.setEstimatedData(estimatedData);
                            progressList.add(order);


                        } else if (orderStatus.equals("2")) {
                            Orders order = new Orders();
                            EstimatedData estimatedData = new EstimatedData();
                            estimatedData.setNotes(user.getString("notes"));
                            estimatedData.setTotalCost(user.getNumber("totalCost").doubleValue());
                            estimatedData.setTranRef(user.getString("tranRef"));
                            estimatedData.setSubTotal(user.getNumber("subTotal").doubleValue());
                            estimatedData.setOrderStatus(user.getNumber("orderStatus").intValue());
                            estimatedData.setTaxId(user.getString("taxId"));
                            estimatedData.setObjectId(user.getObjectId());
                            //   estimatedData.setObjectId(objectId);
                            estimatedData.setDiscountAmount(user.getString("discountAmount"));
                            estimatedData.setOfferEnanbled(user.getBoolean("offerEnabled"));
                            State state = new State();
                            state.setCreatedAt(user.getCreatedAt().toString());
                            order.setState(state);
                            ParseObject customer = user.getParseObject("user");
                            ParseObject shop = user.getParseObject("shop");
                            ParseObject offer = user.getParseObject("offerDetails");

                            try {
                                if (offer != null) {
                                    estimatedData.setOfferObjectId(offer.fetchIfNeeded().getObjectId());
                                }
                                estimatedData.setCustomerName(customer.fetchIfNeeded().getString("name"));
                                estimatedData.setCarDetails(customer.fetchIfNeeded().getString("carDetails"));
                                estimatedData.setShopLocationName(shop.fetchIfNeeded().getString("locationName"));
                                estimatedData.setLocationId(shop.fetchIfNeeded().getObjectId());
                                estimatedData.setShopPhoneNo(shop.fetchIfNeeded().getNumber("phoneNo"));
                                estimatedData.setCustomerPhoneNumber(customer.fetchIfNeeded().getNumber("phoneNumber"));
                                phoneNo = customer.fetchIfNeeded().getNumber("phoneNumber");
                                ParseGeoPoint point = customer.getParseGeoPoint("liveLocation");
                                if (point != null) {
                                    Location location = new Location();
                                    location.setUserlatitude(point.getLatitude());
                                    location.setUserlongitude(point.getLongitude());
                                    estimatedData.setUserLocation(location);
                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            estimatedData.setUserId(user.getString("userString"));
                            estimatedData.setIsPaid(user.getBoolean("isPaid"));
                            if (user.getNumber("refundCost") != null) {
                                estimatedData.setRefundCost(user.getNumber("refundCost").doubleValue());

                            }
//                            estimatedData.setRefundCost(user.getNumber("refundCost").doubleValue());
                            estimatedData.setCancelledBy(user.getString("cancelledBy"));
                            estimatedData.setRefund(user.getString("refund"));
                            estimatedData.setRefund(user.getString("cancelNote"));
                            estimatedData.setPin(Integer.parseInt(pin));
                            estimatedData.setShopStatus(Integer.parseInt(shopStatus));
                            estimatedData.setPhoneNo(phoneNo);
                            estimatedData.setTax(user.getNumber("tax").intValue());

                            estimatedData.setOrder(user.getString("order"));
                            estimatedData.setCurrency(user.getString("currency"));
                            estimatedData.setCancelNote(user.getString("cancelNote"));
                            estimatedData.setTotalTime(user.getNumber("totalTime").doubleValue());
                            estimatedData.setCreatedDateAt(user.getCreatedAt().toString());
                            estimatedData.setTime(String.valueOf(user.getJSONArray("time")));
                            //  estimatedData.setTime(user.getJSONArray("time").toString());

                            Business business = new Business();
                            BusinessEstimatedData businessEstimateData = new BusinessEstimatedData();
                            businessEstimateData.setBusinessName(user.getString("Business_name"));
                            business.setBusinessEstimatedData(businessEstimateData);
                            estimatedData.setBusiness(business);
                            Location location = new Location();
                            if (latitude != null) {
                                if (latitude.length() > 0) {
                                    location.setLatitude(Double.valueOf(latitude));
                                    location.setLongitude(Double.valueOf(longitude));
                                    estimatedData.setLocation(location);
                                }
                            }
                            estimatedData.setButtonStatus("Pick up");
                            order.setEstimatedData(estimatedData);
                            readyList.add(order);

                        }

                        if (newOrderList.size() > 0) {
                            // do nothing
                        } else {
                            stopService(new Intent(OrderListActivityNew.this, Alertservice.class));
                        }

                        listDataChild.put(listDataHeader.get(0), newOrderList);
                        listDataChild.put(listDataHeader.get(1), progressList);
                        listDataChild.put(listDataHeader.get(2), readyList);

                        ll_progressBar.setVisibility(View.GONE);
                        orderExpandableList.setVisibility(View.VISIBLE);

                    }
                } else {
                    ll_progressBar.setVisibility(View.GONE);
                    orderExpandableList.setVisibility(View.VISIBLE);
                    listDataChild.put(listDataHeader.get(0), newOrderList);
                    listDataChild.put(listDataHeader.get(1), progressList);
                    listDataChild.put(listDataHeader.get(2), readyList);
                }
            }

        }
    }
}