package com.pikbusiness;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.pikbusiness.Adapters.InprogressAdapter;
import com.pikbusiness.Adapters.NeworderAdapter;
import com.pikbusiness.Adapters.Readyadapter;
import com.pikbusiness.Editmenu.EditMenutabs;
import com.pikbusiness.Loginmodule.SessionManager;
import com.pikbusiness.services.Alertservice;
import com.pikbusiness.services.Toasty;
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
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

public class Orderslist extends AppCompatActivity {

    @BindView(R.id.neworder)RecyclerView neworders_recyclerView;
    @BindView(R.id.inprogress)RecyclerView inprogress_recyclerView;
    @BindView(R.id.ready)RecyclerView ready_recyclerView;
    @BindView(R.id.shopname)TextView shopname;
    @BindView(R.id.shoploc)TextView shoploc;
    @BindView(R.id.txt_switch)TextView txtswitch;
    @BindView(R.id.switch1) Switch switch1;
    @BindView(R.id.scrollview)NestedScrollView scrollview;
    @BindView(R.id.more) LinearLayout options;
    @BindView(R.id.txt_neworder)TextView txt_neworder;
    @BindView(R.id.txt_inprogress)TextView txt_inprogress;
    @BindView(R.id.txt_ready)TextView txt_ready;
    RecyclerView.LayoutManager l1,l2,l3;
    List<ParseObject> object11;
    private NeworderAdapter testAdapter;
    private InprogressAdapter testAdapter2;
    private Readyadapter testAdapter3;
    private String lname,bname,idd,shopsts,lat,logg,pass;
    private Boolean sts1 = false,sts2 = false,sts3 = false;
    @BindView(R.id.swipe_refresh)SwipeRefreshLayout mSwipeRefreshLayout;
    private DialogPlus dialog;
    private SessionManager session;
    private Handler mHandler;
    private int size1,size2;
//    ArrayList<HashMap<String, String>> maplist1,maplist2,maplist3;
    private AlertDialog alertDialog1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderslist);
        ButterKnife.bind(this);
        Fabric.with(this, new Crashlytics());
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.setCurrentScreen(Orderslist.this, "Orderslist", "ss");
        session = new SessionManager(this);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("Reg", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        pass = pref.getString("Pass", null);
        String chk = pref.getString("lname", null);
        String pinn = pref.getString("pin", null);
        if(pinn != null){
            if (pinn.equals("")) {
                editor.clear();
                editor.apply();
                session.logoutUser();
            }
        }else{
            editor.clear();
            editor.apply();
            session.logoutUser();
        }

        this.mHandler = new Handler();
        txt_ready.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        txt_inprogress.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        txt_neworder.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        txtswitch.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        shopname.setTypeface(Typer.set(this).getFont(Font.ROBOTO_MEDIUM));
        shoploc.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));

        l1 = new LinearLayoutManager(getApplicationContext());
        l2 = new LinearLayoutManager(getApplicationContext());
        l3 = new LinearLayoutManager(getApplicationContext());

        neworders_recyclerView.setNestedScrollingEnabled(false);
        neworders_recyclerView.setHasFixedSize(true);
        neworders_recyclerView.setLayoutManager(l1);
        neworders_recyclerView.setItemAnimator(new DefaultItemAnimator());

        inprogress_recyclerView.setLayoutManager(l2);
        inprogress_recyclerView.setItemAnimator(new DefaultItemAnimator());
        inprogress_recyclerView.setNestedScrollingEnabled(false);
        inprogress_recyclerView.setHasFixedSize(true);

        ready_recyclerView.setLayoutManager(l3);
        ready_recyclerView.setItemAnimator(new DefaultItemAnimator());
        ready_recyclerView.setNestedScrollingEnabled(false);
        ready_recyclerView.setHasFixedSize(true);
        l1 = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        l2 = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        l3 = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

//        Log.d("chk", "done:chkkk =new");
        if (chk != null) {
            lname = pref.getString("lname", null);
            bname = pref.getString("bname", null);
            idd = pref.getString("id", null);
            lat = pref.getString("lat", null);
            logg = pref.getString("log", null);
        } else {
            lname = getIntent().getStringExtra("lname");
            bname = getIntent().getStringExtra("bname");
            idd = getIntent().getStringExtra("id");
        }

        String sts = getIntent().getStringExtra("sts");

        if (sts != null) {
            if (sts.equals("1")) {
                sts2 = true;
                inprogress_recyclerView.setVisibility(View.VISIBLE);
            } else if (sts.equals("2")) {
                sts3 = true;
                ready_recyclerView.setVisibility(View.VISIBLE);
            }
        }
        start();
        String stschk = getIntent().getStringExtra("stschk");

        if(stschk != null){
          if(stschk.equals("0")){
              switch1.setChecked(true);
              txtswitch.setText("Online");
              new Getneworders_list().execute();
              new Getinprogresslist().execute();
              new Getreadyorders_list().execute();

          }else if(stschk.equals("1")){
              switch1.setChecked(true);
              txtswitch.setText("Online");
              new Getneworders_list().execute();
              new Getinprogresslist().execute();
              new Getreadyorders_list().execute();

          }else if(stschk.equals("2")){
              switch1.setChecked(true);
              txtswitch.setText("Online");
              new Getneworders_list().execute();
              new Getinprogresslist().execute();
              new Getreadyorders_list().execute();
          }
          else{

              if(checkInternetConenction()){
                  new Getorderslist().execute();
              }
          }
        }
        else{

            if(checkInternetConenction()){
                initiatedata();
                new Getorderslist().execute();
            }
    }
        shopname.setText(bname);
        shoploc.setText(lname);
        switch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d("chk", "done:shopsts "+shopsts);
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Orders");
                ParseObject obj = ParseObject.createWithoutData("ShopLocations",idd);
                query.whereEqualTo("shop", obj);
                query.whereEqualTo("isPaid",true);
                query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
                query.orderByDescending("updatedAt");
                query.findInBackground((object, e) -> {
                    if (e == null) {
                        if (switch1.isChecked()) {

                            txtswitch.setText("Online");
                            onoff(idd, 1, "You will receive orders now");
                            restart();
                        } else {
                            stop();
                            if(object.size()> 0){
                                restart();
                                offlinepopup();
                                switch1.setChecked(true);
                                txtswitch.setText("Online");
                            }else{

                                txtswitch.setText("Offline");
                                onoff(idd, 0, "You will not receive orders");
                                stop();

                            }
                        }

                    }
                });
//                if (switch1.isChecked()) {

//                        txtswitch.setText("Online");
//                        onoff(idd, 1, "You will receive orders now");
//                } else {
//                    if(sts1){
//                        offlinepopup();
//                        switch1.setChecked(true);
//                        txtswitch.setText("Online");
//                    }else{
//                        txtswitch.setText("Offline");
//                        onoff(idd, 0, "You will not receive orders");
//                    }
//                }
            }
        });
        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = DialogPlus.newDialog(Orderslist.this)
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
                        Intent e = new Intent(Orderslist.this,EditMenutabs.class);
                        e.putExtra("lname",lname);
                        e.putExtra("id", idd);
                        e.putExtra("lat", lat);
                        e.putExtra("log",logg);
                        e.putExtra("pin", getIntent().getStringExtra("pin"));
                        e.putExtra("shopStatus", shopsts);
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
                        if(switch1.isChecked()){
                            dialog.dismiss();
                            Toast.makeText(Orderslist.this, "Please make it Shop is offline", Toast.LENGTH_SHORT).show();
                        }else{
                            apr_msg(v);
                            dialog.dismiss();
                        }

                    }
                });
                dialog.show();
            }
        });

        mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor
                (Orderslist.this,R.color.colorPrimary));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(checkInternetConenction()){
                    Log.d("chk", "done:shopsts "+shopsts);
                    new Getorderslist().execute();
                }

            }
        });

    }
    public void offlinepopup() {

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

                alertDialog1.dismiss();
            }
        });

        dialogBuilder.setView(dialogView);
        alertDialog1 = dialogBuilder.create();
        alertDialog1.setCancelable(true);
        alertDialog1.show();
    }

    private boolean checkInternetConenction() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // test for connection
        if(cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            customToast("Please check the internet connection");
            return false;
        }
    }

    private void customToast(String msg){
        Toast toast = Toasty.error(Orderslist.this,msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 230);
        toast.show();
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
    private final Runnable m_Runnable = new Runnable()
    {
        public void run()

        {
//            Log.d("chk", "running : ");
            if(checkInternetConenction()){
//
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Orders");
                ParseObject obj = ParseObject.createWithoutData("ShopLocations",idd);
                query.whereEqualTo("shop", obj);
                query.whereEqualTo("orderStatus",0);
                query.whereEqualTo("isPaid",true);
                query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
                query.orderByDescending("updatedAt");
                query.findInBackground((object, e) -> {
                    if (e == null) {

                        if(object.size()> 0){

                            new Getneworders_list().execute();
                        }
                    }
                });
//                  new Getorderslist().execute();
            }
            Orderslist.this.mHandler.postDelayed(m_Runnable,5000);
        }

    };

    @Override
    protected void onStop() {
        super.onStop();
//        mHandler.removeCallbacks(m_Runnable);
    }

    public void initiatedata(){
//        Log.d("chk", "initiatedata:check ");
        if(ParseUser.getCurrentUser()!= null) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("ShopLocations");
            query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
            query.getInBackground(idd, new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {

                        shopsts = String.valueOf(object.getNumber("shopStatus"));

                        if (shopsts != null) {
                            if (shopsts.equals("0")) {
                                stop();
                                stopService(new Intent(Orderslist.this, Alertservice.class));
                                switch1.setChecked(false);
                                txtswitch.setText("Offline");
                            } else if (shopsts.equals("1")) {
                                switch1.setChecked(true);
                                txtswitch.setText("Online");
                            } else if(shopsts.equals("2")){
                                stop();
                                stopService(new Intent(Orderslist.this, Alertservice.class));
                                switch1.setChecked(false);
                                txtswitch.setText("Disabled");
                            }
                        }
                        // object will be your game score
                    } else {
                        if(e.getMessage().equals("Invalid session token")){
                            Toast.makeText(Orderslist.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            session.logoutUser();
                        }
                        Crashlytics.logException(e);
                        // something went wrong
                    }
                }
            });
        }else{
            Log.d("chk", "done:no user ");
            session.logoutUser();
        }

    }
    public void apr_msg(View view) {
        LayoutInflater inflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.passwordpopup,null);
        AlertDialog.Builder alertbox = new AlertDialog.Builder(view.getRootView().getContext());
        alertbox.setView(layout);
        Button submit = layout.findViewById(R.id.adsubmit);
        EditText adpass = layout.findViewById(R.id.adpass);
        TextView entertxt = layout.findViewById(R.id.entertxt);
        submit.setTypeface(Typer.set(this).getFont(Font.ROBOTO_MEDIUM));
        adpass.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        entertxt.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        alertbox.setCancelable(true);
        AlertDialog alertDialog = alertbox.create();
        alertDialog.setCancelable(true);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adpass.getText().toString().trim() != null){

                if (pass.equals(adpass.getText().toString().trim())) {
                    stopService(new Intent(Orderslist.this, Alertservice.class));
                    session.Pinlogin("","","", "","","","");
                    saveinstall();
                    alertDialog.dismiss();

                } else {
                    Toast.makeText(Orderslist.this, "Invalid password", Toast.LENGTH_SHORT).show();
                }

            }else{
                Toast.makeText(Orderslist.this, "Enter password", Toast.LENGTH_SHORT).show();
            }
        }
        });
        alertDialog.show();

    }
    public void saveinstall(){
        if(ParseUser.getCurrentUser()!=null) {

            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
            JSONArray ary = installation.getJSONArray("channels");
            if( installation.getJSONArray("channels") != null) {
                if (installation.getJSONArray("channels").length() > 0) {
                    try {

                  for (int i = 0 ; i < ary.length() ;i++){
                     String st = ary.getString(i);

                          if((st.equals("shop_"+idd))){
                              ary.remove(i);
                              installation.put("channels",ary);
                              installation.saveInBackground();

                          }
                  }
                  installation.put("channels",ary);
                  installation.saveInBackground();
                  } catch (JSONException e) {
                            e.printStackTrace();
                        }

                }
            }

        }else {
            checkInternetConenction();
//            session.logoutUser();
        }

        Intent i = new Intent(Orderslist.this,Dashboard.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
    }
    @Override
    protected void onRestart() {
        super.onRestart();
//        m_Runnable.run();
        SharedPreferences pref = getApplicationContext().getSharedPreferences("Reg", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        String pinn = pref.getString("pin", null);
        if(checkInternetConenction()){
//            initiatedata();
            new Getorderslist().execute();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void onoff(String id,int sts,String msg){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ShopLocations");
        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.whereEqualTo("business",ParseUser.getCurrentUser().getObjectId());
        query.getInBackground(id, new GetCallback<ParseObject>() {
            public void done(ParseObject shop, ParseException e) {
                if (e == null) {
                    // Now let's update it with some new data.
                    shop.put("shopStatus",sts);
                    shop.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {

                            if (e == null) {
                                Toast.makeText(Orderslist.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private class Getorderslist extends AsyncTask<Void, Void, List<ParseObject>> {

        ArrayList<HashMap<String, String>>  neworders_hashmap,inprogress_hashmap,reay_hashmap;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            neworders_hashmap = new ArrayList<HashMap<String, String>>();
            inprogress_hashmap = new ArrayList<HashMap<String, String>>();
            reay_hashmap = new ArrayList<HashMap<String, String>>();
        }

        @Override
        protected List<ParseObject> doInBackground(Void... params) {

            // Create the array
//            if(ParseUser.getCurrentUser()!=null) {
            try {

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Orders");
                    ParseObject obj = ParseObject.createWithoutData("ShopLocations", idd);
                    query.whereEqualTo("shop", obj);
                    query.whereEqualTo("isPaid",true);
                    query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
                    query.orderByDescending("updatedAt");
                    object11 = query.find();

            } catch (ParseException e) {
                Log.d("Error", e.getMessage());
                e.printStackTrace();
            }
//            }
//            else {
//                Toast.makeText(Orderslist.this, "Invalid session", Toast.LENGTH_SHORT).show();
//                session.logoutUser();
//                finish();
//            }
            return object11;
        }

        @Override
        protected void onPostExecute(List<ParseObject> result) {
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
//            Log.d("chk", "onPostExecute: "+result);

            if(result != null) {

                if (result.size() > 0) {
//                m_Runnable.run(result.size());

                    for (ParseObject user : result) {

                        String sts = String.valueOf(user.getNumber("orderStatus"));

                        if (sts.equals("0")) {

                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("notes", user.getString("notes"));
                            map.put("total", String.valueOf(user.getNumber("totalCost")));
                            map.put("tranRef", user.getString("tranRef"));
                            map.put("subtotal", String.valueOf(user.getNumber("subTotal")));
                            map.put("orderstatus", String.valueOf(user.getNumber("orderStatus")));
                            map.put("taxid", user.getString("taxId"));
                            map.put("objid", user.getObjectId());
                            map.put("discountAmount", String.valueOf(user.getNumber("discountAmount")));
                            ParseObject offer = user.getParseObject("offerDetails");
                            map.put("offerEnabled",String.valueOf(user.getBoolean("offerEnabled")));
//                            Log.d("chk", "onPostExecute: ");
                            map.put("date", user.getCreatedAt().toString());
                            map.put("id", idd);
                            ParseObject customer = user.getParseObject("user");
                            ParseObject shop = user.getParseObject("shop");
                            String name = "", car = "",shop_name= "",shop_phno = "";

                            try {
                                if(offer != null){
                                    map.put("offerDetails", offer.fetchIfNeeded().getObjectId());
                                }
//                                map.put("offerDetails", offer.fetchIfNeeded().getObjectId());
                                name = customer.fetchIfNeeded().getString("name");
                                car = customer.fetchIfNeeded().getString("carDetails");
                                shop_name = shop.fetchIfNeeded().getString("locationName");
                                shop_phno = String.valueOf(shop.fetchIfNeeded().getNumber("phoneNo"));
                                map.put("shop_name",shop_name);
                                map.put("shop_phno",shop_phno);
                                String customer_phno = String.valueOf(
                                        customer.fetchIfNeeded().getNumber("phoneNumber"));
                                map.put("phno", customer_phno);
                                map.put("username", name);
                                map.put("car", car);
                                ParseGeoPoint point = customer.getParseGeoPoint("liveLocation");
                                if (point != null) {
                                    map.put("ulat", String.valueOf(point.getLatitude()));
                                    map.put("ulong", String.valueOf(point.getLongitude()));
                                }
                            } catch (ParseException er) {
                                er.printStackTrace();
                            }
                            map.put("userid", user.getString("userString"));
                            map.put("ispaid", String.valueOf(user.getBoolean("isPaid")));
                            map.put("refundCost", String.valueOf(user.getNumber("refundCost")));
                            map.put("cancelleby", user.getString("cancelledBy"));
                            map.put("refund", user.getString("refund"));
                            map.put("cancelNote", user.getString("cancelNote"));
                            map.put("totaltime", String.valueOf(user.getNumber("totalTime")));
                            map.put("time", String.valueOf(user.getJSONArray("time")));
                            map.put("pin", getIntent().getStringExtra("pin"));
                            map.put("shopStatus", shopsts);
                            map.put("phoneNo", getIntent().getStringExtra("phoneNo"));
                            map.put("tax", String.valueOf(user.getNumber("tax")));
                            map.put("order", user.getString("order"));
                            map.put("currency", user.getString("currency"));
                            map.put("business", user.getString("Business_name"));
                            map.put("sname", bname);
                            map.put("sloc", lname);
                            map.put("slat", lat);
                            map.put("slong", logg);

                     neworders_hashmap.add(map);

                        } else if (sts.equals("1")) {

                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("notes", user.getString("notes"));
                            map.put("total", String.valueOf(user.getNumber("totalCost")));
                            map.put("subtotal", String.valueOf(user.getNumber("subTotal")));
                            map.put("orderstatus", String.valueOf(user.getNumber("orderStatus")));
                            map.put("taxid", user.getString("taxId"));
                            map.put("tranRef", user.getString("tranRef"));
                            map.put("objid", user.getObjectId());
                            map.put("date", user.getCreatedAt().toString());
                            map.put("id", idd);
                            map.put("discountAmount", String.valueOf(user.getNumber("discountAmount")));
                            ParseObject offer = user.getParseObject("offerDetails");
                            map.put("offerEnabled",String.valueOf(user.getBoolean("offerEnabled")));
                            ParseObject customer = user.getParseObject("user");
                            ParseObject shop = user.getParseObject("shop");
                            String name = "", car = "",shop_name= "",shop_phno = "";
                            try {
                                if(offer != null){
                                    map.put("offerDetails", offer.fetchIfNeeded().getObjectId());
                                }

                                name = customer.fetchIfNeeded().getString("name");
                                car = customer.fetchIfNeeded().getString("carDetails");
                                shop_name = shop.fetchIfNeeded().getString("locationName");
                                shop_phno = String.valueOf(shop.fetchIfNeeded().getNumber("phoneNo"));
                                map.put("shop_name",shop_name);
                                map.put("shop_phno",shop_phno);

                                String phno = String.valueOf(customer.fetchIfNeeded().getNumber("phoneNumber"));
                                map.put("phno", phno);
                                map.put("username", name);
                                map.put("car", car);
                                ParseGeoPoint point = customer.getParseGeoPoint("liveLocation");
                                if (point != null) {
                                    map.put("ulat", String.valueOf(point.getLatitude()));
                                    map.put("ulong", String.valueOf(point.getLongitude()));
                                }
                            } catch (ParseException er) {
                                er.printStackTrace();
                            }
                            map.put("userid", user.getString("userString"));
                            map.put("ispaid", String.valueOf(user.getBoolean("isPaid")));
                            map.put("refundCost", String.valueOf(user.getNumber("refundCost")));
                            map.put("cancelleby", user.getString("cancelledBy"));
                            map.put("refund", user.getString("refund"));
                            map.put("cancelNote", user.getString("cancelNote"));
                            map.put("time", String.valueOf(user.getJSONArray("time")));
                            map.put("pin", getIntent().getStringExtra("pin"));
                            map.put("shopStatus", shopsts);
                            map.put("phoneNo", getIntent().getStringExtra("phoneNo"));
                            map.put("tax", String.valueOf(user.getNumber("tax")));
                            map.put("order", user.getString("order"));
                            map.put("currency", user.getString("currency"));
                            map.put("totaltime", String.valueOf(user.getNumber("totalTime")));
                            map.put("business", user.getString("Business_name"));
                            map.put("sname", bname);
                            map.put("sloc", lname);
                            map.put("slat", lat);
                            map.put("slong", logg);
                             inprogress_hashmap.add(map);
//                                   maplist2.add(map);
                        } else if (sts.equals("2")) {
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("notes", user.getString("notes"));
//                                   map.put("orderno",String.valueOf(user.getNumber("orderNo")));
                            map.put("total", String.valueOf(user.getNumber("totalCost")));
                            map.put("subtotal", String.valueOf(user.getNumber("subTotal")));
                            map.put("orderstatus", String.valueOf(user.getNumber("orderStatus")));
                            map.put("taxid", user.getString("taxId"));
                            map.put("objid", user.getObjectId());
                            map.put("date", user.getCreatedAt().toString());
                            map.put("id", idd);
                            map.put("discountAmount", String.valueOf(user.getNumber("discountAmount")));
                            ParseObject offer = user.getParseObject("offerDetails");
                            map.put("offerEnabled",String.valueOf(user.getBoolean("offerEnabled")));
                            ParseObject customer = user.getParseObject("user");
                            ParseObject shop = user.getParseObject("shop");
                            String name = "", car = "",shop_name= "",shop_phno = "";
                            try {
                                if(offer != null){
                                    map.put("offerDetails", offer.fetchIfNeeded().getObjectId());
                                }
                                name = customer.fetchIfNeeded().getString("name");
                                car = customer.fetchIfNeeded().getString("carDetails");
                                shop_name = shop.fetchIfNeeded().getString("locationName");
                                shop_phno = String.valueOf(shop.fetchIfNeeded().getNumber("phoneNo"));
                                map.put("shop_name",shop_name);
                                map.put("shop_phno",shop_phno);
                                String phno = String.valueOf(customer.fetchIfNeeded().getNumber("phoneNumber"));
                                map.put("phno", phno);
                                map.put("username", name);
                                map.put("car", car);
                                ParseGeoPoint point = customer.getParseGeoPoint("liveLocation");
                                if (point != null) {
                                    map.put("ulat", String.valueOf(point.getLatitude()));
                                    map.put("ulong", String.valueOf(point.getLongitude()));
                                }
                            } catch (ParseException er) {
                                er.printStackTrace();
                            }
                            map.put("userid", user.getString("userString"));
                            map.put("tranRef", user.getString("tranRef"));
                            map.put("ispaid", String.valueOf(user.getBoolean("isPaid")));
                            map.put("refundCost", String.valueOf(user.getNumber("refundCost")));
                            map.put("cancelleby", user.getString("cancelledBy"));
                            map.put("refund", user.getString("refund"));
                            map.put("cancelNote", user.getString("cancelNote"));
                            map.put("time", String.valueOf(user.getJSONArray("time")));
                            map.put("pin", getIntent().getStringExtra("pin"));
                            map.put("shopStatus", shopsts);
                            map.put("phoneNo", getIntent().getStringExtra("phoneNo"));
                            map.put("tax", String.valueOf(user.getNumber("tax")));
                            map.put("order", user.getString("order"));
                            map.put("currency", user.getString("currency"));
                            map.put("totaltime", String.valueOf(user.getNumber("totalTime")));
                            map.put("business", user.getString("Business_name"));
                            map.put("sname", bname);
                            map.put("sloc", lname);
                            map.put("slat", lat);
                            map.put("slong", logg);
                          reay_hashmap.add(map);
                        }
                    }
//                    chec(neworders_hashmap.size());
//                    m_Runnable.run();

                    if (neworders_hashmap.size() > 0) {
                        Intent serviceIntent = new Intent(Orderslist.this, Alertservice.class);
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                            Orderslist.this.startForegroundService(serviceIntent);
                        } else {
                            startService(serviceIntent);
                        }
                        startService(new Intent(Orderslist.this, Alertservice.class));
                        neworders_recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        stopService(new Intent(Orderslist.this, Alertservice.class));
                        neworders_recyclerView.setVisibility(View.GONE);
                    }
                    if (inprogress_hashmap.size() > 0) {
                        inprogress_recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        inprogress_recyclerView.setVisibility(View.GONE);
                    }
                    if (reay_hashmap.size() > 0) {
                        ready_recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        ready_recyclerView.setVisibility(View.GONE);
                    }
//                    if(testAdapter != null){
//                        testAdapter.setData(neworders_hashmap);
//                    }else{
                        testAdapter = new NeworderAdapter(Orderslist.this, neworders_hashmap);
                        neworders_recyclerView.setAdapter(testAdapter);
//                    }

//                    if(testAdapter2 != null){
//                        testAdapter2.setData(inprogress_hashmap);
//                    }else{
                        testAdapter2 = new InprogressAdapter(Orderslist.this, inprogress_hashmap);
                        inprogress_recyclerView.setAdapter(testAdapter2);
//                    }

//                    if(testAdapter3 != null){
//                        testAdapter3.setData(reay_hashmap);
//                    }else{
                        testAdapter3 = new Readyadapter(Orderslist.this, reay_hashmap);
                        ready_recyclerView.setAdapter(testAdapter3);
//                    }

                    String s1 = String.valueOf(neworders_hashmap.size());
                    String s2 = String.valueOf(inprogress_hashmap.size());
                    String s3 = String.valueOf(reay_hashmap.size());
                    txt_neworder.setText("NEW ORDER" + " - " + s1);
                    txt_inprogress.setText("IN PROGRESS" + " - " + s2);
                    txt_ready.setText("READY FOR PICK UP" + " - " + s3);
                    if (neworders_hashmap.size() > 0 || inprogress_hashmap.size() > 0 || reay_hashmap.size() > 0) {
//                        offlinepopup();
                        sts1 = true;
                    } else {
                        sts1 = false;
//                        txtswitch.setText("Offline");
//                        onoff(idd, 0, "You will not receive orders");
                    }

                } else {
                    sts1 = false;
                    txt_neworder.setText("NEW ORDER");
                    txt_inprogress.setText("IN PROGRESS");
                    txt_ready.setText("READY FOR PICK UP");
                }
            }
        }
    }

    private class Getneworders_list extends AsyncTask<Void, Void, List<ParseObject>> {

        ArrayList<HashMap<String, String>>  neworders_hashmap;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            neworders_recyclerView.getRecycledViewPool().clear();
        }

        @Override
        protected List<ParseObject> doInBackground(Void... params) {

            // Create the array
//            if(ParseUser.getCurrentUser()!=null) {
            try {

                ParseQuery<ParseObject> query = ParseQuery.getQuery("Orders");
                ParseObject obj = ParseObject.createWithoutData("ShopLocations", idd);
                query.whereEqualTo("shop", obj);
                query.whereEqualTo("orderStatus",0);
                query.whereEqualTo("isPaid",true);
                query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
                query.orderByDescending("updatedAt");
                object11 = query.find();

            } catch (ParseException e) {
                Log.d("Error", e.getMessage());
                e.printStackTrace();
            }

            return object11;
        }

        @Override
        protected void onPostExecute(List<ParseObject> result) {

            neworders_hashmap = new ArrayList<HashMap<String, String>>();

            if(result != null) {

                if (result.size() > 0) {
//                m_Runnable.run(result.size());

                    for (ParseObject user : result) {

                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("notes", user.getString("notes"));
                            map.put("total", String.valueOf(user.getNumber("totalCost")));
                            map.put("subtotal", String.valueOf(user.getNumber("subTotal")));
                            map.put("orderstatus", String.valueOf(user.getNumber("orderStatus")));
                            map.put("taxid", user.getString("taxId"));
                            map.put("objid", user.getObjectId());
                            map.put("tranRef", user.getString("tranRef"));
                            map.put("date", user.getCreatedAt().toString());
                            map.put("id", idd);
                        map.put("discountAmount", String.valueOf(user.getNumber("discountAmount")));
                        ParseObject offer = user.getParseObject("offerDetails");
                        map.put("offerEnabled",String.valueOf(user.getBoolean("offerEnabled")));
                            ParseObject customer = user.getParseObject("user");
                        ParseObject shop = user.getParseObject("shop");
                        String name = "", car = "",shop_name= "",shop_phno = "";
                        try {
                            if(offer != null){
                                map.put("offerDetails", offer.fetchIfNeeded().getObjectId());
                            }
//                            map.put("offerDetails", offer.fetchIfNeeded().getObjectId());
                            name = customer.fetchIfNeeded().getString("name");
                            car = customer.fetchIfNeeded().getString("carDetails");
                            shop_name = shop.fetchIfNeeded().getString("locationName");
                            shop_phno = String.valueOf(shop.fetchIfNeeded().getNumber("phoneNo"));
                            map.put("shop_name",shop_name);
                            map.put("shop_phno",shop_phno);
                                String phno = String.valueOf(customer.fetchIfNeeded().getNumber("phoneNumber"));
                                map.put("phno", phno);
                                map.put("username", name);
                                map.put("car", car);
                                ParseGeoPoint point = customer.getParseGeoPoint("liveLocation");
                                if (point != null) {
                                    map.put("ulat", String.valueOf(point.getLatitude()));
                                    map.put("ulong", String.valueOf(point.getLongitude()));
                                }
                            } catch (ParseException er) {
                                er.printStackTrace();
                            }
                            map.put("userid", user.getString("userString"));
                            map.put("ispaid", String.valueOf(user.getBoolean("isPaid")));
                            map.put("refundCost", String.valueOf(user.getNumber("refundCost")));
                            map.put("cancelleby", user.getString("cancelledBy"));
                            map.put("refund", user.getString("refund"));
                            map.put("cancelNote", user.getString("cancelNote"));
                            map.put("totaltime", String.valueOf(user.getNumber("totalTime")));
                            map.put("time", String.valueOf(user.getJSONArray("time")));
                            map.put("pin", getIntent().getStringExtra("pin"));
                            map.put("shopStatus", shopsts);
                            map.put("phoneNo", getIntent().getStringExtra("phoneNo"));
                            map.put("tax", String.valueOf(user.getNumber("tax")));
                            map.put("order", user.getString("order"));
                            map.put("currency", user.getString("currency"));
                            map.put("business", user.getString("Business_name"));
                            map.put("sname", bname);
                            map.put("sloc", lname);
                            map.put("slat", lat);
                            map.put("slong", logg);
                        neworders_hashmap.add(map);
                    }
//                    chec(neworders_hashmap.size());
//                    m_Runnable.run();
//                    restart();
                    if (neworders_hashmap.size() > 0) {
                        Intent serviceIntent = new Intent(Orderslist.this, Alertservice.class);
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                            Orderslist.this.startForegroundService(serviceIntent);
                        } else {
                            startService(serviceIntent);
                        }
                        startService(new Intent(Orderslist.this, Alertservice.class));
                        neworders_recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        stopService(new Intent(Orderslist.this, Alertservice.class));
                        neworders_recyclerView.setVisibility(View.GONE);
                    }
                   if(testAdapter != null){
                       testAdapter.setData(neworders_hashmap);
                   }else{
                       testAdapter = new NeworderAdapter(Orderslist.this, neworders_hashmap);
                       neworders_recyclerView.setAdapter(testAdapter);
                   }

                    String s1 = String.valueOf(neworders_hashmap.size());
                    txt_neworder.setText("NEW ORDER" + " - " + s1);

                }else {
                    stopService(new Intent(Orderslist.this, Alertservice.class));
//                    restart();
                    txt_neworder.setText("NEW ORDER" + " - " + neworders_hashmap.size());

                }
            }

        }
    }
    private class Getinprogresslist extends AsyncTask<Void, Void, List<ParseObject>> {

        ArrayList<HashMap<String, String>>  inprogress_hashmap;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            neworders_recyclerView.getRecycledViewPool().clear();
        }

        @Override
        protected List<ParseObject> doInBackground(Void... params) {

            // Create the array
//            if(ParseUser.getCurrentUser()!=null) {
            try {

                ParseQuery<ParseObject> query = ParseQuery.getQuery("Orders");
                ParseObject obj = ParseObject.createWithoutData("ShopLocations", idd);
                query.whereEqualTo("shop", obj);
                query.whereEqualTo("isPaid",true);
                query.whereEqualTo("orderStatus",1);
                query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
                query.orderByDescending("updatedAt");
                object11 = query.find();

            } catch (ParseException e) {
                Log.d("Error", e.getMessage());
                e.printStackTrace();
            }

            return object11;
        }

        @Override
        protected void onPostExecute(List<ParseObject> result) {

            inprogress_hashmap = new ArrayList<HashMap<String, String>>();
            if(result != null) {

                if (result.size() > 0) {
//                m_Runnable.run(result.size());

                    for (ParseObject user : result) {

                        String sts = String.valueOf(user.getNumber("orderStatus"));
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("notes", user.getString("notes"));
                            map.put("total", String.valueOf(user.getNumber("totalCost")));
                            map.put("subtotal", String.valueOf(user.getNumber("subTotal")));
                            map.put("orderstatus", String.valueOf(user.getNumber("orderStatus")));
                            map.put("taxid", user.getString("taxId"));
                            map.put("objid", user.getObjectId());
                        map.put("tranRef", user.getString("tranRef"));
                            map.put("date", user.getCreatedAt().toString());
                            map.put("id", idd);
                        map.put("discountAmount", String.valueOf(user.getNumber("discountAmount")));
                        ParseObject offer = user.getParseObject("offerDetails");
                        map.put("offerEnabled",String.valueOf(user.getBoolean("offerEnabled")));
                            ParseObject customer = user.getParseObject("user");
                        ParseObject shop = user.getParseObject("shop");
                        String name = "", car = "",shop_name= "",shop_phno = "";
                        try {
                            if(offer != null){
                                map.put("offerDetails", offer.fetchIfNeeded().getObjectId());
                            }
//                            map.put("offerDetails", offer.fetchIfNeeded().getObjectId());
                            name = customer.fetchIfNeeded().getString("name");
                            car = customer.fetchIfNeeded().getString("carDetails");
                            shop_name = shop.fetchIfNeeded().getString("locationName");
                            shop_phno = String.valueOf(shop.fetchIfNeeded().getNumber("phoneNo"));
                            map.put("shop_name",shop_name);
                            map.put("shop_phno",shop_phno);
                                String phno = String.valueOf(customer.fetchIfNeeded().getNumber("phoneNumber"));
                                map.put("phno", phno);
                                map.put("username", name);
                                map.put("car", car);
                                ParseGeoPoint point = customer.getParseGeoPoint("liveLocation");
                                if (point != null) {
                                    map.put("ulat", String.valueOf(point.getLatitude()));
                                    map.put("ulong", String.valueOf(point.getLongitude()));
                                }
                            } catch (ParseException er) {
                                er.printStackTrace();
                            }
                            map.put("userid", user.getString("userString"));
                            map.put("ispaid", String.valueOf(user.getBoolean("isPaid")));
                            map.put("refundCost", String.valueOf(user.getNumber("refundCost")));
                            map.put("cancelleby", user.getString("cancelledBy"));
                            map.put("refund", user.getString("refund"));
                            map.put("cancelNote", user.getString("cancelNote"));
                            map.put("time", String.valueOf(user.getJSONArray("time")));
                            map.put("pin", getIntent().getStringExtra("pin"));
                            map.put("shopStatus", shopsts);
                            map.put("phoneNo", getIntent().getStringExtra("phoneNo"));
                            map.put("tax", String.valueOf(user.getNumber("tax")));
                            map.put("order", user.getString("order"));
                            map.put("currency", user.getString("currency"));
                            map.put("totaltime", String.valueOf(user.getNumber("totalTime")));
                            map.put("business", user.getString("Business_name"));
                            map.put("sname", bname);
                            map.put("sloc", lname);
                            map.put("slat", lat);
                            map.put("slong", logg);
                            inprogress_hashmap.add(map);
                    }

                    if (inprogress_hashmap.size() > 0) {
                        inprogress_recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        inprogress_recyclerView.setVisibility(View.GONE);
                    }
                    if(testAdapter2 != null){
                        testAdapter2.setData(inprogress_hashmap);
                    }else{
                        testAdapter2 = new InprogressAdapter(Orderslist.this, inprogress_hashmap);
                        inprogress_recyclerView.setAdapter(testAdapter2);
                    }
                    String s2 = String.valueOf(inprogress_hashmap.size());
                    txt_inprogress.setText("IN PROGRESS" + " - " + s2);

                } else {

                    txt_inprogress.setText("IN PROGRESS" + " - " + inprogress_hashmap.size());
                }
            }

        }
    }
    private class Getreadyorders_list extends AsyncTask<Void, Void, List<ParseObject>> {

        ArrayList<HashMap<String, String>>  reay_hashmap;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            neworders_recyclerView.getRecycledViewPool().clear();
        }

        @Override
        protected List<ParseObject> doInBackground(Void... params) {

            // Create the array
//            if(ParseUser.getCurrentUser()!=null) {
            try {

                ParseQuery<ParseObject> query = ParseQuery.getQuery("Orders");
                ParseObject obj = ParseObject.createWithoutData("ShopLocations", idd);
                query.whereEqualTo("shop", obj);
                query.whereEqualTo("orderStatus",2);
                query.whereEqualTo("isPaid",true);
                query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
                query.orderByDescending("updatedAt");
                object11 = query.find();

            } catch (ParseException e) {
                Log.d("Error", e.getMessage());
                e.printStackTrace();
            }

            return object11;
        }

        @Override
        protected void onPostExecute(List<ParseObject> result) {

            reay_hashmap = new ArrayList<HashMap<String, String>>();
            if(result != null) {

                if (result.size() > 0) {
//                m_Runnable.run(result.size());

                    for (ParseObject user : result) {

                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("notes", user.getString("notes"));
//                                   map.put("orderno",String.valueOf(user.getNumber("orderNo")));
                            map.put("total", String.valueOf(user.getNumber("totalCost")));
                            map.put("subtotal", String.valueOf(user.getNumber("subTotal")));
                            map.put("orderstatus", String.valueOf(user.getNumber("orderStatus")));
                            map.put("taxid", user.getString("taxId"));
                            map.put("objid", user.getObjectId());
                        map.put("tranRef", user.getString("tranRef"));
                            map.put("date", user.getCreatedAt().toString());
                            map.put("id", idd);
                        map.put("discountAmount", String.valueOf(user.getNumber("discountAmount")));
                        ParseObject offer = user.getParseObject("offerDetails");
                        map.put("offerEnabled",String.valueOf(user.getBoolean("offerEnabled")));
                            ParseObject customer = user.getParseObject("user");
                        ParseObject shop = user.getParseObject("shop");
                        String name = "", car = "",shop_name= "",shop_phno = "";
                        try {
                            if(offer != null){
                                map.put("offerDetails", offer.fetchIfNeeded().getObjectId());
                            }
//                            map.put("offerDetails", offer.fetchIfNeeded().getObjectId());
                            name = customer.fetchIfNeeded().getString("name");
                            car = customer.fetchIfNeeded().getString("carDetails");
                            shop_name = shop.fetchIfNeeded().getString("locationName");
                            shop_phno = String.valueOf(shop.fetchIfNeeded().getNumber("phoneNo"));
                            map.put("shop_name",shop_name);
                            map.put("shop_phno",shop_phno);
                                String phno = String.valueOf(customer.fetchIfNeeded().getNumber("phoneNumber"));
                                map.put("phno", phno);
                                map.put("username", name);
                                map.put("car", car);
                                ParseGeoPoint point = customer.getParseGeoPoint("liveLocation");
                                if (point != null) {
                                    map.put("ulat", String.valueOf(point.getLatitude()));
                                    map.put("ulong", String.valueOf(point.getLongitude()));
                                }
                            } catch (ParseException er) {
                                er.printStackTrace();
                            }
                            map.put("userid", user.getString("userString"));
                            map.put("ispaid", String.valueOf(user.getBoolean("isPaid")));
                            map.put("refundCost", String.valueOf(user.getNumber("refundCost")));
                            map.put("cancelleby", user.getString("cancelledBy"));
                            map.put("refund", user.getString("refund"));
                            map.put("cancelNote", user.getString("cancelNote"));
                            map.put("time", String.valueOf(user.getJSONArray("time")));
                            map.put("pin", getIntent().getStringExtra("pin"));
                            map.put("shopStatus", shopsts);
                            map.put("phoneNo", getIntent().getStringExtra("phoneNo"));
                            map.put("tax", String.valueOf(user.getNumber("tax")));
                            map.put("order", user.getString("order"));
                            map.put("currency", user.getString("currency"));
                            map.put("totaltime", String.valueOf(user.getNumber("totalTime")));
                            map.put("business", user.getString("Business_name"));
                            map.put("sname", bname);
                            map.put("sloc", lname);
                            map.put("slat", lat);
                            map.put("slong", logg);
                          reay_hashmap.add(map);
                    }

                    if (reay_hashmap.size() > 0) {
                        ready_recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        ready_recyclerView.setVisibility(View.GONE);
                    }

                    if(testAdapter3 != null){
                        testAdapter3.setData(reay_hashmap);
                    }else{
                        testAdapter3 = new Readyadapter(Orderslist.this, reay_hashmap);
                        ready_recyclerView.setAdapter(testAdapter3);
                    }

                    String s3 = String.valueOf(reay_hashmap.size());
                    txt_ready.setText("READY FOR PICK UP" + " - " + s3);

                } else {
                    txt_ready.setText("READY FOR PICK UP" + " - " + reay_hashmap.size());
                }
            }

        }
    }

    private JSONObject parseObjectToJson(ParseObject parseObject) throws ParseException, JSONException {
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
//        Log.d("list", "parseObjectToJson:chk "+jsonObject.toString());
        return jsonObject;
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
    @Override
    protected void onDestroy() {

        super.onDestroy();
        mHandler.removeCallbacks(m_Runnable);
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
}
