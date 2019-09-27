package com.pikbusiness.Adapters;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elmargomez.typer.Font;
import com.elmargomez.typer.Typer;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.pikbusiness.Activity.OrderListActivityNew;
import com.pikbusiness.R;
import com.pikbusiness.model.Response.OrderItem;
import com.pikbusiness.model.Response.Orders;

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

public class OrderListAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private List<String> listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<Orders>> listDataChild;
    AlertDialog alertDialog;
    String currencyType;
    ProgressDialog dialog;
    private List<OrderItem> orderItemList;
    private OrderItemListAdapter orderItemAdapter;
    Timer updateTimer = new Timer();
    int count = 0;
    TimerTask timerTask;
    ExpandableListView expandableList;

    public OrderListAdapter(OrderListActivityNew context, List<String> Header, HashMap<String, List<Orders>> Child,
                            ExpandableListView orderExpandableList) {
        mContext = context;
        listDataHeader = Header;
        listDataChild = Child;
        orderItemList = new ArrayList<>();
        expandableList = orderExpandableList;

    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return listDataChild.get(listDataHeader.get(groupPosition)).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView,
                             ViewGroup parent) {

        final Orders childData = (Orders) getChild(groupPosition, childPosition);

        TextView tvCustomerName;
        TextView tvDistance;
        TextView tvDistanceTime;
        TextView tvTimer;
        TextView tvNote;
        TextView tvSubTotal;
        TextView tvVatText;
        TextView tvVatPrice;
        TextView tvTotal;
        TextView tvCancelOrder;
        TextView tvTagLine;
        TextView btnCall;
        LinearLayout headBackgroundLayout;
        Button btnChangeStatus;
        RecyclerView itemRecyclerView;


        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.order_list_child, parent, false);
            View myView = row.findViewById(R.id.child_view);
            ViewHolder holder = new ViewHolder();
            holder.addView(myView);
            row.setTag(holder);
        }

        // Get the stored ViewHolder that also contains our views
        ViewHolder holder = (ViewHolder) row.getTag();
        View myView = holder.getView(R.id.child_view);
        tvCustomerName = myView.findViewById(R.id.customer_name);

        //  tvCustomerName = (TextView) row.findViewById(R.id.customer_name);
        tvDistance = (TextView) row.findViewById(R.id.tv_distance);
        tvDistanceTime = (TextView) row.findViewById(R.id.tv_distance_time);
        headBackgroundLayout = (LinearLayout) row.findViewById(R.id.head_background_color);
        tvTimer = (TextView) myView.findViewById(R.id.timer);
        itemRecyclerView = (RecyclerView) row.findViewById(R.id.item_recycler_view);
        orderItemAdapter = new OrderItemListAdapter(mContext, orderItemList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        itemRecyclerView.setLayoutManager(mLayoutManager);
        itemRecyclerView.setItemAnimator(new DefaultItemAnimator());
        itemRecyclerView.setAdapter(orderItemAdapter);


        tvNote = (TextView) row.findViewById(R.id.tv_note);
        tvSubTotal = (TextView) row.findViewById(R.id.tv_subtotal);
        tvVatText = (TextView) row.findViewById(R.id.tv_vat_txt);
        tvVatPrice = (TextView) row.findViewById(R.id.vatprice);
        tvTotal = (TextView) row.findViewById(R.id.tv_total);
        btnChangeStatus = (Button) row.findViewById(R.id.btn_change_status);
        tvCancelOrder = (TextView) row.findViewById(R.id.tv_cancel_order);
        tvTagLine = (TextView) row.findViewById(R.id.txt_tag_line);
        btnCall = (Button) row.findViewById(R.id.call);


        currencyType = childData.getEstimatedData().getCurrency();
        tvCustomerName.setText(childData.getEstimatedData().getCustomerName());
        btnChangeStatus.setText(childData.getEstimatedData().getButtonStatus());
        if (childData.getEstimatedData().getButtonStatus().equals("Pick up")) {
            tvTagLine.setVisibility(View.GONE);
        } else {
            tvTagLine.setVisibility(View.VISIBLE);
        }
        if (childData.getEstimatedData().getNotes() != null) {
            if (childData.getEstimatedData().getNotes().length() > 0) {
                tvNote.setText("Note : " + childData.getEstimatedData().getNotes());
            } else {
                tvNote.setVisibility(View.GONE);
            }
        } else {
            tvNote.setVisibility(View.GONE);
        }


        /*This function is to check distance and distance time start*/
        if (childData.getEstimatedData().getLocation().getLatitude() != null && childData.getEstimatedData().getLocation().getLongitude() != null
                && childData.getEstimatedData().getUserLocation().getUserlatitude() != null && childData.getEstimatedData().getUserLocation().getUserlongitude() != null) {
            Double sl = Double.valueOf(childData.getEstimatedData().getLocation().getLatitude());
            Double slg = Double.valueOf(childData.getEstimatedData().getLocation().getLongitude());
            Double ul = Double.valueOf(childData.getEstimatedData().getUserLocation().getUserlatitude());
            Double ulg = Double.valueOf(childData.getEstimatedData().getUserLocation().getUserlongitude());

            Location startPoint = new Location("Shop");
            startPoint.setLatitude(sl);
            startPoint.setLongitude(slg);
            Location endPoint = new Location("User");
            endPoint.setLatitude(ul);
            endPoint.setLongitude(ulg);
            float distanceInMeters = startPoint.distanceTo(endPoint);
            Integer intmeters = (int) distanceInMeters;
            //For example spead is 10 meters per minute.
            int speedIs10MetersPerMinute = 60;
            float estimatedDriveTimeInMinutes = distanceInMeters / speedIs10MetersPerMinute;
            Integer intValue = (int) estimatedDriveTimeInMinutes;
            if (intValue > 60) {

                if (intValue / 60 > 1) {
                    tvDistanceTime.setText(intValue / 60 + " hours away");
                } else {
                    tvDistanceTime.setText(intValue / 60 + " hour away");
                }
            } else {
                tvDistanceTime.setText(intValue + " minutes away");
            }

            double distance = startPoint.distanceTo(endPoint) / 1000;
            NumberFormat nf = NumberFormat.getInstance(); // get instance
            nf.setMaximumFractionDigits(0); // set decimal places
            Integer intdist = (int) distance;
            // String s = nf.format(distance);
            if (intdist < 1) {
                tvDistance.setText(intmeters + " m");
            } else {
                tvDistance.setText(intdist + " kms");
            }

            /* end logic to check distance and distance time*/
        }



        /*  start logic to show category name and other*/
        double itemPriceIncludingExtra = 0;
        double totalPrice = 0;
        double finalPrice = 0;
        try {
            JSONArray orderArray = new JSONArray(childData.getEstimatedData().getOrder());
            orderItemList.clear();
            for (int i = 0; i < orderArray.length(); i++) {
                OrderItem item = new OrderItem();
                JSONObject rowObject = orderArray.getJSONObject(i);
                System.out.println("row Object ===" + rowObject);
                String menuName = rowObject.getString("menuName");
                String count = rowObject.getString("count");
                String itemTotalPrice = rowObject.getString("itemTotalPrice").replace(currencyType, "");
                String categoryName = rowObject.getString("categoryName");
                String menuPrice = rowObject.getString("menuPrice");
                String tax = String.valueOf(childData.getEstimatedData().getTax());
                double itemPrice = Integer.parseInt(count) * Double.valueOf(menuPrice);
                JSONArray extrasArray = rowObject.getJSONArray("extras");
                StringBuilder sb = new StringBuilder();
                StringBuilder sb2 = new StringBuilder();
                if (extrasArray.length() > 0) {
                    for (int j = 0; j < extrasArray.length(); j++) {
                        JSONObject nameValueObject = extrasArray.getJSONObject(j);
                        String extraName = nameValueObject.getString("extraName");
                        double extraPrice = Double.parseDouble(nameValueObject.getString("extraPrice"));
                        itemPriceIncludingExtra = extraPrice + itemPrice;

                        sb.append(extraName);
                        //sb.append(" + ");
                    }
                } else {
                    itemPriceIncludingExtra = itemPrice;
                }
                item.setExtraItem(sb.toString());
                //  tvExtraItem.setText(sb);
                if (sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 2);
                }
                item.setCategoryName(categoryName);
                item.setMenuName(menuName + " x " + count);
                item.setItemPrice(itemPriceIncludingExtra + " " + currencyType);
                tvVatText.setText("Vat" + " " + tax + "%");
                totalPrice = itemPriceIncludingExtra + totalPrice;
                tvSubTotal.setText(totalPrice + " " + currencyType);
                Double taxCalculation = totalPrice * (Double.parseDouble(tax)/100);
                tvVatPrice.setText(taxCalculation + " " + currencyType);
                finalPrice = totalPrice + taxCalculation;
                tvTotal.setText(finalPrice + " " + currencyType);
                // item.setTotalPrice(totalPrice);
                orderItemList.add(item);

            }
            orderItemAdapter.notifyDataSetChanged();


        } catch (JSONException e) {
            e.printStackTrace();
        }





//        /* start logic to set header color and count down timer*/

        if (childData.getEstimatedData().getOrderStatus() == 0) {
            timerTask = new TimerTask() {
                public void run() {
                    try {

                        SimpleDateFormat date1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
                        Date date = null;
                        Date c = Calendar.getInstance().getTime();
                        if (Build.VERSION.SDK_INT >= 23) {
                            try {
                                date = date1.parse(childData.getEstimatedData().getCreatedDateAt());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else {
                            DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
                            try {
                                date = (Date) formatter.parse(childData.getEstimatedData().getCreatedDateAt());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        long diffTimeMillis = c.getTime() - date.getTime();

                        long secondsInMilli = 1000;
                        long minutesInMilli = secondsInMilli * 60;
                        long hoursInMilli = minutesInMilli * 60;
                        long daysInMilli = hoursInMilli * 24;

                        diffTimeMillis = diffTimeMillis % daysInMilli;
                        long elapsedHours = diffTimeMillis / hoursInMilli;
                        diffTimeMillis = diffTimeMillis % hoursInMilli;
                        long elapsedMinutes = diffTimeMillis / minutesInMilli;
                        diffTimeMillis = diffTimeMillis % minutesInMilli;
                        long elapsedSeconds = diffTimeMillis / secondsInMilli;

                        Handler mainThread = new Handler(Looper.getMainLooper());
                        mainThread.post(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("group position ===" + groupPosition);
                                System.out.println("child Position  ===" + childPosition);

                                if (elapsedHours == 0) {
                                    tvTimer.setText(elapsedMinutes + ":" + elapsedSeconds);
                                    System.out.println("minutes==" + elapsedMinutes + ":" + "seconds==" + elapsedSeconds);
                                } else {
                                    tvTimer.setText(elapsedHours + ":" + elapsedMinutes + ":" + elapsedSeconds);
                                    System.out.println("elapsedHours==" + elapsedHours + "minutes==" + elapsedMinutes + ":" +
                                            "seconds==" + elapsedSeconds);
                                }

                                if (elapsedHours == 0) {
                                    if (elapsedMinutes < 3) {
                                        headBackgroundLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.green));
                                    } else if (elapsedMinutes >= 3 && elapsedMinutes < 5) {
                                        headBackgroundLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.yellow));
                                    } else if (elapsedMinutes >= 5) {
                                        headBackgroundLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.red));
                                    }
                                } else {
                                    headBackgroundLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.red));
                                }

                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            };

            updateTimer.schedule(timerTask, 60, 1000);

        } else if (childData.getEstimatedData().getOrderStatus() == 1) {
            timerTask = new TimerTask() {
                public void run() {
                    try {

                        SimpleDateFormat date1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
                        Date date = null;
                        Date c = Calendar.getInstance().getTime();
                        if (Build.VERSION.SDK_INT >= 23) {
                            try {
                                date = date1.parse(childData.getEstimatedData().getCreatedDateAt());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else {
                            DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
                            try {
                                date = (Date) formatter.parse(childData.getEstimatedData().getCreatedDateAt());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        long diffTimeMillis = c.getTime() - date.getTime();

                        long secondsInMilli = 1000;
                        long minutesInMilli = secondsInMilli * 60;
                        long hoursInMilli = minutesInMilli * 60;
                        long daysInMilli = hoursInMilli * 24;

                        diffTimeMillis = diffTimeMillis % daysInMilli;
                        long elapsedHours = diffTimeMillis / hoursInMilli;
                        diffTimeMillis = diffTimeMillis % hoursInMilli;
                        long elapsedMinutes = diffTimeMillis / minutesInMilli;
                        diffTimeMillis = diffTimeMillis % minutesInMilli;
                        long elapsedSeconds = diffTimeMillis / secondsInMilli;

                        Handler mainThread = new Handler(Looper.getMainLooper());
                        mainThread.post(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("group position ===" + groupPosition);
                                System.out.println("child Position  ===" + childPosition);

                                if (elapsedHours == 0) {
                                    tvTimer.setText(elapsedMinutes + ":" + elapsedSeconds);
                                    System.out.println("minutes==" + elapsedMinutes + ":" + "seconds==" + elapsedSeconds);
                                } else {
                                    tvTimer.setText(elapsedHours + ":" + elapsedMinutes + ":" + elapsedSeconds);
                                    System.out.println("elapsedHours==" + elapsedHours + "minutes==" + elapsedMinutes + ":" +
                                            "seconds==" + elapsedSeconds);
                                }

                                if (elapsedHours == 0) {
                                    if (elapsedMinutes < 3) {
                                        headBackgroundLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.green));
                                    } else if (elapsedMinutes >= 3 && elapsedMinutes < 5) {
                                        headBackgroundLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.yellow));
                                    } else if (elapsedMinutes >= 5) {
                                        headBackgroundLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.red));
                                    }
                                } else {
                                    headBackgroundLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.red));
                                }

                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            };

            updateTimer.schedule(timerTask, 60, 1000);
        } else if (childData.getEstimatedData().getOrderStatus() == 2) {
            timerTask = new TimerTask() {
                public void run() {
                    try {

                        SimpleDateFormat date1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
                        Date date = null;
                        Date c = Calendar.getInstance().getTime();
                        if (Build.VERSION.SDK_INT >= 23) {
                            try {
                                date = date1.parse(childData.getEstimatedData().getCreatedDateAt());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else {
                            DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
                            try {
                                date = (Date) formatter.parse(childData.getEstimatedData().getCreatedDateAt());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        long diffTimeMillis = c.getTime() - date.getTime();

                        long secondsInMilli = 1000;
                        long minutesInMilli = secondsInMilli * 60;
                        long hoursInMilli = minutesInMilli * 60;
                        long daysInMilli = hoursInMilli * 24;

                        diffTimeMillis = diffTimeMillis % daysInMilli;
                        long elapsedHours = diffTimeMillis / hoursInMilli;
                        diffTimeMillis = diffTimeMillis % hoursInMilli;
                        long elapsedMinutes = diffTimeMillis / minutesInMilli;
                        diffTimeMillis = diffTimeMillis % minutesInMilli;
                        long elapsedSeconds = diffTimeMillis / secondsInMilli;

                        Handler mainThread = new Handler(Looper.getMainLooper());
                        mainThread.post(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("group position ===" + groupPosition);
                                System.out.println("child Position  ===" + childPosition);

                                if (elapsedHours == 0) {
                                    tvTimer.setText(elapsedMinutes + ":" + elapsedSeconds);
                                    System.out.println("minutes==" + elapsedMinutes + ":" + "seconds==" + elapsedSeconds);
                                } else {
                                    tvTimer.setText(elapsedHours + ":" + elapsedMinutes + ":" + elapsedSeconds);
                                    System.out.println("elapsedHours==" + elapsedHours + "minutes==" + elapsedMinutes + ":" +
                                            "seconds==" + elapsedSeconds);
                                }

                                if (elapsedHours == 0) {
                                    if (elapsedMinutes < 3) {
                                        headBackgroundLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.green));
                                    } else if (elapsedMinutes >= 3 && elapsedMinutes < 5) {
                                        headBackgroundLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.yellow));
                                    } else if (elapsedMinutes >= 5) {
                                        headBackgroundLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.red));
                                    }
                                } else {
                                    headBackgroundLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.red));
                                }

                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            };

            updateTimer.schedule(timerTask, 60, 1000);
        } else {
            tvTimer.setText("");
        }
        /* end logic to set header color and count down timer*/


        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (String.valueOf(childData.getEstimatedData().getCustomerPhoneNumber()) != null) {
                    if (String.valueOf(childData.getEstimatedData().getCustomerPhoneNumber()).length() > 0) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + "+" + childData.getEstimatedData().getCustomerPhoneNumber()));
                        mContext.startActivity(intent);
                    } else {
                        Toast.makeText(mContext, "user not provided phone number", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(mContext, "user not provided phone number", Toast.LENGTH_SHORT).show();

                }
            }
        });

        btnChangeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog = new ProgressDialog(v.getContext());
                dialog.setMessage("Please wait.....");
                dialog.setCancelable(false);
                dialog.show();

                if (childData.getEstimatedData().getOrderStatus() == 0 ||
                        childData.getEstimatedData().getOrderStatus() == 1) {

                    SimpleDateFormat date1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy",
                            Locale.ENGLISH);
                    Date d1 = null;
                    Date c = Calendar.getInstance().getTime();
                    try {
                        double tr;
                        try {
                            tr = new Double(String.valueOf(childData.getEstimatedData().getTotalTime()));
                        } catch (NumberFormatException f) {
                            tr = 0; // your default value
                        }
                        if (Build.VERSION.SDK_INT >= 23) {
                            d1 = date1.parse(childData.getEstimatedData().getCreatedDateAt());
                            // Call some material design APIs here
                        } else {
                            DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
                            d1 = (Date) formatter.parse(childData.getEstimatedData().getCreatedDateAt());
                        }
                        long different = c.getTime() - d1.getTime();
                        long secondsInMilli = 1000;
                        long minutesInMilli = secondsInMilli * 60;
                        long elapsedMinutes = different / minutesInMilli;
                        different = different % minutesInMilli;
                        long elapsedSeconds = different / secondsInMilli;
                        String totalTime = String.valueOf(tr + elapsedMinutes);
                        String tiim = elapsedMinutes + "." + elapsedSeconds;

                        JSONArray jsonArray = null;

                        if (childData.getEstimatedData().getOrderStatus() == 1) {

                            try {
                                jsonArray = new JSONArray(childData.getEstimatedData().getTime());

                                if (jsonArray.length() > 0) {

                                    jsonArray.put(tiim);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d("chk", "onClick: " + e.getMessage());
                            }

                            changeStatus(childData.getEstimatedData().getObjectId(),
                                    childData.getEstimatedData().getLocationName(),
                                    childData.getEstimatedData().getShopLocationName(),
                                    childData.getEstimatedData().getLocation().getLatitude(),
                                    childData.getEstimatedData().getLocation().getLongitude(),
                                    childData.getEstimatedData().getShopStatus(),
                                    childData.getEstimatedData().getPin(),
                                    childData.getEstimatedData().getPhoneNo(),
                                    childData.getEstimatedData().getLocationObjectId(), v, tiim, totalTime,
                                    childData.getEstimatedData().getUserId(),
                                    childData.getEstimatedData().getOrderStatus(),
                                    childData, childPosition, groupPosition, jsonArray);

                        } else if (childData.getEstimatedData().getOrderStatus() == 0) {
                            changeStatus(childData.getEstimatedData().getObjectId(),
                                    childData.getEstimatedData().getLocationName(),
                                    childData.getEstimatedData().getShopLocationName(),
                                    childData.getEstimatedData().getLocation().getLatitude(),
                                    childData.getEstimatedData().getLocation().getLongitude(),
                                    childData.getEstimatedData().getShopStatus(),
                                    childData.getEstimatedData().getPin(),
                                    childData.getEstimatedData().getPhoneNo(),
                                    childData.getEstimatedData().getLocationObjectId(), v, tiim, totalTime,
                                    childData.getEstimatedData().getUserId(),
                                    childData.getEstimatedData().getOrderStatus(),
                                    childData, childPosition, groupPosition, jsonArray);
                        } else {
                            // do nothing
                        }


                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                } else if (childData.getEstimatedData().getOrderStatus() == 2) {

                    String pik = "0";
                    pik = String.valueOf(ParseUser.getCurrentUser().getNumber("pikPercentage"));
                    if (pik != null) {

                        Double a = Double.valueOf(String.valueOf(childData.getEstimatedData().getTotalCost()));
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
                                tr = new Double(String.valueOf(childData.getEstimatedData().getTotalTime()));
                            } catch (NumberFormatException f) {
                                tr = 0; // your default value
                            }
                            if (Build.VERSION.SDK_INT >= 23) {
                                try {
                                    d1 = date1.parse(childData.getEstimatedData().getCreatedDateAt());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                // Call some material design APIs here
                            } else {
                                DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
                                try {
                                    d1 = (Date) formatter.parse(childData.getEstimatedData().getTime());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
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
                                jsonArray = new JSONArray(childData.getEstimatedData().getTime());

                                if (jsonArray.length() > 0) {

                                    jsonArray.put(tiim);
                                }
                            } catch (JSONException t1) {
                                t1.printStackTrace();
//                                    Log.d("chk", "onClick:error "+ t1.getMessage());
                            }

                            try {
                                jsonArray = new JSONArray(childData.getEstimatedData().getTime());

                                if (jsonArray.length() > 0) {

                                    jsonArray.put(tiim);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d("chk", "onClick: " + e.getMessage());
                            }


                            changeReadyStatus(childData.getEstimatedData().getObjectId(), childData.getEstimatedData().getLocationObjectId(),
                                    v, childData.getEstimatedData().getNotes(), childData.getEstimatedData().getTotalTime(),
                                    childData.getEstimatedData().getSubTotal(), childData.getEstimatedData().getTaxId(),
                                    childData.getEstimatedData().getTax(), childData.getEstimatedData().getOrder(),
                                    jsonArray, childData.getEstimatedData().getIsPaid(), childData.getEstimatedData().getCreatedDateAt(),
                                    pikper, childData.getEstimatedData().getTotalCost(), childData.getEstimatedData().getUserId(),
                                    childPosition, groupPosition, childData, childData.getEstimatedData().getDiscountAmount(),
                                    childData.getEstimatedData().getOfferObjectId(), childData.getEstimatedData().getOfferEnanbled(),tot,d1);

                        } catch (Exception t1) {
                            t1.printStackTrace();
//                                    Log.d("chk", "onClick:error "+ t1.getMessage());
                        }

                    }
                } else {
                    // do nothing

                }
            }
        });


        tvCancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog = new ProgressDialog(v.getContext());
                dialog.setMessage("Please wait.....");
                dialog.setCancelable(false);
                dialog.show();
                String id1 = childData.getEstimatedData().getObjectId();
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("cancelledBy", "business");
                params.put("status", childData.getEstimatedData().getOrderStatus());
                params.put("orderNo", id1);
                params.put("totalCost", childData.getEstimatedData().getTotalCost());
                ParseCloud.callFunctionInBackground("refund", params,
                        (FunctionCallback<Map<String, List<ParseObject>>>) (object, e) -> {
                            if (e == null) {
//                        Log.d("chk", "onClick:refund "+object);
                                SimpleDateFormat date1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy",
                                        Locale.ENGLISH);
                                Date d1 = null;
                                Date c = Calendar.getInstance().getTime();
                                String cacenlby = String.valueOf(object.get("cancelledBy"));
                                String refuncost = String.valueOf(object.get("refundCost"));
                                String refundtrans = String.valueOf(object.get("refundTrans"));
                                String refundper = String.valueOf(object.get("refundPercentage"));
                                String pikPercentage = String.valueOf(object.get("pikPercentage"));
                                String pikCharges = String.valueOf(object.get("pikCharges"));
                                String refundForBusiness = String.valueOf(object.get("refundForBusiness"));
                                String refund = String.valueOf(object.get("refund"));

                                if (Build.VERSION.SDK_INT >= 23) {
                                    try {
                                        d1 = date1.parse(childData.getEstimatedData().getCreatedDateAt());
                                    } catch (java.text.ParseException e1) {
                                        e1.printStackTrace();
                                    }
                                    // Call some material design APIs here
                                } else {
                                    DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
                                    try {
                                        d1 = (Date) formatter.parse(childData.getEstimatedData().getCreatedDateAt());
                                    } catch (java.text.ParseException e1) {
                                        e1.printStackTrace();
                                    }
                                }

                                long different = c.getTime() - d1.getTime();
                                long secondsInMilli = 1000;
                                long minutesInMilli = secondsInMilli * 60;
                                long elapsedMinutes = different / minutesInMilli;
                                different = different % minutesInMilli;
                                long elapsedSeconds = different / secondsInMilli;
                                String tiim = elapsedMinutes + "." + elapsedSeconds;

                                cancelOrder(childData.getEstimatedData().getObjectId(),
                                        refuncost, cacenlby, v,
                                        childData.getEstimatedData().getLocationObjectId(),
                                        childData.getEstimatedData().getNotes(),
                                        childData.getEstimatedData().getTotalCost(),
                                        childData.getEstimatedData().getSubTotal(),
                                        childData.getEstimatedData().getTaxId(),
                                        childData.getEstimatedData().getTax(),
                                        childData.getEstimatedData().getOrder(),
                                        tiim, childData.getEstimatedData().getIsPaid(), d1,
                                        refundtrans, refundper, pikCharges, pikPercentage,
                                        childData.getEstimatedData().getUserId(), refund,
                                        refundForBusiness,
                                        childData.getEstimatedData().getDiscountAmount(),
                                        childData.getEstimatedData().getOfferObjectId(),
                                        childData.getEstimatedData().getOfferEnanbled(),
                                        childData.getEstimatedData().getShopLocationName(),
                                        childData.getEstimatedData().getShopPhoneNo(),
                                        childData.getEstimatedData().getTranRef(), childPosition, groupPosition, childData, parent);


                            } else {
                                Log.d("chk", "done:error ==" + e.getMessage());
                            }
                        });
            }
        });



        return row;
    }

    private void changeReadyStatus(String objectId, String locationObjectId, View v, String notes, Number
            totalTime, Number subTotal, String taxId, int tax, String order, JSONArray jsonArray,
                                   Boolean isPaid, String createdDateAt, String pikper, Number totalCost, String userId,
                                   int childPosition, int groupPosition, Orders childData, String discountAmount,
                                   String offerObjectId, Boolean offerEnanbled, String tot, Date d1) {


        ParseObject shop = new ParseObject("OrderHistory");
        ParseObject obj = ParseObject.createWithoutData("ShopLocations",locationObjectId);
        shop.put("shop", obj);
        shop.put("notes",notes);
        shop.put("orderNo",objectId);
        shop.put("orderCost", Double.valueOf(String.valueOf(totalCost)));
        shop.put("orderTime",d1);
        shop.put("orderStatus",3);
        if(taxId != null){
            shop.put("taxId", taxId);
        }else{
            shop.put("taxId", "");
        }
        shop.put("tax", tax);
        shop.put("subTotal",Double.valueOf(String.valueOf(subTotal)));
        if(jsonArray != null){
            shop.put("time",jsonArray);
        }
        if(userId != null){
            ParseObject userobj = ParseObject.createWithoutData("_User",userId);
            shop.put("user",userobj);
            shop.put("userString",userId);
        }
        if(offerObjectId != null){
            ParseObject offerobj = ParseObject.createWithoutData("Offers",offerObjectId);
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
        if(offerEnanbled != null){
            if(offerEnanbled.equals("true")){
                Boolean rf = true;
                shop.put("offerEnabled",rf);
            }else if(offerEnanbled.equals("false")){
                Boolean rf = false;
                shop.put("offerEnabled",rf);
            }
        }
//        shop.put("refundCost",Double.valueOf(refuncost));
        DecimalFormat df = new DecimalFormat("###0.00");
        shop.put("order",order);
        shop.put("owner", ParseUser.getCurrentUser());
        shop.put("business",ParseUser.getCurrentUser().getObjectId());
        shop.put("shopstring",locationObjectId);
        shop.put("currency",currencyType);
        shop.put("totalCost",Double.valueOf(String.valueOf(totalCost)));

        tot = tot.replaceAll(",", "");
        Double chkdis = Double.parseDouble(tot);
        shop.put("totalTime",Double.valueOf(df.format(chkdis)));

        String totalC = String.valueOf(totalCost).replaceAll(",", "");
        Double pikstr = Double.parseDouble(totalC);
        shop.put("pikCharges",Double.valueOf(df.format(pikstr)));

        shop.put("pikPercentage",Double.valueOf(pikper));

        if(isPaid != null){
            Boolean b = Boolean.valueOf(isPaid);
            shop.put("isPaid",b);
        }
        shop.saveInBackground(e -> {
            // TODO Auto-generated method stub

            if (e == null) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Orders");
                ParseObject obj1 = ParseObject.createWithoutData("ShopLocations",locationObjectId);
                query.whereEqualTo("shop", obj1);
                query.setCachePolicy(ParseQuery.CachePolicy.IGNORE_CACHE);
                query.getInBackground(objectId, new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, com.parse.ParseException e) {
                        if (e == null) {
                            object.deleteInBackground();
                            HashMap<String, String> params = new HashMap<>();
                            params.put("status", "3");
                            params.put("orderNo", objectId);
                            params.put("business",ParseUser.getCurrentUser().getObjectId());
                            params.put("location", locationObjectId);
                            params.put("user", userId);
                            params.put("sendBy", "business");
                            params.put("cancelNote", "");
                            ParseCloud.callFunctionInBackground("push", params, (FunctionCallback<Float>) (ratings, re) -> {
                            });
                            if(dialog != null){
                                dialog.dismiss();
                            }
                            ((OrderListActivityNew) mContext).changeStatus(childPosition, groupPosition, childData);
                            Toast.makeText(mContext, "Order Pick up ", Toast.LENGTH_SHORT).show();
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

    private void changeStatus(String objectId, String locationName, String shopLocationName, Double latitude,
                              Double longitude, int shopStatus, int pin, Number phoneNo,
                              String locationObjectId, View v, String tim, String totalTime, String userId,
                              Integer orderStatus, Orders childData, int childPosition, int groupPosition, JSONArray jsonArray) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Orders");
        query.setCachePolicy(ParseQuery.CachePolicy.IGNORE_CACHE);
        query.getInBackground(objectId, (shop, e) -> {
            if (e == null) {

                if (orderStatus == 1) {
                    shop.put("orderStatus", 2);
                    shop.put("totalTime", Double.valueOf(totalTime));
                    shop.put("time", jsonArray);
                } else {
                    JSONArray json = new JSONArray();
                    json.put(tim);
                    shop.put("orderStatus", 1);
                    shop.put("totalTime", Double.valueOf(tim));
                    shop.put("time", jsonArray);
                }

                shop.saveInBackground(e1 -> {

                    if (e1 == null) {
                        HashMap<String, String> params = new HashMap<>();

                        if (orderStatus == 1) {
                            params.put("status", "2");
                        } else {
                            params.put("status", "1");
                        }
                        params.put("orderNo", objectId);
                        params.put("business", ParseUser.getCurrentUser().getObjectId());
                        params.put("location", locationObjectId);
                        params.put("user", userId);
                        params.put("sendBy", "business");
                        params.put("cancelNote", "");
                        ParseCloud.callFunctionInBackground("push", params, (FunctionCallback<Float>) (ratings, re) -> {
                        });
                        dialog.dismiss();
                        ((OrderListActivityNew) mContext).changeStatus(childPosition, groupPosition, childData);
                        if (orderStatus == 1) {
                            Toast.makeText(mContext, "Order Ready", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, "Order started", Toast.LENGTH_SHORT).show();
                        }
                    } else {
//                            Log.d("chk", "error: "+ e1.getMessage());
                    }
                });
            } else {
                e.printStackTrace();
//                    Log.d("chk", "done: error"+e.getMessage());
            }
        });


    }

    private void cancelOrder(String objectId, String refundCost, String cancelBy, View v, String locationObjectId,
                             String notes, Number totalCost, Number subTotal, String taxId, int tax, String order,
                             String tim, Boolean isPaid, Date date, String refunndTrans, String refundPer, String pikCharges,
                             String pikPercentage, String userId, String refund, String refundForBusiness,
                             String discountAmount, String offerObjectId, Boolean offerEnanbled,
                             String shopLocationName, Number shopPhoneNo, String tranRef, int childPosition,
                             int groupPosition, Orders childData, ViewGroup parent) {

        String sts = "4";
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.cancelorder, null);
        AlertDialog.Builder alertbox = new AlertDialog.Builder(v.getRootView().getContext());
        alertbox.setView(layout);

        TextView desc = layout.findViewById(R.id.desc);
        EditText note = layout.findViewById(R.id.note);
        Button disagree = layout.findViewById(R.id.disagree);
        Button agree = layout.findViewById(R.id.agree);
        TextView tit = layout.findViewById(R.id.title);
        desc.setTypeface(Typer.set(mContext).getFont(Font.ROBOTO_REGULAR));
        note.setTypeface(Typer.set(mContext).getFont(Font.ROBOTO_REGULAR));
        disagree.setTypeface(Typer.set(mContext).getFont(Font.ROBOTO_MEDIUM));
        agree.setTypeface(Typer.set(mContext).getFont(Font.ROBOTO_MEDIUM));
        tit.setTypeface(Typer.set(mContext).getFont(Font.ROBOTO_MEDIUM));
        note.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() > 0) {
                    agree.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                } else {
                    agree.setTextColor(ContextCompat.getColor(mContext, R.color.gray));
                }

            }
        });
        DecimalFormat df = new DecimalFormat("###0.00");
        refundCost = refundCost.replaceAll(",", "");
        Double refundd = Double.parseDouble(refundCost);
        desc.setText(" If you cancel the order you will be charged our fees" + "(" + df.format(refundd) + " AED" + ")");

        String finalRefundcost = refundCost;
        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cancelnote = note.getText().toString().trim();
                if (cancelnote.length() > 1) {
//                     Log.d("chk", "onClick:cncel ");
                    cancelOrderApi(objectId, finalRefundcost, cancelBy, cancelnote, locationObjectId, v,
                            totalCost, subTotal, taxId, tax, order, tim, isPaid, date,
                            refunndTrans, refundPer, pikCharges,
                            pikPercentage, userId, refund, refundForBusiness,
                            offerObjectId, offerEnanbled, discountAmount, shopLocationName, shopPhoneNo, tranRef,
                            childPosition, groupPosition, childData, parent);
                } else {
                    Toast.makeText(mContext, "Please provide cancel reason", Toast.LENGTH_SHORT).show();
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
        alertDialog = alertbox.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void cancelOrderApi(String objectId, String finalRefundCost, String cancelBy, String cancelNote,
                                String locationObjectId, View v, Number totalCost, Number subTotal, String taxId,
                                int tax, String order, String tim, Boolean isPaid, Date date, String refundTrans,
                                String refundPer, String pikCharges, String pikPercentage, String userId, String refund,
                                String refundForBusiness, String offerObjectId, Boolean offerEnabled, String discountAmount,
                                String shopLocationName, Number shopPhoneNo, String tranRef, int childPosition,
                                int groupPosition, Orders childData, ViewGroup parent) {

        JSONArray jsonArray = new JSONArray();
        jsonArray.put(tim);
        ParseObject shop = new ParseObject("OrderHistory");
        ParseObject obj = ParseObject.createWithoutData("ShopLocations", locationObjectId);
        if (userId != null) {
            ParseObject userobj = ParseObject.createWithoutData("_User", userId);
            shop.put("user", userobj);
            shop.put("userString", userId);
        }
        if (offerObjectId != null) {
            ParseObject offerobj = ParseObject.createWithoutData("Offers", offerObjectId);
            shop.put("offerDetails", offerobj);
        }
        if (discountAmount != null) {
            if (discountAmount.equals("null")) {
                shop.put("discountAmount", 0);
            } else {
                shop.put("discountAmount", Double.valueOf(discountAmount));
            }

        }
        if (offerEnabled != null) {
            if (offerEnabled.equals("true")) {
                Boolean rf = true;
                shop.put("offerEnabled", rf);
            } else if (offerEnabled.equals("false")) {
                Boolean rf = false;
                shop.put("offerEnabled", rf);
            }
        }
        shop.put("shop", obj);
        shop.put("notes", cancelNote);
        shop.put("orderStatus", 4);
        shop.put("orderNo", objectId);
        shop.put("orderCost", totalCost);
        shop.put("orderTime", date);
        shop.put("refundString", refund);
        shop.put("refundForBusiness", Double.valueOf(refundForBusiness));
        if (taxId != null) {
            shop.put("taxId", taxId);
        } else {
            shop.put("taxId", "");
        }
        DecimalFormat df = new DecimalFormat("###0.00");
        shop.put("tax", tax);
        shop.put("subTotal", subTotal);
        shop.put("time", jsonArray);

        finalRefundCost = finalRefundCost.replaceAll(",", "");
        Double refdd = Double.valueOf(finalRefundCost);
        shop.put("refundCost", Double.valueOf(df.format(refdd)));

//        shop.put("refundCost",Double.valueOf(refuncost));
        shop.put("cancelNote", cancelNote);
        shop.put("order", order);
        shop.put("owner", ParseUser.getCurrentUser());
        shop.put("business", ParseUser.getCurrentUser().getObjectId());
        shop.put("cancelledBy", cancelBy);
        shop.put("shopstring", locationObjectId);
        shop.put("currency", currencyType);
//        shop.put("totalTime",Double.valueOf(tiim));
        shop.put("totalCost", totalCost);
        shop.put("refundPercentage", Integer.parseInt(refundPer));
        shop.put("refundTrans", Integer.parseInt(refundTrans));
        shop.put("pikPercentage", Integer.valueOf(pikPercentage));

        tim = tim.replaceAll(",", "");
        Double chkdis = Double.parseDouble(tim);
        shop.put("totalTime", Double.valueOf(df.format(chkdis)));

        pikCharges = pikCharges.replaceAll(",", "");
        Double pikstr = Double.parseDouble(pikCharges);
        shop.put("pikCharges", Double.valueOf(df.format(pikstr)));
        shop.put("refundTime", 0);

        if (isPaid != null) {
            Boolean b = Boolean.valueOf(isPaid);
            shop.put("isPaid", b);
        }
        shop.saveInBackground();
        String finalPikCharges = pikCharges;
        shop.saveInBackground(e -> {
            // TODO Auto-generated method stub

            if (e == null) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Orders");
                ParseObject obj1 = ParseObject.createWithoutData("ShopLocations", locationObjectId);
                query.whereEqualTo("shop", obj1);

                query.setCachePolicy(ParseQuery.CachePolicy.IGNORE_CACHE);
                query.getInBackground(objectId, new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, com.parse.ParseException e) {
                        if (e == null) {
                            HashMap<String, String> params = new HashMap<>();
                            params.put("status", "1");
                            params.put("orderNo", objectId);
                            params.put("business", ParseUser.getCurrentUser().getObjectId());
                            params.put("location", locationObjectId);
                            params.put("user", userId);
                            params.put("sendBy", "business");
                            params.put("cancelNote", cancelNote);
                            ParseCloud.callFunctionInBackground("push", params, (FunctionCallback<Float>) (ratings, re) -> {
                                if (re == null) {
                                    // ratings is 4.5
//                                    Log.d("chk", "done:chkkk ");
                                } else {
//                                    Log.d("chk", "done:error "+re.getMessage());
                                }
                            });
//                            Log.d("chk", "done: deleted"+idd1);
                            object.deleteInBackground();
                            dialog.dismiss();

                            ((OrderListActivityNew) mContext).removeItem(childPosition, groupPosition, childData);

                            HashMap<String, String> params2 = new HashMap<>();
                            params2.put("busObjId", ParseUser.getCurrentUser().getObjectId());
                            params2.put("busName", ParseUser.getCurrentUser().getString("Business_name"));
                            params2.put("busPhno",
                                    String.valueOf(ParseUser.getCurrentUser().getNumber("phoneNumber")));
                            params2.put("busEmail", ParseUser.getCurrentUser().getEmail());
                            params2.put("shopName", shopLocationName);
                            params2.put("shopPhno", String.valueOf(shopPhoneNo));
                            params2.put("orderNo", objectId);
                            params2.put("totalCost", String.valueOf(totalCost));
                            params2.put("cancelledBy", cancelBy);
                            params2.put("cancelNote", cancelNote);
                            params2.put("pikPercentage", pikPercentage);
                            params2.put("pikCharges", String.valueOf(pikstr));
                            params2.put("tax", String.valueOf(tax));
                            params2.put("tranRef", tranRef);
                            params2.put("refundString", refund);
                            params2.put("refundForBusiness", refundForBusiness);
                            params2.put("refundTime", "0");
                            params2.put("refundCost", String.valueOf(refdd));
                            params2.put("refundPercentage", refundPer);
                            params2.put("refundTrans", refundTrans);
//                            Log.d("chk", "done: "+params2.toString());
                            ParseCloud.callFunctionInBackground("emailAtCancel",
                                    params2, (FunctionCallback<Map<String,
                                            List<ParseObject>>>) (object3, re) -> {
                                        if (re == null) {
                                            Log.d("chk", "done:ank cancel at reg success ");
                                        } else {
                                            Log.d("chk", "done:error emailatreg" + re.getMessage());
                                        }
                                    });


                            Toast.makeText(mContext, "Order cancelled ", Toast.LENGTH_SHORT).show();
                        } else {
                            // something went wrong
                        }
                    }

                });
//                    dialog.dismiss();
                alertDialog.dismiss();

            } else {
                Log.d("chk", "done: " + e.getMessage());
            }
        });
    }


    @Override
    public int getChildrenCount(int groupPosition) {
        if (listDataChild.size() > 0) {
            return listDataChild.get(listDataHeader.get(groupPosition)).size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listDataHeader.get(groupPosition);
    }


    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getGroupCount() {
        return listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        String headerTitle = (String) getGroup(groupPosition);
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.order_list_group, null);
            ViewHolder holder = new ViewHolder();
            holder.addView(v.findViewById(R.id.group_header));
            v.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) v.getTag();
        expandableList.expandGroup(groupPosition);
        TextView tvOrderCount = (TextView) v.findViewById(R.id.tv_order_count);
        TextView txt_order_type = (TextView) v.findViewById(R.id.txt_order_type);
        txt_order_type.setTypeface(null, Typeface.BOLD);
        txt_order_type.setText(headerTitle);
        tvOrderCount.setText("" + listDataChild.get(listDataHeader.get(groupPosition)).size());
        tvOrderCount.setTypeface(null, Typeface.BOLD);
        return v;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    /**
     * Called when a group is expanded.
     *
     * @param groupPosition The group being expanded.
     */
    @Override
    public void onGroupExpanded(int groupPosition) {

    }

    /**
     * Called when a group is collapsed.
     *
     * @param groupPosition The group being collapsed.
     */
    @Override
    public void onGroupCollapsed(int groupPosition) {

    }

    /**
     * Gets an ID for a child that is unique across any item (either group or
     * child) that is in list. Expandable lists require each item (group or
     * child) to have a unique ID among all children and groups in the list.
     * This method is responsible for returning that unique ID given a child's
     * ID and its group's ID. Furthermore, if {@link #hasStableIds()} is true, the
     * returned ID must be stable as well.
     *
     * @param groupId The ID of the group that contains this child.
     * @param childId The ID of the child.
     * @return The unique (and possibly stable) ID of the child across all
     * groups and children in this list.
     */
    @Override
    public long getCombinedChildId(long groupId, long childId) {
        return childId;
    }

    /**
     * Gets an ID for a group that is unique across any item (either group or
     * child) that is in this list. Expandable lists require each item (group or
     * child) to have a unique ID among all children and groups in the list.
     * This method is responsible for returning that unique ID given a group's
     * ID. Furthermore, if {@link #hasStableIds()} is true, the returned ID must be
     * stable as well.
     *
     * @param groupId The ID of the group
     * @return The unique (and possibly stable) ID of the group across all
     * groups and children in this list.
     */
    @Override
    public long getCombinedGroupId(long groupId) {
        return groupId;
    }


    public class OrderItemListAdapter extends RecyclerView.Adapter<OrderItemListAdapter.MyViewHolder> {
        private List<OrderItem> itemList;

        public OrderItemListAdapter(Context context, List<OrderItem> orderItemList) {
            mContext = context;
            itemList = orderItemList;
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {


            TextView tvCategoryName, tvItemName, tvItemPrice, tvExtraName;

            public MyViewHolder(View itemView) {
                super(itemView);
                tvCategoryName = itemView.findViewById(R.id.category_name);
                tvItemName = itemView.findViewById(R.id.item_name);
                tvItemPrice = itemView.findViewById(R.id.item_price);
                tvExtraName = itemView.findViewById(R.id.extra_name);
            }

        }


        @Override
        public OrderItemListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.orderitems_layout, parent, false);
            return new OrderItemListAdapter.MyViewHolder(v);
        }


        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

            holder.tvCategoryName.setText(itemList.get(position).getCategoryName());
            holder.tvItemName.setText(itemList.get(position).getMenuName());
            holder.tvItemPrice.setText(itemList.get(position).getItemPrice());
            holder.tvExtraName.setText(itemList.get(position).getExtraItem());


        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return itemList.size();
        }
    }

    public class ViewHolder {
        private HashMap<Integer, View> storedViews = new HashMap<Integer, View>();

        public ViewHolder() {
        }

        /**
         * @param view The view to add; to reference this view later, simply refer to its id.
         * @return This instance to allow for chaining.
         */
        public ViewHolder addView(View view) {
            int id = view.getId();
            storedViews.put(id, view);
            return this;
        }

        public View getView(int id) {
            return storedViews.get(id);
        }
    }
}