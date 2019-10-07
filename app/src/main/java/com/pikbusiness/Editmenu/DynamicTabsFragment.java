package com.pikbusiness.Editmenu;

import android.content.Intent;
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
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
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

public class DynamicTabsFragment extends Fragment {

    ArrayList<HashMap<String, String>> maplist;
    View view;
    String pin = "";
    Boolean firsttime_login ;
    List<ParseObject> object11;
    @BindView(R.id.editmenu)RecyclerView menulist;
    private  SwipeRefreshLayout refreshLayout;
    private boolean isViewShown = false;
    String cat_objectid,shopid;
    LinearLayout ll_progressBar;
    int apicount=0;

    public static DynamicTabsFragment newInstance(int val)
    {
        DynamicTabsFragment fragment = new DynamicTabsFragment();
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
        ll_progressBar=view.findViewById(R.id.ll_progressBar);
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
                    ll_progressBar.setVisibility(View.VISIBLE);
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
            if (pin.length() > 1)
            {

                new GetCategories().execute(cat_objectid);
//                outletmeuitems(cat_objectid,shopid);
            }
            else {

                getmenuitems(cat_objectid,shopid);
            }
        }
        else
        {

            getmenuitems(cat_objectid,shopid);
        }






        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
       /*   isViewShown = true;


        if (isViewShown) {
            isViewShown = false;
            if (pin != null) {
                if (pin.length() > 1) {
                    new GetCategories().execute(cat_objectid);
                }
                else {
                    getmenuitems(cat_objectid,shopid);
                }
            }
             else {
                getmenuitems(cat_objectid,shopid);
            }
        }*/
    }


    public void getmenuitems(String cat_objectid,String shopid){
        ll_progressBar.setVisibility(View.VISIBLE);

        ArrayList<HashMap<String,String>> parsearray = new ArrayList<>();
        Log.d("chk", "getmenuitems: ");

        if(ParseUser.getCurrentUser()!= null) {
            ParseQuery<ParseUser> userQueryu = ParseUser.getQuery();
            userQueryu.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
            userQueryu.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
            userQueryu.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> userList, ParseException e) {
                    if (e == null) {
                        //Retrieve user data
                        if (userList.size() > 0) {
                            for (ParseUser user : userList) {

                                firsttime_login = user.getBoolean("defaultPrice");
                                if (firsttime_login!=null){
                                    break;
                                }
                                Log.v("FIRSTTIMEUSE", String.valueOf(firsttime_login));
//                                Log.d("chk", "done: "+firsttime_login);
                            }


                            if (firsttime_login != null) {
                                ParseQuery<ParseObject> query = ParseQuery.getQuery("Menu");
                                ParseObject objectid = ParseObject.createWithoutData("Category",
                                        getArguments().getString("objid"));
                                query.setLimit(1000);
//           Log.d("chk", "getmenuitems: "+getArguments().getString("objid"));
                                query.whereEqualTo("category", objectid);
//           Log.d("chk", "getmenuitems:cat "+objectid);

                                query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
                                query.findInBackground(new FindCallback<ParseObject>() {
                                    public void done(List<ParseObject> object, ParseException e) {
                                        if (e == null) {
                                            if (refreshLayout.isRefreshing()) {
                                                refreshLayout.setRefreshing(false);
                                            }
                                            //  ll_progressBar.setVisibility(View.GONE);


                                            if (object.size() > 0) {
                                                maplist = new ArrayList<>();
                                                for (ParseObject user : object) {
                                                    HashMap<String, String> map = new HashMap<String, String>();
                                                    HashMap<String, String> map_sec = new HashMap<String, String>();
                                                    final int[] count = {0};

                                                    maplist.clear();
                                                    map.clear();
                                                    String user_objectid = null;
                                                    String cat_objid = null;
                                                    String cost=null;
                                                    String defaultpeice=null;
                                                    String extra=null;
                                                    map.put("name", user.getString("itemName"));

                                                    map.put("cost", user.getString("currency"));
                                                    cost=user.getString("currency");
                                                    map.put("basic", user.getString("basic"));
                                                    map.put("defaultPrice", String.valueOf(user.getNumber("defaultPrice")));
                                                    defaultpeice=String.valueOf(user.getNumber("defaultPrice"));
                                                    Log.d("menuname", user.getString("itemName") + " " + String.valueOf(user.getNumber("defaultPrice")) + " " + String.valueOf(firsttime_login));
                                                    map.put("item_objid", user.getObjectId());
                                                    user_objectid = user.getObjectId();
                                                    cat_objid = cat_objectid;
                                                    map.put("cat_objid", cat_objid);
                                                    map.put("extras", user.getString("extras"));
                                                    extra=user.getString("extras");
                                                    map.put("extraTypes", String.valueOf(user.getJSONArray("extraTypes")));
                                                    map.put("shopid", shopid);
                                                    if (firsttime_login != null) {
                                                        map.put("sts", String.valueOf(firsttime_login));
                                                    }
                                                    map.put("itemno", String.valueOf(user.getInt("itemNo")));
                                                    map.put("image", String.valueOf(user.getParseFile("itemPic")));

                                                    ///////
                                                    ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Menus");
                                                    ParseObject obj1 = ParseObject.createWithoutData("Menu",user_objectid);
                                                    ParseObject cat = ParseObject.createWithoutData("Category", cat_objectid);
                                                    query2.whereEqualTo("categoryPointer", cat);
                                                    query2.setLimit(1000);
                                                    query2.whereEqualTo("menuPointer", obj1);
                                                    query2.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
                                                    query2.whereEqualTo("businessPointer", ParseUser.getCurrentUser());
                                                    String finalUser_objectid = user_objectid;
                                                    String finalCat_objid = cat_objid;
                                                    String finalCost = cost;
                                                    String finalDefaultpeice = defaultpeice;
                                                    String finalExtra = extra;
                                                    String finalUser_objectid1 = user_objectid;
                                                    String finalCat_objid1 = cat_objid;
                                                    String finalCost1 = cost;
                                                    String finalDefaultpeice1 = defaultpeice;
                                                    String finalExtra1 = extra;
                                                    query2.findInBackground(new FindCallback<ParseObject>() {
                                                        public void done(List<ParseObject> object, ParseException e) {

                                                            if (e == null) {

                                                                if (object.size() > 0) {

                                                                    for (ParseObject user : object) {


                                                                        map.put("extras", user.getString("extras"));
                                                                        map.put("Currency", user.getString("Currency"));
                                                                        map.put("price", String.valueOf(user.getNumber("price")));
                                                                        Log.d("ITEMPRICE", String.valueOf(user.getNumber("price")));
                                                                        map.put("isEnabled", String.valueOf(user.getNumber("isEnabled")));
                                                                        map.put("objectid", user.getObjectId());


                                                                        maplist.add(map);

                                                                        menulist = view.findViewById(R.id.editmenu);


                                                                        if (menulist != null) {
                                                                            if (pin != null) {
                                                                                if (pin.length() > 1) {
                                                                                    Log.d("chk", "done:outlet ");
                                                                                    ItemsAdpter_outlet testAdapter = new ItemsAdpter_outlet(getContext(), maplist);
                                                                                    menulist.setAdapter(testAdapter);
                                                                                } else {
                                                                                    ArrayList<HashMap<String, String>> parsearray = new ArrayList<>();

                                                                                    ll_progressBar.setVisibility(View.GONE);
                                                                                    ItemsAdapter testAdapter = new ItemsAdapter(getContext(), maplist);
                                                                                    menulist.setAdapter(testAdapter);
                                                                                    menulist.setLayoutManager(new LinearLayoutManager(getContext()));
                                                                                    menulist.setItemAnimator(new DefaultItemAnimator());
                                                                                    menulist.getRecycledViewPool().setMaxRecycledViews(0, 0);
                                                                                    ll_progressBar.setVisibility(View.GONE);

                                                                                }
                                                                            }
                                                                            else {
                                                                                boolean show = false;
                                                                                Log.d("chk", "done:owner ");

                                                                                /////////

                                                                                ll_progressBar.setVisibility(View.GONE);
                                                                                ItemsAdapter testAdapter = new ItemsAdapter(getContext(), maplist);
                                                                                menulist.setAdapter(testAdapter);
                                                                                // maplist.clear();


                                                                                //        }
                                                                                //      }
                                                                                //    }
                                                                                //  });


                                                                                ////////


                                                                            }
                                                                            menulist.setLayoutManager(new LinearLayoutManager(getContext()));
                                                                            menulist.setItemAnimator(new DefaultItemAnimator());
                                                                            menulist.getRecycledViewPool().setMaxRecycledViews(0, 0);
                                                                            ll_progressBar.setVisibility(View.GONE);

                                                                        } else {
                                                                            ll_progressBar.setVisibility(View.GONE);
                                                                        }
                                                                    }
                                                                }
                                                                else {
                                                                    //ll_progressBar.setVisibility(View.GONE);
                                                                    // isViewShown=true;
                                                                    additems(finalUser_objectid1, finalCat_objid1, finalCost1
                                                                            , finalDefaultpeice1, finalExtra1);
                                                                    if (apicount==0) {
                                                                        getmenuitems(cat_objectid, shopid);
                                                                        apicount=1;
                                                                    }//Toast.makeText(getActivity(), "NO DATA FOUND", Toast.LENGTH_SHORT).show();
                                                                }
                                                            } else {
//                    Log.d("chksz", "getlistdata: additemslast+++++");
                                                            }
                                                        }
                                                    });
                                                    //////


                                                }

                                            }
                                            else
                                            {
                                                ll_progressBar.setVisibility(View.GONE);

                                            }
                                        }
                                    }
                                });
                                ///////

                            } else {
                                ll_progressBar.setVisibility(View.GONE);
//                            Log.e("Error", "Unable to find recyclerView");
                            }
                        }
                        else {
                            ll_progressBar.setVisibility(View.GONE);
                        }
                        ////////


                    }
                }

            });

        }
        else {
            ll_progressBar.setVisibility(View.GONE);
        }

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
        isViewShown=false;
    }




    private class GetCategories extends AsyncTask<String, Void, List<ParseObject>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //ll_progressBar.setVisibility(View.VISIBLE);
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
                    }
                    else
                    {
//                                Log.e("Error", "Unable to find recyclerView");
                    }

                }
                else {

                }
            }
            ll_progressBar.setVisibility(View.GONE);

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