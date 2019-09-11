package com.pikbusiness.Loginmodule;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pikbusiness.R;
import com.pikbusiness.services.Toasty;
import com.crashlytics.android.Crashlytics;
import com.elmargomez.typer.Font;
import com.elmargomez.typer.Typer;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.hbb20.CountryCodePicker;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

public class Business_setup extends AppCompatActivity {

    private static final int REQUEST_CODE_LOGO = 22 ;
    private static final int REQUEST_CODE_ID = 33;
    private static final int REQUEST_CODE_SHOP = 44;
    private static final int REQUEST_CODE_LOGOcam = 21 ;
    private static final int REQUEST_CODE_IDcam = 32;
    private static final int REQUEST_CODE_SHOPcam = 43;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    @BindView(R.id.logo)EditText logo;
    @BindView(R.id.businessname)EditText businesname;
    @BindView(R.id.et_contact_number) EditText phone_edittext;
    @BindView(R.id.country_code_picker)
    CountryCodePicker ccp;
    @BindView(R.id.shopelicense)EditText shoplicense;
    @BindView(R.id.uploadid)EditText uploadid;
    @BindView(R.id.next)Button next;
    @BindView(R.id.progressBar)ProgressBar progressBar;
    @BindView(R.id.busns_htxt)TextView business_txt;
    @BindView(R.id.logotxt)TextView logo_txt;
    @BindView(R.id.bnametxt)TextView bname_txt;
    @BindView(R.id.phnotxt)TextView phno_txt;
    @BindView(R.id.shopidtxt)TextView shopid_txt;
    @BindView(R.id.upidtxt)TextView upid_txt;
    @BindView(R.id.tax)EditText tax;
    @BindView(R.id.taxid)EditText taxid;
    private static byte[] byte1,byte2,byte3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_setup);
        //Butterknife injection of ids
        ButterKnife.bind(this);
        Fabric.with(this, new Crashlytics());
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        ccp.registerCarrierNumberEditText(phone_edittext);
        business_txt.setTypeface(Typer.set(this).getFont(Font.ROBOTO_MEDIUM));
        logo_txt.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        logo.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        bname_txt.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        businesname.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));

        phone_edittext.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        phno_txt.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        shoplicense.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        shopid_txt.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        uploadid.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        upid_txt.setTypeface(Typer.set(this).getFont(Font.ROBOTO_REGULAR));
        next.setTypeface(Typer.set(this).getFont(Font.ROBOTO_MEDIUM));
        checkAndRequestPermissions();
        // For full screen no actionbar
        if (Build.VERSION.SDK_INT >= 21)
        {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        logo.setKeyListener(null);
        logo.setCursorVisible(false);
        logo.setPressed(false);
        logo.setFocusable(false);

        shoplicense.setKeyListener(null);
        shoplicense.setCursorVisible(false);
        shoplicense.setPressed(false);
        shoplicense.setFocusable(false);

        uploadid.setKeyListener(null);
        uploadid.setCursorVisible(false);
        uploadid.setPressed(false);
        uploadid.setFocusable(false);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String bname = businesname.getText().toString().trim();
                String pno = phone_edittext.getText().toString().trim();

                if(logo.getText().toString().length() == 0){
                    customToast("Please upload logo image");
                }
                else if(businesname.getText().toString().trim().length() == 0){
                    customToast("Please enter your business name");
                }
                else if(phone_edittext.getText().toString().trim().length() == 0){
                    customToast("Please enter mobile number");
                }
                else if( !ccp.isValidFullNumber()){
                    customToast("Enter valid mobile number");
                }
                else if (uploadid.getText().toString().trim().length() == 0){
                    customToast("Please upload your any Id proof");
                }
                else if(shoplicense.getText().toString().trim().length() == 0){
                    customToast("Please upload your shop licence");
                }else if(checkInternetConenction()){

                    uploadfiles(byte1,byte2,byte3,bname, Long.valueOf(ccp.getFullNumber()));
                }

            }
        });

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popupoptions(REQUEST_CODE_LOGOcam,REQUEST_CODE_LOGO,"Upload Logo");
            }
        });
        uploadid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupoptions(REQUEST_CODE_IDcam,REQUEST_CODE_ID,"Upload Passport or ID");
//                IntentCall(REQUEST_CODE_ID);
            }
        });
        shoplicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupoptions(REQUEST_CODE_SHOPcam,REQUEST_CODE_SHOP,"Upload Trade License");
//                IntentCall(REQUEST_CODE_SHOP);
            }
        });
    }
   public void popupoptions(int code1,int code2,String titletxt){
       final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
               Business_setup.this);
       LayoutInflater inflater = Business_setup.this.getLayoutInflater();
       View dialog4 = inflater.inflate(R.layout.uploadimageoptions, null);
       TextView can1 = dialog4.findViewById(R.id.setdef);
       TextView can2 = dialog4.findViewById(R.id.delimage);
       TextView title = dialog4.findViewById(R.id.title);
       title.setText(titletxt);
       can1.setText("Camera");
       can2.setText("Gallery");

       dialogBuilder.setView(dialog4);
       final AlertDialog alertDialog = dialogBuilder.create();
       alertDialog.setCancelable(true);
       can1.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               callCamera(code1,titletxt);
               alertDialog.dismiss();
           }
       });
       can2.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               IntentCall(code2);
               alertDialog.dismiss();
           }
       });
       alertDialog.show();
   }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i= new Intent(Business_setup.this,Loginscreen.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//        overridePendingTransition(R.anim.pull_in_right, R.anim.pull_out_left);
    }

    private boolean checkAndRequestPermissions()

    {
        int camera = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        int storage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        }
        if (storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    public void callCamera(int code,String name) {
        if (checkAndRequestPermissions()) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile(name);
                } catch (IOException ex) {
                    // Error occurred while creating the File

                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.pikbusiness.android.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, code);
                }
            }
        } else {
            checkAndRequestPermissions();
        }
    }

    // Uploading files and data to parse server current user class
    private void  uploadfiles(byte[] logo,byte[] ownerid,byte[] shoplic,String businname,Long phno){

        progressBar.setVisibility(View.VISIBLE);
        ParseFile file1 = new ParseFile("logo", logo);
        ParseFile file2 = new ParseFile("Owneridproof", ownerid);
        ParseFile file3 = new ParseFile("shoplicense", shoplic);
        file1.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    file1.saveInBackground();
                }else{
                    Crashlytics.log(Log.ERROR, "Business", "error caught!");
                    Crashlytics.logException(e);
                }
            }
        });
        file2.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    file2.saveInBackground();
                }else{
                    Crashlytics.log(Log.ERROR, "Business", "error caught!");
                    Crashlytics.logException(e);
                }
            }
        });
        file3.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){

                    file3.saveInBackground();
                }else{
                    Crashlytics.log(Log.ERROR, "Business", "error caught!");
                    Crashlytics.logException(e);
                }
            }
        });

        ParseUser user = ParseUser.getCurrentUser();
        user.put("logo", file1);
        user.put("ownerLicense",file2);
        user.put("ShopLicense",file3);
        user.put("Business_name",businname);
        user.put("phoneNumber",phno);
        String tc = tax.getText().toString().trim();
        if(tc.length() > 0){
            user.put("tax",Integer.valueOf(tax.getText().toString().trim()));
        }
       if(taxid.getText().toString().trim() != null){
           user.put("taxId",taxid.getText().toString().trim());
       }
        user.saveInBackground(new SaveCallback()
        {
            @Override
            public void done(ParseException e)
            {
               progressBar.setVisibility(View.GONE);
                if (e == null)
                {
//                    Log.i("Parse", "saved successfully");
                    Intent i = new Intent(Business_setup.this,Bank_Details.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    Toast toast = Toasty.success(Business_setup.this,"saved successfully", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 230);
                    toast.show();
                }
                else
                {
                    customToast(e.getMessage());
                    Crashlytics.log(Log.ERROR, "Business", "error caught!");
                    Crashlytics.logException(e);
                }
            }
        });
    }


    // Choose any image or pdf file from gallery in mobile
    private void IntentCall(int code){
        final String[] ACCEPT_MIME_TYPES = {
                "application/pdf",
                "image/*"
        };
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, ACCEPT_MIME_TYPES);
        }
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), code);

    }

    // Picked gallery file then dividing image an pdf and applying action
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
//            Uri uri = data.getData();
//            String name = "";
            case REQUEST_CODE_LOGOcam:
                if (requestCode == REQUEST_CODE_LOGOcam && resultCode == RESULT_OK) {
//                    Log.d("CameraDemo", "Pic saved");
                    intentImage(Uri.fromFile(new File(currentPhotoPath)),REQUEST_CODE_LOGOcam);
                    logo.setText("logo.jpeg");
                }
                break;

            case REQUEST_CODE_LOGO:
                Uri uri = data.getData();
                if(uri.toString().contains("image"))  {
                    intentImage(uri,REQUEST_CODE_LOGO);
                    logo.setText("logo.jpeg");
                }else{
                    intentPdf(uri,REQUEST_CODE_LOGO);
                  logo.setText("logo.jpeg");
                }

                break;
            case REQUEST_CODE_IDcam:
                if (requestCode == REQUEST_CODE_IDcam && resultCode == RESULT_OK) {
                    intentImage(Uri.fromFile(new File(currentPhotoPath)),REQUEST_CODE_IDcam);
                    uploadid.setText("Id_proof.jpeg");
                }
                break;
            case REQUEST_CODE_ID:
                Uri uri1 = data.getData();
                if(uri1.toString().contains("image"))  {

                    intentImage(uri1,REQUEST_CODE_ID);
                    uploadid.setText("Id_proof.jpeg");
                }else{
                    uploadid.setText("Id_proof.jpeg");
                    intentPdf(uri1,REQUEST_CODE_ID);
                }
                break;
            case REQUEST_CODE_SHOPcam:
                if (requestCode == REQUEST_CODE_SHOPcam && resultCode == RESULT_OK) {
                    intentImage(Uri.fromFile(new File(currentPhotoPath)),REQUEST_CODE_SHOPcam);
                    shoplicense.setText("shop_licence.jpeg");
                }

                break;
            case REQUEST_CODE_SHOP:
                Uri uri2 = data.getData();
                if(uri2.toString().contains("image"))  {
                    shoplicense.setText("shop_licence.jpeg");
                    intentImage(uri2,REQUEST_CODE_SHOP);
                }else{
                     shoplicense.setText("shop_licence.jpeg");
                    intentPdf(uri2,REQUEST_CODE_SHOP);
                }
                break;
        }
    }
    private boolean checkInternetConenction() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // test for connection
        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            customToast("Please check the internet connection");
//            Log.v("spalsh", "Internet Connection Not Present");
            return false;
        }
    }

    //For images - code wise saving converted byte data into separate names
    private void intentImage(Uri uri,int code){

        try {

            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
            byte imageInByte[];
            imageInByte = stream.toByteArray();
//            String image = Base64.encodeToString(imageInByte, Base64.DEFAULT);

            if(code == 22){
                byte1 = imageInByte;
            }else if(code == 33){
                byte2 = imageInByte;
            }else if(code == 44 ) {
                byte3 = imageInByte;
            }
            else if(code == 21){
                byte1 = imageInByte;

            }
            else if(code == 32){
                byte2 = imageInByte;
            }
            else if(code == 43){
                byte3 = imageInByte;
            }

//                upload(imageInByte);
        }
        catch(OutOfMemoryError e)
        {
            Toast.makeText(this, "Size should'not be more than 5mb", Toast.LENGTH_SHORT).show();
            Crashlytics.logException(e);
//            Log.d("tag", "OutOfMemoryError: " + e.toString());
        }
        catch (IOException e) {
            Crashlytics.log(Log.ERROR, "Business", "error caught!");
            Crashlytics.logException(e);
            e.printStackTrace();
        }
    }


    //For pdf files- code wise saving converted byte data into separate names
    private void  intentPdf(Uri uri,int code){
        try {
            InputStream is = getApplicationContext().getContentResolver().openInputStream(uri);
            byte[] bytesArray = new byte[is.available()];
            is.read(bytesArray);
//           uploadpdf(bytesArray);
            if(code == 22){
                byte1 = bytesArray;
            }else if(code == 33){
                byte2 = bytesArray;
            }else if(code == 44) {
                byte3 = bytesArray;
            }
        }
        catch(OutOfMemoryError e)
        {
            Toast.makeText(this, "Size should'not be more than 5mb", Toast.LENGTH_SHORT).show();
            Crashlytics.logException(e);
//            Log.d("tag", "OutOfMemoryError: " + e.toString());
        }
        catch (FileNotFoundException e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        } catch (IOException e) {
            Crashlytics.log(Log.ERROR, "Business", "error caught!");
            Crashlytics.logException(e);
            e.printStackTrace();
        }
    }

    //custom toast message for error messages
    private void customToast(String msg){
        Toast toast = Toasty.error(Business_setup.this,msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 230);
        toast.show();
    }
    String currentPhotoPath;

    private File createImageFile(String name) throws IOException {
        // Create an image file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = name;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

}
