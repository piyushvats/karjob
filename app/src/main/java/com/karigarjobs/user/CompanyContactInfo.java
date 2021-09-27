package com.karigarjobs.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.karigarjobs.R;
import com.karigarjobs.provider.HomeActivity;
import com.karigarjobs.provider.UserJobReqDataModel;
import com.karigarjobs.utils.LocaleManager;
import com.karigarjobs.utils.NetworkChangeReceiver;
import com.karigarjobs.utils.RestAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CompanyContactInfo extends AppCompatActivity  implements FragComContectInfo.OnFragmentInteractionListener {

    String jpcomname =null,jpconaddr=null,jpconpername=null,jpcondesig=null,jpconphone=null;
    String jpid = null,jpinterm=null,jpstatusmsg=null;
    boolean jpisapplyied=false;
    public static CompanyContactInfo objJpCom=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_contact_info);
        String comdt = null,interm=null,cname=null;
        JSONObject objjson = null;
        String val="",isagainapl="";
        Bundle b = getIntent().getExtras();
        if(b != null) {
            comdt = b.getString("comdata");
            interm = b.getString("interm");
            cname = b.getString("comname");

            try {
                //jobpostdata = new JSONObject(str);

                objjson = new JSONObject(comdt);
                val = objjson.getString("data");
                if(objjson.has("isaplagain"))
                    isagainapl = objjson.getString("isaplagain");
                jpisapplyied = isagainapl.equals("true");
                if (val.equals("ERR_JOB_APL_REPT") || jpisapplyied)
                {
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.alert_job_apl_rpt),Toast.LENGTH_LONG).show();
                }

                jpcomname = cname;
                jpinterm = interm;
                jpid = val;

                JSONArray jsonArray = objjson.getJSONArray("data1");
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                String caddr = jsonObject.getString("offaddr");
                String caddrp = jsonObject.getString("offaddrpin");
                String cpername1 = jsonObject.getString("conper1_name");
                String cperdesg1 = jsonObject.getString("conper1_desg");
                String cperph1 = jsonObject.getString("conper1_phone");
                String cemail = jsonObject.getString("email");


                jpconaddr = caddr+","+caddrp+","+cemail;
                jpconpername = cpername1;
                jpcondesig = cperdesg1;
                jpconphone = cperph1;

/*                    String msgstr = val+":"+cpername1+"-"+cperdesg1+"\n";
                int intmod = Integer.parseInt(interm);
                if(intmod == 1)
                    msgstr = msgstr + caddr+","+caddrp+","+cemail+"\n";
                else
                    msgstr = msgstr + cperph1+","+cemail+"\n";*/

                //boolean isapplyied = objjson.has("isaplagain");
                String strTitle = getResources().getString(R.string.alert_applyjob_success);
                if(jpisapplyied)
                {
                    strTitle = getResources().getString(R.string.alert_applyagainjob_success);
                }
                jpstatusmsg = strTitle;
/*                new AlertDialog.Builder(CompanyContactInfo.this)
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
                        .show();*/


                objJpCom = this;

                createfragment_firstpage();
            } catch (JSONException e) {
                e.printStackTrace();
                onBackPressed();
            }
        }


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



    private void createfragment_firstpage() {

        Fragment frag1 = new FragComContectInfo();
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack
        transaction.replace(R.id.frag_one, frag1,"stageone");
        //transaction.add(R.id.frag_stageone, newFragment);
        transaction.addToBackStack(null);

// Commit the transaction
        transaction.commit();

    }

    private static final int PHONE_CALL_PERMISSION_CODE = 300;
    private static String callparam = "";
    public static void startGsmCallActivity(String data)
    {
        String phoneno = data.replaceAll("[\\D]", "");
        if(phoneno !="")
        {
            if (ActivityCompat.checkSelfPermission(CompanyContactInfo.objJpCom, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                callparam = data;
                objJpCom.checkPhoneCallpermission(data);
            }
            else {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+phoneno));
                objJpCom.startActivity(callIntent);
            }

        }
    }

    private void checkPhoneCallpermission(String data)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(CompanyContactInfo.objJpCom, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PHONE_CALL_PERMISSION_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PHONE_CALL_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                startGsmCallActivity(callparam);
            }
            else
            {
                Toast.makeText(this, getResources().getString(R.string.alert_callpermission_denied), Toast.LENGTH_LONG).show();
            }
        }
    }


    private static ComCallRecTask mCCRPost=null;
    public void startUserGsmCall()
    {
        //call REST API for save call rec
        if(NetworkChangeReceiver.isNetworkConnected(getApplicationContext())) {
            mCCRPost = new ComCallRecTask(jpid,jpconphone);
            mCCRPost.execute();
        } else {
            Toast.makeText(this, getResources().getString(R.string.alert_network_disconnected), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
        if (count == 0 || count == 1) {

            if(count == 1)
                getFragmentManager().popBackStack();

            //onBackPressed();
            //additional code
            Intent intent = new Intent(getApplicationContext(), HomeActivityU.class);
            startActivity(intent);
            finish();
        } else {
            getFragmentManager().popBackStack();
        }

    }


    @Override
    public void onFragmentInteraction(Uri uri) {
        //Log.d("Info","onFragmentInteraction called");
    }



    public class ComCallRecTask extends AsyncTask<Void, Void, String> {

        private String jsid,jpmob;

        ComCallRecTask(String dt,String mob) {
            jsid = dt;
            jpmob = mob;
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String r = null;
            try
            {
                // Simulate network access.
                r = RestAPI.updateUsercallrec(jsid,getApplicationContext());
                Thread.sleep(2000);
                if(r !=null)
                {
                    startGsmCallActivity(jpmob);
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
            mCCRPost = null;

            if (success!=null) {
                //finish();
            } else {
                //mPasswordView.setError(getString(R.string.error_incorrect_password));
                //mPasswordView.requestFocus();
                Toast.makeText(CompanyContactInfo.this, R.string.error_no_compost, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mCCRPost = null;

        }
    }

}