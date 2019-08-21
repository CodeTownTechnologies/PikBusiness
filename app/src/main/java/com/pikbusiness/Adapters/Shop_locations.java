package com.pikbusiness.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.pikbusiness.Dashboard;
import com.pikbusiness.EditLocation;
import com.pikbusiness.Enter_Pin;
import com.pikbusiness.R;
import com.elmargomez.typer.Font;
import com.elmargomez.typer.Typer;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Shop_locations extends RecyclerView.Adapter<Shop_locations.MyViewHolder> {

    ArrayList<HashMap<String, String>> dataa;
    Context context;
    String approv;
    AlertDialog alertDialog;
    AlertDialog alertDialog1;
    Switch  switch1;

    public Shop_locations(Context context, ArrayList<HashMap<String, String>> aryList) {
        this.context = context;
        this.dataa = aryList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.shoplist_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        // set the data in items
            String name = dataa.get(position).get("locationname");
            String loc = dataa.get(position).get("business");
           holder.locname.setText(name);
           holder.bname.setText(loc);
        ParseQuery<ParseUser> userQueryu=ParseUser.getQuery();
        userQueryu.whereEqualTo("objectId",ParseUser.getCurrentUser().getObjectId());
        userQueryu.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> userList, ParseException e) {
                if (e == null) {
                    //Retrieve user data
                    if (userList.size() > 0) {
                        for (ParseUser user : userList) {

                            approv = String.valueOf(user.getInt("accountStatus"));
//                            Log.d("chk", "onClick:parse "+approv);
                        }
                    }
                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.d("chk", "onClick:event "+approv);
                if(approv !=null){
                    if(approv.equals("0")){

                        apr_msg(view,"Your account verification is under process");
                    }
                    else if(approv.equals("1")){
                        Intent i = new Intent(context,Enter_Pin.class);
                        i.putExtra("lname",dataa.get(position).get("locationname"));
                        i.putExtra("bname",dataa.get(position).get("business"));
                        i.putExtra("id",dataa.get(position).get("objectid"));
                        i.putExtra("lat",dataa.get(position).get("lat"));
                        i.putExtra("log",dataa.get(position).get("log"));
                        i.putExtra("tax",dataa.get(position).get("tax"));
                        i.putExtra("shopStatus",dataa.get(position).get("shopStatus"));
                        i.putExtra("pin",dataa.get(position).get("pin"));
                        i.putExtra("phoneNo",dataa.get(position).get("phoneNo"));
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                    }else{
                        apr_msg(view,"Please contact support team");
                    }
                }
            }
        });

     holder.more.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {

             if(approv.equals("0")){
                 apr_msg(v,"Your account verification is under process");
             }
             else if(approv.equals("1")) {
                    displayPopupWindow(v,position,dataa.get(position).get("shopStatus"));
             }else{
                 apr_msg(v,"Please contact support team");
             }
                 }
             });
    }
    private void displayPopupWindow(View anchorView, int position,String shopStatus) {
        PopupWindow popup = new PopupWindow(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View layout = inflater.inflate(R.layout.popup_options, null);
        popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.setContentView(layout);
        // Set content width and height
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        // Closes the popup window when touch outside of it - when looses focus
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        TextView txt_switch = layout.findViewById(R.id.txt_switch);
        TextView edit = layout.findViewById(R.id.edit);
        TextView delete = layout.findViewById(R.id.delete);
          switch1 = layout.findViewById(R.id.switch1);

        txt_switch.setTypeface(Typer.set(context).getFont(Font.ROBOTO_REGULAR));
        edit.setTypeface(Typer.set(context).getFont(Font.ROBOTO_REGULAR));
        delete.setTypeface(Typer.set(context).getFont(Font.ROBOTO_REGULAR));
//        Log.d("chk", "displayPopupWindow: "+shopStatus);
        if(shopStatus.equals("1")){
            txt_switch.setText("Online");
            switch1.setChecked(true);
        }else if(shopStatus.equals("0")){
            txt_switch.setText("Offline");
            switch1.setChecked(false);
        }
        switch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(switch1.isChecked()){
//                    Log.d("chk", "onClick:"+position);

                    txt_switch.setText("Online");
                    onoff(dataa.get(position).get("objectid"),1,"You will receive orders now",switch1,txt_switch);
                }else{
//                    Log.d("chk", "onClick:"+position);
//                    checkorders(dataa.get(position).get("objectid"));
                    txt_switch.setText("Offline");
                    onoff(dataa.get(position).get("objectid"),0,"You will not receive orders", switch1, txt_switch);
                }
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
                Intent e = new Intent(context, EditLocation.class);
                e.putExtra("lname", dataa.get(position).get("locationname"));
                e.putExtra("id", dataa.get(position).get("objectid"));
                e.putExtra("lat", dataa.get(position).get("lat"));
                e.putExtra("log", dataa.get(position).get("log"));
                e.putExtra("pin", dataa.get(position).get("pin"));
                e.putExtra("tax",dataa.get(position).get("tax"));
                e.putExtra("shopStatus", dataa.get(position).get("shopStatus"));
                e.putExtra("phoneNo", dataa.get(position).get("phoneNo"));
                e.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(e);

            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();

                deletepopup(anchorView, dataa.get(position).get("locationname"),
                        dataa.get(position).get("objectid"));

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
    public void onoff(String id, int sts, String msg, Switch switch1, TextView txt_switch){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ShopLocations");
        query.setCachePolicy(ParseQuery.CachePolicy.IGNORE_CACHE);
        query.whereEqualTo("business",ParseUser.getCurrentUser().getObjectId());
        query.getInBackground(id, new GetCallback<ParseObject>() {
            public void done(ParseObject shop, ParseException e) {
                if (e == null) {
                    shop.put("shopStatus",sts);
                    shop.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {

                            if (e == null) {
                                if(sts == 1){
                                    txt_switch.setText("Online");
                                    switch1.setChecked(true);
                                   Intent i = new Intent(context,Dashboard.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                   context.startActivity(i);
                                }
                                else
                                {
                                    txt_switch.setText("Offline");
                                    switch1.setChecked(false);
                                    Intent i = new Intent(context,Dashboard.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    context.startActivity(i);
                                }
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
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

public  void deletepopup(View view,String name,String objid){

    LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View layout = inflater.inflate(R.layout.waitverify,null);
    AlertDialog.Builder alertbox = new AlertDialog.Builder(view.getRootView().getContext());
    alertbox.setView(layout);

    TextView txt = layout.findViewById(R.id.msg);
    Button login = layout.findViewById(R.id.login);
    ImageView img = layout.findViewById(R.id.img);
    img.setVisibility(View.GONE);
    login.setVisibility(View.GONE);
    alertbox.setCancelable(true);
    login.setText("OKAY");
    txt.setText("Are you sure you want to delete "+name+ " location");
    txt.setTypeface(Typer.set(context).getFont(Font.ROBOTO_REGULAR));
    alertbox.setPositiveButton("YES", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
          deletecall(objid);
          alertDialog.dismiss();
        }
    });

    alertbox.setNegativeButton("NO", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
           alertDialog.dismiss();
        }
    });
    alertDialog = alertbox.create();
    alertDialog.setCancelable(true);
    alertDialog.show();
    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context,R.color.blue));
    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context,R.color.blue));
    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTypeface(Typer.set(context).getFont(Font.ROBOTO_MEDIUM));
    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTypeface(Typer.set(context).getFont(Font.ROBOTO_MEDIUM));

    }

 public void deletecall(String id){

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

                            Intent i = new Intent(context,Dashboard.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            context.startActivity(i);

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
    public void apr_msg(View view,String msg) {
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.waitverify,null);
        AlertDialog.Builder alertbox = new AlertDialog.Builder(view.getRootView().getContext());
             alertbox.setView(layout);

        TextView txt = layout.findViewById(R.id.msg);
        Button login = layout.findViewById(R.id.login);
        txt.setTypeface(Typer.set(context).getFont(Font.ROBOTO_REGULAR));
        login.setTypeface(Typer.set(context).getFont(Font.ROBOTO_REGULAR));
        alertbox.setCancelable(true);
        login.setText("OKAY");
        txt.setText(msg);
        txt.setTypeface(Typer.set(context).getFont(Font.ROBOTO_REGULAR));
      AlertDialog alertDialog = alertbox.create();
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
        return dataa.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView locname, bname;
         LinearLayout more;

        public MyViewHolder(View itemView) {
            super(itemView);

            locname = itemView.findViewById(R.id.locname);
            bname = itemView.findViewById(R.id.bname);
            more = itemView.findViewById(R.id.more);
            locname.setTypeface(Typer.set(context).getFont(Font.ROBOTO_REGULAR));
            bname.setTypeface(Typer.set(context).getFont(Font.ROBOTO_MEDIUM));

            // get the reference of item view's
        }
    }
}
