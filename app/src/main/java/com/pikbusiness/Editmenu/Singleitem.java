package com.pikbusiness.Editmenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.pikbusiness.Loginmodule.SessionManager;
import com.pikbusiness.R;
import com.crashlytics.android.Crashlytics;
import com.elmargomez.typer.Font;
import com.elmargomez.typer.Typer;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Singleitem extends AppCompatActivity {

    String pin,sts,chk;
    SessionManager session;
    @BindView(R.id.menuitem)TextView nam;
    @BindView(R.id.cancel)Button cancel1;
    @BindView(R.id.btn_save_location)Button save;
    @BindView(R.id.edittxt)TextView edittxt;
    @BindView(R.id.mprice)EditText mprice;
    @BindView(R.id.cur)TextView curr;
    @BindView(R.id.dynamiclay)LinearLayout dynamiclay;
    @BindView(R.id.sizelay)LinearLayout sizelay;
    @BindView(R.id.sizechk)CheckBox sizests;
    @BindView(R.id.titlesz)TextView titlesz;
    @BindView(R.id.sizeslist)RecyclerView sizelist;
    JSONObject obj,obj2;
    EditText pric;
    String extras,itemid1,catid,itemname,itemprice,menusid;
    List<EditText> allEds,allEdsinextra;
    ArrayList<String> exnamechklist,exnameunchklist,pricelist,
            namechklist,expricelist,nameunchklist;
    ArrayList<Integer> expriceidslist,priceidslist;
    ArrayList<HashMap<String, String>> maplist;
    Boolean sizeschk = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.singleitem);
        ButterKnife.bind(this);
         session = new SessionManager(this);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("Reg", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
         pin = pref.getString("pin", null);

        itemid1 = getIntent().getStringExtra("itemid");
        itemname = getIntent().getStringExtra("itemname");
        itemprice = getIntent().getStringExtra("price");
        catid = getIntent().getStringExtra("catid");
         menusid = getIntent().getStringExtra("menusid");
         extras = getIntent().getStringExtra("extras");
         String currency = getIntent().getStringExtra("currency");
        sts = getIntent().getStringExtra("sts");
        nam.setText(itemname);
        curr.setText(currency);
        mprice.setText(itemprice);
//        mprice.requestFocusFromTouch();
        Log.d("chk", "onCreate: "+menusid+"=="+itemid1+"=="+catid+ParseUser.getCurrentUser().getObjectId());
        mprice.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        edittxt.setTypeface(Typer.set(this).getFont(Font.ROBOTO_MEDIUM));
        titlesz.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        nam.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        curr.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
//        parsedata(itemid,catid);

        mprice.setInputType(InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_FLAG_DECIMAL |
                InputType.TYPE_NUMBER_FLAG_SIGNED);
        save.setTypeface(Typer.set(this).getFont(Font.ROBOTO_MEDIUM));
        cancel1.setTypeface(Typer.set(this).getFont(Font.ROBOTO_MEDIUM));
        cancel1.setVisibility(View.GONE);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("chk", "onClick:single "+menusid);
                savedaata();
            }
        });
        mprice.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(3,2)});
        if(sts !=null){
            if(sts.equals("true")){
                ParseUser user = ParseUser.getCurrentUser();
                Boolean tr = false;
                user.put("defaultPrice",tr);
                user.saveInBackground(new SaveCallback()
                {
                    @Override
                    public void done(ParseException e)
                    {

                        if (e == null)
                        {
//                            Log.d("chk", "done:sts update");
                        }
                    }
                });
            }
        }


        cancel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent i = new Intent(Singleitem.this,EditMenutabs.class);
//                startActivity(i);
                finish();
            }
        });

        // creating extras data

            try {

                maplist = new ArrayList<HashMap<String, String>>();
                obj = new JSONObject(extras);
                JSONArray jsonArray = obj.getJSONArray("SingleExtras");
                JSONArray jsonArray1 = obj.getJSONArray("Extras");

                if (jsonArray.length() > 0) {

//              sizelay.setVisibility(View.VISIBLE);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject row = jsonArray.getJSONObject(i);
                        String itemid = row.optString("id");
                        String itemname = row.optString("name");
                        String isenable = row.optString("isEnabled");
                        JSONArray arry = row.getJSONArray("values");

                        if (itemname.equals("size")) {
                            sizelay.setVisibility(View.VISIBLE);
                            if (isenable.equals("1")) {
                                sizests.setChecked(true);
                            } else {
                                sizests.setChecked(false);

                            }

                            if (arry.length() > 0) {

                                for (int j = 0; j < arry.length(); j++) {

                                    JSONObject rw = arry.getJSONObject(j);

                                    String extraId = rw.optString("extraId");
                                    String objId = rw.optString("objId");
                                    String extraName = rw.optString("extraName");
                                    String extraPrice = rw.optString("extraPrice");
                                    String currenc = rw.optString("currency");
                                    String chksts = rw.optString("isEnabled");

                                    HashMap<String, String> map = new HashMap<String, String>();
                                    map.put("extraId", extraId);
                                    map.put("objId", menusid);
                                    map.put("extraName", extraName);
                                    map.put("extraPrice", extraPrice);
                                    map.put("currency", currenc);
                                    map.put("item_objid", itemid1);
                                    map.put("cat_objid", catid);
                                    map.put("isEnabled", chksts);
                                    maplist.add(map);
                                }
                            }
                            getlistdata(maplist);
                            if(jsonArray1.length() > 0){
                                // For extras
                                TextView title = new TextView(Singleitem.this);
                                title.setTextSize(16);
                                LinearLayout.LayoutParams paramtitel = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                                paramtitel.setMargins(10, 15, 0, 10);
                                title.setLayoutParams(paramtitel);
                                title.setTextColor(ContextCompat.getColor(Singleitem.this, R.color.gray));
                                title.setGravity(Gravity.LEFT);
                                title.setText("Extras");
                                dynamiclay.addView(title);
                                jsondaat(jsonArray1, "1");
                            }

                        } else {

                            TextView title = new TextView(Singleitem.this);
                            title.setTextSize(16);
                            LinearLayout.LayoutParams paramtitel = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                            paramtitel.setMargins(10, 15, 0, 10);
                            title.setLayoutParams(paramtitel);
                            title.setTextColor(ContextCompat.getColor(Singleitem.this, R.color.gray));
                            title.setGravity(Gravity.LEFT);
                            title.setText(itemname);
                            dynamiclay.addView(title);
                            jsondaat(arry, "0");
                        }
                    }

                } else {

                    if(jsonArray1.length() > 0){
                        // For extras
                        TextView title = new TextView(Singleitem.this);
                        title.setTextSize(16);
                        LinearLayout.LayoutParams paramtitel = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                        paramtitel.setMargins(10, 15, 0, 10);
                        title.setLayoutParams(paramtitel);
                        title.setTextColor(ContextCompat.getColor(Singleitem.this, R.color.gray));
                        title.setGravity(Gravity.LEFT);
                        title.setText("Extras");
                        dynamiclay.addView(title);
                        jsondaat(jsonArray1, "1");
                    }
                    sizelay.setVisibility(View.GONE);
                }
            } catch (Throwable tx) {
//                Log.e("My App", "for loop parse" + "" + tx);
            }

    }
     class DecimalDigitsInputFilter implements InputFilter {

        Pattern mPattern;

        public DecimalDigitsInputFilter(int digitsBeforeZero,int digitsAfterZero) {
            mPattern=Pattern.compile("[0-9]{0," + (digitsBeforeZero-1) + "}+((\\.[0-9]{0," + (digitsAfterZero-1) + "})?)||(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Matcher matcher=mPattern.matcher(dest);
            if(!matcher.matches())
                return "";
            return null;
        }

    }
    public void savedaata(){

        if (obj != null && obj.length() > 0){

            try {

                JSONArray jsonArray = obj.getJSONArray("SingleExtras");
                if (jsonArray.length() > 0) {
//                    Log.d("chk", "savedaata: three");
                    int g = 0;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject row = jsonArray.getJSONObject(i);
                        String itemid = row.optString("id");
                        String iname = row.optString("name");
                        String isenable = row.optString("isEnabled");
                        JSONArray arry = row.getJSONArray("values");
                        if(iname.equals("size")){

                            sizelay.setVisibility(View.VISIBLE);
                            if (arry.length() > 0) {
                                int zcount = 0,ocount = 0;
                                for (int j = 0; j < arry.length(); j++) {

                                    JSONObject rw = arry.getJSONObject(j);
                                    String chksts = rw.optString("isEnabled");
                                    if(chksts.equals("0")){
                                        zcount++;
                                    }else{
                                        ocount++;
                                    }
                                }
                                if(isenable.equals("0")){
                                   if(ocount >0){
                                       Toast.makeText(this, "Please enable sizes", Toast.LENGTH_SHORT).show();
                                   }
                                }else
                                    {
                                   if(zcount == 3){
                                       Toast.makeText(this, "Please enable sizes", Toast.LENGTH_SHORT).show();
                                   }
                                }

                            }
                        }
                        else
                        {
//                            Log.d("chk", "savedaata: four");
                            pricelist = new ArrayList<String>();
                            priceidslist = new ArrayList<Integer>();

                            for(int l=0; l < allEds.size(); l++){
//                                Log.d("chk", "savedaata: five");
                                pricelist.add(allEds.get(l).getText().toString().trim());
                                priceidslist.add(allEds.get(l).getId());
                            }

                            final int ar = arry.length();
                            final int tot = g + ar ;
                            int h = 0;
                            for (int j = g ; j < tot; j++) {

                                JSONObject rw = arry.getJSONObject(h);
                                String extraName = rw.optString("extraName");
                                String extraId = rw.optString("extraId");
                                if(namechklist.size() > 0){
                                    if(namechklist.contains(extraName)){
                                        rw.put("isEnabled",1);
                                    }
                                }
                                if(nameunchklist.size()>0){
                                    if(nameunchklist.contains(extraName)){
                                        rw.put("isEnabled",0);
                                    }
                                }
                                if(j >= arry.length()){

                                }
                                else{

                                    String gh = String.valueOf(priceidslist.get(j));
                                    if(gh.equals(extraId)){
                                        rw.put("extraPrice",Double.valueOf(pricelist.get(j)));
                                    }
                                }
                                h++;
                            }
                             g = arry.length();

                        }
                    }
                }
                JSONArray extraArray = obj.getJSONArray("Extras");
//                Log.d("chk", "savedaata:size2 "+extraArray);
                if(extraArray.length() > 0){
//                    Log.d("chk", "savedaata: extras two"+extraArray.length());
                    for(int i=0; i < allEdsinextra.size(); i++){
                        expricelist.add(allEdsinextra.get(i).getText().toString().trim());
                        expriceidslist.add(allEdsinextra.get(i).getId());
                    }

                    for (int i = 0; i < extraArray.length(); i++) {
                        JSONObject rw = extraArray.getJSONObject(i);
                        String extraName = rw.optString("extraName");
                        String extraId = rw.optString("extraId");

                        if(exnamechklist.size() > 0){
                            if(exnamechklist.contains(extraName)){
                                rw.put("isEnabled",1);
                            }
                        }
                        if(exnameunchklist.size()>0){
                            if(exnameunchklist.contains(extraName)){
                                rw.put("isEnabled",0);
                            }
                        }

                        String gh = String.valueOf(expriceidslist.get(i));
                        if(gh.equals(extraId)){
                            rw.put("extraPrice",Double.valueOf(expricelist.get(i)));
                        }
                    }
                }
//                Log.d("chk", "savedaata:total two === "+obj.toString());
                if(mprice.getText().toString().trim().equals("")||mprice.getText().toString().trim().length() == 0){
                    Toast.makeText(this, "Please enter valid data ", Toast.LENGTH_SHORT).show();
                }else{

                    obj2 = obj;
//                    Log.d("chk", "savedaata:total obj2 chk == "+obj2.toString());
                    editupdate(obj2.toString(),mprice.getText().toString().trim());
                }
            } catch (Throwable tx) {
//                Log.e("My App", "Could not parse JSON: " +  tx.getMessage());
            }

        }else{
//            Log.d("chk", "savedaata:total empty == "+obj2.toString());
//            sizelay.setVisibility(View.GONE);
            if(mprice.getText().toString().length() == 0){

                Toast.makeText(this.getApplicationContext(), "Please enter valid data ", Toast.LENGTH_SHORT).show();
            }else{

                editupdate("{}",mprice.getText().toString().trim());
            }
        }
    }
    public void editupdate(String extrass,String price){
        Log.d("chk", "done:one update "+menusid);
        if(ParseUser.getCurrentUser()!= null) {

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Menus");
            query.setCachePolicy(ParseQuery.CachePolicy.IGNORE_CACHE);
            query.getInBackground(menusid, new GetCallback<ParseObject>() {
                public void done(ParseObject shop, ParseException e) {
                    if (e == null) {
                        // Now let's update it with some new data.
                        shop.put("extras", extrass);
                        shop.put("price",Double.parseDouble(price));
                        shop.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {

                                if (e == null) {
                                    finish();
                                }
                                Crashlytics.logException(e);
                            }
                        });
                    } else {
                        Log.d("chk", "done:extras save "+e.getMessage());
                        Crashlytics.logException(e);
                        e.printStackTrace();
                    }
                }
            });

        }

    }


    public void jsondaat(JSONArray arry, String s){
        if(s.equals("1")){
            allEdsinextra = new ArrayList<EditText>();
            expricelist = new ArrayList<String>();
            expriceidslist = new ArrayList<Integer>();
            exnamechklist = new ArrayList<String>();
            exnameunchklist = new ArrayList<String>();
            allEdsinextra = new ArrayList<EditText>();
        }

        namechklist = new ArrayList<String>();
        nameunchklist = new ArrayList<String>();
        allEds = new ArrayList<EditText>();
        if (arry.length() > 0) {
            try {
                for (int j = 0; j < arry.length(); j++) {

                    JSONObject rw = arry.getJSONObject(j);
                    String extraId = rw.optString("extraId");
                    String objId = rw.optString("objId");
                    String extraName = rw.optString("extraName");
                    String extraPrice = rw.optString("extraPrice");
                    String currenc = rw.optString("currency");
                    String chksts = rw.optString("isEnabled");
                    // parent linear layout
                    LinearLayout parentl = new LinearLayout(this);
                    LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
//                linearParams.setMargins(0, 0, 0, 0);
                    parentl.setLayoutParams(linearParams);
                    parentl.setGravity(Gravity.CENTER);
                    parentl.setOrientation(LinearLayout.HORIZONTAL);
//                    dynamiclay.addView(parentl);
                    LinearLayout newparent = new LinearLayout(this);
                    LinearLayout.LayoutParams newparam = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
//                linearParams.setMargins(0, 0, 0, 0);
                    newparent.setLayoutParams(newparam);
                    newparent.setOrientation(LinearLayout.VERTICAL);

                    dynamiclay.addView(newparent);
                    newparent.addView(parentl);
                    // checkbox dyanmic in parent layout
                    CheckBox chk = new CheckBox(this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    chk.setLayoutParams(params);
                    chk.setId(j);
                    parentl.addView(chk);
                    if (chksts.equals("1")) {
                        chk.setChecked(true);
                    } else {
                        chk.setChecked(false);
                    }
                    // another linear layout for textview,edittext and textview
                    LinearLayout child1 = new LinearLayout(this);
                    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    child1.setLayoutParams(params2);
                    child1.setGravity(Gravity.CENTER);
                    child1.setOrientation(LinearLayout.HORIZONTAL);
                    parentl.addView(child1);

                    // Textview for currency
                    TextView name = new TextView(this);
                    name.setTextSize(16);
                    name.setTextColor(ContextCompat.getColor(this,R.color.black));
                    LinearLayout.LayoutParams paramname = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    paramname.setMargins(10,0,0,0);
                    name.setLayoutParams(paramname);
                    name.setText(extraName);
                    name.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
                    // Edittext dynamic

                    pric = new EditText(this);
                    pric.setBackground(null);
                    pric.setGravity(Gravity.RIGHT);
                    pric.setTextSize(16);
                    pric.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(3,2)});

//                    int maxLength = 2;
//                    pric.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
                    pric.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
                    pric.setText(extraPrice);
                    LinearLayout.LayoutParams paramprice = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,1.0f);
                    paramprice.setMargins(0,5,0,0);
                    pric.setLayoutParams(paramprice);
                    pric.setId(Integer.parseInt(extraId));
                    pric.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    pric.setTextColor(ContextCompat.getColor(this,R.color.black));
                    pric.setInputType(InputType.TYPE_CLASS_NUMBER |
                            InputType.TYPE_NUMBER_FLAG_DECIMAL |
                            InputType.TYPE_NUMBER_FLAG_SIGNED);

                    allEds.add(pric);
                    if(s.equals("1")){
                        allEdsinextra.add(pric);
                    }
                    // Textview for currency
                    TextView money = new TextView(this);
                    money.setTextSize(16);
                    money.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
                    money.setTextColor(ContextCompat.getColor(this,R.color.black));
                    LinearLayout.LayoutParams parammny = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    parammny.setMargins(10,5,0,0);
                    money.setLayoutParams(parammny);
                    money.setText(currenc);
                    money.setAllCaps(true);
                    child1.addView(name);
                    child1.addView(pric);
                    child1.addView(money);
                    View vline = new View(this);
                    LinearLayout.LayoutParams vparams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, 3);
                    vparams.setMargins(65, 0, 0, 0);
                    vline.setLayoutParams(vparams);
                    vline.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimary));
                    newparent.addView(vline);

                    chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if(isChecked){
                                if(s.equals("1")){
                                    exnamechklist.add(extraName);

                                    if(exnameunchklist.size() > 0){
                                        exnameunchklist.remove(extraName);
                                    }
                                }else{
                                    namechklist.add(extraName);
                                    if(nameunchklist.size() > 0){
                                        nameunchklist.remove(extraName);
                                    }
                                }

                            }else
                            {
                                if(s.equals("1")){
                                    exnameunchklist.add(extraName);
                                    if(exnamechklist.size() > 0){
                                        exnamechklist.remove(extraName);
                                    }
                                }else{
                                    nameunchklist.add(extraName);
                                    if(namechklist.size() > 0){
                                        namechklist.remove(extraName);
                                    }
                                }

                            }
                        }
                    });
                }

            } catch (Throwable tx) {
                Crashlytics.logException(tx);
//                Log.e("My App", "jsondata() " + tx);
            }

        }
    }
    private void getlistdata(ArrayList<HashMap<String, String>> maplist) {

        if (sizelist != null) {
            SizesAdapter testAdapter = new SizesAdapter(this, maplist);
            sizelist.setLayoutManager(new LinearLayoutManager(this));
            sizelist.setItemAnimator(new DefaultItemAnimator());
            sizelist.setAdapter(testAdapter);

        }
    }
    private class SizesAdapter extends RecyclerView.Adapter<SizesAdapter.MyViewHolder2>  {

        ArrayList<HashMap<String, String>> listdata;
        Context context;


        public SizesAdapter(Context context,ArrayList<HashMap<String, String>> aryList) {
            this.context = context;
            this.listdata = aryList;
        }

        @Override
        public MyViewHolder2 onCreateViewHolder(ViewGroup parent, int viewType) {
            // infalte the item Layout

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sizesitems, parent, false);
            return new MyViewHolder2(v);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindViewHolder(MyViewHolder2 holder, final int position) {
            // set the data in items
            String extraId = listdata.get(position).get("extraId");
            String objId = listdata.get(position).get("objId");
            String extraName = listdata.get(position).get("extraName");
            String extraPrice = listdata.get(position).get("extraPrice");
            String currency = listdata.get(position).get("currency");
            String isEnabled = listdata.get(position).get("isEnabled");
            String catid =  listdata.get(position).get("cat_objid");
            String itemid =  listdata.get(position).get("item_objid");
            holder.priceedit.setFilters(new InputFilter[]
                    {new DecimalDigitsInputFilter(3,2)});
            holder.priceedit.setText(extraPrice);
            holder.titlenm.setText(extraName);
            holder.curr.setText(currency);
            if(isEnabled.equals("1")){
                holder.sizechk.setChecked(true);
                chk = "1";
            }else{
                holder.sizechk.setChecked(false);
                chk = "0";
            }
            String pr = holder.priceedit.getText().toString().trim();
            holder.sizechk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){

                        Singlearray("1",extraId,pr);
                        chk = "1";

                    }else{
                        Singlearray("0",extraId,pr);
                        chk = "0";
                    }
                }
            });

            holder.priceedit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.priceedit.setCursorVisible(true);
                }
            });
            holder.priceedit.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {
                    String pr = holder.priceedit.getText().toString().trim();
//                    Log.d("chktext", "onBindViewHolder: "+objId);
                    Singlearray(chk,extraId,pr);
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return listdata.size();
        }

        public class MyViewHolder2 extends RecyclerView.ViewHolder {

            TextView  titlenm,curr;
            CheckBox sizechk;
            EditText priceedit;

            public MyViewHolder2(View itemView) {
                super(itemView);

                titlenm = itemView.findViewById(R.id.titlenm);
                sizechk = itemView.findViewById(R.id.sizechk);
                curr = itemView.findViewById(R.id.curr);
                priceedit = itemView.findViewById(R.id.pricedit);
                priceedit.setInputType(InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_DECIMAL |
                        InputType.TYPE_NUMBER_FLAG_SIGNED);
                priceedit.requestFocusFromTouch();

            }
        }
    }

    private void Singlearray(String enable,String extraid, String pr) {
        try {

//            obj = new JSONObject(extras);
            JSONArray jsonArray = obj.getJSONArray("SingleExtras");

            if (jsonArray.length() > 0) {

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject row = jsonArray.getJSONObject(i);
                    String itemid = row.optString("id");
                    String itemname = row.optString("name");
                    String isenable = row.optString("isEnabled");
                    JSONArray arry = row.getJSONArray("values");
//                    Log.d("chkiner", "Singlearray: outloop"+jsonArray.toString());
                    if (itemname.equals("size")) {
                        if (enable.equals("1")) {
                            row.put("isEnabled",Integer.valueOf(enable));
                        } else {
                            row.put("isEnabled",Integer.valueOf(enable));
                        }
                    }
                    for (int j = 0 ; j < arry.length(); j++) {
                        JSONObject rw = arry.getJSONObject(j);
                        String extid = rw.optString("extraId");
                        if (itemname.equals("size")) {

                            if(extraid.equals(extid)){
//                                Log.d("chk", "Singlearray:== ");
                                rw.put("isEnabled",Integer.valueOf(enable));
                                rw.put("extraPrice",Double.valueOf(pr));
                            }
                        }
                    }
                }
            }

            obj2 = obj;

//            Log.d("chk", "Singlearray: sizes==="+obj2.toString());
//            sizeeditupdate(obj2.toString(),item_objid,cat_objid,objectId);

        } catch (Throwable tx) {
//            Log.e("My App", "Could not parse JSON: " +  tx.getMessage());
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }
}
