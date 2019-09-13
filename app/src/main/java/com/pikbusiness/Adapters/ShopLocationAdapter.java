package com.pikbusiness.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.elmargomez.typer.Font;
import com.elmargomez.typer.Typer;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.pikbusiness.Activity.EditLocationActivity;
import com.pikbusiness.EnterPinActivity;
import com.pikbusiness.R;
import com.pikbusiness.model.Response.EstimatedData;

import java.util.List;


public class ShopLocationAdapter extends RecyclerView.Adapter<ShopLocationAdapter.MyViewHolder> {


    Context context;
    AlertDialog alertDialog;
    Switch toggleSwitch;
    private List<EstimatedData> estimateDataList;

    public ShopLocationAdapter(Context context, List<EstimatedData> dataList) {
        this.context = context;
        estimateDataList = dataList;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.shoplist_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        // set the data in items

        holder.tvLocationName.setText(estimateDataList.get(position).getLocationName());
        holder.tvBusinessName.setText(estimateDataList.get(position).getBusiness().getBusinessEstimatedData().getBusinessName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (estimateDataList.get(position).getApproveStatus() != null) {
                    if (estimateDataList.get(position).getApproveStatus().equals("0")) {
                        approveAlert(view, "Your account verification is under process");
                    } else if (estimateDataList.get(position).getApproveStatus().equals("1")) {
                        Intent i = new Intent(context, EnterPinActivity.class);
                        i.putExtra("locationName", estimateDataList.get(position).getLocationName());
                        i.putExtra("businessName", estimateDataList.get(position).getBusiness().getBusinessEstimatedData().getBusinessName());
                        i.putExtra("objectId", estimateDataList.get(position).getObjectId());
                        Bundle b = new Bundle();
                        b.putDouble("latitude", estimateDataList.get(position).getLocation().getLatitude());
                        b.putDouble("longitude", estimateDataList.get(position).getLocation().getLongitude());
                        i.putExtras(b);
                        i.putExtra("tax", estimateDataList.get(position).getTax());
                        i.putExtra("shopStatus", "" + estimateDataList.get(position).getShopStatus());
                        i.putExtra("pin", "" + estimateDataList.get(position).getPin());
                        i.putExtra("phoneNo", "" + estimateDataList.get(position).getPhoneNo());
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                    } else {
                        approveAlert(view, "Please contact support team");
                    }
                }
            }
        });

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (estimateDataList.get(position).getApproveStatus().equals("0")) {
                    approveAlert(v, "Your account verification is under process");
                } else if (estimateDataList.get(position).getApproveStatus().equals("1")) {
                    displayPopupWindow(v, position, estimateDataList.get(position).getShopStatus());
                } else {
                    approveAlert(v, "Please contact support team");
                }
            }
        });
    }

    private void displayPopupWindow(View anchorView, int position, int shopStatus) {
        PopupWindow popup = new PopupWindow(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.popup_options, null);
        popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.setContentView(layout);
        // Set content width and height
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        TextView txt_switch = layout.findViewById(R.id.txt_switch);
        TextView edit = layout.findViewById(R.id.edit);
        TextView delete = layout.findViewById(R.id.delete);
        toggleSwitch = layout.findViewById(R.id.toggle_switch);

        txt_switch.setTypeface(Typer.set(context).getFont(Font.ROBOTO_REGULAR));
        edit.setTypeface(Typer.set(context).getFont(Font.ROBOTO_REGULAR));
        delete.setTypeface(Typer.set(context).getFont(Font.ROBOTO_REGULAR));
        //        Log.d("chk", "displayPopupWindow: "+shopStatus);
        if (shopStatus == 1) {
            txt_switch.setText("Online");
            toggleSwitch.setChecked(true);
        } else if (shopStatus == 0) {
            txt_switch.setText("Offline");
            toggleSwitch.setChecked(false);
        }
        toggleSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggleSwitch.isChecked()) {
                    txt_switch.setText("Online");
                    shopStatusOnOff(estimateDataList.get(position).getObjectId(), 1, "You will receive orders now", toggleSwitch, txt_switch, position);
                } else {
                    txt_switch.setText("Offline");
                    shopStatusOnOff(estimateDataList.get(position).getObjectId(), 0, "You will not receive orders", toggleSwitch, txt_switch, position);
                }
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
                Intent e = new Intent(context, EditLocationActivity.class);
                e.putExtra("location_name", "" + estimateDataList.get(position).getLocationName());
                e.putExtra("objectId", estimateDataList.get(position).getObjectId());
                Bundle b = new Bundle();
                b.putDouble("latitude", estimateDataList.get(position).getLocation().getLatitude());
                b.putDouble("longitude", estimateDataList.get(position).getLocation().getLongitude());
                e.putExtras(b);
                e.putExtra("pin", "" + estimateDataList.get(position).getPin());
                e.putExtra("tax", "" + estimateDataList.get(position).getTax());
                e.putExtra("shopStatus", "" + estimateDataList.get(position).getShopStatus());
                e.putExtra("phoneNo", "" + estimateDataList.get(position).getPhoneNo());
                context.startActivity(e);

            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();

                deletePopUp(anchorView, estimateDataList.get(position).getLocationName(),
                        estimateDataList.get(position).getObjectId(), position);


            }
        });
        //First we get the position of the menu icon in the screen
        int[] values = new int[2];
        anchorView.getLocationInWindow(values);
        int positionOfIcon = values[1];
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int height = (displayMetrics.heightPixels * 2) / 3;

        if (positionOfIcon > height) {
            popup.showAsDropDown(anchorView, 0, -320);
        } else {
            popup.showAsDropDown(anchorView, 0, 0);
        }
        popup.showAsDropDown(anchorView);
    }

    public void shopStatusOnOff(String id, int status, String msg, Switch switch1, TextView txt_switch, int position) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ShopLocations");
        query.setCachePolicy(ParseQuery.CachePolicy.IGNORE_CACHE);
        query.whereEqualTo("business", ParseUser.getCurrentUser().getObjectId());
        query.getInBackground(id, new GetCallback<ParseObject>() {
            public void done(ParseObject shop, ParseException e) {
                if (e == null) {
                    shop.put("shopStatus", status);
                    shop.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {

                            if (e == null) {
                                if (status == 1) {
                                    txt_switch.setText("Online");
                                    switch1.setChecked(true);
                                    estimateDataList.get(position).setShopStatus(status);
                                    notifyDataSetChanged();

                                } else {
                                    txt_switch.setText("Offline");
                                    switch1.setChecked(false);
                                    estimateDataList.get(position).setShopStatus(status);
                                    notifyDataSetChanged();
                                }
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                            } else {

                            }
                        }
                    });
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public void deletePopUp(View view, String name, String objectId, int position) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.waitverify, null);
        AlertDialog.Builder alertBox = new AlertDialog.Builder(view.getRootView().getContext());
        alertBox.setView(layout);

        TextView txt = layout.findViewById(R.id.msg);
        Button login = layout.findViewById(R.id.login);
        ImageView img = layout.findViewById(R.id.img);
        img.setVisibility(View.GONE);
        login.setVisibility(View.GONE);
        alertBox.setCancelable(true);
        login.setText("OKAY");
        txt.setText("Are you sure you want to delete " + name + " location");
        txt.setTypeface(Typer.set(context).getFont(Font.ROBOTO_REGULAR));
        alertBox.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteCall(objectId, position);
                alertDialog.dismiss();
            }
        });

        alertBox.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                alertDialog.dismiss();
            }
        });
        alertDialog = alertBox.create();
        alertDialog.setCancelable(true);
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.blue));
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.blue));
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTypeface(Typer.set(context).getFont(Font.ROBOTO_MEDIUM));
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTypeface(Typer.set(context).getFont(Font.ROBOTO_MEDIUM));

    }

    public void deleteCall(String id, int position) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("ShopLocations");
        query.setCachePolicy(ParseQuery.CachePolicy.IGNORE_CACHE);
        query.whereEqualTo("business", ParseUser.getCurrentUser().getObjectId());
        query.getInBackground(id, new GetCallback<ParseObject>() {
            public void done(ParseObject shop, ParseException e) {
                if (e == null) {

                    shop.put("shopStatus", Integer.parseInt("2"));
                    shop.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {

                            if (e == null) {

                                estimateDataList.remove(position);
                                notifyDataSetChanged();
//                                dataa.remove(position);
//                                notifyItemRemoved(position);
//                                notifyItemRangeChanged(position, dataa.size());
                                //
                                //                            notifyItemChanged(position);
                                //                            Intent i = new Intent(context,DashboardActivity.class);
                                //                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                //                            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                //                            context.startActivity(i);

                            } else {

                            }
                        }
                    });
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public void approveAlert(View view, String msg) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.waitverify, null);
        AlertDialog.Builder alertBox = new AlertDialog.Builder(view.getRootView().getContext());
        alertBox.setView(layout);

        TextView txt = layout.findViewById(R.id.msg);
        Button login = layout.findViewById(R.id.login);
        txt.setTypeface(Typer.set(context).getFont(Font.ROBOTO_REGULAR));
        login.setTypeface(Typer.set(context).getFont(Font.ROBOTO_REGULAR));
        alertBox.setCancelable(true);
        login.setText("OKAY");
        txt.setText(msg);
        txt.setTypeface(Typer.set(context).getFont(Font.ROBOTO_REGULAR));
        AlertDialog alertDialog = alertBox.create();
        alertDialog.setCancelable(true);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    public int getItemCount() {
        return estimateDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvLocationName, tvBusinessName;
        LinearLayout more;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvLocationName = itemView.findViewById(R.id.locname);
            tvBusinessName = itemView.findViewById(R.id.bname);
            more = itemView.findViewById(R.id.more);
            tvLocationName.setTypeface(Typer.set(context).getFont(Font.ROBOTO_REGULAR));
            tvBusinessName.setTypeface(Typer.set(context).getFont(Font.ROBOTO_MEDIUM));

            // get the reference of item view's
        }
    }
}
