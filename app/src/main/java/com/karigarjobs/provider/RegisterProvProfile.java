package com.karigarjobs.provider;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.karigarjobs.MainActivity;
import com.karigarjobs.R;
import com.karigarjobs.Session;
import com.karigarjobs.utils.LocaleManager;
import com.karigarjobs.utils.NetworkChangeReceiver;
import com.karigarjobs.utils.RestAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterProvProfile extends AppCompatActivity {

    public class LoginCred
    {
        String usrid;
        String mob;
        String passwd;
        String email;
    };

    public LoginCred logincred =null;

    public static class JobCategory
    {
        String catnum;
        String catname;
    };

    ArrayList<String> jtlist = null;
    public static ArrayList<JobCategory> jclist=null;
    private RegisComProfileTask mRegComProfile = null;
    //private RegisComProfileUploadTask mRegisComProfileUp = null;
    private AddComProfileTask mAddComProfile = null;
    Spinner spinner5;
    ProgressDialog progress;
    private Handler mHandler;
    String launcher = "HOME";
    Button btnupload;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_prov_profile);

        logincred = new LoginCred();


        Bundle b = getIntent().getExtras();
        if(b != null) {
            launcher = b.getString("parent");
            logincred.usrid = b.getString("uid");
            logincred.mob = b.getString("mob");
            logincred.passwd = b.getString("pswd");
            logincred.email = b.getString("mail");
        }

        btnupload = (Button)(findViewById(R.id.imageButton2));
        btnupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnupload.setEnabled(false);
                updatecomprofile();
            }
        });

        if(NetworkChangeReceiver.isNetworkConnected(getApplicationContext())) {
            showprogressbar();
            mRegComProfile = new RegisComProfileTask("0");
            mRegComProfile.execute();
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.alert_network_disconnected), Toast.LENGTH_LONG).show();
        }

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {

                if(message.what ==1) {
                    //String str = message.obj.toString();
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_no_usrprofupt), Toast.LENGTH_SHORT).show();
                    finish();
                } else if(message.what ==2) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.alert_compprofilecreate), Toast.LENGTH_SHORT).show();
                    finish();
                } else if(message.what == 3) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.alert_compprofileupdate), Toast.LENGTH_SHORT).show();
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
        hideprogrssbar();
/*        if(launcher.equals("REGIS")) {
            finish();
            Intent mIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(mIntent);
        } else {
            finish();
            Intent mIntent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(mIntent);
        }*/
        finish();
        Intent mIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(mIntent);
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

        Spinner editjobtype = findViewById(R.id.editspinner1);
        String  jobtype = editjobtype.getSelectedItem().toString();
        int listindx = jtlist.indexOf(jobtype);
        String jtidx = jclist.get(listindx).catnum;

        if(cname.isEmpty() || offaddr.isEmpty() || offaddrpin.isEmpty() || ccname1.isEmpty() || ccdesg1.isEmpty() || ccphone1.isEmpty() )
        {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.err_fill_emptyfield), Toast.LENGTH_LONG).show();
            btnupload.setEnabled(true);
            return;
        }

        try {

            JSONObject jsonObject = new JSONObject();

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
            jsonObject.put("catname",jtidx);

            //if launch from REGIS usrid = loginid name , if launch from HOME usrid = prov_login db id
            jsonObject.put("logusrid",logincred.usrid);
            jsonObject.put("logmob",logincred.mob);
            jsonObject.put("logemail",logincred.email);
            jsonObject.put("logcred",logincred.passwd);


            if(NetworkChangeReceiver.isNetworkConnected(getApplicationContext())) {
/*                if(launcher.equals("REGIS")) {
                    showprogressbar();
                    mRegisComProfileUp = new RegisComProfileUploadTask(jsonObject, logincred.usrid, logincred.mob, logincred.email);
                    mRegisComProfileUp.execute();
                } else {
                    showprogressbar();
                    mAddComProfile = new AddComProfileTask(jsonObject,logincred.usrid);
                    mAddComProfile.execute();
                }*/
                showprogressbar();
                mAddComProfile = new AddComProfileTask(jsonObject,logincred.usrid);
                mAddComProfile.execute();
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.alert_network_disconnected), Toast.LENGTH_LONG).show();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    public static ArrayAdapter<String> spinadap4;
    private void updateviewdetail()
    {
        jtlist = new ArrayList<>(jclist.size());
        for(int i = 0;i< jclist.size();i++) {
            jtlist.add(jclist.get(i).catname);
        }

        spinner5 = (Spinner) findViewById(R.id.editspinner1);
        spinadap4 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, jtlist);
        spinadap4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // Stuff that updates the UI

                spinner5.setAdapter(spinadap4);
                spinner5.setOnItemSelectedListener(new CustomOnItemSelectedListener());

            }
        });



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

    private void updateCompInfoView(String res)
    {

        try {
            JSONObject r = new JSONObject(res);
            JSONArray result = r.getJSONArray("data");
            jclist = new ArrayList<>(result.length());
            boolean ishi_lan = LocaleManager.getlanguage().equals(LocaleManager.LOCALE_HI);
            for(int i=0;i<result.length();i++)
            {
                JSONObject obj = result.getJSONObject(i);
                JobCategory item = new JobCategory();
                item.catname = obj.getString("cname");
                if(ishi_lan)
                    item.catname = obj.getString("cname_hi");
                item.catnum = obj.getString("cid");
                jclist.add(item);
            }

            updateviewdetail();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public class RegisComProfileTask extends AsyncTask<Void, Void, String> {

        private String comid;
        RegisComProfileTask(String id) {
            comid = id;
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String r = null;
            try
            {
                // Simulate network access.
                r = RestAPI.getJPView(comid,getApplicationContext());
                Thread.sleep(2000);
                hideprogrssbar();
                if(r !=null)
                {
                    updateCompInfoView(r);
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
            mRegComProfile = null;

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
            mRegComProfile = null;

        }
    }

    public class AddComProfileTask extends AsyncTask<Void, Void, String> {

        private JSONObject jsonObject;
        String mobile,mailid,uid;
        AddComProfileTask(JSONObject obj,String usrid) {
            jsonObject = obj;
            uid = usrid;
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String r = null;
            try
            {
                // Simulate network access.
                r = RestAPI.addProvInfoRegister(jsonObject,getApplicationContext());
                Thread.sleep(2000);
                hideprogrssbar();
                if(r !=null)
                {
                    JSONObject obj = new JSONObject(r);

                    if(obj.has("error"))
                    {
                        Message message = mHandler.obtainMessage(1, "Error");
                        message.sendToTarget();
                    }
                    else
                    {
                        //String PROV_LOG_STR = "PROV";
                        //Session session = new Session(RegisterProvProfile.this);
                        if(launcher.equals("REGIS")) {
                            String PROV_LOG_STR = "PROV";
                            Session session = new Session(RegisterProvProfile.this);
                            //String d1 = obj.getString("prvlog");
                            String d2 = obj.getString("cid");
                            session.createSession(uid, mobile, mailid,PROV_LOG_STR,Integer.parseInt(uid),d2,LocaleManager.getlanguage());
                            Message message = mHandler.obtainMessage(2, "NEW");
                            message.sendToTarget();

                        } else {
                            String d2 = obj.getString("cid");
                            Session.setupid(d2);
                            Message message = mHandler.obtainMessage(3, "UPDATE");
                            message.sendToTarget();
                        }

                        finish();
                        Intent mIntent = new Intent(getApplication(), HomeActivity.class);
                        startActivity(mIntent);
                    }

                    //uploadjobpostreq(r);
                    //Toast.makeText(JobPostCreateActivity.this, "Job Post created ID: "+r, Toast.LENGTH_SHORT).show();

                    finish();
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
            mAddComProfile = null;

            Button btnupload = (Button)(findViewById(R.id.imageButton2));
            btnupload.setEnabled(true);

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
            mAddComProfile = null;

        }
    }


}

