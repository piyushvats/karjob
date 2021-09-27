package com.karigarjobs.provider;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.karigarjobs.R;
import com.karigarjobs.Session;
import com.karigarjobs.user.HomeActivityU;
import com.karigarjobs.user.SettingsUser;
import com.karigarjobs.utils.CopyFiles;
import com.karigarjobs.utils.LocaleManager;
import com.karigarjobs.utils.NetworkChangeReceiver;
import com.karigarjobs.utils.RestAPI;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;


public class SettingsProv extends AppCompatActivity {

    ImageView imgtn;
    LoginProfileTask mLoginProfCheck = null;
    LoginOTPVerifyTask mLoginOTPVer= null;

    ProgressDialog progress;
    private Handler mHandler;

    String strmo = "";
    public String provfileSelectPath =  "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_prov);

        EditText edtMob = findViewById(R.id.textView15);
        edtMob.setText(Session.getmob());

        imgtn = (ImageView)findViewById(R.id.imageView_doc);
        imgtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Bitmap fl =
                //imgtn.setImageBitmap();
                imgtn.setEnabled(false);
                opendir();

            }
        });

        Button btnSendOtp = findViewById(R.id.imageButton2);
        btnSendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText edtMob = findViewById(R.id.textView15);
                strmo = edtMob.getText().toString();

                EditText edtpwd = findViewById(R.id.textView17);
                String strpwd = edtpwd.getText().toString();

                String usrlogid = String.valueOf(Session.getUsr_id());

                if(strmo.isEmpty() || strmo == " "  )
                {
                    Toast.makeText(getApplicationContext(),R.string.err_req_field_empty,Toast.LENGTH_LONG).show();
                    return;
                }


                if(!isValidMobile(strmo))
                {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_mobile_req), Toast.LENGTH_LONG).show();
                    return;
                }

                if(strmo.equals(Session.getmob())) {
                    Intent mIntent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(mIntent);
                    finish();
                    return;
                }

                if(NetworkChangeReceiver.isNetworkConnected(getApplicationContext())) {
                    showprogressbar();
                    mLoginProfCheck = new LoginProfileTask(strmo,usrlogid);
                    mLoginProfCheck.execute();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.alert_network_disconnected), Toast.LENGTH_LONG).show();
                }

            }
        });

        Button btnVerifyOtp = findViewById(R.id.imageButton3);
        btnVerifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText edtotp = findViewById(R.id.textView17);
                String strotp = edtotp.getText().toString();
                String usrlogid = String.valueOf(Session.getUsr_id());
                if(strotp.isEmpty() || strotp.equals(" "))
                {
                    Toast.makeText(getApplicationContext(),R.string.err_req_field_empty,Toast.LENGTH_LONG).show();
                    return;
                }

                if( strmo.length() != 6 )
                {
                    Toast.makeText(getApplicationContext(),R.string.err_usr_enter_value,Toast.LENGTH_LONG).show();
                    return;
                }
                if(NetworkChangeReceiver.isNetworkConnected(getApplicationContext())) {
                    showprogressbar();
                    mLoginOTPVer = new LoginOTPVerifyTask(strmo,usrlogid,strotp);
                    mLoginOTPVer.execute();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.alert_network_disconnected), Toast.LENGTH_LONG).show();
                }

            }
        });


        provfileSelectPath = getExternalFilesDir(null).getPath() + "/prprofile/";
        String propic = provfileSelectPath + "profpic.jpg";
        File f = new File(propic);
        if(f.exists()) {
            try {
                imgtn.setEnabled(false);
                setproffilename(propic);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {

                if(message.what ==1) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.err_loginupdate), Toast.LENGTH_LONG).show();
                } else if(message.what == 2) {
                    RelativeLayout rl = findViewById(R.id.registration_pg);
                    rl.setVisibility(View.GONE);
                    RelativeLayout r2 = findViewById(R.id.registration_pg2);
                    r2.setVisibility(View.VISIBLE);
                } else if(message.what == 3) {
                    RelativeLayout rl = findViewById(R.id.registration_pg2);
                    rl.setVisibility(View.GONE);
                    Session.setmob(strmo);
                    Intent mIntent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(mIntent);
                    finish();

                }

            }
        };

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
        Intent mIntent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(mIntent);
        finish();
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


    private static final int RESULT_CODE = 0x321;
    private static final int READ_REQUEST_CODE = 42;

    public String opendir() {
        String outdirpath = provfileSelectPath + "profpic.jpg";

        Intent data = new Intent(this, CopyFiles.class);
        data.putExtra("outdirpath",outdirpath);
        //setResult(RESULT_OK,data);

        startActivityForResult(data, RESULT_CODE);
        return null;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                String flpath = data.getStringExtra("outdirpath");
                try {
                    setproffilename(flpath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }



    void setproffilename(String flpath) throws IOException {
        Bitmap  btmp = getResizedBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(new File(flpath))),400,400);
        ImageView imgtn = (ImageView)findViewById(R.id.imageView_doc);
        imgtn.setImageBitmap(btmp);
        imgtn.setEnabled(true);
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // Create a matrix for the manipulation
        Matrix matrix = new Matrix();

        // Resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);

        // Recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;

    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 6;
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_lang_en:
                if (checked) {
                    LocaleManager.setlan(LocaleManager.LOCALE_EN);
                }
                break;
            case R.id.radio_lang_hi:
                if (checked) {
                    LocaleManager.setlan(LocaleManager.LOCALE_HI);
                }
                break;
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
                r = RestAPI.getLoginMob(true,mobile,uid,getApplicationContext());
                Thread.sleep(2000);
                hideprogrssbar();
                if(r !=null)
                {
                    //updateCompInfoView(r);
                    JSONObject res = new JSONObject(r);
                    if(res.has("data"))
                    {
                        String result = res.getString("data");
                        Message message = mHandler.obtainMessage(2, result);
                        message.sendToTarget();
                    } else {
                        Message message = mHandler.obtainMessage(1, "error");
                        message.sendToTarget();
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
                r = RestAPI.getLoginCred(true,mobile,uid,otp);
                Thread.sleep(2000);
                hideprogrssbar();
                if(r !=null)
                {
                    //updateCompInfoView(r);
                    JSONObject res = new JSONObject(r);
                    if(res.has("data"))
                    {
                        String result = res.getString("data");
                        Message message = mHandler.obtainMessage(3, result);
                        message.sendToTarget();
                    } else {
                        Message message = mHandler.obtainMessage(1, "error");
                        message.sendToTarget();
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
