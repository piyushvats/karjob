package com.karigarjobs.provider;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.karigarjobs.R;
import com.karigarjobs.utils.LocaleManager;
import com.karigarjobs.utils.NetworkChangeReceiver;
import com.karigarjobs.utils.RestAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;

public class JobPostInform extends AppCompatActivity {

    JSONObject jobpostdata = null;
    private DelJobPostTask  mDelJPost = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_post_inform);
        String str= null;
        Bundle b = getIntent().getExtras();
        if(b != null)
            str = b.getString("jpdata");



        try {
            jobpostdata = new JSONObject(str);

            ((TextView) findViewById(R.id.textView2)).setText("Job Post ID: "+jobpostdata.getString("jid"));
            ((TextView) findViewById(R.id.textView5)).setText(convertFormat(jobpostdata.getString("jpdate")));
            ((TextView) findViewById(R.id.textView7)).setText(jobpostdata.getString("title"));
            ((TextView) findViewById(R.id.textView9)).setText(jobpostdata.getString("cname"));
            ((TextView) findViewById(R.id.textView11)).setText((String.valueOf(HomeActivity.objSing.user_id)));
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
            String strot = otlimiarr.get(Integer.parseInt(jobpostdata.getString("otlimit")) - 1);
            ((TextView) findViewById(R.id.textView28)).setText(strot);

            ((TextView) findViewById(R.id.textView30)).setText(jobpostdata.getString("intert"));

            ArrayList<String> interm = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.interview_mode)));
            String strinterm = interm.get(Integer.parseInt(jobpostdata.getString("interm")) - 1);
            ((TextView) findViewById(R.id.textView32)).setText(strinterm);

            String benreslist = "";
            String benval = jobpostdata.getString("benlist");
            if(benval != null &&  !benval.equals("") &&  !benval.equals("null")) {
                String [] benstr = benval.split(",");

                for (int k = 0; k < benstr.length; k++) {
                    benreslist = benreslist + HomeActivity.jbenlist.get(Integer.valueOf(benstr[k])-1).catname +"\n";
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
                // your handler code here
                //Intent mIntent = new Intent(getApplicationContext(), ListJobPostApplication.class);
                //startActivity(mIntent);
                //finish();
                HomeActivity.parent = "VIEWUSR";
                Intent mIntent = new Intent(getApplicationContext(), HomeActivity.class);
                Bundle b = new Bundle();
                try {
                    b.putString("jpid", jobpostdata.getString("jid")); //Your id
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mIntent.putExtras(b);
                startActivity(mIntent);
                finish();


            }
        });

        final ImageButton button3 = (ImageButton) findViewById(R.id.button_deletejobpost);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // your handler code here
                try {
                    deactivatepost(jobpostdata.getString("jid"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

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


    void deactivatepost(final String jpid)
    {

        new AlertDialog.Builder(JobPostInform.this)
                .setTitle(R.string.action_doc_info)
                .setMessage(R.string.alert_jp_del)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        if(NetworkChangeReceiver.isNetworkConnected(getApplicationContext())) {
                            mDelJPost = new DelJobPostTask(jpid);
                            mDelJPost.execute();
                        } else {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.alert_network_disconnected), Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no,null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }



    public class DelJobPostTask extends AsyncTask<Void, Void, String> {

        private String jobpid;
        DelJobPostTask(String jpid) {
            jobpid = jpid;
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String r = null;
            try
            {
                // Simulate network access.
                r = RestAPI.setDeactivateJobPost(jobpid,getApplicationContext());
                Thread.sleep(2000);
                if(r !=null)
                {
                    Intent mIntent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(mIntent);
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
            mDelJPost = null;

            if (success!=null) {
                //finish();
            } else {
                //mPasswordView.setError(getString(R.string.error_incorrect_password));
                //mPasswordView.requestFocus();
                Toast.makeText(JobPostInform.this, R.string.error_no_compost, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mDelJPost = null;

        }
    }



}
