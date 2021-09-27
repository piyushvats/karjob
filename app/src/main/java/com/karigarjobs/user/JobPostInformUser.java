package com.karigarjobs.user;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.karigarjobs.R;
import com.karigarjobs.provider.JobPostInform;
import com.karigarjobs.user.cvbutton.UserCvCreateSimple;
import com.karigarjobs.user.cvrecord.UserProfileCreateVoice;
import com.karigarjobs.utils.LocaleManager;
import com.karigarjobs.utils.NetworkChangeReceiver;
import com.karigarjobs.utils.RestAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;

import static com.karigarjobs.user.HomeActivityU.reqcvid;
import static com.karigarjobs.user.cvrecord.UserProfileCreateVoice.objcvvoice;

public class JobPostInformUser extends AppCompatActivity {

    JSONObject jobpostdata = null;
    String usrid=null;
    private UsrJobPostTask  mAplJPost = null;
    private Handler mHandler;

    ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_post_inform_user);
        String str= null;
        Bundle b = getIntent().getExtras();
        if(b != null)
            str = b.getString("jpdata");



        try {
            jobpostdata = new JSONObject(str);
            if(jobpostdata.has("uid"))
                usrid = jobpostdata.getString("uid");

            ((TextView) findViewById(R.id.textView2)).setText("Job Post ID: "+jobpostdata.getString("jid"));
            ((TextView) findViewById(R.id.textView5)).setText(convertFormat(jobpostdata.getString("jpdate")));
            EditText det = ((EditText) findViewById(R.id.textView7));
            det.setText(jobpostdata.getString("detail"));
            det.setMovementMethod(new ScrollingMovementMethod());

            EditText edcname =  ((EditText) findViewById(R.id.textView9));
            edcname.setText(jobpostdata.getString("cname"));
            ((TextView) findViewById(R.id.textView11)).setText(jobpostdata.getString("cid"));
            ((TextView) findViewById(R.id.textView13)).setText(jobpostdata.getString("catname"));
            ((TextView) findViewById(R.id.textView15)).setText(jobpostdata.getString("location"));
            ((TextView) findViewById(R.id.textView17)).setText(jobpostdata.getString("expdate"));

            ArrayList<String> worktype = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.work_type)));
            String strcontract = worktype.get(Integer.parseInt(jobpostdata.getString("iscontract")) - 1);
            ((TextView) findViewById(R.id.textView19)).setText(strcontract);

            ArrayList<String> dnshift = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.work_shift)));
            String strshift = dnshift.get(Integer.parseInt(jobpostdata.getString("dnshift")) - 1);
            ((TextView) findViewById(R.id.textView21)).setText(strshift);

            ((TextView) findViewById(R.id.textView23)).setText(jobpostdata.getString("salmin") +" - " + jobpostdata.getString("salmax"));

            ((TextView) findViewById(R.id.textView26)).setText(jobpostdata.getString("vacencyno"));

            ArrayList<String> otlimiarr = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.over_time)));
            String strot = "";
            if(!jobpostdata.getString("otlimit").isEmpty())
                   strot = otlimiarr.get(Integer.parseInt(jobpostdata.getString("otlimit")) - 1);
            ((TextView) findViewById(R.id.textView28)).setText(strot);

            ((TextView) findViewById(R.id.textView30)).setText(jobpostdata.getString("intert"));

            ArrayList<String> interm = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.interview_mode)));
            String strinterm = "";
            if(!jobpostdata.getString("interm").isEmpty())
                strinterm = interm.get(Integer.parseInt(jobpostdata.getString("interm")) - 1);
            ((TextView) findViewById(R.id.textView32)).setText(strinterm);

            String benreslist = "";
            String benval = jobpostdata.getString("benlist");
            if(benval != null &&  !benval.equals("") &&  !benval.equals("null")) {
                String [] benstr = benval.split(",");

                for (int k = 0; k < benstr.length; k++) {
                    benreslist = benreslist + HomeActivityU.jbenlist.get(Integer.valueOf(benstr[k])-1).catname +"\n";
                }
                ((TextView) findViewById(R.id.textView34)).setText(benreslist);
            } else {
                ((TextView) findViewById(R.id.textView34)).setText("");
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }



        final Button button2 = (Button) findViewById(R.id.button_viewjobapp);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(usrid == null || usrid.equals("null"))
                {
                   // if(reqcvid == null || reqcvid.equals("null"))
                   // {
                        //Toast.makeText(getApplicationContext(), getResources().getString(R.string.alert_create_cv_to_applyjob), Toast.LENGTH_LONG).show();
                        new AlertDialog.Builder(JobPostInformUser.this)
                                .setTitle(getResources().getString(R.string.cv_info))
                                .setMessage(getResources().getString(R.string.alert_create_cv_to_applyjob))
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Continue with delete operation
                                    Intent mIntent = new Intent(getApplicationContext(), UserCvCreateSimple.class);
                                    startActivity(mIntent);
                                    finish();

                                    }
                                })
                                .setNegativeButton(android.R.string.no,null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();


                    //    return;
                    //}
                }

                try {
                    button2.setEnabled(false);
                    applyjobpost(jobpostdata.getString("jid"),jobpostdata.getString("cid"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });


        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {

                if(message.what ==1) {
                    JSONObject objjson = null;
                    String val="",isagainapl="";
                    try {
                        objjson = new JSONObject(message.obj.toString());
                        val = objjson.getString("data");
                        if(objjson.has("isaplagain"))
                            isagainapl = objjson.getString("isaplagain");
                    //Toast.makeText(getApplicationContext(),"Job Applied successfully",Toast.LENGTH_LONG).show();

                    //String str = message.obj.toString();
                    //Toast.makeText(JobPostInformUser.this, R.string.alert_applyjob_success + ","+str, Toast.LENGTH_LONG).show();
                        if(val.equals("ERR_JOB_APL_LIM"))
                        {
                            Toast.makeText(getApplicationContext(),getResources().getString(R.string.alert_job_apl_lmt),Toast.LENGTH_LONG).show();
                        } else {
                            Intent mIntent = new Intent(getApplicationContext(), CompanyContactInfo.class);
                            Bundle b = new Bundle();
                            b.putString("comname",jobpostdata.getString("cname")); //com contact info
                            b.putString("interm", jobpostdata.getString("interm")); //job post interview mode
                            b.putString("comdata", objjson.toString()); //com contact info

                            mIntent.putExtras(b);
                            startActivity(mIntent);
                            finish();
                        }
/*
                        else if (val.equals("ERR_JOB_APL_REPT") || isagainapl.equals("true"))
                        {
                            Toast.makeText(getApplicationContext(),getResources().getString(R.string.alert_job_apl_rpt),Toast.LENGTH_LONG).show();
                        }
                        else {
                            JSONArray jsonArray = objjson.getJSONArray("data1");
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String caddr = jsonObject.getString("offaddr");
                            String caddrp = jsonObject.getString("offaddrpin");
                            String cpername1 = jsonObject.getString("conper1_name");
                            String cperdesg1 = jsonObject.getString("conper1_desg");
                            String cperph1 = jsonObject.getString("conper1_phone");
                            String cemail = jsonObject.getString("email");

                            String msgstr = val+":"+cpername1+"-"+cperdesg1+"\n";
                            int intmod = Integer.parseInt(jobpostdata.getString("interm"));
                            if(intmod == 1)
                                msgstr = msgstr + caddr+","+caddrp+","+cemail+"\n";
                            else
                                msgstr = msgstr + cperph1+","+cemail+"\n";

                            boolean isapplyied = objjson.has("isaplagain");
                            String strTitle = getResources().getString(R.string.alert_applyjob_success);
                            if( isapplyied && objjson.getString("isaplagain").equals("true"))
                            {
                                strTitle = getResources().getString(R.string.alert_applyagainjob_success);
                            }
                            new AlertDialog.Builder(JobPostInformUser.this)
                                    .setTitle("Note")
                                    .setMessage(strTitle +"\n"+ msgstr)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Continue with delete operation
                                            Intent mIntent = new Intent(getApplicationContext(), HomeActivityU.class);
                                            startActivity(mIntent);
                                            finish();
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_info)
                                    .show();
                        }
*/





                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



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
        Intent mIntent = new Intent(getApplicationContext(), HomeActivityU.class);
        startActivity(mIntent);
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

    void applyjobpost(String jpid,String cid)
    {
        if(NetworkChangeReceiver.isNetworkConnected(getApplicationContext())) {
            showprogressbar();

            mAplJPost = new UsrJobPostTask(jpid,cid);
            mAplJPost.execute();
        } else {
            Toast.makeText(this, getResources().getString(R.string.alert_network_disconnected), Toast.LENGTH_LONG).show();
        }
    }

    public static String convertFormat(String inputDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        Date date = null;
        try {
            date = simpleDateFormat.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (date == null) {
            return "";
        }

        SimpleDateFormat convertDateFormat = new SimpleDateFormat("dd MMM yy");
        SimpleDateFormat convettimeFormat = new SimpleDateFormat("hh:mm a");
        String condtft = convertDateFormat.format(date) +"  "+ convettimeFormat.format(date);
        return condtft;
    }


    public class UsrJobPostTask extends AsyncTask<Void, Void, String> {

        private String jobpid,comid;
        UsrJobPostTask(String jpid,String cid) {
            jobpid = jpid;
            comid = cid;
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String r = null;
            try
            {
                // Simulate network access.
                if(usrid != null && !usrid.equals("null"))
                    r = RestAPI.setUsrApyJobPost(jobpid,comid,usrid);
                else if(reqcvid != null && !reqcvid.equals("null"))
                    r = RestAPI.setUsrApyJobPostWithCv(jobpid,comid,reqcvid);
                Thread.sleep(2000);
                hideprogrssbar();
                if(r !=null)
                {
                    Message message = mHandler.obtainMessage(1, r);
                    message.sendToTarget();
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
            mAplJPost = null;

            if (success!=null) {
                //finish();
            } else {
                //mPasswordView.setError(getString(R.string.error_incorrect_password));
                //mPasswordView.requestFocus();
                Toast.makeText(JobPostInformUser.this, R.string.error_no_compost, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAplJPost = null;

        }
    }


}

