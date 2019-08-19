package com.pikbusiness.Editmenu;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.pikbusiness.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public class Dynamic_tabs_fragment extends Fragment {

    ArrayList<HashMap<String, String>> maplist;
    View view;
    String pin = "";
    Boolean firsttime_login ;
    List<ParseObject> object11;
    @BindView(R.id.editmenu)RecyclerView menulist;
    private  SwipeRefreshLayout refreshLayout;
    private boolean isViewShown = false;
    String cat_objectid,shopid;

    public static Dynamic_tabs_fragment newInstance(int val) {
        Dynamic_tabs_fragment fragment = new Dynamic_tabs_fragment();
        Bundle args = new Bundle();
        args.putInt("someInt", val);
        fragment.setArguments(args);
//        fragment.getFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
        return fragment;
    }

    int val;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.coldcoffee, container, false);
        ButterKnife.bind(view);

         refreshLayout = view.findViewById(R.id.swipe_refresh);
        SharedPreferences pref = this.getActivity().getSharedPreferences("Reg", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        pin = pref.getString("pin", null);
        if(pin != null){
            if(pin.length() >0 ){
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)refreshLayout.getLayoutParams();
                params.setMargins(0, 0, 0, 90);
                refreshLayout.setLayoutParams(params);
                refreshLayout.requestLayout();
            }
        }
//         ct = new Categories();
//        Log.d("chk", "onCreateView:tab "+ct.getCategoryName());
        val = getArguments().getInt("someInt", 0);
         cat_objectid = getArguments().getString("objid");
         shopid = getArguments().getString("id");

        refreshLayout.setColorSchemeColors(ContextCompat.getColor(view.getContext(),R.color.colorPrimary));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isViewShown = true;
                if (isViewShown) {

                    isViewShown = false;
                    if (pin != null) {
                        if (pin.length() > 1) {
                         new GetCategories().execute(cat_objectid);
                        }
                        else {
                            getmenuitems(cat_objectid,shopid);
                        }
                    } else {
                           getmenuitems(cat_objectid,shopid);
                    }
                }
            }
        });
        if (pin != null) {
            if (pin.length() > 1) {

                new GetCategories().execute(cat_objectid);
//                outletmeuitems(cat_objectid,shopid);
            }
            else {
                getmenuitems(cat_objectid,shopid);
            }
        } else {
            getmenuitems(cat_objectid,shopid);
        }
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        isViewShown = true;
        if (isViewShown) {
            isViewShown = false;
            if (pin != null) {
                if (pin.length() > 1) {
                    new GetCategories().execute(cat_objectid);
                }
                else {
                    getmenuitems(cat_objectid,shopid);
                }
            } else {
                getmenuitems(cat_objectid,shopid);
            }
        }
    }


   public void getmenuitems(String cat_objectid,String shopid){
       Log.d("chk", "getmenuitems: ");
       if(ParseUser.getCurrentUser()!= null) {
           ParseQuery<ParseUser> userQueryu=ParseUser.getQuery();
           userQueryu.whereEqualTo("objectId",ParseUser.getCurrentUser().getObjectId());
           userQueryu.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
           userQueryu.findInBackground(new FindCallback<ParseUser>() {
               @Override
               public void done(List<ParseUser> userList, ParseException e) {
                   if (e == null) {
                       //Retrieve user data
                       if (userList.size() > 0) {
                           for (ParseUser user : userList) {

                               firsttime_login = user.getBoolean("defaultPrice");
//                                Log.d("chk", "done: "+firsttime_login);
                           }
                       }
                   }
               }
           });

           ParseQuery<ParseObject> query = ParseQuery.getQuery("Menu");
           ParseObject objectid = ParseObject.createWithoutData("Category",
                   getArguments().getString("objid"));
           query.setLimit(1000);
           Log.d("chk", "getmenuitems: "+getArguments().getString("objid"));
           query.whereEqualTo("category", objectid);
           Log.d("chk", "getmenuitems:cat "+objectid);
           query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
           query.findInBackground(new FindCallback<ParseObject>() {
               public void done(List<ParseObject> object, ParseException e) {
                   if (e == null) {
                       if (refreshLayout.isRefreshing()) {
                           refreshLayout.setRefreshing(false);
                       }
                       maplist = new ArrayList<>();

                       if (object.size() > 0) {
                           for (ParseObject user : object) {
                               HashMap<String, String> map = new HashMap<String, String>();

                               map.put("name",user.getString("itemName"));
                               map.put("cost",user.getString("currency"));
                               map.put("basic",user.getString("basic"));
                               map.put("defaultPrice", String.valueOf(user.getNumber("defaultPrice")));
                               map.put("item_objid",user.getObjectId());
                               map.put("cat_objid",cat_objectid);
                               map.put("extras",user.getString("extras"));
                               map.put("extraTypes", String.valueOf(user.getJSONArray("extraTypes")));
                               map.put("shopid",shopid);
                               if(firsttime_login != null){
                                   map.put("sts", String.valueOf(firsttime_login));
                               }
                               map.put("itemno",String.valueOf(user.getInt("itemNo")));
                               map.put("image", String.valueOf(user.getParseFile("itemPic")));
                               maplist.add(map);
                           }
                           menulist = view.findViewById(R.id.editmenu);
                           if (menulist != null) {

                               if(pin != null){
                                   if(pin.length() >1 ){
                                       Log.d("chk", "done:outlet ");
                                       ItemsAdpter_outlet testAdapter = new ItemsAdpter_outlet(getContext(), maplist);
                                       menulist.setAdapter(testAdapter);
                                   }else{
                                       Log.d("chk", "done:ownerr ");
                                       ItemsAdapter testAdapter = new ItemsAdapter(getContext(), maplist);
                                       menulist.setAdapter(testAdapter);
                                   }
                               } else{
                                   Log.d("chk", "done:owner ");
                                   ItemsAdapter testAdapter = new ItemsAdapter(getContext(), maplist);
                                   menulist.setAdapter(testAdapter);
                               }

                               menulist.setLayoutManager(new LinearLayoutManager(getContext()));
                               menulist.setItemAnimator(new DefaultItemAnimator());
                               menulist.getRecycledViewPool().setMaxRecycledViews(0, 0);
                           }
                           else {
//                                Log.e("Error", "Unable to find recyclerView");
                           }
                       }
                   }
               }
           });
       }else{

       }
   }
    private class GetCategories extends AsyncTask<String, Void, List<ParseObject>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected List<ParseObject> doInBackground(String... params) {

            try {

                ParseQuery<ParseObject> query = ParseQuery.getQuery("Menus");
                ParseObject obj = ParseObject.createWithoutData("Category", params[0]);
                query.whereEqualTo("categoryPointer",obj);
                query.whereEqualTo("isEnabled",1);
                query.whereEqualTo("businessPointer",ParseUser.getCurrentUser());
                query.include("businessPointer");
                query.include("categoryPointer");
                query.include("menuPointer");
                query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
//                query.orderByDescending("updatedAt");
                object11 = query.find();

            } catch (ParseException e) {
                Log.d("Error", e.getMessage());
                e.printStackTrace();
            }

            return object11;
        }

        @Override
        protected void onPostExecute(List<ParseObject> result) {
//            Log.d("chk", "onPostExecute: "+result.size());
            if(result != null) {
                if (refreshLayout.isRefreshing()) {
                    refreshLayout.setRefreshing(false);
                }
                maplist = new ArrayList<>();
                if (result.size() > 0) {

                    for (ParseObject user : result) {
                        String pricee = String.valueOf(user.getNumber("price"));
                        HashMap<String, String> map = new HashMap<String, String>();
                        if (firsttime_login != null) {
                            map.put("sts", String.valueOf(firsttime_login));
                        }
                        map.put("outofstock",String.valueOf(user.getJSONArray("outOfStock")));
                        map.put("isEnabled",String.valueOf(user.getNumber("isEnabled")));
                        map.put("menus_objectid",user.getObjectId());
                        map.put("price",pricee);
                        map.put("shopid",getArguments().getString("id"));
                        map.put("currency",user.getString("Currency"));
                        map.put("extras",user.getString("extras"));
                        map.put("cat_objid",user.getString("categoryId"));
                        map.put("menuid",user.getString("menuId"));

                        ParseObject menuObject = user.getParseObject("menuPointer");
                        String name1 = "",no1 = "",oid1 = "";
                        try {
                            name1 = menuObject.fetchIfNeeded().getString("itemName");
                            map.put("image", String.valueOf(menuObject.fetchIfNeeded().
                                    getParseFile("itemPic")));
                            map.put("name",name1);
                            map.put("item_objid",menuObject.fetchIfNeeded().getObjectId());
                        } catch (ParseException er) {
                            er.printStackTrace();
                        }
                        maplist.add(map);

                    }
                    menulist = view.findViewById(R.id.editmenu);
                    if (menulist != null) {
                        ItemsAdpter_outlet testAdapter = new ItemsAdpter_outlet(getContext(), maplist);
                        menulist.setLayoutManager(new LinearLayoutManager(getContext()));
                        menulist.setItemAnimator(new DefaultItemAnimator());
                        menulist.setAdapter(testAdapter);
                        menulist.getRecycledViewPool().setMaxRecycledViews(0, 0);
                    } else {
//                                Log.e("Error", "Unable to find recyclerView");
                    }

                } else {

                }
            }

        }
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isViewShown){
            if (isVisibleToUser) {
                getFragmentManager().beginTransaction().detach(this).attach(this).commit();
            }
        }
    }
}