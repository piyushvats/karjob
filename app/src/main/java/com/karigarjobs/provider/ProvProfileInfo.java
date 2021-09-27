package com.karigarjobs.provider;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.karigarjobs.R;
import com.karigarjobs.Session;
import com.karigarjobs.utils.LocaleManager;
import com.karigarjobs.utils.NetworkChangeReceiver;
import com.karigarjobs.utils.RestAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;

import static com.karigarjobs.user.cvrecord.UserProfileCreateVoice.objcvvoice;

public class ProvProfileInfo extends AppCompatActivity {

    ComProfileTask mComProfile;
    ComProfileUploadTask mComProfileUp;
    String provid = null;
    private Handler mHandler;
    ProgressDialog progress;
    Button btnupload;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prov_profile_info);

        provid = String.valueOf(Session.getupid());

        btnupload = (Button)(findViewById(R.id.imageButton2));
        btnupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnupload.setEnabled(false);
                updatecomprofile();

            }
        });

        updateview();

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {

                if(message.what == 2) {
                    String str = message.obj.toString();
                    updateCompInfoView(str);
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
        finish();
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

    private void updatecomprofile()
    {
        String cname = null;
        String offaddr = null;
        String offaddrpin = null;
        String ccname1 = null;
        String ccdesg1 = null;
        String ccphone1 = null;
        String ccname2 = null;
        String ccdesg2 = null;
        String ccphone2 = null;
        String gstnumber = null;
        String cemail = null;
        String catname = null;

        EditText editname = (EditText)(findViewById(R.id.editText1));
        cname = editname.getText().toString();
        EditText editoffaddr = (EditText)(findViewById(R.id.editText2));
        offaddr = editoffaddr.getText().toString();
        EditText editpincode = (EditText)(findViewById(R.id.editText11));
        offaddrpin = editpincode.getText().toString();

        EditText editcname1 = (EditText)(findViewById(R.id.editText3));
        ccname1 = editcname1.getText().toString();
        EditText editcdesg1 = (EditText)(findViewById(R.id.editText4));
        ccdesg1 = editcdesg1.getText().toString();
        EditText editcmob1 = (EditText)(findViewById(R.id.editText5));
        ccphone1 = editcmob1.getText().toString();
        //EditText editcname2 = (EditText)(findViewById(R.id.editText6));
        ccname2 = "";//editcname2.getText().toString();
        //EditText editcdesg2 = (EditText)(findViewById(R.id.editText7));
        ccdesg2 = "";//editcdesg2.getText().toString();
        //EditText editcmob2 = (EditText)(findViewById(R.id.editText8));
        ccphone2 = "";//editcmob2.getText().toString();

        EditText editgstnum = (EditText)(findViewById(R.id.editText9));
        gstnumber = editgstnum.getText().toString();
        EditText editemail = (EditText)(findViewById(R.id.editText10));
        cemail = editemail.getText().toString();

        try {

            JSONObject jsonObject = new JSONObject();

            jsonObject.put("comid",provid);
            jsonObject.put("name",cname);
            jsonObject.put("offaddr",offaddr);
            jsonObject.put("offaddrpin",offaddrpin);
            jsonObject.put("conper1_name",ccname1);
            jsonObject.put("conper1_desg",ccdesg1);
            jsonObject.put("conper1_phone",ccphone1);
            jsonObject.put("conper2_name",ccname2);
            jsonObject.put("conper2_desg",ccdesg2);
            jsonObject.put("conper2_phone",ccphone2);
            jsonObject.put("gstnumber",gstnumber);
            jsonObject.put("email",cemail);
            jsonObject.put("catname",catname);

            if(cname.isEmpty() || offaddr.isEmpty() || offaddrpin.isEmpty() || ccname1.isEmpty() || ccdesg1.isEmpty() || ccphone1.isEmpty() )
            {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.err_fill_emptyfield), Toast.LENGTH_LONG).show();
                btnupload.setEnabled(true);
                return;
            }

            if(NetworkChangeReceiver.isNetworkConnected(getApplicationContext())) {
                mComProfileUp = new ComProfileUploadTask(jsonObject);
                mComProfileUp.execute();
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.alert_network_disconnected), Toast.LENGTH_LONG).show();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void updateview()
    {
        if(NetworkChangeReceiver.isNetworkConnected(getApplicationContext())) {
            showprogressbar();
            mComProfile = new ComProfileTask(provid);
            mComProfile.execute();
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.alert_network_disconnected), Toast.LENGTH_LONG).show();
        }

    }



    private void updateCompInfoView(String response)
    {
        try {
            JSONObject jsonObj = new JSONObject(response);
            JSONArray jsonArray = jsonObj.getJSONArray("data");
            //for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            String cname = jsonObject.getString("name");
            String offaddr = jsonObject.getString("offaddr");
            String offaddrpin = jsonObject.getString("offaddrpin");
            String ccname1 = jsonObject.getString("conper1_name");
            String ccdesg1 = jsonObject.getString("conper1_desg");
            String ccphone1 = jsonObject.getString("conper1_phone");
            String ccname2 = jsonObject.getString("conper2_name");
            String ccdesg2 = jsonObject.getString("conper2_desg");
            String ccphone2 = jsonObject.getString("conper2_phone");

            String gstnumber = jsonObject.getString("gstnumber");
            String cemail = jsonObject.getString("email");
            String catname = jsonObject.getString("catname");

            if(LocaleManager.getlanguage().equals(LocaleManager.LOCALE_HI))
                catname = jsonObject.getString("catname_hi");

            TextView textcid = (TextView)(findViewById(R.id.textView15));
            textcid.setText(provid);
            EditText editname = (EditText)(findViewById(R.id.editText1));
            editname.setText(cname);
            EditText editoffaddr = (EditText)(findViewById(R.id.editText2));
            editoffaddr.setText(offaddr);
            EditText editpincode = (EditText)(findViewById(R.id.editText11));
            editpincode.setText(offaddrpin);

            EditText editcname1 = (EditText)(findViewById(R.id.editText3));
            editcname1.setText(ccname1);
            EditText editcdesg1 = (EditText)(findViewById(R.id.editText4));
            editcdesg1.setText(ccdesg1);
            EditText editcmob1 = (EditText)(findViewById(R.id.editText5));
            editcmob1.setText(ccphone1);
/*            EditText editcname2 = (EditText)(findViewById(R.id.editText6));
            editcname2.setText(ccname2);
            EditText editcdesg2 = (EditText)(findViewById(R.id.editText7));
            editcdesg2.setText(ccdesg2);
            EditText editcmob2 = (EditText)(findViewById(R.id.editText8));
            editcmob2.setText(ccphone2);*/

            EditText editgstnum = (EditText)(findViewById(R.id.editText9));
            editgstnum.setText(gstnumber);
            EditText editemail = (EditText)(findViewById(R.id.editText10));
            editemail.setText(cemail);
            TextView editcat = (TextView) (findViewById(R.id.textView13));
            editcat.setText(catname);

            //}

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        hideprogrssbar();

    }

    public class ComProfileTask extends AsyncTask<Void, Void, String> {

        private String comid;
        ComProfileTask(String id) {
            comid = id;
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String r = null;
            try
            {
                // Simulate network access.
                r = RestAPI.getComProfile(comid,getApplicationContext());
                Thread.sleep(2000);
                if(r !=null)
                {
                    Message message = mHandler.obtainMessage(2, r);
                    message.sendToTarget();
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
            mComProfile = null;

            if (success!=null) {
                //finish();
            } else {
                //mPasswordView.setError(getString(R.string.error_incorrect_password));
                //mPasswordView.requestFocus();
                //Toast.makeText(HomeActivity.this, R.string.error_no_compost, Toast.LENGTH_SHORT).show();

            }
        }

        @Override
        protected void onCancelled() {
            mComProfile = null;

        }
    }

    public class ComProfileUploadTask extends AsyncTask<Void, Void, String> {

        private JSONObject jsonObj;
        ComProfileUploadTask(JSONObject obj) {
            jsonObj = obj;
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String r = null;
            try
            {
                // Simulate network access.
                r = RestAPI.uploadComProfile(jsonObj,getApplicationContext());
                Thread.sleep(2000);
                if(r !=null)
                {
                    //updateCompInfoView(r);
                    finish();
                }

            } catch (InterruptedException e) {
                Log.e("", e.toString());
                return r;
            }
            catch ( Exception ex)
            {
                Log.e("", ex.toString());
                return r;
            }

            // TODO: register the new account here.
            return r;
        }

        @Override
        protected void onPostExecute(final String success) {
            mComProfileUp = null;

            if (success!=null) {
                //finish();
            } else {
                //mPasswordView.setError(getString(R.string.error_incorrect_password));
                //mPasswordView.requestFocus();
                //Toast.makeText(HomeActivity.this, R.string.error_no_compost, Toast.LENGTH_SHORT).show();

            }
        }

        @Override
        protected void onCancelled() {
            mComProfileUp = null;

        }
    }

}
