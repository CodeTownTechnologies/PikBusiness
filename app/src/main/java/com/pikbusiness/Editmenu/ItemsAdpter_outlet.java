package com.pikbusiness.Editmenu;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.pikbusiness.R;
import com.pikbusiness.services.Toasty;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

public class ItemsAdpter_outlet extends RecyclerView.Adapter<ItemsAdpter_outlet
        .MyViewHolder> {

    ArrayList<HashMap<String, String>> dataa;
    Context context;
    String pin = "";
    String pricee = "", sts;
    String shopid;
    JSONArray outstock = null;
    JSONArray result;
    HashMap<String, String> mapdata;
    ArrayList<HashMap<String, String>> checkeditems;


    public ItemsAdpter_outlet(Context context, ArrayList<HashMap<String, String>> aryList) {
        this.context = context;
        this.dataa = aryList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_menuitem, parent, false);
        return new MyViewHolder(v);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        // set the data in items
        //   setFadeAnimation(holder.itemView);

        sts = dataa.get(position).get("sts");
        holder.cost.setText(dataa.get(position).get("price") + "  " + dataa.get(position).get("currency"));
        checkeditems = new ArrayList<HashMap<String, String>>();
        SharedPreferences pref = context.getSharedPreferences("Reg", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        pin = pref.getString("pin", null);
        shopid = dataa.get(position).get("shopid");
        holder.it_name.setText(dataa.get(position).get("name"));

        try {
            if (dataa.get(position).get("outofstock") != null) {
                 if (dataa.get(position).get("outofstock").equals("null")) {
                    holder.it_name.setTextColor(context.getResources().getColor(R.color.black));
                    holder.cost.setTextColor(context.getResources().getColor(R.color.black));
                    holder.check.setChecked(true);
                } else {
                    JSONArray jsonArray = new JSONArray(dataa.get(position).get("outofstock"));
                    if (jsonArray.length() > 0) {

                        for (int k = 0; k < jsonArray.length(); k++) {

                            if (shopid.equals(jsonArray.get(k))) {
                                holder.it_name.setTextColor(context.getResources().getColor(R.color.lightcolortrans));
                                holder.cost.setTextColor(context.getResources().getColor(R.color.lightcolortrans));
                                holder.check.setChecked(false);
                            } else {
                                holder.it_name.setTextColor(context.getResources().getColor(R.color.black));
                                holder.cost.setTextColor(context.getResources().getColor(R.color.black));
                                holder.check.setChecked(true);
                            }
                        }
                    } else {
                        holder.it_name.setTextColor(context.getResources().getColor(R.color.black));
                        holder.cost.setTextColor(context.getResources().getColor(R.color.black));
                        holder.check.setChecked(true);
                    }
                }

            } else {
                holder.it_name.setTextColor(context.getResources().getColor(R.color.black));
                holder.cost.setTextColor(context.getResources().getColor(R.color.black));
                holder.check.setChecked(true);
            }

        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("chk", "onBindViewHolder:itemclck " + dataa.get(position).get("menus_objectid"));

            }
        });
        holder.check.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                Log.d("chk", "onBindViewHolder:checkbox " + dataa.get(position).get("menus_objectid"));
                holder.it_name.setTextColor(context.getResources().getColor(R.color.black));
                holder.cost.setTextColor(context.getResources().getColor(R.color.black));

                try {
                    if (dataa.get(position).get("outofstock") != null) {

                       if (dataa.get(position).get("outofstock").equals("null")) {
                          // dataa.get(position).put("outofstock",shopid);
                           dataa.get(position).put("outofstock","null");
                           notifyDataSetChanged();
                        //   System.out.println("out of stock==" + String.valueOf(user.getJSONArray("outOfStock")));
                            } else {
                                JSONArray jsonArray = new JSONArray(dataa.get(position).get("outofstock"));
                                if (jsonArray != null) {
                                    if (jsonArray.length() > 0) {
                                        for (int i = 0; i < jsonArray.length(); i++) {
//                                                        Log.d("chk", "onCheckedChanged:remove "+outstock);
                                            if (shopid.equals(jsonArray.get(i))) {
                                                jsonArray.remove(i);
                                                dataa.get(position).put("outofstock","null");
                                               // dataa.get(position).put("outofstock",shopid);
                                                checkupdate(buttonView, dataa.get(position).get("menus_objectid"), jsonArray);

                                            }
                                        }
                                    }
                                }

                            }
                        }

                    } catch(JSONException e1){
                        Crashlytics.logException(e1);
                        e1.printStackTrace();
                    }

                } else{
                    Log.d("chk", "onBindViewHolder:menusid " + dataa.get(position).get("menus_objectid"));
                    holder.it_name.setTextColor(context.getResources().getColor(R.color.lightcolortrans));
                    holder.cost.setTextColor(context.getResources().getColor(R.color.lightcolortrans));
                    try {
                        if (dataa.get(position).get("outofstock") != null) {
                            if (dataa.get(position).get("outofstock").equals("null")) {
                                JSONArray outstock1 = new JSONArray();
                                outstock1.put(shopid);
                                dataa.get(position).put("outofstock",outstock1.toString());
                                checkupdate(buttonView, dataa.get(position).get("menus_objectid"), outstock1);
                            } else {
                                JSONArray jsonArray = new JSONArray(dataa.get(position).get("outofstock"));
                                jsonArray.put(shopid);
                                dataa.get(position).put("outofstock",jsonArray.toString());
                                checkupdate(buttonView, dataa.get(position).get("menus_objectid"), jsonArray);
                            }

                        } else {
                            JSONArray outstock1 = new JSONArray();
                            outstock1.put(shopid);
                            dataa.get(position).put("outofstock",outstock1.toString());
                            checkupdate(buttonView, dataa.get(position).get("menus_objectid"), outstock1);
                        }

                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            });

        }

        private boolean checkInternetConenction () {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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

        public long getItemId ( int position){
            return position;
        }

        @Override
        public int getItemViewType ( int position){
            return position;
        }


        @Override
        public int getItemCount () {
            return dataa.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView cost, it_name;
            CheckBox check;
            LinearLayout blurlayout;

            public MyViewHolder(View itemView) {
                super(itemView);
                this.setIsRecyclable(false);
                blurlayout = itemView.findViewById(R.id.blurlayout);
                cost = itemView.findViewById(R.id.itemcost);
                check = itemView.findViewById(R.id.checkbox);
                it_name = itemView.findViewById(R.id.it_name);
            }
        }
        // Checking Internet connection


    @Override
    public void onViewRecycled(MyViewHolder holder) {
        holder.check.setChecked(false); // - this line do the trick
        super.onViewRecycled(holder);
    }

        private void customToast (String msg){
            Toast toast = Toasty.error(context, msg, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 230);
            toast.show();
        }



        public void checkupdate (View v, String objid2, JSONArray outstock){

            if (ParseUser.getCurrentUser() != null) {

                ParseQuery<ParseObject> query = ParseQuery.getQuery("Menus");
                query.setCachePolicy(ParseQuery.CachePolicy.IGNORE_CACHE);
                query.getInBackground(objid2, new GetCallback<ParseObject>() {
                    public void done(ParseObject shop, ParseException e) {
                        if (e == null) {
                            // Now let's update it with some new data.

                            shop.put("outOfStock", outstock.toString().replace("]","").replace("[",""));
                            System.out.println("out of stock result===" + outstock.toString().replace("]","").replace("[",""));
                            shop.saveInBackground();

                            shop.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {

                                    if (e == null) {
                                        notifyDataSetChanged();

//                                    Log.d("chk", "done: menus"+shopid);
                                    } else {
//                                    Log.d("reschek", "done: menus"+e.getMessage());
                                    }
                                    Crashlytics.logException(e);
                                }
                            });
                        } else {
                            e.printStackTrace();
                        }
                    }
                });

            } else {

            }
        }
    }
