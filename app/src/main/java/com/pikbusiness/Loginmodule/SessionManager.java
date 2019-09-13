package com.pikbusiness.Loginmodule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.pikbusiness.services.Alertservice;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import org.json.JSONArray;
import java.util.HashMap;

public class SessionManager {

    private static final String PREFER_NAME = "Reg";
    private static final String IS_USER_LOGIN = "IsUserLoggedIn";
    private static final String KEY_NAME = "Name";
    private static final String KEY_UID = "UID";
    private static final String KEY_MOB = "MOBILE";
    private static final String KEY_EMAIL = "EMAIL";
    private static final String KEY_PASS = "Pass";
    private static final String KEY_PIN = "pin";
    private SharedPreferences pref;
    private Editor editor;
    private Context _context;
    int PRIVATE_MODE = 0;

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String email, String pass) {

        editor.putBoolean(IS_USER_LOGIN, true);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASS, pass);
        editor.commit();
    }
    public void Pinlogin(String pin,String lname,String bname,String id,
                         String shopstatus,String lat,String longg){

        editor.putString(KEY_PIN,pin);
        editor.putString("lname",lname);
        editor.putString("bname",bname);
        editor.putString("id",id);
        editor.putString("shopstatus",shopstatus);
        editor.putString("lat", lat);
        editor.putString("log", longg);
        editor.commit();
        editor.apply();
    }
    public void savesession(String locname, String phone,String setpin,String repin) {

        editor.putString("lname", locname);
        editor.putString("phone", phone);
        editor.putString("setpin", setpin);
        editor.putString("repin", repin);
        editor.apply();
        editor.commit();
    }
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_PASS, pref.getString(KEY_PASS, null));
        user.put(KEY_MOB, pref.getString(KEY_MOB, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        user.put(KEY_UID, pref.getString(KEY_UID, null));

        return user;
    }
    public void checkLogin() {
        if (!this.isUserLoggedIn()) {
            Intent i = new Intent(_context, LoginScreenActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        }
    }

    public void logoutUser() {

        editor.clear();
        editor.commit();
        ParseUser.logOut();
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        JSONArray pusharray = new JSONArray();
        installation.put("channels",pusharray);
        installation.saveInBackground();
        Intent i = new Intent(_context, LoginScreenActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
        _context.stopService(new Intent(_context, Alertservice.class));

    }

    public boolean isUserLoggedIn() {
        return pref.getBoolean(IS_USER_LOGIN, false);
    }
}