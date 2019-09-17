package com.pikbusiness;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.orhanobut.dialogplus.DialogPlus;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.pikbusiness.Adapters.InProgressAdapter;
import com.pikbusiness.Adapters.NewOrderAdapter;
import com.pikbusiness.Adapters.ReadyAdapter;
import com.pikbusiness.Loginmodule.SessionManager;
import com.pikbusiness.model.Response.Business;
import com.pikbusiness.model.Response.BusinessEstimatedData;
import com.pikbusiness.model.Response.EstimatedData;
import com.pikbusiness.model.Response.Location;
import com.pikbusiness.model.Response.Orders;
import com.pikbusiness.model.Response.State;
import com.pikbusiness.services.Toasty;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

public class OrderListActivity extends AppCompatActivity {

    @BindView(R.id.neworder)
    RecyclerView newOrderRecyclerView;
    @BindView(R.id.inprogress)
    RecyclerView inProgressRecyclerView;
    @BindView(R.id.ready)
    RecyclerView readyRecyclerView;
    @BindView(R.id.shopname)
    TextView tvShopName;
    @BindView(R.id.shoploc)
    TextView tvShopLocation;
    @BindView(R.id.txt_switch)
    TextView tvShopStatus;
    @BindView(R.id.toggle_switch)
    Switch toggleSwitch;
    @BindView(R.id.scrollview)
    NestedScrollView scrollview;
    @BindView(R.id.more)
    LinearLayout options;
    @BindView(R.id.txt_neworder)
    TextView txt_neworder;
    @BindView(R.id.txt_inprogress)
    TextView txt_inprogress;
    @BindView(R.id.txt_ready)
    TextView txt_ready;
    // RecyclerView.LayoutManager l1, l2, l3;
    // List<ParseObject> object11;
    private NewOrderAdapter mNewOrderAdapter;
    private InProgressAdapter mInProgressAdapter;
    private ReadyAdapter mReadyAdapter;
    //private String lname, bname, idd, shopsts, lat, logg, pass;
    //private Boolean sts1 = false, sts2 = false, sts3 = false;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    private DialogPlus dialog;
    private SessionManager session;
    private List<Orders> newOrderList;
    private List<Orders> progressList;
    private List<Orders> readyList;
    // private Handler mHandler;
    //   private int size1, size2;
    //    ArrayList<HashMap<String, String>> maplist1,maplist2,maplist3;
    // private AlertDialog alertDialog1;


    private double latitude, longitude;
    private String locationName, pin, objectId, shopStatus, phoneNo, businessName;
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderslist);
        ButterKnife.bind(this);
        Fabric.with(this, new Crashlytics());
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.setCurrentScreen(OrderListActivity.this, "OrderListActivity", "ss");
        session = new SessionManager(this);
        mContext = OrderListActivity.this;


//        SharedPreferences pref = getApplicationContext().getSharedPreferences("Reg", 0); // 0 - for private mode
//        SharedPreferences.Editor editor = pref.edit();
//        pass = pref.getString("Pass", null);
//        String chk = pref.getString("locationName", null);
//        String pinn = pref.getString("pin", null);
//        if(pinn != null){
//            if (pinn.equals("")) {
//                editor.clear();
//                editor.apply();
//                session.logoutUser();
//            }
//        }else{
//            editor.clear();
//            editor.apply();
//            session.logoutUser();
//        }

//
        Bundle b = getIntent().getExtras();
        latitude = b.getDouble("latitude");
        longitude = b.getDouble("longitude");
        locationName = getIntent().getStringExtra("locationName");
        businessName = getIntent().getStringExtra("businessName");
        pin = getIntent().getStringExtra("pin");
        objectId = getIntent().getStringExtra("objectId");
        shopStatus = getIntent().getStringExtra("shopStatus");
        phoneNo = getIntent().getStringExtra("phoneNo");

        tvShopName.setText(businessName);
        tvShopLocation.setText(locationName);


        //this.mHandler = new Handler();
/*        txt_ready.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        txt_inprogress.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        txt_neworder.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        tvShopStatus.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        tvShopName.setTypeface(Typer.set(this).getFont(Font.ROBOTO_MEDIUM));
        tvShopLocation.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));*/

        //   l1 = new LinearLayoutManager(getApplicationContext());
        // l2 = new LinearLayoutManager(getApplicationContext());
        //l3 = new LinearLayoutManager(getApplicationContext());

        mNewOrderAdapter = new NewOrderAdapter(mContext, newOrderList);
        newOrderRecyclerView.setNestedScrollingEnabled(false);
        newOrderRecyclerView.setHasFixedSize(true);
        newOrderRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        newOrderRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mInProgressAdapter = new InProgressAdapter(mContext, progressList);
        inProgressRecyclerView.setNestedScrollingEnabled(false);
        inProgressRecyclerView.setHasFixedSize(true);
        inProgressRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        inProgressRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mReadyAdapter = new ReadyAdapter(mContext, readyList);
        readyRecyclerView.setNestedScrollingEnabled(false);
        readyRecyclerView.setHasFixedSize(true);
        readyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        readyRecyclerView.setItemAnimator(new DefaultItemAnimator());


//        l1 = new LinearLayoutManager(this) {
//            @Override
//            public boolean canScrollVertically() {
//                return false;
//            }
//        };
//        l2 = new LinearLayoutManager(this) {
//            @Override
//            public boolean canScrollVertically() {
//                return false;
//            }
//        };
//
//        l3 = new LinearLayoutManager(this) {
//            @Override
//            public boolean canScrollVertically() {
//                return false;
//            }
//        };

//        Log.d("chk", "done:chkkk =new");
//        if (chk != null) {
//            lname = pref.getString("locationName", null);
//            bname = pref.getString("bname", null);
//            idd = pref.getString("objectId", null);
//            lat = pref.getString("lat", null);
//            logg = pref.getString("log", null);
//        } else {
//            lname = getIntent().getStringExtra("locationName");
//            bname = getIntent().getStringExtra("bname");
//            idd = getIntent().getStringExtra("objectId");
//        }
//
//        String sts = getIntent().getStringExtra("sts");
//
//        if (sts != null) {
//            if (sts.equals("1")) {
//                sts2 = true;
//                inProgressRecyclerView.setVisibility(View.VISIBLE);
//            } else if (sts.equals("2")) {
//                sts3 = true;
//                readyRecyclerView.setVisibility(View.VISIBLE);
//            }
//        }
//        start();
//        String stschk = getIntent().getStringExtra("stschk");
//
//        if (stschk != null) {
//            if (stschk.equals("0")) {
//                toggleSwitch.setChecked(true);
//                tvShopStatus.setText("Online");
//                new Getneworders_list().execute();
//                new Getinprogresslist().execute();
//                new Getreadyorders_list().execute();
//
//            } else if (stschk.equals("1")) {
//                toggleSwitch.setChecked(true);
//                tvShopStatus.setText("Online");
//                new Getneworders_list().execute();
//                new Getinprogresslist().execute();
//                new Getreadyorders_list().execute();
//
//            } else if (stschk.equals("2")) {
//                toggleSwitch.setChecked(true);
//                tvShopStatus.setText("Online");
//                new Getneworders_list().execute();
//                new Getinprogresslist().execute();
//                new Getreadyorders_list().execute();
//            } else {
//
//                if (checkInternetConenction()) {
//                    new getOrderList().execute();
//                }
//            }
//        } else {
//
        //  if (checkInternetConenction()) {
        initiateData();
        new getOrderList().execute();
        //   }


//        toggleSwitch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Log.d("chk", "done:shopsts "+shopsts);
//                ParseQuery<ParseObject> query = ParseQuery.getQuery("Orders");
//                ParseObject obj = ParseObject.createWithoutData("ShopLocations", idd);
//                query.whereEqualTo("shop", obj);
//                query.whereEqualTo("isPaid", true);
//                query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
//                query.orderByDescending("updatedAt");
//                query.findInBackground((object, e) -> {
//                    if (e == null) {
//                        if (toggleSwitch.isChecked()) {
//
//                            tvShopStatus.setText("Online");
//                            onOff(idd, 1, "You will receive orders now");
//                            restart();
//                        } else {
//                            stop();
//                            if (object.size() > 0) {
//                                restart();
//                                offlinePopup();
//                                toggleSwitch.setChecked(true);
//                                tvShopStatus.setText("Online");
//                            } else {
//
//                                tvShopStatus.setText("Offline");
//                                onOff(idd, 0, "You will not receive orders");
//                                stop();
//
//                            }
//                        }
//
//                    }
//                });
////                if (toggleSwitch.isChecked()) {
//
////                        tvShopStatus.setText("Online");
////                        onOff(objectId, 1, "You will receive orders now");
////                } else {
////                    if(sts1){
////                        offlinePopup();
////                        toggleSwitch.setChecked(true);
////                        tvShopStatus.setText("Online");
////                    }else{
////                        tvShopStatus.setText("Offline");
////                        onOff(objectId, 0, "You will not receive orders");
////                    }
////                }
//            }
//        });
//        options.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog = DialogPlus.newDialog(OrderListActivity.this)
//                        .setContentHolder(new ViewHolder(R.layout.bottomoptions))
//                        .setOnItemClickListener(new OnItemClickListener() {
//                            @Override
//                            public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
//
//                            }
//                        })
//                        .setExpanded(false)
//                        .create();
//
//                View view = dialog.getHolderView();
//                LinearLayout editop = view.findViewById(R.id.editoption);
//                LinearLayout how = view.findViewById(R.id.how);
//                LinearLayout support = view.findViewById(R.id.support);
//                LinearLayout logout = view.findViewById(R.id.logout);
//                LinearLayout exit = view.findViewById(R.id.exit);
//                editop.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                        Intent e = new Intent(OrderListActivity.this, EditMenuTabsActivity.class);
//                        e.putExtra("locationName", lname);
//                        e.putExtra("objectId", idd);
//                        e.putExtra("lat", lat);
//                        e.putExtra("log", logg);
//                        e.putExtra("pin", getIntent().getStringExtra("pin"));
//                        e.putExtra("shopStatus", shopsts);
//                        e.putExtra("phoneNo", getIntent().getStringExtra("phoneNo"));
//                        startActivity(e);
//                        dialog.dismiss();
//                    }
//                });
//
//                exit.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        finishAffinity();
//                        finish();
//                    }
//                });
//                how.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                    }
//                });
//
//                support.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                    }
//                });
//
//                logout.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
////                        Log.d("chk", "done:shopsts "+shopsts);
////                        if(toggleSwitch.isChecked()){
////                            dialog.dismiss();
////                            Toast.makeText(OrderListActivity.this, "Please make it Shop is offline", Toast.LENGTH_SHORT).show();
////                        }else{
//                        alertMessage(v);
//                        dialog.dismiss();
////                        }
//
//                    }
//                });
//                dialog.show();
//            }
//        });


//
        mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor
                (OrderListActivity.this, R.color.colorPrimary));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //if (checkInternetConenction()) {
                Log.d("chk", "done:shopstatus " + shopStatus);
                new getOrderList().execute();
                //   }

            }
        });

    }
//
//    public void offlinePopup() {
//
//        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
//        LayoutInflater inflater = this.getLayoutInflater();
//        View dialogView = inflater.inflate(R.layout.toastpopup, null);
//
//        TextView txt = dialogView.findViewById(R.id.msg);
//        Button login = dialogView.findViewById(R.id.ok);
//        TextView txt1 = dialogView.findViewById(R.id.txt);
//        txt.setText("Orders Pending!");
//        txt1.setText("Please complete all orders");
//        txt.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
//        txt1.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
//        login.setTypeface(Typer.set(this).getFont(Font.ROBOTO_MEDIUM));
//        login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                alertDialog1.dismiss();
//            }
//        });
//
//        dialogBuilder.setView(dialogView);
//        alertDialog1 = dialogBuilder.create();
//        alertDialog1.setCancelable(true);
//        alertDialog1.show();
//    }
//


    //
//    public void start() {
//        mHandler.postDelayed(m_Runnable, 5000);
//    }
//
//    public void stop() {
//        mHandler.removeCallbacks(m_Runnable);
//    }
//
//    public void restart() {
//        mHandler.removeCallbacks(m_Runnable);
//        mHandler.postDelayed(m_Runnable, 5000);
//    }
//
//    private final Runnable m_Runnable = new Runnable() {
//        public void run() {
////            Log.d("chk", "running : ");
//            if (checkInternetConenction()) {
////
//                ParseQuery<ParseObject> query = ParseQuery.getQuery("Orders");
//                ParseObject obj = ParseObject.createWithoutData("ShopLocations", idd);
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
//                            new Getneworders_list().execute();
//                        }
//                    }
//                });
////                  new getOrderList().execute();
//            }
//            OrderListActivity.this.mHandler.postDelayed(m_Runnable, 5000);
//        }
//
//    };
//
//    @Override
//    protected void onStop() {
//        super.onStop();
////        mHandler.removeCallbacks(m_Runnable);
//    }
//
    public void initiateData() {
//        Log.d("chk", "initiatedata:check ");
        if (ParseUser.getCurrentUser() != null) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("ShopLocations");
            query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
            query.getInBackground(objectId, new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {

                        if (mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }

                        shopStatus = String.valueOf(object.getNumber("shopStatus"));

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
                    } else {
                        if (e.getMessage().equals("Invalid session token")) {
                            Toast.makeText(OrderListActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    //
//    public void alertMessage(View view) {
//        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View layout = inflater.inflate(R.layout.passwordpopup, null);
//        AlertDialog.Builder alertbox = new AlertDialog.Builder(view.getRootView().getContext());
//        alertbox.setView(layout);
//        Button submit = layout.findViewById(R.id.adsubmit);
//        EditText adpass = layout.findViewById(R.id.adpass);
//        TextView entertxt = layout.findViewById(R.id.entertxt);
//        submit.setTypeface(Typer.set(this).getFont(Font.ROBOTO_MEDIUM));
//        adpass.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
//        entertxt.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
//        alertbox.setCancelable(true);
//        AlertDialog alertDialog = alertbox.create();
//        alertDialog.setCancelable(true);
//        submit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (adpass.getText().toString().trim() != null) {
//
//                    if (pass.equals(adpass.getText().toString().trim())) {
//                        stopService(new Intent(OrderListActivity.this, Alertservice.class));
//                        session.Pinlogin("", "", "", "", "", "", "");
//                        saveinstall();
//                        alertDialog.dismiss();
//
//                    } else {
//                        Toast.makeText(OrderListActivity.this, "Invalid password", Toast.LENGTH_SHORT).show();
//                    }
//
//                } else {
//                    Toast.makeText(OrderListActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//        alertDialog.show();
//
//    }
//
//    public void saveinstall() {
//        if (ParseUser.getCurrentUser() != null) {
//
//            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
//            JSONArray ary = installation.getJSONArray("channels");
//            if (installation.getJSONArray("channels") != null) {
//                if (installation.getJSONArray("channels").length() > 0) {
//                    try {
//
//                        for (int i = 0; i < ary.length(); i++) {
//                            String st = ary.getString(i);
//
//                            if ((st.equals("shop_" + idd))) {
//                                ary.remove(i);
//                                installation.put("channels", ary);
//                                installation.saveInBackground();
//
//                            }
//                        }
//                        installation.put("channels", ary);
//                        installation.saveInBackground();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            }
//
//        } else {
//            checkInternetConenction();
////            session.logoutUser();
//        }
//
//        Intent i = new Intent(OrderListActivity.this, DashboardActivity.class);
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//        startActivity(i);
//    }
//
//    @Override
//    protected void onRestart() {
//        super.onRestart();
////        m_Runnable.run();
//        SharedPreferences pref = getApplicationContext().getSharedPreferences("Reg", 0); // 0 - for private mode
//        SharedPreferences.Editor editor = pref.edit();
//        String pinn = pref.getString("pin", null);
//        if (checkInternetConenction()) {
////            initiatedata();
//            new getOrderList().execute();
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//    }
//
//    public void onOff(String id, int sts, String msg) {
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("ShopLocations");
//        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
//        query.whereEqualTo("business", ParseUser.getCurrentUser().getObjectId());
//        query.getInBackground(id, new GetCallback<ParseObject>() {
//            public void done(ParseObject shop, ParseException e) {
//                if (e == null) {
//                    // Now let's update it with some new data.
//                    shop.put("shopStatus", sts);
//                    shop.saveInBackground(new SaveCallback() {
//                        @Override
//                        public void done(ParseException e) {
//
//                            if (e == null) {
//                                Toast.makeText(OrderListActivity.this, msg, Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//                } else {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//
    private class getOrderList extends AsyncTask<Void, Void, List<ParseObject>> {

        //   ArrayList<HashMap<String, String>> neworders_hashmap, inprogress_hashmap, reay_hashmap;

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
            // Create the array
//            if(ParseUser.getCurrentUser()!=null) {
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
//            }
//            else {
//                Toast.makeText(OrderListActivity.this, "Invalid session", Toast.LENGTH_SHORT).show();
//                session.logoutUser();
//                finish();
//            }
            return object;
        }

        @Override
        protected void onPostExecute(List<ParseObject> result) {
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
//            Log.d("chk", "onPostExecute: "+result);

            if (result != null) {

                if (result.size() > 0) {
//                m_Runnable.run(result.size());

                    for (ParseObject user : result) {

                        String orderStatus = String.valueOf(user.getNumber("orderStatus"));

                        if (orderStatus.equals("0")) {
                            Orders order = new Orders();
                            EstimatedData estimatedData = new EstimatedData();
                            estimatedData.setNotes(user.getString("notes"));
                            estimatedData.setTotalCost(user.getInt("totalCost"));
                            estimatedData.setTranRef(user.getString("tranRef"));
                            estimatedData.setSubTotal(user.getInt("subTotal"));
                            estimatedData.setOrderStatus(user.getInt("orderStatus"));
                            estimatedData.setTaxId(user.getString("taxId"));
                            estimatedData.setObjectId(objectId);
                            estimatedData.setDiscountAmount(user.getString("discountAmount"));
                            estimatedData.setOfferEnanbled(user.getBoolean("offerEnabled"));
                            State state = new State();
                            state.setCreatedAt(user.getInt("createdAt"));
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
                                estimatedData.setShopPhoneNo(shop.fetchIfNeeded().getInt("phoneNo"));
                                estimatedData.setCustomerPhoneNumber(customer.fetchIfNeeded().getInt("phoneNumber"));
                                ParseGeoPoint point = customer.getParseGeoPoint("liveLocation");
                                if (point != null) {
                                    Location location = new Location();
                                    location.setLatitude(point.getLatitude());
                                    location.setLongitude(point.getLongitude());
                                    estimatedData.setLocation(location);
                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            estimatedData.setUserId(user.getString("userString"));
                            estimatedData.setIsPaid(user.getBoolean("isPaid"));
                            estimatedData.setRefundCost(user.getInt("refundCost"));
                            estimatedData.setCancelledBy(user.getString("cancelledBy"));
                            estimatedData.setRefund(user.getString("refund"));
                            estimatedData.setRefund(user.getString("cancelNote"));
                            estimatedData.setPin(Integer.parseInt(pin));
                            estimatedData.setShopStatus(Integer.parseInt(shopStatus));
                            estimatedData.setPhoneNo(Integer.parseInt(phoneNo));
                            estimatedData.setTax(user.getInt("tax"));
                            estimatedData.setOrder(user.getString("order"));
                            estimatedData.setCurrency(user.getString("currency"));
                            estimatedData.setCancelNote(user.getString("cancelNote"));
                            estimatedData.setTotalTime(user.getInt("totalTime"));
                            estimatedData.setTime(user.getJSONArray("time").toString());

                            Business business = new Business();
                            BusinessEstimatedData businessEstimateData = new BusinessEstimatedData();
                            businessEstimateData.setBusinessName(user.getString("Business_name"));
                            business.setBusinessEstimatedData(businessEstimateData);
                            estimatedData.setBusiness(business);
                            estimatedData.setButtonStatus("Start Order");
                            order.setEstimatedData(estimatedData);
                            newOrderList.add(order);
                      //      mNewOrderAdapter.notifyDataSetChanged();


                            //      map.put("date", user.getCreatedAt().toString());
                            //  map.put("notes", user.getString("notes"));
//                            map.put("total", String.valueOf(user.getNumber("totalCost")));
//                            map.put("tranRef", user.getString("tranRef"));
//                            map.put("subtotal", String.valueOf(user.getNumber("subTotal")));
//                            map.put("orderstatus", String.valueOf(user.getNumber("orderStatus")));
//                              map.put("taxid", user.getString("taxId"));
//                              map.put("objid", user.getObjectId());
//                              map.put("discountAmount", String.valueOf(user.getNumber("discountAmount")));
//                           ParseObject offer = user.getParseObject("offerDetails");
//                              map.put("offerEnabled", String.valueOf(user.getBoolean("offerEnabled")));
////                            Log.d("chk", "onPostExecute: ");
//                            map.put("date", user.getCreatedAt().toString());
//                          map.put("objectId", objectId);
//                          ParseObject customer = user.getParseObject("user");
//                          ParseObject shop = user.getParseObject("shop");
//                          String name = "", car = "", shop_name = "", shop_phno = "";
//

                            //       ParseObject offer = user.getParseObject("offerDetails");

//                        try {
//                            if (offer != null) {
//                                map.put("offerDetails", offer.fetchIfNeeded().getObjectId());
//                            }
////                                map.put("offerDetails", offer.fetchIfNeeded().getObjectId());
//                            name = customer.fetchIfNeeded().getString("name");
//                            car = customer.fetchIfNeeded().getString("carDetails");
//                            shop_name = shop.fetchIfNeeded().getString("locationName");
//                            shop_phno = String.valueOf(shop.fetchIfNeeded().getNumber("phoneNo"));
//                            map.put("shop_name", shop_name);
//                            map.put("shop_phno", shop_phno);
//                            String customer_phno = String.valueOf(
//                                    customer.fetchIfNeeded().getNumber("phoneNumber"));
//                            map.put("tvPhoneNo", customer_phno);
//                            map.put("username", name);
//                            map.put("car", car);
//                            ParseGeoPoint point = customer.getParseGeoPoint("liveLocation");
//                            if (point != null) {
//                                map.put("ulat", String.valueOf(point.getLatitude()));
//                                map.put("ulong", String.valueOf(point.getLongitude()));
//                            }
//                        } catch (ParseException er) {
//                            er.printStackTrace();
//                        }


//                        map.put("userid", user.getString("userString"));
                            //                       map.put("ispaid", String.valueOf(user.getBoolean("isPaid")));
                            //                      map.put("refundCost", String.valueOf(user.getNumber("refundCost")));
                            //                      map.put("cancelleby", user.getString("cancelledBy"));
                            //                     map.put("refund", user.getString("refund"));
//                        map.put("cancelNote", user.getString("cancelNote"));
//                        map.put("totaltime", String.valueOf(user.getNumber("totalTime")));
//                        map.put("time", String.valueOf(user.getJSONArray("time")));
//                        map.put("pin", getIntent().getStringExtra("pin"));
//                        map.put("shopStatus", shopsts);
                            //        map.put("phoneNo", getIntent().getStringExtra("phoneNo"));
                            //      map.put("tax", String.valueOf(user.getNumber("tax")));
                            //                      map.put("order", user.getString("order"));
                            //                     map.put("currency", user.getString("currency"));
                            //                      map.put("business", user.getString("Business_name"));
//                        map.put("sname", bname);
//                        map.put("sloc", lname);
//                        map.put("slat", lat);
//                        map.put("slong", logg);


                            //  map.put("cancelNote", user.getString("cancelNote"));
                            //             map.put("totaltime", String.valueOf(user.getNumber("totalTime")));
                            //             map.put("time", String.valueOf(user.getJSONArray("time")));


//
//                        neworders_hashmap.add(map);
//
                        }  else if (orderStatus.equals("1")) {
                            Orders order = new Orders();
                            EstimatedData estimatedData = new EstimatedData();
                            estimatedData.setNotes(user.getString("notes"));
                            estimatedData.setTotalCost(user.getInt("totalCost"));
                            estimatedData.setTranRef(user.getString("tranRef"));
                            estimatedData.setSubTotal(user.getInt("subTotal"));
                            estimatedData.setOrderStatus(user.getInt("orderStatus"));
                            estimatedData.setTaxId(user.getString("taxId"));
                            estimatedData.setObjectId(objectId);
                            estimatedData.setDiscountAmount(user.getString("discountAmount"));
                            estimatedData.setOfferEnanbled(user.getBoolean("offerEnabled"));
                            State state = new State();
                            state.setCreatedAt(user.getInt("createdAt"));
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
                                estimatedData.setShopPhoneNo(shop.fetchIfNeeded().getInt("phoneNo"));
                                estimatedData.setCustomerPhoneNumber(customer.fetchIfNeeded().getInt("phoneNumber"));
                                ParseGeoPoint point = customer.getParseGeoPoint("liveLocation");
                                if (point != null) {
                                    Location location = new Location();
                                    location.setLatitude(point.getLatitude());
                                    location.setLongitude(point.getLongitude());
                                    estimatedData.setLocation(location);
                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            estimatedData.setUserId(user.getString("userString"));
                            estimatedData.setIsPaid(user.getBoolean("isPaid"));
                            estimatedData.setRefundCost(user.getInt("refundCost"));
                            estimatedData.setCancelledBy(user.getString("cancelledBy"));
                            estimatedData.setRefund(user.getString("refund"));
                            estimatedData.setRefund(user.getString("cancelNote"));
                            estimatedData.setPin(Integer.parseInt(pin));
                            estimatedData.setShopStatus(Integer.parseInt(shopStatus));
                            estimatedData.setPhoneNo(Integer.parseInt(phoneNo));
                            estimatedData.setTax(user.getInt("tax"));
                            estimatedData.setOrder(user.getString("order"));
                            estimatedData.setCurrency(user.getString("currency"));
                            estimatedData.setCancelNote(user.getString("cancelNote"));
                            estimatedData.setTotalTime(user.getInt("totalTime"));
                            estimatedData.setTime(user.getJSONArray("time").toString());

                            Business business = new Business();
                            BusinessEstimatedData businessEstimateData = new BusinessEstimatedData();
                            businessEstimateData.setBusinessName(user.getString("Business_name"));
                            business.setBusinessEstimatedData(businessEstimateData);
                            estimatedData.setBusiness(business);
                            estimatedData.setButtonStatus("Ready");
                            order.setEstimatedData(estimatedData);
                            progressList.add(order);
                           // mNewOrderAdapter.notifyDataSetChanged();
                        } else if (orderStatus.equals("2")) {
                            Orders order = new Orders();
                            EstimatedData estimatedData = new EstimatedData();
                            estimatedData.setNotes(user.getString("notes"));
                            estimatedData.setTotalCost(user.getInt("totalCost"));
                            estimatedData.setTranRef(user.getString("tranRef"));
                            estimatedData.setSubTotal(user.getInt("subTotal"));
                            estimatedData.setOrderStatus(user.getInt("orderStatus"));
                            estimatedData.setTaxId(user.getString("taxId"));
                            estimatedData.setObjectId(objectId);
                            estimatedData.setDiscountAmount(user.getString("discountAmount"));
                            estimatedData.setOfferEnanbled(user.getBoolean("offerEnabled"));
                            State state = new State();
                            state.setCreatedAt(user.getInt("createdAt"));
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
                                estimatedData.setShopPhoneNo(shop.fetchIfNeeded().getInt("phoneNo"));
                                estimatedData.setCustomerPhoneNumber(customer.fetchIfNeeded().getInt("phoneNumber"));
                                ParseGeoPoint point = customer.getParseGeoPoint("liveLocation");
                                if (point != null) {
                                    Location location = new Location();
                                    location.setLatitude(point.getLatitude());
                                    location.setLongitude(point.getLongitude());
                                    estimatedData.setLocation(location);
                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            estimatedData.setUserId(user.getString("userString"));
                            estimatedData.setIsPaid(user.getBoolean("isPaid"));
                            estimatedData.setRefundCost(user.getInt("refundCost"));
                            estimatedData.setCancelledBy(user.getString("cancelledBy"));
                            estimatedData.setRefund(user.getString("refund"));
                            estimatedData.setRefund(user.getString("cancelNote"));
                            estimatedData.setPin(Integer.parseInt(pin));
                            estimatedData.setShopStatus(Integer.parseInt(shopStatus));
                            estimatedData.setPhoneNo(Integer.parseInt(phoneNo));
                            estimatedData.setTax(user.getInt("tax"));
                            estimatedData.setOrder(user.getString("order"));
                            estimatedData.setCurrency(user.getString("currency"));
                            estimatedData.setCancelNote(user.getString("cancelNote"));
                            estimatedData.setTotalTime(user.getInt("totalTime"));
                            estimatedData.setTime(user.getJSONArray("time").toString());

                            Business business = new Business();
                            BusinessEstimatedData businessEstimateData = new BusinessEstimatedData();
                            businessEstimateData.setBusinessName(user.getString("Business_name"));
                            business.setBusinessEstimatedData(businessEstimateData);
                            estimatedData.setBusiness(business);

                            estimatedData.setButtonStatus("Pick up");
                            order.setEstimatedData(estimatedData);
                            readyList.add(order);
                            // mNewOrderAdapter.notifyDataSetChanged();
                        }



                        //    else if (orderStatus.equals("1")) {
//
//                        HashMap<String, String> map = new HashMap<String, String>();
//                        map.put("notes", user.getString("notes"));
//                        map.put("total", String.valueOf(user.getNumber("totalCost")));
//                        map.put("subtotal", String.valueOf(user.getNumber("subTotal")));
//                        map.put("orderstatus", String.valueOf(user.getNumber("orderStatus")));
//                        map.put("taxid", user.getString("taxId"));
//                        map.put("tranRef", user.getString("tranRef"));
//                        map.put("objid", user.getObjectId());
//                        map.put("date", user.getCreatedAt().toString());
//                        map.put("objectId", objectId);
//                        map.put("discountAmount", String.valueOf(user.getNumber("discountAmount")));
//                        ParseObject offer = user.getParseObject("offerDetails");
//                        map.put("offerEnabled", String.valueOf(user.getBoolean("offerEnabled")));
//                        ParseObject customer = user.getParseObject("user");
//                        ParseObject shop = user.getParseObject("shop");
//                        String name = "", car = "", shop_name = "", shop_phno = "";
//                        try {
//                            if (offer != null) {
//                                map.put("offerDetails", offer.fetchIfNeeded().getObjectId());
//                            }
//
//                            name = customer.fetchIfNeeded().getString("name");
//                            car = customer.fetchIfNeeded().getString("carDetails");
//                            shop_name = shop.fetchIfNeeded().getString("locationName");
//                            shop_phno = String.valueOf(shop.fetchIfNeeded().getNumber("phoneNo"));
//                            map.put("shop_name", shop_name);
//                            map.put("shop_phno", shop_phno);
//
//                            String phno = String.valueOf(customer.fetchIfNeeded().getNumber("phoneNumber"));
//                            map.put("tvPhoneNo", phno);
//                            map.put("username", name);
//                            map.put("car", car);
//                            ParseGeoPoint point = customer.getParseGeoPoint("liveLocation");
//                            if (point != null) {
//                                map.put("ulat", String.valueOf(point.getLatitude()));
//                                map.put("ulong", String.valueOf(point.getLongitude()));
//                            }
//                        } catch (ParseException er) {
//                            er.printStackTrace();
//                        }
//                        map.put("userid", user.getString("userString"));
//                        map.put("ispaid", String.valueOf(user.getBoolean("isPaid")));
//                        map.put("refundCost", String.valueOf(user.getNumber("refundCost")));
//                        map.put("cancelleby", user.getString("cancelledBy"));
//                        map.put("refund", user.getString("refund"));
//                        map.put("cancelNote", user.getString("cancelNote"));
//                        map.put("time", String.valueOf(user.getJSONArray("time")));
//                        map.put("pin", getIntent().getStringExtra("pin"));
//                        map.put("shopStatus", shopsts);
//                        map.put("phoneNo", getIntent().getStringExtra("phoneNo"));
//                        map.put("tax", String.valueOf(user.getNumber("tax")));
//                        map.put("order", user.getString("order"));
//                        map.put("currency", user.getString("currency"));
//                        map.put("totaltime", String.valueOf(user.getNumber("totalTime")));
//                        map.put("business", user.getString("Business_name"));
//                        map.put("sname", bname);
//                        map.put("sloc", lname);
//                        map.put("slat", lat);
//                        map.put("slong", logg);
//                        inprogress_hashmap.add(map);
////                                   maplist2.add(map);
//                    } else if (orderStatus.equals("2")) {
//                        HashMap<String, String> map = new HashMap<String, String>();
//                        map.put("notes", user.getString("notes"));
////                                   map.put("orderno",String.valueOf(user.getNumber("orderNo")));
//                        map.put("total", String.valueOf(user.getNumber("totalCost")));
//                        map.put("subtotal", String.valueOf(user.getNumber("subTotal")));
//                        map.put("orderstatus", String.valueOf(user.getNumber("orderStatus")));
//                        map.put("taxid", user.getString("taxId"));
//                        map.put("objid", user.getObjectId());
//                        map.put("date", user.getCreatedAt().toString());
//                        map.put("objectId", idd);
//                        map.put("discountAmount", String.valueOf(user.getNumber("discountAmount")));
//                        ParseObject offer = user.getParseObject("offerDetails");
//                        map.put("offerEnabled", String.valueOf(user.getBoolean("offerEnabled")));
//                        ParseObject customer = user.getParseObject("user");
//                        ParseObject shop = user.getParseObject("shop");
//                        String name = "", car = "", shop_name = "", shop_phno = "";
//                        try {
//                            if (offer != null) {
//                                map.put("offerDetails", offer.fetchIfNeeded().getObjectId());
//                            }
//                            name = customer.fetchIfNeeded().getString("name");
//                            car = customer.fetchIfNeeded().getString("carDetails");
//                            shop_name = shop.fetchIfNeeded().getString("locationName");
//                            shop_phno = String.valueOf(shop.fetchIfNeeded().getNumber("phoneNo"));
//                            map.put("shop_name", shop_name);
//                            map.put("shop_phno", shop_phno);
//                            String phno = String.valueOf(customer.fetchIfNeeded().getNumber("phoneNumber"));
//                            map.put("tvPhoneNo", phno);
//                            map.put("username", name);
//                            map.put("car", car);
//                            ParseGeoPoint point = customer.getParseGeoPoint("liveLocation");
//                            if (point != null) {
//                                map.put("ulat", String.valueOf(point.getLatitude()));
//                                map.put("ulong", String.valueOf(point.getLongitude()));
//                            }
//                        } catch (ParseException er) {
//                            er.printStackTrace();
//                        }
//                        map.put("userid", user.getString("userString"));
//                        map.put("tranRef", user.getString("tranRef"));
//                        map.put("ispaid", String.valueOf(user.getBoolean("isPaid")));
//                        map.put("refundCost", String.valueOf(user.getNumber("refundCost")));
//                        map.put("cancelleby", user.getString("cancelledBy"));
//                        map.put("refund", user.getString("refund"));
//                        map.put("cancelNote", user.getString("cancelNote"));
//                        map.put("time", String.valueOf(user.getJSONArray("time")));
//                        map.put("pin", getIntent().getStringExtra("pin"));
//                        map.put("shopStatus", shopsts);
//                        map.put("phoneNo", getIntent().getStringExtra("phoneNo"));
//                        map.put("tax", String.valueOf(user.getNumber("tax")));
//                        map.put("order", user.getString("order"));
//                        map.put("currency", user.getString("currency"));
//                        map.put("totaltime", String.valueOf(user.getNumber("totalTime")));
//                        map.put("business", user.getString("Business_name"));
//                        map.put("sname", bname);
//                        map.put("sloc", lname);
//                        map.put("slat", lat);
//                        map.put("slong", logg);
//                        reay_hashmap.add(map);
//                    }
                        //               }
//                    chec(neworders_hashmap.size());
//                    m_Runnable.run();

//                    if (neworders_hashmap.size() > 0) {
//                        Intent serviceIntent = new Intent(OrderListActivity.this, Alertservice.class);
//                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//
//                            OrderListActivity.this.startForegroundService(serviceIntent);
//                        } else {
//                            startService(serviceIntent);
//                        }
//                        startService(new Intent(OrderListActivity.this, Alertservice.class));
//                        newOrderRecyclerView.setVisibility(View.VISIBLE);
//                    } else {
//                        stopService(new Intent(OrderListActivity.this, Alertservice.class));
//                        newOrderRecyclerView.setVisibility(View.GONE);
//                    }
//                    if (inprogress_hashmap.size() > 0) {
//                        inProgressRecyclerView.setVisibility(View.VISIBLE);
//                    } else {
//                        inProgressRecyclerView.setVisibility(View.GONE);
//                    }
//                    if (reay_hashmap.size() > 0) {
//                        readyRecyclerView.setVisibility(View.VISIBLE);
//                    } else {
//                        readyRecyclerView.setVisibility(View.GONE);
//                    }
//                    if(mNewOrderAdapter != null){
//                        mNewOrderAdapter.setData(neworders_hashmap);
//                    }else{
//                    mNewOrderAdapter = new NewOrderAdapter(OrderListActivity.this, neworders_hashmap);
//                    newOrderRecyclerView.setAdapter(mNewOrderAdapter);
//                    }

//                    if(mInProgressAdapter != null){
//                        mInProgressAdapter.setData(inprogress_hashmap);
//                    }else{
//                    mInProgressAdapter = new InProgressAdapter(OrderListActivity.this, inprogress_hashmap);
//                    inProgressRecyclerView.setAdapter(mInProgressAdapter);
//                    }

//                    if(mReadyAdapter != null){
//                        mReadyAdapter.setData(reay_hashmap);
//                    }else{
//                    mReadyAdapter = new ReadyAdapter(OrderListActivity.this, reay_hashmap);
//                    readyRecyclerView.setAdapter(mReadyAdapter);
//                    }

//                    String s1 = String.valueOf(neworders_hashmap.size());
//                    String s2 = String.valueOf(inprogress_hashmap.size());
//                    String s3 = String.valueOf(reay_hashmap.size());
//                    txt_neworder.setText("NEW ORDER" + " - " + neworders_hashmap.size());
//                    txt_inprogress.setText("IN PROGRESS" + " - " + inprogress_hashmap.size());
//                    txt_ready.setText("READY FOR PICK UP" + " - " + reay_hashmap.size());
//                    if (neworders_hashmap.size() > 0 || inprogress_hashmap.size() > 0 || reay_hashmap.size() > 0) {
////                        offlinePopup();
//                        sts1 = true;
//                    } else {
//                        sts1 = false;
////                        tvShopStatus.setText("Offline");
////                        onOff(objectId, 0, "You will not receive orders");
//                    }

//                } else {
//                    sts1 = false;
//                    txt_neworder.setText("NEW ORDER");
//                    txt_inprogress.setText("IN PROGRESS");
//                    txt_ready.setText("READY FOR PICK UP");
//                }
                    }
                }
            }
//
//    private class Getneworders_list extends AsyncTask<Void, Void, List<ParseObject>> {
//
//        ArrayList<HashMap<String, String>> neworders_hashmap;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
////            newOrderRecyclerView.getRecycledViewPool().clear();
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
//                ParseObject obj = ParseObject.createWithoutData("ShopLocations", idd);
//                query.whereEqualTo("shop", obj);
//                query.whereEqualTo("orderStatus", 0);
//                query.whereEqualTo("isPaid", true);
//                query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
//                query.orderByDescending("updatedAt");
//                object11 = query.find();
//
//            } catch (ParseException e) {
//                Log.d("Error", e.getMessage());
//                e.printStackTrace();
//            }
//
//            return object11;
//        }
//
//        @Override
//        protected void onPostExecute(List<ParseObject> result) {
//
//            neworders_hashmap = new ArrayList<HashMap<String, String>>();
//
//            if (result != null) {
//
//                if (result.size() > 0) {
////                m_Runnable.run(result.size());
//
//                    for (ParseObject user : result) {
//
//                        HashMap<String, String> map = new HashMap<String, String>();
//                        map.put("notes", user.getString("notes"));
//                        map.put("total", String.valueOf(user.getNumber("totalCost")));
//                        map.put("subtotal", String.valueOf(user.getNumber("subTotal")));
//                        map.put("orderstatus", String.valueOf(user.getNumber("orderStatus")));
//                        map.put("taxid", user.getString("taxId"));
//                        map.put("objid", user.getObjectId());
//                        map.put("tranRef", user.getString("tranRef"));
//                        map.put("date", user.getCreatedAt().toString());
//                        map.put("objectId", idd);
//                        map.put("discountAmount", String.valueOf(user.getNumber("discountAmount")));
//                        ParseObject offer = user.getParseObject("offerDetails");
//                        map.put("offerEnabled", String.valueOf(user.getBoolean("offerEnabled")));
//                        ParseObject customer = user.getParseObject("user");
//                        ParseObject shop = user.getParseObject("shop");
//                        String name = "", car = "", shop_name = "", shop_phno = "";
//                        try {
//                            if (offer != null) {
//                                map.put("offerDetails", offer.fetchIfNeeded().getObjectId());
//                            }
////                            map.put("offerDetails", offer.fetchIfNeeded().getObjectId());
//                            name = customer.fetchIfNeeded().getString("name");
//                            car = customer.fetchIfNeeded().getString("carDetails");
//                            shop_name = shop.fetchIfNeeded().getString("locationName");
//                            shop_phno = String.valueOf(shop.fetchIfNeeded().getNumber("phoneNo"));
//                            map.put("shop_name", shop_name);
//                            map.put("shop_phno", shop_phno);
//                            String phno = String.valueOf(customer.fetchIfNeeded().getNumber("phoneNumber"));
//                            map.put("tvPhoneNo", phno);
//                            map.put("username", name);
//                            map.put("car", car);
//                            ParseGeoPoint point = customer.getParseGeoPoint("liveLocation");
//                            if (point != null) {
//                                map.put("ulat", String.valueOf(point.getLatitude()));
//                                map.put("ulong", String.valueOf(point.getLongitude()));
//                            }
//                        } catch (ParseException er) {
//                            er.printStackTrace();
//                        }
//                        map.put("userid", user.getString("userString"));
//                        map.put("ispaid", String.valueOf(user.getBoolean("isPaid")));
//                        map.put("refundCost", String.valueOf(user.getNumber("refundCost")));
//                        map.put("cancelleby", user.getString("cancelledBy"));
//                        map.put("refund", user.getString("refund"));
//                        map.put("cancelNote", user.getString("cancelNote"));
//                        map.put("totaltime", String.valueOf(user.getNumber("totalTime")));
//                        map.put("time", String.valueOf(user.getJSONArray("time")));
//                        map.put("pin", getIntent().getStringExtra("pin"));
//                        map.put("shopStatus", shopsts);
//                        map.put("phoneNo", getIntent().getStringExtra("phoneNo"));
//                        map.put("tax", String.valueOf(user.getNumber("tax")));
//                        map.put("order", user.getString("order"));
//                        map.put("currency", user.getString("currency"));
//                        map.put("business", user.getString("Business_name"));
//                        map.put("sname", bname);
//                        map.put("sloc", lname);
//                        map.put("slat", lat);
//                        map.put("slong", logg);
//                        neworders_hashmap.add(map);
//                    }
////                    chec(neworders_hashmap.size());
////                    m_Runnable.run();
////                    restart();
//                    if (neworders_hashmap.size() > 0) {
//                        Intent serviceIntent = new Intent(OrderListActivity.this, Alertservice.class);
//                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//
//                            OrderListActivity.this.startForegroundService(serviceIntent);
//                        } else {
//                            startService(serviceIntent);
//                        }
//                        startService(new Intent(OrderListActivity.this, Alertservice.class));
//                        newOrderRecyclerView.setVisibility(View.VISIBLE);
//                    } else {
//                        stopService(new Intent(OrderListActivity.this, Alertservice.class));
//                        newOrderRecyclerView.setVisibility(View.GONE);
//                    }
//                    if (mNewOrderAdapter != null) {
//                        mNewOrderAdapter.setData(neworders_hashmap);
//                    } else {
//                        mNewOrderAdapter = new NewOrderAdapter(OrderListActivity.this, neworders_hashmap);
//                        newOrderRecyclerView.setAdapter(mNewOrderAdapter);
//                    }
//
//                    String s1 = String.valueOf(neworders_hashmap.size());
//                    txt_neworder.setText("NEW ORDER" + " - " + s1);
//
//                } else {
//                    stopService(new Intent(OrderListActivity.this, Alertservice.class));
////                    restart();
//                    txt_neworder.setText("NEW ORDER" + " - " + neworders_hashmap.size());
//
//                }
//            }
//
//        }
//    }
//
//    private class Getinprogresslist extends AsyncTask<Void, Void, List<ParseObject>> {
//
//        ArrayList<HashMap<String, String>> inprogress_hashmap;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
////            newOrderRecyclerView.getRecycledViewPool().clear();
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
//                ParseObject obj = ParseObject.createWithoutData("ShopLocations", idd);
//                query.whereEqualTo("shop", obj);
//                query.whereEqualTo("isPaid", true);
//                query.whereEqualTo("orderStatus", 1);
//                query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
//                query.orderByDescending("updatedAt");
//                object11 = query.find();
//
//            } catch (ParseException e) {
//                Log.d("Error", e.getMessage());
//                e.printStackTrace();
//            }
//
//            return object11;
//        }
//
//        @Override
//        protected void onPostExecute(List<ParseObject> result) {
//
//            inprogress_hashmap = new ArrayList<HashMap<String, String>>();
//            if (result != null) {
//
//                if (result.size() > 0) {
////                m_Runnable.run(result.size());
//
//                    for (ParseObject user : result) {
//
//                        String sts = String.valueOf(user.getNumber("orderStatus"));
//                        HashMap<String, String> map = new HashMap<String, String>();
//                        map.put("notes", user.getString("notes"));
//                        map.put("total", String.valueOf(user.getNumber("totalCost")));
//                        map.put("subtotal", String.valueOf(user.getNumber("subTotal")));
//                        map.put("orderstatus", String.valueOf(user.getNumber("orderStatus")));
//                        map.put("taxid", user.getString("taxId"));
//                        map.put("objid", user.getObjectId());
//                        map.put("tranRef", user.getString("tranRef"));
//                        map.put("date", user.getCreatedAt().toString());
//                        map.put("objectId", idd);
//                        map.put("discountAmount", String.valueOf(user.getNumber("discountAmount")));
//                        ParseObject offer = user.getParseObject("offerDetails");
//                        map.put("offerEnabled", String.valueOf(user.getBoolean("offerEnabled")));
//                        ParseObject customer = user.getParseObject("user");
//                        ParseObject shop = user.getParseObject("shop");
//                        String name = "", car = "", shop_name = "", shop_phno = "";
//                        try {
//                            if (offer != null) {
//                                map.put("offerDetails", offer.fetchIfNeeded().getObjectId());
//                            }
////                            map.put("offerDetails", offer.fetchIfNeeded().getObjectId());
//                            name = customer.fetchIfNeeded().getString("name");
//                            car = customer.fetchIfNeeded().getString("carDetails");
//                            shop_name = shop.fetchIfNeeded().getString("locationName");
//                            shop_phno = String.valueOf(shop.fetchIfNeeded().getNumber("phoneNo"));
//                            map.put("shop_name", shop_name);
//                            map.put("shop_phno", shop_phno);
//                            String phno = String.valueOf(customer.fetchIfNeeded().getNumber("phoneNumber"));
//                            map.put("tvPhoneNo", phno);
//                            map.put("username", name);
//                            map.put("car", car);
//                            ParseGeoPoint point = customer.getParseGeoPoint("liveLocation");
//                            if (point != null) {
//                                map.put("ulat", String.valueOf(point.getLatitude()));
//                                map.put("ulong", String.valueOf(point.getLongitude()));
//                            }
//                        } catch (ParseException er) {
//                            er.printStackTrace();
//                        }
//                        map.put("userid", user.getString("userString"));
//                        map.put("ispaid", String.valueOf(user.getBoolean("isPaid")));
//                        map.put("refundCost", String.valueOf(user.getNumber("refundCost")));
//                        map.put("cancelleby", user.getString("cancelledBy"));
//                        map.put("refund", user.getString("refund"));
//                        map.put("cancelNote", user.getString("cancelNote"));
//                        map.put("time", String.valueOf(user.getJSONArray("time")));
//                        map.put("pin", getIntent().getStringExtra("pin"));
//                        map.put("shopStatus", shopsts);
//                        map.put("phoneNo", getIntent().getStringExtra("phoneNo"));
//                        map.put("tax", String.valueOf(user.getNumber("tax")));
//                        map.put("order", user.getString("order"));
//                        map.put("currency", user.getString("currency"));
//                        map.put("totaltime", String.valueOf(user.getNumber("totalTime")));
//                        map.put("business", user.getString("Business_name"));
//                        map.put("sname", bname);
//                        map.put("sloc", lname);
//                        map.put("slat", lat);
//                        map.put("slong", logg);
//                        inprogress_hashmap.add(map);
//                    }
//
//                    if (inprogress_hashmap.size() > 0) {
//                        inProgressRecyclerView.setVisibility(View.VISIBLE);
//                    } else {
//                        inProgressRecyclerView.setVisibility(View.GONE);
//                    }
//                    if (mInProgressAdapter != null) {
//                        mInProgressAdapter.setData(inprogress_hashmap);
//                    } else {
//                        mInProgressAdapter = new InProgressAdapter(OrderListActivity.this, inprogress_hashmap);
//                        inProgressRecyclerView.setAdapter(mInProgressAdapter);
//                    }
//                    String s2 = String.valueOf(inprogress_hashmap.size());
//                    txt_inprogress.setText("IN PROGRESS" + " - " + s2);
//
//                } else {
//
//                    txt_inprogress.setText("IN PROGRESS" + " - " + inprogress_hashmap.size());
//                }
//            }
//
//        }
//    }
//
//    private class Getreadyorders_list extends AsyncTask<Void, Void, List<ParseObject>> {
//
//        ArrayList<HashMap<String, String>> reay_hashmap;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
////            newOrderRecyclerView.getRecycledViewPool().clear();
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
//                ParseObject obj = ParseObject.createWithoutData("ShopLocations", idd);
//                query.whereEqualTo("shop", obj);
//                query.whereEqualTo("orderStatus", 2);
//                query.whereEqualTo("isPaid", true);
//                query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
//                query.orderByDescending("updatedAt");
//                object11 = query.find();
//
//            } catch (ParseException e) {
//                Log.d("Error", e.getMessage());
//                e.printStackTrace();
//            }
//
//            return object11;
//        }
//
//        @Override
//        protected void onPostExecute(List<ParseObject> result) {
//
//            reay_hashmap = new ArrayList<HashMap<String, String>>();
//            if (result != null) {
//
//                if (result.size() > 0) {
////                m_Runnable.run(result.size());
//
//                    for (ParseObject user : result) {
//
//                        HashMap<String, String> map = new HashMap<String, String>();
//                        map.put("notes", user.getString("notes"));
////                                   map.put("orderno",String.valueOf(user.getNumber("orderNo")));
//                        map.put("total", String.valueOf(user.getNumber("totalCost")));
//                        map.put("subtotal", String.valueOf(user.getNumber("subTotal")));
//                        map.put("orderstatus", String.valueOf(user.getNumber("orderStatus")));
//                        map.put("taxid", user.getString("taxId"));
//                        map.put("objid", user.getObjectId());
//                        map.put("tranRef", user.getString("tranRef"));
//                        map.put("date", user.getCreatedAt().toString());
//                        map.put("objectId", idd);
//                        map.put("discountAmount", String.valueOf(user.getNumber("discountAmount")));
//                        ParseObject offer = user.getParseObject("offerDetails");
//                        map.put("offerEnabled", String.valueOf(user.getBoolean("offerEnabled")));
//                        ParseObject customer = user.getParseObject("user");
//                        ParseObject shop = user.getParseObject("shop");
//                        String name = "", car = "", shop_name = "", shop_phno = "";
//                        try {
//                            if (offer != null) {
//                                map.put("offerDetails", offer.fetchIfNeeded().getObjectId());
//                            }
////                            map.put("offerDetails", offer.fetchIfNeeded().getObjectId());
//                            name = customer.fetchIfNeeded().getString("name");
//                            car = customer.fetchIfNeeded().getString("carDetails");
//                            shop_name = shop.fetchIfNeeded().getString("locationName");
//                            shop_phno = String.valueOf(shop.fetchIfNeeded().getNumber("phoneNo"));
//                            map.put("shop_name", shop_name);
//                            map.put("shop_phno", shop_phno);
//                            String phno = String.valueOf(customer.fetchIfNeeded().getNumber("phoneNumber"));
//                            map.put("tvPhoneNo", phno);
//                            map.put("username", name);
//                            map.put("car", car);
//                            ParseGeoPoint point = customer.getParseGeoPoint("liveLocation");
//                            if (point != null) {
//                                map.put("ulat", String.valueOf(point.getLatitude()));
//                                map.put("ulong", String.valueOf(point.getLongitude()));
//                            }
//                        } catch (ParseException er) {
//                            er.printStackTrace();
//                        }
//                        map.put("userid", user.getString("userString"));
//                        map.put("ispaid", String.valueOf(user.getBoolean("isPaid")));
//                        map.put("refundCost", String.valueOf(user.getNumber("refundCost")));
//                        map.put("cancelleby", user.getString("cancelledBy"));
//                        map.put("refund", user.getString("refund"));
//                        map.put("cancelNote", user.getString("cancelNote"));
//                        map.put("time", String.valueOf(user.getJSONArray("time")));
//                        map.put("pin", getIntent().getStringExtra("pin"));
//                        map.put("shopStatus", shopsts);
//                        map.put("phoneNo", getIntent().getStringExtra("phoneNo"));
//                        map.put("tax", String.valueOf(user.getNumber("tax")));
//                        map.put("order", user.getString("order"));
//                        map.put("currency", user.getString("currency"));
//                        map.put("totaltime", String.valueOf(user.getNumber("totalTime")));
//                        map.put("business", user.getString("Business_name"));
//                        map.put("sname", bname);
//                        map.put("sloc", lname);
//                        map.put("slat", lat);
//                        map.put("slong", logg);
//                        reay_hashmap.add(map);
//                    }
//
//                    if (reay_hashmap.size() > 0) {
//                        readyRecyclerView.setVisibility(View.VISIBLE);
//                    } else {
//                        readyRecyclerView.setVisibility(View.GONE);
//                    }
//
//                    if (mReadyAdapter != null) {
//                        mReadyAdapter.setData(reay_hashmap);
//                    } else {
//                        mReadyAdapter = new ReadyAdapter(OrderListActivity.this, reay_hashmap);
//                        readyRecyclerView.setAdapter(mReadyAdapter);
//                    }
//
//                    String s3 = String.valueOf(reay_hashmap.size());
//                    txt_ready.setText("READY FOR PICK UP" + " - " + s3);
//
//                } else {
//                    txt_ready.setText("READY FOR PICK UP" + " - " + reay_hashmap.size());
//                }
//            }
//
//        }
//    }
//
//    private JSONObject parseObjectToJson(ParseObject parseObject) throws ParseException, JSONException {
//        JSONObject jsonObject = new JSONObject();
//        parseObject.fetchIfNeeded();
//        Set<String> keys = parseObject.keySet();
//        for (String key : keys) {
//            Object objectValue = parseObject.get(key);
//            if (objectValue instanceof ParseObject) {
//                jsonObject.put(key, parseObjectToJson(parseObject.getParseObject(key)));
//                // keep in mind about "pointer" to it self, will gain stackoverlow
//            } else if (objectValue instanceof ParseRelation) {
//                // handle relation
//            } else {
//                jsonObject.put(key, objectValue.toString());
//            }
//        }
////        Log.d("mShopRecyclerView", "parseObjectToJson:chk "+jsonObject.toString());
//        return jsonObject;
//    }
//
//    @Override
//
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        if (item.getItemId() == android.R.id.home) {
//
//            finish();
//            return true;
//        }
//        return (super.onOptionsItemSelected(item));
//    }
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//
//        finishAffinity();
//        finish();
////        System.exit(0);
//    }
//
//    @Override
//    protected void onDestroy() {
//
//        super.onDestroy();
//        mHandler.removeCallbacks(m_Runnable);
//        try {
//            trimCache(this);
//            // Toast.makeText(this,"onDestroy " ,Toast.LENGTH_LONG).show();
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//
//    public static void trimCache(Context context) {
//        try {
//            File dir = context.getCacheDir();
//            if (dir != null && dir.isDirectory()) {
//                deleteDir(dir);
//            }
//        } catch (Exception e) {
//            // TODO: handle exception
//        }
//    }
//
//    public static boolean deleteDir(File dir) {
//        if (dir != null && dir.isDirectory()) {
//            String[] children = dir.list();
//            for (int i = 0; i < children.length; i++) {
//                boolean success = deleteDir(new File(dir, children[i]));
//                if (!success) {
//                    return false;
//                }
//            }
//        }
//        // The directory is now empty so delete it
//        return dir.delete();
//    }
//
//
//    public void updateNewOrderCount(ArrayList<HashMap<String, String>> data) {
//        String s1 = String.valueOf(data.size());
//        txt_neworder.setText("NEW ORDER" + " - " + s1);
//    }
//
//    public void updateInProgressCount(ArrayList<HashMap<String, String>> data) {
//        String s2 = String.valueOf(data.size());
//        txt_inprogress.setText("IN PROGRESS" + " - " + s2);
//    }
//
//    public void updateReadyCount(ArrayList<HashMap<String, String>> data) {
//        String s3 = String.valueOf(data.size());
//        txt_ready.setText("READY FOR PICK UP" + " - " + s3);
//
//}

        }
            private boolean checkInternetConenction () {
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
            Toast toast = Toasty.error(OrderListActivity.this, msg, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 230);
            toast.show();
        }
    }
}
