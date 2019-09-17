package com.pikbusiness.Adapters;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseObject;
import com.pikbusiness.Activity.OrderListActivityNew;
import com.pikbusiness.R;
import com.pikbusiness.model.Response.Orders;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    Timer updateTimer;
    String currencyType;
    ProgressDialog dialog;


    public OrderListAdapter(OrderListActivityNew context, List<String> listDataHeader, HashMap<String, List<Orders>> listDataChild) {
        this.mContext = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listDataChild;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition)).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final Orders childData = (Orders) getChild(groupPosition, childPosition);

        TextView tvCustomerName, tvDistance, tvDistanceTime, tvTimer, tvCategoryName, tvMenuName, tvExtraItem, tvNote, tvItemPrice,
                tvSubTotal, tvVatText, tvVatPrice, tvTotal, tvCancelOrder, tvTagLine, btnCall;
        LinearLayout headBackgroundLayout;
        Button btnChangeStatus;

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.order_list_child, null);
        }


        tvCustomerName = (TextView) convertView.findViewById(R.id.customer_name);
        tvDistance = (TextView) convertView.findViewById(R.id.tv_distance);
        tvDistanceTime = (TextView) convertView.findViewById(R.id.tv_distance_time);
        headBackgroundLayout = (LinearLayout) convertView.findViewById(R.id.head_background_color);
        tvTimer = (TextView) convertView.findViewById(R.id.timer);
        tvCategoryName = (TextView) convertView.findViewById(R.id.category_name);
        tvMenuName = (TextView) convertView.findViewById(R.id.menu_name);
        tvExtraItem = (TextView) convertView.findViewById(R.id.extra_item);
        tvNote = (TextView) convertView.findViewById(R.id.tv_note);
        tvItemPrice = (TextView) convertView.findViewById(R.id.tv_item_price);
        tvSubTotal = (TextView) convertView.findViewById(R.id.tv_subtotal);
        tvVatText = (TextView) convertView.findViewById(R.id.tv_vat_txt);
        tvVatPrice = (TextView) convertView.findViewById(R.id.vatprice);
        tvTotal = (TextView) convertView.findViewById(R.id.tv_total);
        btnChangeStatus = (Button) convertView.findViewById(R.id.btn_change_status);
        tvCancelOrder = (TextView) convertView.findViewById(R.id.tv_cancel_order);
        tvTagLine = (TextView) convertView.findViewById(R.id.txt_tag_line);
        btnCall = (Button) convertView.findViewById(R.id.call);


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
//            Log.d("chk", "onBindViewHolder: meters"+distanceInMeters);
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

            /* start logic to set header color and count down timer*/
            updateTimer = new Timer();
            TimerTask hour = new TimerTask() {
                public void run() {
                    try {
                        SimpleDateFormat date1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy",
                                Locale.ENGLISH);
                        Date d1 = null;
                        Date c = Calendar.getInstance().getTime();
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
                                if (elapsedHours == 0) {
                                    tvTimer.setText(elapsedMinutes + ":" + elapsedSeconds);
                                } else {
                                    tvTimer.setText(elapsedHours + ":" + elapsedMinutes + ":" + elapsedSeconds);
                                }
//                            holder.msg.setText(elapsedMinutes+" minutes away");

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

            updateTimer.schedule(hour, 60, 1000);
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

//            tvCancelOrder.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    dialog = new ProgressDialog(v.getContext());
//                    dialog.setMessage("Please wait.....");
//                    dialog.setCancelable(false);
//                    dialog.show();
//                    HashMap<String, Object> params = new HashMap<String, Object>();
//                    params.put("cancelledBy", "business");
//                    params.put("status", childData.getEstimatedData().getOrderStatus());
//                    params.put("orderNo", childData.getEstimatedData().getObjectId());
//                    params.put("totalCost", childData.getEstimatedData().getTotalCost());
//                    ParseCloud.callFunctionInBackground("refund", params, new FunctionCallback<Map<String, List<ParseObject>>>() {
//                        @Override
//                        public void done(Map<String, List<ParseObject>> object, com.parse.ParseException e) {
//                            if (e == null) {
////                            Log.d("chk", "done:res == "+object.get("cancelledBy"));
//                                SimpleDateFormat date1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy",
//                                        Locale.ENGLISH);
//                                Date d1;
//                                Date c = Calendar.getInstance().getTime();
//                                String cacenlby = String.valueOf(object.get("cancelledBy"));
//                                String refuncost = String.valueOf(object.get("refundCost"));
//                                String refundtrans = String.valueOf(object.get("refundTrans"));
//                                String refundper = String.valueOf(object.get("refundPercentage"));
//                                String pikPercentage = String.valueOf(object.get("pikPercentage"));
//                                String pikCharges = String.valueOf(object.get("pikCharges"));
//                                String refundForBusiness = String.valueOf(object.get("refundForBusiness"));
//                                String refund = String.valueOf(object.get("refund"));
//                                try {
//                                    Double tr = Double.valueOf(childData.getEstimatedData().getTotalTime());
//                                    if (Build.VERSION.SDK_INT >= 23) {
//                                        d1 = date1.parse(childData.getEstimatedData().getCreatedDateAt());
//                                        // Call some material design APIs here
//                                    } else {
//                                        DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
//                                        d1 = (Date) formatter.parse(childData.getEstimatedData().getCreatedDateAt());
//                                    }
//                                    long different = c.getTime() - d1.getTime();
//                                    long secondsInMilli = 1000;
//                                    long minutesInMilli = secondsInMilli * 60;
//                                    long elapsedMinutes = different / minutesInMilli;
//                                    different = different % minutesInMilli;
//                                    long elapsedSeconds = different / secondsInMilli;
//                                    String tot = String.valueOf(tr + elapsedMinutes);
//                                    String tiim = elapsedMinutes + "." + elapsedSeconds;
//
//                                    JSONArray jsonArray = null;
//                                    try {
//                                        jsonArray = new JSONArray(childData.getEstimatedData().getTime());
//
//                                        if (jsonArray.length() > 0) {
//
//                                            jsonArray.put(tiim);
//                                        }
//                                    } catch (JSONException r) {
//                                        r.printStackTrace();
//                                        Log.d("chk", "onClick:error " + r.getMessage());
//                                    }
//                                    Cancelorder(childData.getEstimatedData().getObjectId(), refund, refuncost,
//                                            cacenlby, v, childData.getEstimatedData().getLocationName(),
//                                            childData.getEstimatedData().getBusiness().getBusinessEstimatedData().getBusinessName(),
//                                            childData.getEstimatedData().getLocation().getLatitude(),
//                                            childData.getEstimatedData().getLocation().getLongitude(),
//                                            childData.getEstimatedData().getShopStatus(),
//                                            childData.getEstimatedData().getPin(),
//                                            childData.getEstimatedData().getShopPhoneNo(),
//                                            dataa.get(position).get("id"),
//                                            childData.getEstimatedData().getNotes(),
//                                            childData.getEstimatedData().getTotalCost(),
//                                            childData.getEstimatedData().getSubTotal(),
//                                            childData.getEstimatedData().getTaxId(),
//                                            childData.getEstimatedData().getTax(),
//                                            childData.getEstimatedData().getOrder(), jsonArray, tot,
//                                            childData.getEstimatedData().getIsPaid(), d1,
//                                            refundtrans, refundper, pikCharges, pikPercentage,
//                                            childData.getEstimatedData().getUserId(), refundForBusiness,
//                                            childData.getEstimatedData().getShopLocationName(),
//                                            childData.getEstimatedData().getShopPhoneNo(),
//                                            childData.getEstimatedData().getTranRef(),
//                                            childData.getEstimatedData().getDiscountAmount(),
//                                            childData.getEstimatedData().getOfferEnanbled(),
//                                            childData.getEstimatedData().setOfferObjectId(),
//                                            childPosition);
//                                } catch (ParseException r) {
//                                    r.printStackTrace();
//                                }
//
//                            } else {
//                                Log.d("chk", "done:error ==" + e.getMessage());
//                            }
//                        }
//                    });
//                }
//            });


            /*  start logic to show category name and other*/
            double itemPriceIncludingExtra = 0;
            try {
                JSONArray orderArray = new JSONArray(childData.getEstimatedData().getOrder());
                for (int i = 0; i < orderArray.length(); i++) {
                    JSONObject rowObject = orderArray.getJSONObject(i);
                    System.out.println("row Object ===" + rowObject);
                    String menuName = rowObject.getString("menuName");
                    String count = rowObject.getString("count");
                    String itemTotalPrice = rowObject.getString("itemTotalPrice").replace(currencyType, "");
                    String categoryName = rowObject.getString("categoryName");
                    String menuPrice = rowObject.getString("menuPrice");
                    String tax = rowObject.getString("tax");
                    double itemPrice = Integer.parseInt(count) * Double.valueOf(menuPrice);
                    JSONArray extrasArray = rowObject.getJSONArray("extras");
                    for (int j = 0; j < extrasArray.length(); j++) {
                        JSONObject nameValueObject = extrasArray.getJSONObject(j);
                        String extraName = nameValueObject.getString("extraName");
                        double extraPrice = Double.parseDouble(nameValueObject.getString("extraPrice"));
                        itemPriceIncludingExtra = extraPrice + itemPrice;
                        tvExtraItem.setText(extraName);
                    }
                    tvCategoryName.setText(categoryName);
                    tvMenuName.setText(menuName + " x " + count);
                    int itemPriceWithExtra = (int) itemPriceIncludingExtra;
                    tvItemPrice.setText("" + itemPriceWithExtra + " " + currencyType);
                    tvSubTotal.setText(itemTotalPrice + " " + currencyType);
                    tvVatText.setText("Vat" + " " + tax + "%");
                    tvVatPrice.setText(tax + " " + currencyType);
                    tvTotal.setText(itemTotalPrice + " " + currencyType);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (listDataChild.size() > 0) {
            return listDataChild.get(this.listDataHeader.get(groupPosition)).size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.order_list_group, null);
        }


        TextView tvOrderCount = (TextView) convertView.findViewById(R.id.tv_order_count);
        TextView txt_order_type = (TextView) convertView.findViewById(R.id.txt_order_type);
        txt_order_type.setTypeface(null, Typeface.BOLD);
        txt_order_type.setText(headerTitle);
        tvOrderCount.setText("" + listDataChild.get(this.listDataHeader.get(groupPosition)).size());
        tvOrderCount.setTypeface(null, Typeface.BOLD);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}