package com.pikbusiness.Editmenu;

import com.crashlytics.android.Crashlytics;
import com.elmargomez.typer.Font;
import com.elmargomez.typer.Typer;
import com.google.android.material.tabs.TabLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.pikbusiness.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;
import static android.text.Html.fromHtml;

public class EditMenuTabsActivity extends AppCompatActivity {


    @BindView(R.id.progressBar)ProgressBar pDialog;
    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.viewpager)ViewPager viewPager;
    @BindView(R.id.tabs)TabLayout tabLayout;
    @BindView(R.id.savebtn)
    Button savebtn;
    ArrayList<String> catnameslist,catnolist,catidslist,pricelist,itemnames,
            sorted_cat_names,sorted_cat_objids;
    String pin = "";
    ArrayList<HashMap<Integer,String>> cat_names,cat_objids;

    List<ParseObject> object11;
    int i = 0;
    Boolean chk = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_menu);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        Fabric.with(this, new Crashlytics());
        SharedPreferences pref = getApplicationContext().getSharedPreferences("Reg", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        pin = pref.getString("pin", null);
        savebtn.setTypeface(Typer.set(this).getFont(Font.ROBOTO_MEDIUM));
        catnameslist = new ArrayList<String>();
        catnolist = new ArrayList<String>();
        pricelist = new ArrayList<String>();
        catidslist = new ArrayList<String>();
        itemnames = new ArrayList<String>();
        sorted_cat_names = new ArrayList<String>();
        sorted_cat_objids = new ArrayList<String>();
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setHomeButtonEnabled(true);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        TextView txt = new TextView(EditMenuTabsActivity.this);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        txt.setLayoutParams(lp);
        txt.setText("Edit Menu");
        txt.setMaxLines(1);
        txt.setTextSize(18);
        txt.setTextColor(ContextCompat.getColor(this,R.color.white));
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        actionBar.setCustomView(txt);

        savebtn.setVisibility(View.GONE);
        if(pin != null){
            if(pin.length() >0 ){
                new GetCategories().execute();
                savebtn.setVisibility(View.VISIBLE);
                savebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        finish();
//                    Intent i = new Intent(EditMenuTabsActivity.this,OrderListActivity.class);
//                    startActivity(i);
                    }
                });
            }else{
//                new GetCategories().execute();
                GetCategories();
                savebtn.setVisibility(View.GONE);
            }

        }else{
//            new GetCategories().execute();
            GetCategories();
            savebtn.setVisibility(View.GONE);
        }


        intiateview();
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this,R.color.white));
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    public void intiateview(){
        int defaultValue = 0;
        int page = getIntent().getIntExtra("TAB", defaultValue);
        viewPager.setCurrentItem(page);

        PlansPagerAdapter adapter = new PlansPagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
//        viewPager.setOffscreenPageLimit(0);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public class PlansPagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;
        public PlansPagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {
            Bundle b = new Bundle();
            b.putInt("position", position);
            if (pin != null) {
                if (pin.length() > 1) {
                    b.putString("objid",sorted_cat_objids.get(position));
                }
                else {
                    b.putString("objid",catidslist.get(position));
                }
            } else {
                b.putString("objid",catidslist.get(position));
               }

            b.putString("catno",catnolist.get(position));
            b.putString("id",getIntent().getStringExtra("id"));
            Fragment frag = DynamicTabsFragment.newInstance(position);
            frag.setArguments(b);
            return frag;
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }
    private class GetCategories extends AsyncTask<Void, Void, List<ParseObject>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            catnameslist = new ArrayList<String>();
            catnolist = new ArrayList<String>();
            catidslist = new ArrayList<String>();
        }

        @Override
        protected List<ParseObject> doInBackground(Void... params) {

            // Create the array
//            if(ParseUser.getCurrentUser()!=null) {
            try {

                ParseQuery<ParseObject> query = ParseQuery.getQuery("Menus");
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
            if(result != null) {

                cat_objids = new ArrayList<HashMap<Integer,String>>();
                cat_names = new ArrayList<HashMap<Integer,String>>();

                if (result.size() > 0) {

                    for (ParseObject user : result) {
//                        String pricee = String.valueOf(user.getNumber("price"));
                        ParseObject categoryObject = user.getParseObject("categoryPointer");
                        String name = "",no = "",oid = "";
                        try {
                            name = categoryObject.fetchIfNeeded().getString("categoryName");
                            no = categoryObject.fetchIfNeeded().getString("categoryNo");
                            oid = categoryObject.getObjectId();

                             catnameslist.add(name);
                             catnolist.add(no);
                             catidslist.add(oid);

                        } catch (ParseException er) {
                            er.printStackTrace();
                        }

                    }

                    if(catnameslist.size()> 0){

                        // sorting items without duplicates in list
                        Object[] st = catnameslist.toArray();
                        for (Object s : st) {
                            if (catnameslist.indexOf(s) != catnameslist.lastIndexOf(s)) {
                                catnameslist.remove(catnameslist.lastIndexOf(s));
                            }
                        }
                        Object[] st2 = catnolist.toArray();
                        for (Object s : st2) {
                            if (catnolist.indexOf(s) != catnolist.lastIndexOf(s)) {
                                catnolist.remove(catnolist.lastIndexOf(s));
                            }
                        }
                        Object[] st3 = catidslist.toArray();
                        for (Object s : st3) {
                            if (catidslist.indexOf(s) != catidslist.lastIndexOf(s)) {
                                catidslist.remove(catidslist.lastIndexOf(s));
                            }
                        }
                        // Sorting catagories by using categrory numbers
                        HashMap<Integer,String> names_map = null,objids_map = null;
                        names_map = new HashMap<Integer,String>();
                        objids_map = new HashMap<Integer,String>();
                        for (int k = 0; k < catnameslist.size(); k++) {

                            names_map.put(Integer.valueOf(catnolist.get(k)),catnameslist.get(k));
                            objids_map.put( Integer.valueOf(catnolist.get(k)),catidslist.get(k));

                            cat_names.add(names_map);
                            cat_objids.add(objids_map);
                        }

                        // sorting hashmap data using keys
                        Map<Integer,String> sortedMapByKeys = new TreeMap<Integer,String>();
                        sortedMapByKeys.putAll(names_map);
                        Map<Integer,String> sortedMapByKeys2 = new TreeMap<Integer,String>();
                        sortedMapByKeys2.putAll(objids_map);

                      // getting sorted data names in Treemap
                        for (Map.Entry<Integer, String> e: sortedMapByKeys.entrySet()) {
//                            catnameslist11.add(e.getKey());
                            sorted_cat_names.addAll(Collections.singleton(e.getValue()));
                        }
                        for (Map.Entry<Integer, String> e: sortedMapByKeys2.entrySet()) {
//                            catnameslist11.add(e.getKey());
                            sorted_cat_objids.addAll(Collections.singleton(e.getValue()));
                        }
                        for (int k = 0; k < catnameslist.size(); k++) {

                            tabLayout.addTab(tabLayout.newTab().setText("" +  sorted_cat_names.get(k)));
                        }
                        // data sorted order
//                        Log.d("chk", "done: sorted" +"=="+sorted_cat_names);
                        PlansPagerAdapter CatgoriesAdapter = new PlansPagerAdapter
                                (getSupportFragmentManager(), tabLayout.getTabCount());
                        viewPager.setAdapter(CatgoriesAdapter);
                        viewPager.setCurrentItem(0);
                        viewPager.setOffscreenPageLimit(sorted_cat_names.size());
//                        Log.d("chk", "onPostExecute: "+sorted_cat_objids);
                    }
                } else {

                }
            }
        }
    }

    public void GetCategories(){

        if(ParseUser.getCurrentUser()!= null) {

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Category");
            query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
            query.setLimit(1000);
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> object, ParseException e) {
                    if (e == null) {
                        catnameslist = new ArrayList<String>();
                        catnolist = new ArrayList<String>();
                        catidslist = new ArrayList<String>();
//                        maplist = new ArrayList<HashMap<String, String>>();
                        if (object.size() > 0)
                        {
                            for (ParseObject user : object)
                            {

                                String catname = user.getString("categoryName");
                                String catno = user.getString("categoryNo");
                                String objectid = user.getObjectId();
                                catnameslist.add(catname);
                                catnolist.add(catno);
                                catidslist.add(objectid);
                            }

                            for (int k = 0; k < catnameslist.size(); k++) {

                                tabLayout.addTab(tabLayout.newTab().setText("" +  catnameslist.get(k)));
                            }
                            PlansPagerAdapter CatgoriesAdapter = new PlansPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
                            viewPager.setAdapter(CatgoriesAdapter);
                            viewPager.setOffscreenPageLimit(catnameslist.size());
                            viewPager.setCurrentItem(0);
                        }else{
                            Crashlytics.logException(e);
                        }
                    }else{
                        Crashlytics.logException(e);
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
//          finish();
      backpopup();
    }

   public void backpopup(){
       AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
       alertDialogBuilder.setMessage("Do you want to "+ "SAVE"+" your changes");
       alertDialogBuilder.setCancelable(false);
       alertDialogBuilder.setPositiveButton("Yes",
               new DialogInterface.OnClickListener() {

                   @Override
                   public void onClick(DialogInterface arg0, int arg1) {

                       finish();
                       overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                   }
               });
       alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {

           }
       });

       AlertDialog alertDialog = alertDialogBuilder.create();
       alertDialog.show();

   }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                 finish();
              backpopup();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
        Log.d("chk", "parseObjectToJson:chk "+jsonObject.toString());
        return jsonObject;
    }
}
