package com.karigarjobs;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.karigarjobs.provider.HomeActivity;
import com.karigarjobs.provider.RegisterProvProfile;
import com.karigarjobs.user.HomeActivityU;
import com.karigarjobs.user.UserProfileCreateActivity;
import com.karigarjobs.user.cvbutton.UserCvCreateSimple;
import com.karigarjobs.utils.LocaleManager;
import com.karigarjobs.utils.NetworkChangeReceiver;
import com.karigarjobs.utils.RestAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static com.karigarjobs.AllUsrProvInfo.logcatdump;


public class MainActivity extends AppCompatActivity  {

    private static int SPLASH_SCREEN_TIME_OUT=2000;
    CheckVersionTask mcheckver = null;
    private boolean isUserLogin = true;
    Context context= null;
    public static MainActivity objMain = null;
    ProgressDialog progress;

    public class LoginCred
    {
        String usrid;
        String mob;
        String passwd;
        String email;
    };
    public LoginCred logincred =null;



    //UserLoginProfileTask mUsrLoginProfCheck = null;
    //ComLoginProfileTask mComLoginProfCheck = null;

    LoginProfileTask mLoginProfCheck = null;
    LoginOTPVerifyTask mLoginOTPVer= null;

    private Handler mHandler;
    EditText emailView;
    TextView mLoginView;
    Button btnVerifyOtp ;
    String noti_isuser=null,noti_uid=null,noti_upid=null;
    //#After completion of 2000 ms, the next activity will get started.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //This method is used so that your splash activity
        //can cover the entire screen.
        /*Note: Enable for dump log in log.txt,also add
        * android:debuggable="true"  inside <application> tag in AndroidManifest.xml
        * */
        //logcatdump(getExternalFilesDir(null).getPath());
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        populateAutoComplete();

        Bundle b = getIntent().getExtras();
        if(b != null) {
            noti_isuser = b.getString("isuser");
            noti_uid = b.getString("uid");
            noti_upid = b.getString("upid");

        }

        logincred = new LoginCred();
        logincred.email = "";
        logincred.mob = "";
        logincred.passwd = "";
        logincred.usrid = "";
        //emailView = findViewById(R.id.textView21);

        final Button submitbtn = findViewById(R.id.imageButton2);
        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String usrid = ((EditText)findViewById(R.id.textView21)).getText().toString();
                String mob = ((EditText)findViewById(R.id.textView15)).getText().toString();
                //String pass = ((EditText)findViewById(R.id.textView17)).getText().toString();
                //String mail = "";//editmail.getText().toString();

                if(mob.isEmpty()  ||  mob.equals(" ") )
                {
                    Toast.makeText(getApplicationContext(),R.string.err_req_field_empty,Toast.LENGTH_LONG).show();
                    return;
                }

                //if( usrid.length() > 20 || mob.length() != 10 || pass.length() <= 6 || ( !mail.isEmpty() && !mail.equals(" ") && !mail.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")))
/*                if( mob.length() != 10 )
                {
                    Toast.makeText(getApplicationContext(),R.string.err_usr_enter_value,Toast.LENGTH_LONG).show();
                    return;
                }*/
                if(!isValidMobile(mob))
                {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_mobile_req), Toast.LENGTH_LONG).show();
                    return;
                }



                logincred.mob = mob;


                submitbtn.setEnabled(false);

                if (NetworkChangeReceiver.isNetworkConnected(getApplicationContext())) {
                    showprogressbar();
/*                    if(isUserLogin) {
                        mUsrLoginProfCheck = new UserLoginProfileTask(mob, mail, usrid, pass);
                        mUsrLoginProfCheck.execute();
                    } else {
                        mComLoginProfCheck = new ComLoginProfileTask(mob, mail, usrid);
                        mComLoginProfCheck.execute();
                    }*/
                    mLoginProfCheck = new LoginProfileTask(logincred.mob,logincred.usrid);
                    mLoginProfCheck.execute();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.alert_network_disconnected), Toast.LENGTH_LONG).show();
                }


            }
        });


        btnVerifyOtp = findViewById(R.id.imageButton3);
        btnVerifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otpval = ((EditText)findViewById(R.id.textView16)).getText().toString();

                if(logincred.mob.equals("") || logincred.usrid.equals("") || otpval.equals(""))
                {
                    Toast.makeText(getApplicationContext(),R.string.err_req_field_empty,Toast.LENGTH_LONG).show();
                }
                if(otpval.length() > 6)
                {
                    Toast.makeText(getApplicationContext(),R.string.err_usr_enter_value,Toast.LENGTH_LONG).show();
                }

                if (NetworkChangeReceiver.isNetworkConnected(getApplicationContext())) {
                    showprogressbar();
                    mLoginOTPVer = new LoginOTPVerifyTask(logincred.mob,logincred.usrid,otpval);
                    mLoginOTPVer.execute();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.alert_network_disconnected), Toast.LENGTH_LONG).show();
                }
            }
        });
        //delete old folder if exist
        delpredirpath();
        //only for user
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {

                if(message.what ==1) {
                    String str = message.obj.toString();
                    submitbtn.setEnabled(true);
                    Toast.makeText(MainActivity.this, R.string.err_same_id, Toast.LENGTH_LONG).show();
                } else if(message.what == 2) {
                    String str = message.obj.toString();
                    chooseNextActivity(str);
                } else if(message.what == 3){
                    ((EditText)findViewById(R.id.textView16)).setText(logincred.passwd); // Remove method for get OTP from SMS, directly fill value
                    switchview(OTP_REGIS_STAGE);
                } else if(message.what == 4){
                    submitbtn.setEnabled(true);
                    Toast.makeText(MainActivity.this, R.string.error_incorrect_otp, Toast.LENGTH_LONG).show();
                }

            }
        };



        //final Session sess = new Session(getApplicationContext());
        //this will bind your MainActivity.class file with activity_main.
        handle_launch.postDelayed(launchView, SPLASH_SCREEN_TIME_OUT);
        MainActivity.objMain = this;

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
        //TODO : Need to check onCreate Called if app shown back to to top of screen
        finish();
        Intent mIntent = new Intent(getIntent());
        startActivity(mIntent);
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

    private void populateAutoComplete() {
        mayRequestContacts();
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


    private static final int REQUEST_READ_CONTACTS = 201;
    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

/*        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            isReadStorage();
            return true;
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }*/
        isReadStorage();
        return false;
    }

    public int REQUEST_READ_EXSTORAGE = 202;
    private boolean isReadStorage()
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if(checkSelfPermission(READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        else {
            requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, REQUEST_READ_EXSTORAGE);
        }
        return false;
    }
    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_CONTACTS) {
            isReadStorage();
        } else if (requestCode == REQUEST_READ_EXSTORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                delpredirpath();
                String profilepath = "profile/rec/";
                File fd = new File(getExternalFilesDir(null), profilepath);
                //String recfilePath = Environment.getExternalStorageDirectory().getPath() + "/karijob/rsm/rec_ans/";
                if (!fd.exists()) {
                    if (!fd.mkdirs()) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_file_create_falied), Toast.LENGTH_LONG).show();
                        Log.i("Error", "Folder not created ");
                    } else {
                        Log.i("Info", "Folder created successfully!");
                    }
                }

            }
        }
    }


    private static final int MY_EXT_STORAGE_READ_PERMISSION_CODE = 200;
    void delpredirpath()
    {
        String proffile = Environment.getExternalStorageDirectory().getPath() + "/karijob/profile/";
        String recfilePath = Environment.getExternalStorageDirectory().getPath() + "/karijob/rsm/rec_ans/";


        File f = new File(proffile);
        if( !f.exists() || !f.isDirectory())
            return ;

        if(!f.canWrite())
        {
            isReadStorage();
            return;
        }

        FileFilter filt = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                String suffix = ".jpg";
                if( pathname.isFile()  &&  (pathname.length() != 0) && pathname.getName().toLowerCase().endsWith(suffix) ) {
                    return true;
                }
                return false;
            }
        };

        //Add all files that comply with the given filter
        File[] files = f.listFiles(filt);
        for( File fi : files) {
            fi.delete();
        }

        File f1 = new File(recfilePath);
        if( !f1.exists() || !f1.isDirectory() )
            return ;


        FileFilter filt1 = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                String suffix = ".mp3";
                if( pathname.isFile()  &&  (pathname.length() != 0) && pathname.getName().toLowerCase().endsWith(suffix) ) {
                    return true;
                }
                return false;
            }
        };

        //Add all files that comply with the given filter
        File[] files1 = f1.listFiles(filt1);
        for( File fi : files1) {
            fi.delete();
        }

    }


    void chooseNextActivity(final String usrid)
    {
        final String USER_LOG_STR = "USER";
        Session sess = new Session(getApplicationContext());
        sess.createSession(logincred.usrid,logincred.mob,logincred.email,USER_LOG_STR, Integer.parseInt(usrid),null,LocaleManager.getlanguage());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.info_create_resume));
        builder.setMessage(getResources().getString(R.string.dialog_fill_detail))
                .setPositiveButton(getResources().getString(R.string.yes_launch), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //launch for create user  profile/resume
                        Intent mIntent = new Intent(getApplicationContext(), UserCvCreateSimple.class);
                        startActivity(mIntent);
                        finish();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.skip_launch), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        Intent mIntent = new Intent(getApplicationContext(), HomeActivityU.class);
                        startActivity(mIntent);
                        finish();
                    }
                });
        builder.show();

    }

    Handler handle_launch = new Handler();
    final Runnable launchView = new Runnable()
    {
        public void run()
        {
            //Toast.makeText(getApplicationContext(), getResources().getString(R.string.alert_network_disconnected), Toast.LENGTH_LONG).show();
            try {
                if(NetworkChangeReceiver.isNetworkConnected(getApplicationContext())) {
                    showprogressbar();
                    mcheckver = new CheckVersionTask(getApplicationContext());
                    mcheckver.execute();
                } else {
                    //Toast.makeText(getApplicationContext(), getResources().getString(R.string.alert_network_disconnected), Toast.LENGTH_LONG).show();
                    final Session sess = new Session(getApplicationContext());
                    if (sess.checkLogin())
                    {
                        if (sess.checkLogintype()) {
                            //user
                            LocaleManager.setlan(Session.getlancode());
                            if(noti_isuser != null && noti_isuser.equals("USER") && noti_uid != null && noti_uid.equals(String.valueOf(Session.getUsr_id())) && noti_upid != null )
                                Session.setupid(noti_upid);

                            Intent mIntent = new Intent(getApplication(), HomeActivityU.class);
                            startActivity(mIntent);
                            finish();
                        } else {
                            //prov
                            LocaleManager.setlan(Session.getlancode());
                            Intent mIntent = new Intent(getApplication(), HomeActivity.class);
                            startActivity(mIntent);
                            finish();
                        }
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    };

    Handler handle_end = new Handler();
    final Runnable changeView = new Runnable()
    {
        public void run()
        {
            //Toast.makeText(getApplicationContext(), getResources().getString(R.string.alert_network_disconnected), Toast.LENGTH_LONG).show();

            ImageView imgv = (ImageView)findViewById(R.id.splashscreen);
            LinearLayout.LayoutParams layoutparam = ( LinearLayout.LayoutParams )imgv.getLayoutParams();
            layoutparam.setMargins(0,50,0,0);
            layoutparam.height = 500;
            imgv.setLayoutParams(layoutparam);
            imgv.requestLayout();


            if(LocaleManager.getlanguage().equals("null_null"))
                switchview(LAN_STAGE);
            else
                switchview(ISUSER_STAGE);

        }

    };




    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_lang_en:
                if (checked) {

                    if(!LocaleManager.getlanguage().equals(LocaleManager.LOCALE_EN)) {
                        LocaleManager.setlan(LocaleManager.LOCALE_EN);
                        Intent intent= getIntent();
                        finish();
                        startActivity(intent);
                    } else {
                        switchview(ISUSER_STAGE);
                    }
                }
                break;
            case R.id.radio_lang_hi:
                if (checked) {

                    if(!LocaleManager.getlanguage().equals(LocaleManager.LOCALE_HI)) {
                        LocaleManager.setlan(LocaleManager.LOCALE_HI);
                        Intent intent= getIntent();
                        finish();
                        startActivity(intent);
                    } else {
                        switchview(ISUSER_STAGE);
                    }
                }
                break;
            case R.id.radio_user:
                if (checked) {
                    isUserLogin = true;
                    //switchview(REGIS_STAGE);
                    if (NetworkChangeReceiver.isNetworkConnected(getApplicationContext())) {
                        showprogressbar();
                        mLoginProfCheck = new LoginProfileTask(logincred.mob,logincred.usrid);
                        mLoginProfCheck.execute();
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.alert_network_disconnected), Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case R.id.radio_prov:
                if (checked) {
                    isUserLogin = false;
                    switchview(REGIS_STAGE);
                }
                break;
        }
    }

    private static final int LAN_STAGE = 1;
    private static final int ISUSER_STAGE = 2;
    private static final int REGIS_STAGE = 3;
    private static final int OTP_REGIS_STAGE = 4;
    void switchview(int stage)
    {
        RadioGroup rOption = findViewById(R.id.radio_lan_gp);
        RadioGroup rg_seluser = findViewById(R.id.radio_isuseropt);
        RelativeLayout lay_regis = findViewById(R.id.registration_pg);
        RelativeLayout lay_otpregis = findViewById(R.id.registration_pg2);
        switch(stage)
        {
            case LAN_STAGE:
                //Set Locale
                rOption.setVisibility(View.VISIBLE);
                rg_seluser.setVisibility(View.GONE);
                lay_regis.setVisibility(View.GONE);
                lay_otpregis.setVisibility(View.GONE);
                break;
            case ISUSER_STAGE:
                rOption.setVisibility(View.GONE);
                rg_seluser.setVisibility(View.VISIBLE);
                lay_regis.setVisibility(View.GONE);
                lay_otpregis.setVisibility(View.GONE);
                break;
            case REGIS_STAGE:
                rOption.setVisibility(View.GONE);
                rg_seluser.setVisibility(View.GONE);
                lay_regis.setVisibility(View.VISIBLE);
                lay_otpregis.setVisibility(View.GONE);
                break;
            case OTP_REGIS_STAGE:
                rOption.setVisibility(View.GONE);
                rg_seluser.setVisibility(View.GONE);
                lay_regis.setVisibility(View.GONE);
                lay_otpregis.setVisibility(View.VISIBLE);
                break;
            default :
                break;

        }

    }


    public class CheckVersionTask extends AsyncTask<Void, Void, String> {
        private Context ctx;
        CheckVersionTask(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String r = null;
            try
            {
                // Simulate network access.
                r = RestAPI.getAppVer("a");
                Thread.sleep(2000);
                hideprogrssbar();
                if(r != null)
                {
                    //updateCompInfoView(r);
                    JSONObject res = new JSONObject(r);

                    if(!res.has("error"))
                    {
                        //String result = res.getString("data");
                        //JSONObject dt2 = new JSONObject(result);
                        String newVar = res.getString("version");
                        String currversion = BuildConfig.VERSION_NAME;
                        int curvarval = 0;
                        int newVarVal = 0;
                        curvarval = (int)(Float.parseFloat(currversion) * 10);

                        if(newVar != null && !newVar.equals("null") && !newVar.isEmpty())
                        {
                            newVarVal = (int)(Float.parseFloat(newVar)*10);
                        }
                        Log.d("Current App Version:",String.valueOf(curvarval));
                        Log.d("Server App Version:",String.valueOf(newVarVal));
                        if(curvarval < newVarVal)
                        {
                            //update APP from play store
                            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                            }

                        } else {
                            final Session sess = new Session(getApplicationContext());
                            if (sess.checkLogin())
                            {
                                if (sess.checkLogintype()) {
                                    //user
                                    LocaleManager.setlan(Session.getlancode());
                                    if(noti_isuser != null && noti_isuser.equals("USER") && noti_uid != null && noti_uid.equals(String.valueOf(Session.getUsr_id())) && noti_upid != null )
                                        Session.setupid(noti_upid);

                                    Intent mIntent = new Intent(getApplication(), HomeActivityU.class);
                                    startActivity(mIntent);
                                    finish();
                                } else {
                                    //prov
                                    LocaleManager.setlan(Session.getlancode());
                                    Intent mIntent = new Intent(getApplication(), HomeActivity.class);
                                    startActivity(mIntent);
                                    finish();
                                }
                            }
                            else
                            {
                                handle_end.postDelayed(changeView, 10);
                                //invoke the SecondActivity.
                            }
                        }
                    }
                    else
                    {
                        //handle_finish.postDelayed(changeView, SPLASH_SCREEN_TIME_OUT);
                        Log.d("Server Error",res.getString("error"));
                    }
                }

            } catch (InterruptedException e) {
                hideprogrssbar();
                Log.e("", e.toString());
                return r;
            }
            catch ( Exception ex)
            {
                hideprogrssbar();
                Log.e("", ex.toString());
                return r;
            }

            // TODO: register the new account here.
            return r;
        }

        @Override
        protected void onPostExecute(final String success) {
            mcheckver = null;
        }

        @Override
        protected void onCancelled() {
            mcheckver = null;
        }
    }

    public class LoginProfileTask extends AsyncTask<Void, Void, String> {

        private String mobile;
        private String uid;
        LoginProfileTask(String mob,String uid) {
            this.mobile = mob;
            this.uid = uid;
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String r = null;
            try
            {
                // Simulate network access.
                r = RestAPI.getLoginMob(isUserLogin,mobile,uid,getApplicationContext());
                Thread.sleep(2000);
                hideprogrssbar();
                if(r !=null)
                {
                    //updateCompInfoView(r);
                    JSONObject res = new JSONObject(r);
                    if(res.has("data"))
                    {
                        String result = res.getString("data");
                        String otp = res.getString("cred");
                        logincred.usrid = result;
                        logincred.passwd = otp;
                        if(isUserLogin) {
                            Message message = mHandler.obtainMessage(2, logincred.usrid);
                            message.sendToTarget();
                        } else {
                            Message message = mHandler.obtainMessage(3, logincred.usrid);
                            message.sendToTarget();
                        }
                    }
                }

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
            mLoginProfCheck = null;
        }

        @Override
        protected void onCancelled() {
            mLoginProfCheck = null;

        }
    }


    public class LoginOTPVerifyTask extends AsyncTask<Void, Void, String> {

        private String mobile;
        private String uid,otp;
        LoginOTPVerifyTask(String mob,String uid,String val) {
            this.mobile = mob;
            this.uid = uid;
            this.otp = val;
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String r = null;
            try
            {
                // Simulate network access.
                r = RestAPI.getLoginCred(isUserLogin,mobile,uid,otp);
                Thread.sleep(2000);
                hideprogrssbar();
                if(r !=null)
                {
                    //updateCompInfoView(r);
                    JSONObject res = new JSONObject(r);
                    if(res.has("data"))
                    {
                        String result = res.getString("data");
                        if(isUserLogin)
                        {
                            if(result.equals(logincred.usrid)) {
                                Message message = mHandler.obtainMessage(2, logincred.usrid);
                                message.sendToTarget();
                            }else {
                                Message message = mHandler.obtainMessage(4, logincred.usrid);
                                message.sendToTarget();
                            }

                        } else {
                            final String PROV_LOG_STR = "PROV";
                            Session sess = new Session(getApplicationContext());
                            sess.createSession(logincred.usrid,logincred.mob,logincred.email,PROV_LOG_STR, Integer.parseInt(uid),null,LocaleManager.getlanguage());

                            finish();
                            Intent mIntent = new Intent(getApplicationContext(), RegisterProvProfile.class);
                            Bundle b = new Bundle();
                            b.putString("parent","REGIS");
                            b.putString("uid", logincred.usrid); //Your id
                            b.putString("mob", logincred.mob); //Your id
                            b.putString("mail", logincred.email); //Your id
                            b.putString("pswd", logincred.passwd); //Your id
                            mIntent.putExtras(b);
                            startActivity(mIntent);
                        }

                    }
                }

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
            mLoginOTPVer = null;
        }

        @Override
        protected void onCancelled() {
            mLoginOTPVer = null;
        }
    }



}
