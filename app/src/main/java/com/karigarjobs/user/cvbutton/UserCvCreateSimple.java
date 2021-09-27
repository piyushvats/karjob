package com.karigarjobs.user.cvbutton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.textclassifier.TextClassifier;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.slider.Slider;
import com.karigarjobs.AllUsrProvInfo;
import com.karigarjobs.R;
import com.karigarjobs.Session;
import com.karigarjobs.user.FragUsrProfDocUpload;
import com.karigarjobs.user.HomeActivityU;
import com.karigarjobs.user.UserProfileCreateActivity;
import com.karigarjobs.user.cvrecord.UserProfileCreateVoice;
import com.karigarjobs.utils.ImageCache;
import com.karigarjobs.utils.LocaleManager;
import com.karigarjobs.utils.NetworkChangeReceiver;
import com.karigarjobs.utils.PlayRecordAct;
import com.karigarjobs.utils.RestAPI;
import com.karigarjobs.utils.VolleyMultiPart;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.karigarjobs.AllUsrProvInfo.getAddresFromLocation;
import static com.karigarjobs.AllUsrProvInfo.getPastDateFromDays;
import static com.karigarjobs.user.HomeActivityU.jclist;

public class UserCvCreateSimple extends AppCompatActivity {

    String TAG = "UserCvCreateSimple";
    public static String usrloginid, usrprofid = null;
    public static UserCvCreateSimple objcv;
    public static ArrayList<AllUsrProvInfo.JobCategory> usrjclist = null;
    public static ArrayList<String> selectedjobcat = null;
    GetJobTypeTask mJobTypeRet = null;
    UserProCreateTask mUsrCvType = null;
    ProgressDialog progress;
    Slider slider;
    TextView ageval;
    Button btnsubmit;

    String genderid = "";
    String englishid = "";
    String workexpid = "";
    String educationid = "";

    boolean isLocationGranted = false;
    LocationListener locationListener = null;
    LocationManager locationManager;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback mLocationCallback;


    Location curlocation = null;

    Button btnjobcat;
    public static int REQUEST_MY_LOCATION = 101;
    String appfilelocation = "";

    boolean isfineloc = true, iscoarseloc = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_cv_create_simple);

        if (Session.IsCreated) {
            usrloginid = String.valueOf(Session.getUsr_id());
        }

        appfilelocation = getExternalFilesDir(null).getPath();
        //locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                //onNewLocation(locationResult.getLastLocation());
                curlocation = locationResult.getLastLocation();
                Log.i("onLocationResult", String.valueOf(curlocation));
            }
        };


        isLocationGranted = true;
        createLocationRequest();
        if(checklocpermission()) {
            if(!canGetLocation())
            {
                showSettingsAlert();
            }
            //getLastLocation();
        } else {
            Log.i("Permission","Location permission is not granted");
        }



        if (jclist != null && !jclist.isEmpty()) {
            usrjclist = new ArrayList<>(jclist.size());
            usrjclist.addAll(jclist);

        }

        //For Age
        int defaultage = 25;
        slider = findViewById(R.id.age_slider);
        //rangeSlider.setThumbIndices(oldleftThumbIndex , oldrightThumbIndex);
        ageval = (TextView) findViewById(R.id.textView3);
        ageval.setText(String.valueOf(defaultage) + " " + getResources().getString(R.string.title_inyear));

        slider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(Slider slider) {
                // Responds to when slider's touch event is being started
            }

            @Override
            public void onStopTrackingTouch(Slider slider) {
                // Responds to when slider's touch event is being stopped
            }
        });

        slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                TextView tvmin = (TextView) findViewById(R.id.textView3);
                int val = ((int) slider.getValue());
                tvmin.setText(String.valueOf(val) + " " + getResources().getString(R.string.title_inyear));
            }
        });

        //for choose job cat
        btnjobcat = findViewById(R.id.choosejobcat);
        btnjobcat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showJobCatOptionsDialog();
            }
        });

        CheckBox chkterm = findViewById(R.id.checkBox_term);
        chkterm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://karigarjobs.com/portal/server/admin/termdocsinfp.pdf")));
            }
        });

        btnsubmit = findViewById(R.id.submit_button);
        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitallinfo();
            }
        });


        //get profile from server . Only For edit/update profile
        if (NetworkChangeReceiver.isNetworkConnected(getApplicationContext())) {
            //Only new profile but from Login Registration acivity . async = get Job type list + createfragment_firstpage()
            showprogressbar();
            mJobTypeRet = new GetJobTypeTask("0");
            mJobTypeRet.execute();
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.alert_network_disconnected), Toast.LENGTH_LONG).show();
        }

        //make static context
        objcv = this;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.updateResources(base));
    }

    @Override
    public void applyOverrideConfiguration(Configuration overrideConfiguration) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
            // update overrideConfiguration with your locale
            LocaleManager.updateResourcesNew(overrideConfiguration); // you will need to implement this
        }
        super.applyOverrideConfiguration(overrideConfiguration);
    }

    @Override
    public void onBackPressed() {
        Log.i("Back Pressed", "UserCvCreateSimple Activity");
        fusedLocationClient.removeLocationUpdates(mLocationCallback);
        Intent intent = new Intent(getApplicationContext(), HomeActivityU.class);
        startActivity(intent);
        finish();
    }

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    /**
     * Contains parameters used by {@link com.google.android.gms.location.FusedLocationProviderApi}.
     */
    private LocationRequest mLocationRequest;

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_MY_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {

                    isLocationGranted = true;
                    fusedLocationClient.requestLocationUpdates(mLocationRequest,mLocationCallback, Looper.myLooper());
                    if(!canGetLocation())
                    {
                        showSettingsAlert();
                    }
                } else {
                    Toast.makeText(this, getResources().getString(R.string.error_locationper_grant), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, getResources().getString(R.string.error_locationper_nogrant), Toast.LENGTH_SHORT).show();
            }
            return;
        }
    }

    public boolean checklocpermission()
    {

        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_MY_LOCATION);
            return false;
        } else {
            fusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            return true;
        }
    }

    public boolean canGetLocation() {
        boolean result = true;
        LocationManager lm;
        boolean gpsEnabled = false;
        boolean networkEnabled = false;

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // exceptions will be thrown if provider is not permitted.
        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            networkEnabled = lm
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        return gpsEnabled /*&& networkEnabled*/;
    }
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getResources().getString(R.string.title_note));
        alertDialog.setMessage(getResources().getString(R.string.error_location_service));
        alertDialog.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });

        alertDialog.show();
    }




    public void submitallinfo() {
        String mobile = "", ageval = "", usrname = "";
        String curaddress = "", curaddpin = "";
        mobile = ((EditText) findViewById(R.id.editText1)).getText().toString();
        usrname = ((EditText) findViewById(R.id.editText0)).getText().toString();

        if (usrname.equals("")) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.err_usrnoname), Toast.LENGTH_LONG).show();
            return;
        }

        if (mobile.equals("")) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_mobile_req), Toast.LENGTH_LONG).show();
            return;
        }

        if(!isValidMobile(mobile))
        {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_mobile_req), Toast.LENGTH_LONG).show();
            return;
        }
        Log.i("INFO", String.valueOf(slider.getValue()));
        ageval = getPastDateFromDays((int) slider.getValue() * 365);

        //for select gender
        RadioGroup rgpg = findViewById(R.id.radioGroupGender);
        switch (rgpg.getCheckedRadioButtonId()) {
            case R.id.radiomale:
                genderid = "1";
                break;
            case R.id.radiofemale:
                genderid = "2";
                break;
            case R.id.radioother:
                genderid = "3";
                break;
            default:
                break;
        }

        //for select english
        RadioGroup rgeng = findViewById(R.id.radioGroupEngLang);
        switch (rgeng.getCheckedRadioButtonId()) {
            case R.id.radioengyes:
                englishid = "1";
                break;
            case R.id.radioengno:
                englishid = "0";
                break;
            default:
                break;
        }

        //for work exp
        RadioGroup rgexp = findViewById(R.id.radioGroupExp);
        switch (rgexp.getCheckedRadioButtonId()) {
            case R.id.radioexpyes:
                workexpid = "1";
                break;
            case R.id.radioexpno:
                workexpid = "0";
                break;
            default:
                break;
        }

        //for educaton
        RadioGroup rgedu = findViewById(R.id.radioGroupEdu);
        switch (rgedu.getCheckedRadioButtonId()) {
            case R.id.radioedu10th:
                educationid = "1";
                break;
            case R.id.radioedu12th:
                educationid = "2";
                break;
            case R.id.radioedugrad:
                educationid = "3";
                break;
            case R.id.radionoedu:
                educationid = "0";
                break;
            default:
                break;
        }


        if (genderid.equals("")) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_gender_required), Toast.LENGTH_LONG).show();
            return;
        }

        if (englishid.equals("")) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_english_req), Toast.LENGTH_LONG).show();
            return;
        }

        if (workexpid.equals("")) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_worlexp_req), Toast.LENGTH_LONG).show();
            return;
        }

        if (educationid.equals("")) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_edulevel_req), Toast.LENGTH_LONG).show();
            return;
        }

        if (selectedjobcat == null || selectedjobcat.isEmpty()) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.erro_worktype_req), Toast.LENGTH_LONG).show();
            return;
        }

        if (curlocation == null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Toast.makeText(this, getResources().getString(R.string.error_locationper_grant), Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if(curlocation!=null)
        {
            Address addr = getAddresFromLocation(getApplicationContext(),curlocation.getLatitude(),curlocation.getLongitude());

            if(addr.getLocality()!=null)
                curaddress = addr.getLocality();

            if(addr.getSubAdminArea()!=null)
                curaddress = curaddress +" "+addr.getSubAdminArea();

            if(addr.getAdminArea()!=null)
                curaddress = curaddress +" "+ addr.getAdminArea();

            if(addr.getPremises()!=null)
                curaddress = curaddress +" "+ addr.getPremises();

            curaddpin = addr.getPostalCode();

        }else{
            curaddress="";
            curaddpin="";
            Toast.makeText(this, getResources().getString(R.string.error_location_service), Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject jsonObj = new JSONObject();
        try {

            jsonObj.put("adminid", UserCvCreateSimple.usrloginid);
            jsonObj.put("uid", UserCvCreateSimple.usrprofid);
            jsonObj.put("uname", usrname);
            jsonObj.put("dob", ageval);
            jsonObj.put("sex", genderid);
            jsonObj.put("caddr", curaddress);
            jsonObj.put("caddrpin", curaddpin);
            jsonObj.put("umob", mobile);
            jsonObj.put("isenglish", englishid);
            jsonObj.put("isexp", workexpid);
            jsonObj.put("edulevel", educationid);

            JSONArray objArr1 = new JSONArray();
            JSONObject objtemp0 = new JSONObject();
            for(int j=0;j<selectedjobcat.size();j++)
            {
                objArr1.put(selectedjobcat.get(j));
            }
            objtemp0.put("myrows",objArr1);
            jsonObj.put("jtype",objtemp0);

            Log.i("Info",jsonObj.toString());

        } catch(JSONException ex)
        {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.err_try_again), Toast.LENGTH_LONG).show();
        }

        CheckBox chekbx = findViewById(R.id.checkBox_term);
        if(chekbx.isChecked()) {
            btnsubmit.setEnabled(false);
            //send all info to server
            if(NetworkChangeReceiver.isNetworkConnected(getApplicationContext())) {
                showprogressbar();
                mUsrCvType = new UserProCreateTask(jsonObj);
                mUsrCvType.execute();

            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.alert_network_disconnected), Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(getApplicationContext(), R.string.info_click_term_condition, Toast.LENGTH_SHORT).show();
        }

    }

    public boolean isValidMobile(String mobile)
    {
        boolean flag = true;
        String mobilepattern = "[0-9]{10,}";

        if(!(mobile.length()>9 && mobile.length() < 14))
            flag = false;

        if(!mobile.matches(mobilepattern))
            flag = false;

        return flag ;

    }

    public void showJobCatOptionsDialog()
    {
        final int radiobtnidoffeset = 1000;
        final LayoutInflater inflater = LayoutInflater.from(UserCvCreateSimple.this);
        final View alertLayout = inflater.inflate(R.layout.alert_selection_job_type, null);
        final AlertDialog.Builder alert = new AlertDialog.Builder(UserCvCreateSimple.this);

        //alert.setTitle(getResources().getString(R.string.title_sel_jobcat));
        alert.setTitle(getResources().getString(R.string.title_select_work_type));
        final RadioGroup radiogrp = alertLayout.findViewById(R.id.radioGroupjobcat);
        if (selectedjobcat != null)
            selectedjobcat.clear();

        //Add category button in view
        for(int i=0;i<usrjclist.size();i++)
        {
            final CheckBox rdbtn = new CheckBox(this);
            rdbtn.setId(Integer.parseInt(usrjclist.get(i).catnum)+radiobtnidoffeset);
            rdbtn.setText(usrjclist.get(i).catname);
            rdbtn.setTag(usrjclist.get(i).catnum);
            rdbtn.setTextAlignment(Layout.Alignment.ALIGN_CENTER.ordinal());
            rdbtn.setAllCaps(false);
            rdbtn.setTextColor(0xFFFFFFFF);
            rdbtn.setTextSize(15);


            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT,130);
            params.setMargins(10,10,10,10);

            rdbtn.setLayoutParams(params);
            rdbtn.setBackground(getResources().getDrawable(R.drawable.mybuttonbackground));
            //rdbtn.setBackgroundColor(Color.TRANSPARENT);
            rdbtn.setFocusable(true);

            rdbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox btn = v.findViewById(v.getId());
                    Integer catnum = v.getId()- radiobtnidoffeset;

                    if (catnum < 1 || catnum > usrjclist.size())
                        return ;

                    if(btn.isChecked()) {
                        if (selectedjobcat == null)
                            selectedjobcat = new ArrayList<>();

                        if(selectedjobcat.size()>=6)
                        {
                            Toast.makeText(getApplicationContext(),getResources().getString(R.string.alert_max3_comp),Toast.LENGTH_LONG).show();
                            rdbtn.setChecked(false);
                            return;
                        }

                        if(selectedjobcat.indexOf(String.valueOf(catnum)) != -1)
                            return;


                        selectedjobcat.add(String.valueOf(catnum));
                        Log.i("Check radio button", String.valueOf(catnum));
                    } else {
                        if(selectedjobcat != null && !selectedjobcat.isEmpty())
                        {
                            int idx = selectedjobcat.indexOf(String.valueOf(catnum));
                            if(idx != -1)
                            {
                                selectedjobcat.remove(idx);
                            }
                        }
                        //btn.setChecked(false);
                        Log.i("Check radio button", String.valueOf(catnum));
                    }
                }
            });
            radiogrp.addView(rdbtn);
        }
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        alert.setNegativeButton(getResources().getString(R.string.alert_cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(selectedjobcat!=null) {
                    selectedjobcat.clear();
                    btnjobcat.setBackgroundResource(R.drawable.mybutton);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        btnjobcat.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
                    }
                }
            }
        });
        alert.setPositiveButton(getResources().getString(R.string.action_submitpwd), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(selectedjobcat!=null)
                {
                    Log.i("Item selected",String.valueOf(selectedjobcat.size()));
                    //AllUsrProvInfo.logdumpfile(appfilelocation,TAG,"Item selected"+String.valueOf(selectedjobcat.size()));
                }


                if(selectedjobcat!=null && !selectedjobcat.isEmpty()) {
                    btnjobcat.setBackgroundResource(R.drawable.mybuttongrn);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        btnjobcat.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccept));
                    }
                }

            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }




    public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
            //Toast.makeText(parent.getContext(),"OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString(),Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }

    }

    void showprogressbar()
    {
        if(progress == null)
            progress = new ProgressDialog(this);

        progress.setMessage(getResources().getString(R.string.text_status_processing));
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();
    }

    void hideprogrssbar()
    {
        if(progress !=null)
            progress.dismiss();
    }

    public void showupjplist(String obj)
    {
        try {
            JSONObject jsonObj = new JSONObject(obj);
            JSONArray jsonArraymain = jsonObj.getJSONArray("data");
            JSONArray jsonArray2 = jsonArraymain.getJSONArray(0);
            usrjclist = new ArrayList<>(jsonArray2.length());
            if (LocaleManager.getlanguage().equals(LocaleManager.LOCALE_HI)) {
                for (int j = 0; j < jsonArray2.length(); j++) {
                    JSONObject jsonObject = jsonArray2.getJSONObject(j);
                    AllUsrProvInfo.JobCategory item = new AllUsrProvInfo.JobCategory();
                    item.catname = jsonObject.getString("catname_hi");
                    item.catnum = jsonObject.getString("catnum");
                    usrjclist.add(item);
                }
            } else {
                for (int j = 0; j < jsonArray2.length(); j++) {
                    JSONObject jsonObject = jsonArray2.getJSONObject(j);
                    AllUsrProvInfo.JobCategory item = new AllUsrProvInfo.JobCategory();
                    item.catname = jsonObject.getString("catname");
                    item.catnum = jsonObject.getString("catnum");
                    usrjclist.add(item);
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public static void clearall()
    {
        //go back to home activity
        Intent intent = new Intent(objcv, HomeActivityU.class);
        objcv.startActivity(intent);
        objcv.finish();

    }


    Handler handle_finish = new Handler();
    final Runnable changeView_err_finish = new Runnable()
    {
        public void run()
        {
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.err_try_again),Toast.LENGTH_LONG).show();
            return;
        }
    };
    final Runnable changeView_ok_finish = new Runnable()
    {
        public void run()
        {

            String mobile = ((EditText)findViewById(R.id.editText1)).getText().toString();
            String usrname = ((EditText)findViewById(R.id.editText0)).getText().toString();

            Session.setname(usrname);
            Session.setmob(mobile);
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.alert_audiocv_submit_succ),Toast.LENGTH_LONG).show();
            fusedLocationClient.removeLocationUpdates(mLocationCallback);
            return;
        }
    };

    public class GetJobTypeTask extends AsyncTask<Void, Void, String> {

        private String id;

        GetJobTypeTask(String data) {
            id = data;
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String r = null;
            try
            {
                // Simulate network access.
                r = RestAPI.getJobType(id);
                Thread.sleep(2000);
                hideprogrssbar();
                if(r != null)
                {
                    showupjplist(r);
                }

            } catch (InterruptedException e) {
                Log.e("", e.toString());
                hideprogrssbar();
                return r;
            }
            catch ( Exception ex)
            {
                Log.e("", ex.toString());
                hideprogrssbar();
                return r;
            }

            // TODO: register the new account here.
            return r;
        }

        @Override
        protected void onPostExecute(final String success) {
            mJobTypeRet = null;

            if (success!=null) {
                //finish();
            } else {
                //mPasswordView.setError(getString(R.string.error_incorrect_password));
                //mPasswordView.requestFocus();
                Toast.makeText(getApplicationContext(), R.string.err_usr_profile_reterive, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mJobTypeRet = null;

        }
    }



    public class UserProCreateTask extends AsyncTask<Void, Void, String> {

        private JSONObject  jsonobj;

        UserProCreateTask(JSONObject obj) {
            jsonobj = obj;
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String r = null;
            try
            {
                // Simulate network access.
                r = RestAPI.submitusrcvsimple(jsonobj);
                Thread.sleep(2000);
                hideprogrssbar();
                if(r !=null)
                {
                    try {
                        JSONObject obj = new JSONObject(r);
                        if(obj.has("data")) {
                            String usrproid = obj.getString("data");
                            Session.setupid(usrproid);
                            handle_finish.postDelayed(changeView_ok_finish,10);
                        } else {
                            handle_finish.postDelayed(changeView_err_finish,10);
                        }
                        clearall();
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }

                }


            } catch (InterruptedException e) {
                Log.e("", e.toString());
                hideprogrssbar();
                return r;
            }

            // TODO: register the new account here.
            return r;
        }

        @Override
        protected void onPostExecute(final String success) {
            mUsrCvType = null;

            if (success!=null) {
                //finish();
            } else {
                //mPasswordView.setError(getString(R.string.error_incorrect_password));
                //mPasswordView.requestFocus();
                Toast.makeText(getApplicationContext(), R.string.err_usr_profile_reterive, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mUsrCvType = null;

        }
    }
}