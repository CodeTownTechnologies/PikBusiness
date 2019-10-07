package com.pikbusiness.Editmenu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.parse.FindCallback;
import com.pikbusiness.R;
import com.pikbusiness.services.Toasty;
import com.crashlytics.android.Crashlytics;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.MyViewHolder> {

    ArrayList<HashMap<String, String>> dataa;
    Context context;
    String pricee="",sts;
    String shopid;
    HashMap<String, String> mapdata;
    ParseObject user ;
    ArrayList<HashMap<String, String>> checkeditems;
    ArrayList<HashMap<String,String>> newfinalobject;
    public ItemsAdapter(Context context, ArrayList<HashMap<String, String>> aryList) {
        this.context = context;
        this.dataa = aryList;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_menuitem, parent, false);
        return new MyViewHolder(v);
    }
    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1000);
        view.startAnimation(anim);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        // set the data in items
        //  setFadeAnimation(holder.itemView);
        String item_objid = dataa.get(position).get("item_objid");
        String cat_objid = dataa.get(position).get("cat_objid");
        sts = dataa.get(position).get("sts");
        checkeditems = new ArrayList<HashMap<String, String>>();
        mapdata = new HashMap<String, String>();
        SharedPreferences pref = context.getSharedPreferences("Reg", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        shopid = pref.getString("id", null);
        String defaultprice = dataa.get(position).get("defaultPrice");
        if (sts != null) {
            if (sts.equals("true")) {
                holder.cost.setText(defaultprice + "  " + dataa.get(position).get("cost"));
            }
        }

        /*try {
             user = ParseObject.create(dataa.get(position).get("user"));
        }catch (Exception e)
        {
            e.printStackTrace();
            String s=e.toString();
        }*/
        ////

        if (dataa.get(position).get("extras") != null) {

        } else if (dataa.get(position).get("extras").equals("{}") || dataa.get(position).get("extras").isEmpty()) {
            if (dataa.get(position).get("extras") != null) {
                addexinmenus(dataa.get(position).get("objectid"), dataa.get(position).get("extras"));
            }
        }
        pricee = String.valueOf(dataa.get(position).get("price"));
//                            outstock = user.getJSONArray("outOfStock");
        String basic = String.valueOf(dataa.get(position).get("isEnabled"));
//                            String defaultprice = dataa.get(position).get("defaultPrice");
        if (sts != null) {
            if (sts.equals("false")) {
                holder.cost.setText(pricee + "  " + dataa.get(position).get("cost"));
            }
        }
        if (basic.equals("1")) {
            holder.check.setChecked(true);
            holder.it_name.setTextColor(context.getResources().getColor(R.color.black));
            holder.cost.setTextColor(context.getResources().getColor(R.color.black));
        } else {
            holder.it_name.setTextColor(context.getResources().getColor(R.color.lightcolortrans));
            holder.cost.setTextColor(context.getResources().getColor(R.color.lightcolortrans));
            holder.check.setChecked(false);
        }

        holder.check.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                holder.it_name.setTextColor(context.getResources().getColor(R.color.black));
                holder.cost.setTextColor(context.getResources().getColor(R.color.black));
                // for owner
                checkupdate("1", buttonView, dataa.get(position).get("objectid"));
//                                    Log.d("chk", "done:checkox true "+user.getObjectId());

            } else {
                holder.it_name.setTextColor(context.getResources().getColor(R.color.lightcolortrans));
                holder.cost.setTextColor(context.getResources().getColor(R.color.lightcolortrans));
                checkupdate("0", buttonView, dataa.get(position).get("objectid"));
//                                    Log.d("chk", "done:checkox false "+user.getObjectId());
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Log.d("chk", "onClick:itemclick " + user.getObjectId());
            }
        });
        holder.blurlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  Log.d("chk", "onClick:layout " + user.getObjectId());
                if (checkInternetConenction()) {
                    if (holder.check.isChecked()) {
                        Intent i = new Intent(context, Singleitem.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.putExtra("itemid", item_objid);
                        i.putExtra("menusid", dataa.get(position).get("objectid"));
                        i.putExtra("catid", cat_objid);
                        i.putExtra("sts", dataa.get(position).get("sts"));
                        i.putExtra("extras", dataa.get(position).get("extras"));
                        i.putExtra("price", String.valueOf(dataa.get(position).get("price")));
                        i.putExtra("currency", dataa.get(position).get("Currency"));
                        i.putExtra("itemname", dataa.get(position).get("name"));
                        i.putExtra("itemprice", holder.cost.getText().toString());
                        context.startActivity(i);
                    }
                }
            }
        });






        /////



////
////








/*
        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Menus");
        ParseObject obj1 = ParseObject.createWithoutData("Menu",item_objid );
        ParseObject cat = ParseObject.createWithoutData("Category",cat_objid );
        query2.whereEqualTo("categoryPointer", cat);
        query2.setLimit(1000);
        query2.whereEqualTo("menuPointer", obj1);
        query2.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query2.whereEqualTo("businessPointer",ParseUser.getCurrentUser());
        query2.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> object, ParseException e) {

                if (e == null) {

                    if (object.size() > 0) {

                        for (ParseObject user : object) {

                              if(user.getString("extras") != null ){

                              }else if(user.getString("extras").equals("{}" )|| user.getString("extras").isEmpty()){
                                  if(dataa.get(position).get("extras") != null){
                                      addexinmenus(user.getObjectId(),dataa.get(position).get("extras"));
                                  }
                              }
                            pricee = String.valueOf(user.getNumber("price"));
//                            outstock = user.getJSONArray("outOfStock");
                            String basic = String.valueOf(user.getNumber("isEnabled"));
//                            String defaultprice = dataa.get(position).get("defaultPrice");
                            if(sts != null){
                                if(sts.equals("false")){
                                    holder.cost.setText(pricee+"  "+dataa.get(position).get("cost"));
                                }
                            }
                                 if(basic.equals("1")){
                                     holder.check.setChecked(true);
                                     holder.it_name.setTextColor(context.getResources().getColor(R.color.black));
                                     holder.cost.setTextColor(context.getResources().getColor(R.color.black));
                                 }
                                 else
                                     {
                                     holder.it_name.setTextColor(context.getResources().getColor(R.color.lightcolortrans));
                                     holder.cost.setTextColor(context.getResources().getColor(R.color.lightcolortrans));
                                     holder.check.setChecked(false);
                                     }

                            holder.check.setOnCheckedChangeListener((buttonView, isChecked) -> {
                                if(isChecked)
                                {
                                    holder.it_name.setTextColor(context.getResources().getColor(R.color.black));
                                    holder.cost.setTextColor(context.getResources().getColor(R.color.black));
                                     // for owner
                                     checkupdate("1",buttonView,user.getObjectId());
//                                    Log.d("chk", "done:checkox true "+user.getObjectId());

                                }else {
                                    holder.it_name.setTextColor(context.getResources().getColor(R.color.lightcolortrans));
                                    holder.cost.setTextColor(context.getResources().getColor(R.color.lightcolortrans));
                                      checkupdate("0",buttonView,user.getObjectId());
//                                    Log.d("chk", "done:checkox false "+user.getObjectId());
                                }
                            });
                                 holder.itemView.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                         Log.d("chk", "onClick:itemclick "+user.getObjectId());
                                     }
                                 });
                            holder.blurlayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Log.d("chk", "onClick:layout "+user.getObjectId());
                                    if(checkInternetConenction()){
                                          if(holder.check.isChecked()){
                                              Intent i = new Intent(context,Singleitem.class);
                                              i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                              i.putExtra("itemid",item_objid);
                                              i.putExtra("menusid",user.getObjectId());
                                              i.putExtra("catid",cat_objid);
                                              i.putExtra("sts",dataa.get(position).get("sts"));
                                              i.putExtra("extras",user.getString("extras") );
                                              i.putExtra("price",String.valueOf(user.getNumber("price")));
                                              i.putExtra("currency",user.getString("Currency"));
                                              i.putExtra("itemname",dataa.get(position).get("name"));
                                              i.putExtra("itemprice",holder.cost.getText().toString());
                                              context.startActivity(i);
                                          }
                                    }
                                }
                            });
                        }
                    }else
                        {
                        additems(item_objid,cat_objid,dataa.get(position).get("cost")
                       ,dataa.get(position).get("defaultPrice"),dataa.get(position).get("extras"));
                    }
                }
                else{
//                    Log.d("chksz", "getlistdata: additemslast+++++");
                }
            }
        });*/

       if (position>0){
            if (dataa.get(position-1).get("name").equals(dataa.get(position).get("name")))
            {
                holder.main_layout.setVisibility(View.GONE);
            }
            else {
                holder.main_layout.setVisibility(View.VISIBLE);
            }
        }
        else
            {
            holder.main_layout.setVisibility(View.VISIBLE);
        }
        holder.it_name.setText(dataa.get(position).get("name"));


    }

    public void additems(String item_objid, String cat_objid, String cost,
                         String defaultPrice, String extraTypes){

        ParseObject menuss = new ParseObject("Menus");
        menuss.put("isEnabled", 0);
        menuss.put("businessPointer",ParseUser.getCurrentUser());
        menuss.put("menuPointer",ParseObject.createWithoutData("Menu",item_objid ));
        menuss.put("categoryPointer", ParseObject.createWithoutData("Category",cat_objid ));
        menuss.put("menuId",item_objid);
        menuss.put("categoryId", cat_objid);
        menuss.put("Currency", cost);
        menuss.put("price",Long.valueOf(defaultPrice));
        menuss.put("extras",extraTypes);
//        menuss.saveInBackground();
        menuss.saveInBackground(e -> {
            // TODO Auto-generated method stub

            if (e == null) {
//                    Log.d("done", "done:sucess created ");
                // sucess
            } else {
//                Log.d("done", "done:error saving new extras "+e.getMessage());
            }
            Crashlytics.logException(e);
        });
    }
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void addexinmenus(String itemid,String ex){

//    Log.d("chkk", "addexinmenus: "+itemid+"ex = "+ex);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Menus");
        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.getInBackground(itemid, new GetCallback<ParseObject>() {
            public void done(ParseObject shop, ParseException e) {
                if (e == null) {
                    // Now let's update it with some new data.
                    shop.put("extras",ex);
                    shop.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {

                            if (e == null) {

                            }
                            else {

                            }
                        }
                    });
                } else {
                    e.printStackTrace();
                }
            }
        });

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
//        Log.d("chk", "parseObjectToJson: "+jsonObject.toString());
        return jsonObject;
    }
    @Override
    public int getItemCount() {
        return dataa.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView  cost,it_name;
        CheckBox check;
        LinearLayout blurlayout,main_layout;

        public MyViewHolder(View itemView) {
            super(itemView);

            blurlayout = itemView.findViewById(R.id.blurlayout);
            cost = itemView.findViewById(R.id.itemcost);
            check = itemView.findViewById(R.id.checkbox);
            it_name = itemView.findViewById(R.id.it_name);
            main_layout=itemView.findViewById(R.id.main_layout);
        }
    }
    // Checking Internet connection
    private boolean checkInternetConenction() {
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
    private void customToast(String msg){
        Toast toast = Toasty.error(context,msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 230);
        toast.show();
    }
    public void checkupdate(String sts, View v, String objid2){

//        Log.d("chk", "checkUpdate: "+objid2);
        if(ParseUser.getCurrentUser()!= null) {

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Menus");
            query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
            query.getInBackground(objid2, new GetCallback<ParseObject>() {
                public void done(ParseObject shop, ParseException e) {
                    if (e == null) {
                        // Now let's update it with some new data.
                        shop.put("isEnabled", Integer.valueOf(sts));

                        shop.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {

                                if (e == null) {

//                                    Log.d("chk", "done: menus"+shopid);
                                }
                                else {
//                                    Log.d("reschek", "done: menus"+e.getMessage());
                                }
                                Crashlytics.logException(e);
                            }
                        });
                    }
                    else {
                        e.printStackTrace();
                    }
                }
            });

        }else {

        }
    }
}
