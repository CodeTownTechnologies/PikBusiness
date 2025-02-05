package com.pikbusiness.Adapters;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.pikbusiness.Orderslist;
import com.pikbusiness.R;
import com.elmargomez.typer.Font;
import com.elmargomez.typer.Typer;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Readyadapter extends RecyclerView.Adapter<Readyadapter.MyViewHolder> {


    ArrayList<HashMap<String, String>> dataa;
    Context context;
    String cur ="";
    AlertDialog alertDialog;
    Boolean stat1 = false,stat2 = false;
    private Readyadapter adapter;
    ProgressDialog dialog;

    public Readyadapter(Context context,ArrayList<HashMap<String, String>> aryList) {
        this.context = context;
        this.dataa = aryList;
        this.adapter = this;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.orderlist_item, parent, false);
        return new MyViewHolder(v);
    }
    public void update(ArrayList<HashMap<String, String>> data3) {
        data3.clear();
        data3.addAll(data3);
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        String notes = dataa.get(position).get("notes");
        String total = dataa.get(position).get("total");
        String orderstatus = dataa.get(position).get("orderstatus");
        String subtotal = dataa.get(position).get("subtotal");
        String tax = dataa.get(position).get("tax");
        String order = dataa.get(position).get("order");
        String spname = dataa.get(position).get("sname");
        String shop_sts = dataa.get(position).get("shop_sts");

        String car = dataa.get(position).get("car");
        if(car != null){
            if(car.length()>0){
                holder.shopname.setText(dataa.get(position).get("username")+" ( "+car+" ) ");
            }else{
                holder.shopname.setText(dataa.get(position).get("username"));
            }
        }else{
            holder.shopname.setText(dataa.get(position).get("username"));
        }
        cur = dataa.get(position).get("currency");
        String slat = dataa.get(position).get("slat");
        String slong = dataa.get(position).get("slong");
        String ulat = dataa.get(position).get("ulat");
        String ulong = dataa.get(position).get("ulong");
//        Log.d("loc", "onBindViewHolder: "+slat+slong+ulat+ulong);
        holder.vatper.setText("Vat "+tax+"%");
        holder.subtotal.setText(subtotal+" "+cur);
        Double t = Double.valueOf(subtotal);
        double l = (Integer.valueOf(tax) / 100.0f) * t;
        NumberFormat n1 = NumberFormat.getInstance(); // get instance
        n1.setMaximumFractionDigits(2); // set decimal places
        String v = n1.format(l);
        holder.vatprice.setText(String.valueOf(v)+" "+cur);
        holder.vatprice.setAllCaps(true);
        holder.subtotal.setAllCaps(true);
        holder.updateTimer = new Timer();
//        holder.headcolor.setBackgroundColor(ContextCompat.getColor(context,R.color.green));
//        TimerTask hourlyTask = new TimerTask () {
        TimerTask hour = new TimerTask()
        {
            public void run()
            {
                try
                {
                    SimpleDateFormat date1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy",
                            Locale.ENGLISH);
                    Date d1 = null;
                    Date c = Calendar.getInstance().getTime();
                    if (Build.VERSION.SDK_INT >= 23) {
                        d1 = date1.parse(dataa.get(position).get("date"));
                        // Call some material design APIs here
                    } else {
                        DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
                        d1 = (Date)formatter.parse(dataa.get(position).get("date"));
                    }
                    long different = c.getTime() - d1.getTime();
                    long secondsInMilli = 1000;
                    long minutesInMilli = secondsInMilli * 60;
                    long hoursInMilli = minutesInMilli * 60;
                    long daysInMilli = hoursInMilli * 24;

                    different = different % daysInMilli;
                    long elapsedHours = different / hoursInMilli;
                    different = different % hoursInMilli;
                    long elapsedMinutes = different / minutesInMilli;
                    different = different % minutesInMilli;
                    long elapsedSeconds = different / secondsInMilli;
                    Handler mainThread = new Handler(Looper.getMainLooper());
                    mainThread.post(new Runnable() {
                        @Override
                        public void run() {
                            if(elapsedHours == 0){
                                holder.time.setText(elapsedMinutes+":"+elapsedSeconds);
                            }else{
                                holder.time.setText(elapsedHours+":"+elapsedMinutes+":"+elapsedSeconds);
                            }
//                            holder.msg.setText(elapsedMinutes+" minutes away");

                            if(elapsedHours == 0){
                                if(elapsedMinutes < 3){
                                    holder.headcolor.setBackgroundColor(ContextCompat.getColor(context,R.color.green));
                                }
                                else if(elapsedMinutes >= 3 && elapsedMinutes < 5){

                                    holder.headcolor.setBackgroundColor(ContextCompat.getColor(context,R.color.yellow));
                                }
                                else if(elapsedMinutes >= 5){

                                    holder.headcolor.setBackgroundColor(ContextCompat.getColor(context,R.color.red));
                                }
                            }
                            else {
                                holder.headcolor.setBackgroundColor(ContextCompat.getColor(context,R.color.red));
                            }

                        }
                    });

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

        };

        holder.updateTimer.schedule (hour, 60, 1000);
        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(shop_sts.equals("2")){
//                    Toast.makeText(context, "Shop is disabled. Please contact support", Toast.LENGTH_SHORT).show();
//
//                }else{
                    if(dataa.get(position).get("phno") != null){
                        if(dataa.get(position).get("phno").length() > 0){
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:"+"+" +dataa.get(position).get("phno")));
                            context.startActivity(intent);
                        }else{
                            Toast.makeText(context, "Customer doesn't provided phone number", Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        Toast.makeText(context, "Customer doesn't provided phone number", Toast.LENGTH_SHORT).show();

                    }
                }
//            }
        });
        if(slat != null && slong != null && ulat != null && ulong != null){
            Double sl = Double.valueOf(slat);
            Double slg = Double.valueOf(slong);
            Double ul = Double.valueOf(ulat);
            Double ulg = Double.valueOf(ulong);

            Location startPoint=new Location("Shop");
            startPoint.setLatitude(sl);
            startPoint.setLongitude(slg);

            Location endPoint=new Location("User");
            endPoint.setLatitude(ul);
            endPoint.setLongitude(ulg);
            float distanceInMeters = startPoint.distanceTo(endPoint);
            Integer intmeters = (int)distanceInMeters;
            //For example spead is 10 meters per minute.
            int speedIs10MetersPerMinute = 60;
            float estimatedDriveTimeInMinutes = distanceInMeters / speedIs10MetersPerMinute;
            Integer intValue = (int)estimatedDriveTimeInMinutes;
            if(intValue > 60){

                if(intValue/60 > 1){
                    holder.msg.setText(intValue/60+" hours away");
                }else{
                    holder.msg.setText(intValue/60+" hour away");
                }
            }else{
                holder.msg.setText(intValue+" minutes away");
            }

            double distance= startPoint.distanceTo(endPoint)/1000;
//            Log.d("chk", "onBindViewHolder: meters"+distanceInMeters);
            NumberFormat nf = NumberFormat.getInstance(); // get instance
            nf.setMaximumFractionDigits(0); // set decimal places
            Integer intdist = (int)distance;
            String s = nf.format(distance);
            if(intdist < 1){
                holder.distance.setText(intmeters+" m");
            }else{
                holder.distance.setText(intdist+" kms");
            }
        }
        try {

            JSONArray obj = new JSONArray(order);
            ArrayList<HashMap<String, String>>  maplist1 = new ArrayList<HashMap<String, String>>();
            for (int i = 0; i < obj.length(); i++) {

                HashMap<String, String> map = new HashMap<String, String>();
                JSONObject row = obj.getJSONObject(i);
                String item1 = row.getString("menuName");
                String price1 = row.getString("menuPrice");
                String category = row.getString("categoryName");
                int count = Integer.parseInt(row.getString("count"));
                Double r = Double.valueOf(price1);
                int x = r.intValue();
                int tot = count * x ;
                String ex = row.getString("extras");
                JSONArray arry = new JSONArray(ex);
                StringBuilder sb = new StringBuilder();
                StringBuilder sb2 = new StringBuilder();
                Double y = 0.0;
                map.put("category_name",category);
                map.put("item_name",item1 + " x " + count);

                if(arry.length()> 0)
                {
                    for (int j = 0; j < arry.length(); j++)
                    {
                        JSONObject rw = arry.getJSONObject(j);
                        String id = rw.getString("extraId");
                        String type2 = rw.getString("extraName");
                        String price2 = rw.getString("extraPrice");
                        sb.append(type2);
                        sb.append(" + ");
                        y += Double.valueOf(price2);
                        sb2.append(price2);
                    }
                }
                y += Double.valueOf(tot);


                if(sb.length() > 0){
                    sb.deleteCharAt(sb.length() - 2);
                }
             map.put("item_price",y + " " + cur);
                map.put("extra_name",sb.toString());

                maplist1.add(map);
            }
            ItemslistAdapter itemsadapter = new ItemslistAdapter(context, maplist1);
            holder.itemslist.setLayoutManager(new LinearLayoutManager(context));
            holder.itemslist.setItemAnimator(new DefaultItemAnimator());
            holder.itemslist.setAdapter(itemsadapter);

        } catch (Throwable tx) {
            Log.e("My App", "Could not parse JSON:ready \"chk" + "" + tx);
        }

        holder.total.setText(total+" "+cur);
        holder.total.setAllCaps(true);
        if(notes != null){
            if(notes.length() > 0){
                holder.note.setText("Note : "+notes);
            }else{
                holder.note.setVisibility(View.GONE);
            }
        }else{
            holder.note.setVisibility(View.GONE);
        }
//        holder.note.setText("Note : "+notes);
        holder.stsbtn.setText("Pick up");

        holder.cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(shop_sts.equals("2")){
//                    Toast.makeText(context, "Shop is disabled. Please contact support", Toast.LENGTH_SHORT).show();
//
//                }else {
                    if (stat2) {

                    } else {
                        dialog = new ProgressDialog(v.getContext());
                        dialog.setMessage("Please wait.....");
                        dialog.setCancelable(false);
                        dialog.show();
                        String id1 = dataa.get(position).get("objid");
//                Log.d("chk", "onClick: "+id1);
                        HashMap<String, Object> params = new HashMap<String, Object>();
                        params.put("cancelledBy", "business");
                        params.put("status", orderstatus);
                        params.put("orderNo", id1);
                        params.put("totalCost", total);
//                Log.d("cd", "onClick: "+params);
                        ParseCloud.callFunctionInBackground("refund", params, new FunctionCallback<Map<String, List<ParseObject>>>() {
                            @Override
                            public void done(Map<String, List<ParseObject>> object, com.parse.ParseException e) {
                                if (e == null) {
//                            Log.d("chk", "done:res == "+object.get("cancelledBy"));
                                    SimpleDateFormat date1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy",
                                            Locale.ENGLISH);
                                    Date d1;
                                    Date c = Calendar.getInstance().getTime();
                                    String cacenlby = String.valueOf(object.get("cancelledBy"));
                                    String refuncost = String.valueOf(object.get("refundCost"));
                                    String refund = String.valueOf(object.get("refund"));
                                    String refundForBusiness = String.valueOf(object.get("refundForBusiness"));
//                            String refund = String.valueOf(object.get("refund"));
                                    String refundtrans = String.valueOf(object.get("refundTrans"));
                                    String refundper = String.valueOf(object.get("refundPercentage"));
                                    String pikPercentage = String.valueOf(object.get("pikPercentage"));
                                    String pikCharges = String.valueOf(object.get("pikCharges"));
                                    Log.d("chk", "done: " + pikCharges + refuncost);
                                    try {
                                        Double tr = Double.valueOf(dataa.get(position).get("totaltime"));
                                        if (Build.VERSION.SDK_INT >= 23) {
                                            d1 = date1.parse(dataa.get(position).get("date"));
                                            // Call some material design APIs here
                                        } else {
                                            DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
                                            d1 = (Date) formatter.parse(dataa.get(position).get("date"));
                                        }
                                        long different = c.getTime() - d1.getTime();
                                        long secondsInMilli = 1000;
                                        long minutesInMilli = secondsInMilli * 60;
                                        long elapsedMinutes = different / minutesInMilli;
                                        different = different % minutesInMilli;
                                        long elapsedSeconds = different / secondsInMilli;
                                        String tot = String.valueOf(tr + elapsedMinutes);
                                        String tiim = elapsedMinutes + "." + elapsedSeconds;

                                        JSONArray jsonArray = null;
                                        try {
                                            jsonArray = new JSONArray(dataa.get(position).get("time"));

                                            if (jsonArray.length() > 0) {

                                                jsonArray.put(tiim);
                                            }
                                        } catch (JSONException r) {
                                            r.printStackTrace();
//                                    Log.d("chk", "onClick:error "+r.getMessage());
                                        }
                                        Cancelorder(dataa.get(position).get("objid"),
                                                refund, refuncost, cacenlby, v, dataa.get(position).get("lname"),
                                                dataa.get(position).get("sname"),
                                                dataa.get(position).get("lat"),
                                                dataa.get(position).get("log"),
                                                dataa.get(position).get("shopStatus"),
                                                dataa.get(position).get("pin"),
                                                dataa.get(position).get("phoneNo"),
                                                dataa.get(position).get("id"),
                                                dataa.get(position).get("notes"),
                                                dataa.get(position).get("total"),
                                                dataa.get(position).get("subtotal"),
                                                dataa.get(position).get("taxid"),
                                                dataa.get(position).get("tax"),
                                                dataa.get(position).get("order"), jsonArray, tot,
                                                dataa.get(position).get("ispaid"), d1,
                                                refundtrans, refundper, pikCharges, pikPercentage,
                                                dataa.get(position).get("userid"), refundForBusiness,
                                                dataa.get(position).get("shop_name"),
                                                dataa.get(position).get("shop_phno"),
                                                dataa.get(position).get("tranRef"),
                                                dataa.get(position).get("discountAmount"),
                                                dataa.get(position).get("offerDetails"),
                                                dataa.get(position).get("offerEnabled"));
                                    } catch (ParseException r) {
                                        r.printStackTrace();
                                    }
                                } else {
//                            Log.d("chk", "done:error =="+e.getMessage());
                                }
                            }
                        });

                    }
                    stat2 = true;
                }
//            }
        });
        holder.stsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (shop_sts.equals("2")) {
//                    Toast.makeText(context, "Shop is disabled. Please contact support", Toast.LENGTH_SHORT).show();
//
//                } else {
                    holder.cancelbtn.setEnabled(false);
                    if (stat1) {
                        holder.stsbtn.setBackgroundColor(ContextCompat.getColor(context, R.color.blue));
                    } else {
                        dialog = new ProgressDialog(v.getContext());
                        dialog.setMessage("Please wait.....");
                        dialog.setCancelable(false);
                        dialog.show();
                        holder.stsbtn.setBackgroundColor(ContextCompat.getColor(context, R.color.darkblue));

//                    HashMap<String, Object> params = new HashMap<String, Object>();
//                    params.put("totalCost",  dataa.get(position).get("total"));
                        String pik = "0";
                        pik = String.valueOf(ParseUser.getCurrentUser().getNumber("pikPercentage"));
                        if (pik != null) {

                            Double a = Double.valueOf(dataa.get(position).get("total"));
                            Double b = Double.valueOf(pik);
                            Double cres = a * b / 100;
                            String pikper = String.valueOf(pik);
                            String tottalcost = String.valueOf(cres);
                            SimpleDateFormat date1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy",
                                    Locale.ENGLISH);
                            Date d1 = null;
                            Date c = Calendar.getInstance().getTime();
                            try {
                                double tr;
                                try {
                                    tr = new Double(dataa.get(position).get("totaltime"));
                                } catch (NumberFormatException f) {
                                    tr = 0; // your default value
                                }
                                if (Build.VERSION.SDK_INT >= 23) {
                                    d1 = date1.parse(dataa.get(position).get("date"));
                                    // Call some material design APIs here
                                } else {
                                    DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
                                    d1 = (Date) formatter.parse(dataa.get(position).get("date"));
                                }
                                long different = c.getTime() - d1.getTime();
                                long secondsInMilli = 1000;
                                long minutesInMilli = secondsInMilli * 60;
                                long elapsedMinutes = different / minutesInMilli;
                                different = different % minutesInMilli;
                                long elapsedSeconds = different / secondsInMilli;
                                String tot = String.valueOf(tr + elapsedMinutes);
                                String tiim = elapsedMinutes + "." + elapsedSeconds;

                                JSONArray jsonArray = null;
                                try {
                                    jsonArray = new JSONArray(dataa.get(position).get("time"));

                                    if (jsonArray.length() > 0) {

                                        jsonArray.put(tiim);
                                    }
                                } catch (JSONException t1) {
                                    t1.printStackTrace();
//                                    Log.d("chk", "onClick:error "+ t1.getMessage());
                                }
//
                                Changestatus(dataa.get(position).get("objid"),
                                        dataa.get(position).get("lname"),
                                        dataa.get(position).get("sname"),
                                        dataa.get(position).get("lat"),
                                        dataa.get(position).get("log"),
                                        dataa.get(position).get("shopStatus"),
                                        dataa.get(position).get("pin"),
                                        dataa.get(position).get("phoneNo"),
                                        dataa.get(position).get("id"), v,
                                        dataa.get(position).get("notes"),
                                        dataa.get(position).get("total"),
                                        dataa.get(position).get("subtotal"),
                                        dataa.get(position).get("taxid"),
                                        dataa.get(position).get("tax"),
                                        dataa.get(position).get("order"), jsonArray, tot,
                                        dataa.get(position).get("ispaid"), d1, pikper, tottalcost,
                                        dataa.get(position).get("userid"), position,
                                        dataa.get(position).get("discountAmount"),
                                        dataa.get(position).get("offerDetails"),
                                        dataa.get(position).get("offerEnabled"));
                            } catch (ParseException r) {
                                r.printStackTrace();
                            }
                        }

                    }
                    stat1 = true;
                }
//            }
        });
        holder.txtamt.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return dataa.size();
    }
    public void Cancelorder(String id1, String refund, String refundcost,
                            String cancelby, View v, String lname, String sname, String lat
            , String log, String shopStatus, String pin, String phoneNo, String id, String notes,
                            String total, String subtotal, String taxid,
                            String tax, String order, JSONArray jsonArray,
                            String tot, String ispaid, Date date, String refundtrans,
                            String refundper, String pikCharges, String pikPercentage,
                            String userid, String refundForBusiness, String shop_name,
                            String shop_phno, String tranRef,
                            String discountAmount, String offerDetails, String offerEnabled) {
        String sts = "4";
        LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.cancelorder,null);
        AlertDialog.Builder alertbox = new AlertDialog.Builder(v.getRootView().getContext());
        alertbox.setView(layout);

        TextView desc = layout.findViewById(R.id.desc);
        EditText note = layout.findViewById(R.id.note);
        Button disagree = layout.findViewById(R.id.disagree);
        Button agree = layout.findViewById(R.id.agree);
        TextView tit = layout.findViewById(R.id.title);
        desc.setTypeface(Typer.set(context).getFont(Font.ROBOTO_REGULAR));
        note.setTypeface(Typer.set(context).getFont(Font.ROBOTO_REGULAR));
        disagree.setTypeface(Typer.set(context).getFont(Font.ROBOTO_MEDIUM));
        agree.setTypeface(Typer.set(context).getFont(Font.ROBOTO_MEDIUM));
        tit.setTypeface(Typer.set(context).getFont(Font.ROBOTO_MEDIUM));
//        Log.d("chk", "Cancelorder: "+refundcost);
        DecimalFormat df = new DecimalFormat("###0.00");
        refundcost = refundcost.replaceAll(",", "");
        Double refundd = Double.parseDouble(refundcost);

        desc.setText(" If you cancel the order you will be charged our fees"+"("+df.format(refundd)+" AED"+")");
        note.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() > 0){
                    agree.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary));
                }else{
                    agree.setTextColor(ContextCompat.getColor(context,R.color.gray));
                }

            }
        });
        String finalRefundcost = refundcost;
        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cancelnote = note.getText().toString().trim();
                if(cancelnote.length() > 1){
                    sendcancel(id1,sts,refund, finalRefundcost,cancelby,cancelnote,
                            lname,sname,lat,log,shopStatus,pin,phoneNo,id,v,
                            notes,total,subtotal,taxid,tax,order,jsonArray,tot,ispaid,date,
                            refundtrans,refundper,pikCharges,pikPercentage,
                            userid,refundForBusiness,shop_name,shop_phno,tranRef,offerDetails,offerEnabled,discountAmount);
                }else{
                    Toast.makeText(context, "Please provide cancel reason", Toast.LENGTH_SHORT).show();
                }

            }
        });
        disagree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                alertDialog.dismiss();
            }
        });
//        alertbox.setView(dialog);

        alertDialog = alertbox.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
    public void sendcancel(String idd1, String sts, String refd, String refuncost,
                           String cancelby, String cancelnote, String lname, String sname,
                           String lat, String log, String shopStatus, String pin, String phoneNo,
                           String id, View v, String notes, String total, String subtotal, String taxid,
                           String tax, String order, JSONArray jsonArray, String tot,
                           String ispaid, Date date, String refundtrans, String refundper,
                           String pikCharges, String pikPercentage, String userid,
                           String refundForBusiness, String shop_name, String shop_phno,
                           String tranRef, String offerDetails, String offerEnabled, String discountAmount){
        ParseObject shop = new ParseObject("OrderHistory");
        ParseObject obj = ParseObject.createWithoutData("ShopLocations",id);
        shop.put("shop", obj);
        shop.put("notes",notes);
        shop.put("orderNo",idd1);
        shop.put("orderCost", Double.valueOf(total));
        shop.put("orderTime",date);
        shop.put("orderStatus",4);
        shop.put("refundString",refd);
        shop.put("refundForBusiness",Double.valueOf(refundForBusiness));
        if(taxid != null){
            shop.put("taxId", taxid);
        }else{
            shop.put("taxId", "");
        }
        if(userid != null){
            ParseObject userobj = ParseObject.createWithoutData("_User",userid);
            shop.put("user",userobj);
            shop.put("userString",userid);
        }
        if(offerDetails != null){
            ParseObject offerobj = ParseObject.createWithoutData("Offers",offerDetails);
            shop.put("offerDetails",offerobj);
        }
        if(discountAmount != null){
            if(discountAmount.equals("null"))
            {
                shop.put("discountAmount",0);
            }else{
                shop.put("discountAmount",Double.valueOf(discountAmount));
            }

        }
        if(offerEnabled != null){
            if(offerEnabled.equals("true")){
                Boolean rf = true;
                shop.put("offerEnabled",rf);
            }else if(offerEnabled.equals("false")){
                Boolean rf = false;
                shop.put("offerEnabled",rf);
            }
        }
        DecimalFormat df = new DecimalFormat("###0.00");
        shop.put("tax", Integer.parseInt(tax));
        shop.put("subTotal",Double.valueOf(subtotal));
        shop.put("time",jsonArray);
        shop.put("cancelNote",cancelnote);
        shop.put("order",order);
        shop.put("owner", ParseUser.getCurrentUser());
        shop.put("business",ParseUser.getCurrentUser().getObjectId());
        shop.put("cancelledBy",cancelby);
        shop.put("shopstring",id);
        shop.put("currency",cur);
//        shop.put("totalTime",df.format(Double.valueOf(tot)));
        shop.put("totalCost",Double.valueOf(total));
        shop.put("refundPercentage",Integer.valueOf(refundper));
        shop.put("refundTrans",Integer.valueOf(refundtrans));
        shop.put("pikPercentage",Integer.valueOf(pikPercentage));
        // changed

        tot = tot.replaceAll(",", "");
        Double chkdis = Double.parseDouble(tot);
        shop.put("totalTime",Double.valueOf(df.format(chkdis)));

        pikCharges = pikCharges.replaceAll(",", "");
        Double pikstr = Double.parseDouble(pikCharges);
        shop.put("pikCharges",Double.valueOf(df.format(pikstr)));

        refuncost = refuncost.replaceAll(",", "");
        Double refdd = Double.parseDouble(refuncost);

        shop.put("refundCost",Double.valueOf(df.format(refdd)));
        shop.put("refundTime", 1);

        if(ispaid != null){
            Boolean b = Boolean.valueOf(ispaid);
            shop.put("isPaid",b);
        }

//        shop.saveInBackground();
        String finalRefuncost = refuncost;
        String finalPikCharges = pikCharges;
        shop.saveInBackground(e -> {
            // TODO Auto-generated method stub

            if (e == null) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Orders");
                ParseObject obj1 = ParseObject.createWithoutData("ShopLocations",id);
                query.whereEqualTo("shop", obj1);
                query.setCachePolicy(ParseQuery.CachePolicy.IGNORE_CACHE);
                query.getInBackground(idd1, new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, com.parse.ParseException e) {
                        if (e == null) {
//                            Log.d("chk", "done: deleted"+idd1);
                            object.deleteInBackground();
                            HashMap<String, String> params = new HashMap<>();
                            params.put("status", "3");
                            params.put("orderNo", idd1);
                            params.put("business",ParseUser.getCurrentUser().getObjectId());
                            params.put("location", id);
                            params.put("user", userid);
                            params.put("sendBy", "business");
                            params.put("cancelNote", cancelnote);
                            ParseCloud.callFunctionInBackground("push", params, (FunctionCallback<Float>) (ratings, re) -> {
                                if (re == null) {
                                    // ratings is 4.5
//                                    Log.d("chk", "done:chkkk ");
                                }else{
//                                    Log.d("chk", "done:error "+re.getMessage());
                                }
                            });
                            dialog.dismiss();
                            Intent i = new Intent(v.getContext(),Orderslist.class);
                            i.putExtra("lname",lname);
                            i.putExtra("bname",sname);
                            i.putExtra("id",id);
                            i.putExtra("lat",lat);
                            i.putExtra("log",log);
                            i.putExtra("shopStatus",shopStatus);
                            i.putExtra("pin",pin);
                            i.putExtra("phoneNo",phoneNo);
                            if(dataa.size() == 1){
                                i.putExtra("lastitem","1");
                            }
                            i.putExtra("stschk", "2");
                            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            v.getContext().startActivity(i);
                            HashMap<String, String> params2 = new HashMap<>();
                            params2.put("busObjId", ParseUser.getCurrentUser().getObjectId());
                            params2.put("busName", ParseUser.getCurrentUser().getString("Business_name"));
                            params2.put("busPhno",
                                    String.valueOf(ParseUser.getCurrentUser().getNumber("phoneNumber")));
                            params2.put("busEmail", ParseUser.getCurrentUser().getEmail());
                            params2.put("shopName",shop_name);
                            params2.put("shopPhno", shop_phno);
                            params2.put("orderNo", id);
                            params2.put("totalCost", total);
                            params2.put("cancelledBy", cancelby);
                            params2.put("cancelNote",cancelnote);
                            params2.put("pikPercentage", pikPercentage);
                            params2.put("pikCharges", String.valueOf(pikstr));
                            params2.put("tax",tax);
                            params2.put("tranRef", tranRef);
                            params2.put("refundString", refd);
                            params2.put("refundForBusiness", refundForBusiness);
                            params2.put("refundTime","1");
                            params2.put("refundCost", String.valueOf(refdd));
                            params2.put("refundPercentage",refundper);
                            params2.put("refundTrans",refundtrans);
//                            Log.d("chk", "done: "+params2.toString());
                            ParseCloud.callFunctionInBackground("emailAtCancel",
                                    params2,(FunctionCallback<Map<String,
                                            List<ParseObject>>>) (object3, re) -> {
                                        if (re == null) {
                                            Log.d("chk", "done:ank cancel at reg success ");
                                        }
                                        else{
                                            Log.d("chk", "done:error emailatreg"+re.getMessage());
                                        }
                                    });
//                            update(dataa);
                            Toast.makeText(context,"Order cancelled ", Toast.LENGTH_SHORT).show();
                        } else {
                            // something went wrong
                        }
                    }

                });
                alertDialog.dismiss();
//                notifyDataSetChanged();

            } else {
//                Log.d("chk", "done: "+e.getMessage());
            }
        });

    }
    private void deleteItem(int position) {
        dataa.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, dataa.size());
//        holder.itemView.setVisibility(View.GONE);
    }
    public void Changestatus(String idd, String lname, String bname,
                             String lat, String log, String shopStatus, String pin, String phoneNo,
                             String id, View v, String notes, String total, String subtotal,
                             String taxid,
                             String tax, String order, JSONArray jsonArray,
                             String tot, String ispaid, Date d1, String pikper,
                             String tottalcost, String userid, int pos,
                             String discountAmount, String offerDetails, String offerEnabled) {
        ParseObject shop = new ParseObject("OrderHistory");
        ParseObject obj = ParseObject.createWithoutData("ShopLocations",id);
        shop.put("shop", obj);
        shop.put("notes",notes);
        shop.put("orderNo",idd);
        shop.put("orderCost", Double.valueOf(total));
        shop.put("orderTime",d1);
        shop.put("orderStatus",3);
        if(taxid != null){
            shop.put("taxId", taxid);
        }else{
            shop.put("taxId", "");
        }
        shop.put("tax", Integer.parseInt(tax));
        shop.put("subTotal",Double.valueOf(subtotal));
        if(jsonArray != null){
            shop.put("time",jsonArray);
        }
        if(userid != null){
            ParseObject userobj = ParseObject.createWithoutData("_User",userid);
            shop.put("user",userobj);
            shop.put("userString",userid);
        }
        if(offerDetails != null){
            ParseObject offerobj = ParseObject.createWithoutData("Offers",offerDetails);
            shop.put("offerDetails",offerobj);
        }
        if(discountAmount != null){
            if(discountAmount.equals("null"))
            {
                shop.put("discountAmount",0);
            }else{
                shop.put("discountAmount",Double.valueOf(discountAmount));
            }
        }
        if(offerEnabled != null){
            if(offerEnabled.equals("true")){
                Boolean rf = true;
                shop.put("offerEnabled",rf);
            }else if(offerEnabled.equals("false")){
                Boolean rf = false;
                shop.put("offerEnabled",rf);
            }
        }
//        shop.put("refundCost",Double.valueOf(refuncost));
        DecimalFormat df = new DecimalFormat("###0.00");
        shop.put("order",order);
        shop.put("owner", ParseUser.getCurrentUser());
        shop.put("business",ParseUser.getCurrentUser().getObjectId());
        shop.put("shopstring",id);
        shop.put("currency",cur);
        shop.put("totalCost",Double.valueOf(total));

        tot = tot.replaceAll(",", "");
        Double chkdis = Double.parseDouble(tot);
        shop.put("totalTime",Double.valueOf(df.format(chkdis)));

        tottalcost = tottalcost.replaceAll(",", "");
        Double pikstr = Double.parseDouble(tottalcost);
        shop.put("pikCharges",Double.valueOf(df.format(pikstr)));

        shop.put("pikPercentage",Double.valueOf(pikper));

        if(ispaid != null){
            Boolean b = Boolean.valueOf(ispaid);
            shop.put("isPaid",b);
        }
        shop.saveInBackground(e -> {
            // TODO Auto-generated method stub

            if (e == null) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Orders");
                ParseObject obj1 = ParseObject.createWithoutData("ShopLocations",id);
                query.whereEqualTo("shop", obj1);
                query.setCachePolicy(ParseQuery.CachePolicy.IGNORE_CACHE);
                query.getInBackground(idd, new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, com.parse.ParseException e) {
                        if (e == null) {
                            object.deleteInBackground();
                            HashMap<String, String> params = new HashMap<>();
                            params.put("status", "3");
                            params.put("orderNo", idd);
                            params.put("business",ParseUser.getCurrentUser().getObjectId());
                            params.put("location", id);
                            params.put("user", userid);
                            params.put("sendBy", "business");
                            params.put("cancelNote", "");
                            ParseCloud.callFunctionInBackground("push", params, (FunctionCallback<Float>) (ratings, re) -> {
                                if (re == null) {
                                    // ratings is 4.5
//                                    Log.d("chk", "done:chkkk =1");
                                }else{
//                                    Log.d("chk", "done:error "+re.getMessage());
                                }
                            });
                            if(dialog != null){
                                dialog.dismiss();
                            }

//                            Log.d("chk", "done:chkkk =2");
                            Intent i = new Intent(v.getContext(),Orderslist.class);
                            i.putExtra("lname",lname);
                            i.putExtra("bname",bname);
                            i.putExtra("id",id);
                            i.putExtra("lat",lat);
                            i.putExtra("log",log);
                            i.putExtra("shopStatus",shopStatus);
                            i.putExtra("pin",pin);
                            if(dataa.size() == 1){
                                i.putExtra("lastitem","1");
                            }
                            i.putExtra("phoneNo",phoneNo);
                            i.putExtra("stschk", "2");
                            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            v.getContext().startActivity(i);
//                             update(dataa);
                            Toast.makeText(context, "Order Pickedup ", Toast.LENGTH_SHORT).show();
//                            deleteItem(pos);

                        } else {
                            Log.d("chk", "done:ck "+e.getMessage());
                            // something went wrong
                        }
                    }

                });

            } else {
                Log.d("chk", "done:last "+e.getMessage());
            }
        });

    }
    public void setData(ArrayList<HashMap<String, String>> newData) {
        this.dataa.clear();
        dataa.addAll(newData);
        notifyDataSetChanged();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {


        Timer updateTimer;
        @BindView(R.id.call) TextView call;
        @BindView(R.id.msg) TextView msg;
        @BindView(R.id.shopname) TextView shopname;
        @BindView(R.id.distance) TextView distance;
        @BindView(R.id.timer) TextView time;
        @BindView(R.id.note) TextView note;
        @BindView(R.id.itemslist)RecyclerView itemslist;
        @BindView(R.id.itemslay) LinearLayout itemslay;
        @BindView(R.id.headcolor) LinearLayout headcolor;
        @BindView(R.id.total) TextView total;
        @BindView(R.id.stsbtn) Button stsbtn;
        @BindView(R.id.cancelbtn)Button cancelbtn;
        @BindView(R.id.txt_amt) TextView txtamt;
        @BindView(R.id.vatper) TextView vatper;
        @BindView(R.id.vatprice) TextView vatprice;
        @BindView(R.id.subtotal) TextView subtotal;
        @BindView(R.id.subtottxt) TextView subtottxt;
        @BindView(R.id.totaltxt) TextView totaltxt;
        @BindView(R.id.amttxt)TextView amttxt;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            call.setTypeface(Typer.set(context).getFont(Font.ROBOTO_REGULAR));
            msg.setTypeface(Typer.set(context).getFont(Font.ROBOTO_REGULAR));
            shopname.setTypeface(Typer.set(context).getFont(Font.ROBOTO_REGULAR));
            distance.setTypeface(Typer.set(context).getFont(Font.ROBOTO_REGULAR));
            time.setTypeface(Typer.set(context).getFont(Font.ROBOTO_REGULAR));
            note.setTypeface(Typer.set(context).getFont(Font.ROBOTO_REGULAR));
            vatper.setTypeface(Typer.set(context).getFont(Font.ROBOTO_REGULAR));
            vatprice.setTypeface(Typer.set(context).getFont(Font.ROBOTO_REGULAR));
            subtotal.setTypeface(Typer.set(context).getFont(Font.ROBOTO_REGULAR));
            total.setTypeface(Typer.set(context).getFont(Font.ROBOTO_MEDIUM));
            stsbtn.setTypeface(Typer.set(context).getFont(Font.ROBOTO_MEDIUM));
            cancelbtn.setTypeface(Typer.set(context).getFont(Font.ROBOTO_MEDIUM));
            subtottxt.setTypeface(Typer.set(context).getFont(Font.ROBOTO_REGULAR));
            totaltxt.setTypeface(Typer.set(context).getFont(Font.ROBOTO_REGULAR));
            amttxt.setTypeface(Typer.set(context).getFont(Font.ROBOTO_REGULAR));
        }
    }
    private class ItemslistAdapter extends RecyclerView.Adapter<ItemslistAdapter.MyViewHolder2> {

        ArrayList<HashMap<String, String>> listdata;
        Context context;

        public ItemslistAdapter(Context context, ArrayList<HashMap<String, String>> aryList) {
            this.context = context;
            this.listdata = aryList;
        }
        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }
        @Override
        public MyViewHolder2 onCreateViewHolder(ViewGroup parent, int viewType) {
            // infalte the item Layout

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.orderitems_layout, parent, false);
            return new MyViewHolder2(v);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindViewHolder(MyViewHolder2 holder, final int position) {
            // set the data in items
            String extranm = listdata.get(position).get("extra_name");
            String prz = listdata.get(position).get("item_price");
//            String extraName = listdata.get(position).get("item_name");
            String ct = listdata.get(position).get("category_name");

            holder.itemname.setText(listdata.get(position).get("item_name"));
            holder.itemprice.setText(prz);
            holder.catname.setText(ct);
            if (extranm != null && extranm.length() > 0){
                holder.extraname.setVisibility(View.VISIBLE);
                holder.extraname.setText(extranm);
            }else{
                holder.extraname.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return listdata.size();
        }

        public class MyViewHolder2 extends RecyclerView.ViewHolder {

            TextView catname,itemname,itemprice,extraname;

            public MyViewHolder2(View itemView) {
                super(itemView);

                catname = itemView.findViewById(R.id.catname);
                itemname = itemView.findViewById(R.id.itemname);
                itemprice = itemView.findViewById(R.id.itemprice);
                extraname = itemView.findViewById(R.id.extraname);

            }
        }
    }
}


