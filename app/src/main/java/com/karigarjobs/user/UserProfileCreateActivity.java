package com.karigarjobs.user;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.karigarjobs.AllUsrProvInfo;
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

import static com.karigarjobs.user.HomeActivityU.jclist;
import static com.karigarjobs.user.cvrecord.UserProfileCreateVoice.objcvvoice;


public class UserProfileCreateActivity extends AppCompatActivity
    implements FragUsrProfOneCreate.OnFragmentInteractionListener, FragUsrProfExpSecCreate.OnFragmentInteractionListener, FragUsrProfEduThrCreate.OnFragmentInteractionListener,
        FragUsrProfDocUpload.OnFragmentInteractionListener {

    static public String usrloginid,usrprofid;
    ArrayList<String> jtlist = null;

    public String fileSelectPath = "";
    UserProReteriveTask mUsrProRet = null;
    GetJobTypeTask mJobTypeRet = null;
    public static UserProfileCreateActivity objusrpro;
    ProgressDialog progress;

    public static ArrayList<AllUsrProvInfo.JobCategory> usrjclist=null;

    public static class usr_edu
    {
        String id;
        String eduname;
        String edudetail;
        String edudate;
        String location;
        String timestamp;
    }


    public static class usr_exp
    {
        String id;
        String cname;
        String catid;
        String jobtitle;
        String jobdetail;
        String jstartdate;
        String jenddate;
        String location;
        String timestamp;
    }


    public static class UserProfile
    {
        String id;
        String name;
        String age;
        String sex;
        String caddr;
        String caddrpinc;
        String paddr;
        String paddrpinc;
        String mobile;
        String altmobile;
        String idtype;
        String idtypenum;
        String email;
        String csalary;
        String uptmp;
        String jdate;
        ArrayList<AllUsrProvInfo.JobCategory> userindustype;
        ArrayList<usr_edu> usereducaiton;
        ArrayList<usr_exp> userexperience;

    };

    public static UserProfile usrprofileinfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user_profile);

        fileSelectPath = getExternalFilesDir(null).getPath() + "/profile/";

        if(Session.IsCreated) {
            usrloginid = String.valueOf(Session.getUsr_id());
            usrprofid  = Session.getupid();
        }


        if(jclist!=null && !jclist.isEmpty())
        {
            usrjclist = new ArrayList<>(jclist.size());
            jtlist = new ArrayList<>(jclist.size());
            for (int i = 0; i < jclist.size(); i++) {
                jtlist.add(jclist.get(i).catname);
                usrjclist.add(jclist.get(i));
            }

        }

        //get profile from server . Only For edit/update profile
        if(NetworkChangeReceiver.isNetworkConnected(getApplicationContext())) {
            if(usrprofid != null && !usrprofid.equals("null"))  {
                showprogressbar();
                mUsrProRet = new UserProReteriveTask(usrprofid);
                mUsrProRet.execute();

            } else {
                //Only new profile but from home acivity wich have job type list
                if(usrjclist!=null && !usrjclist.isEmpty())
                {
                    createfragment_firstpage();
                } else {
                    //Only new profile but from Login Registration acivity . async = get Job type list + createfragment_firstpage()
                    showprogressbar();
                    mJobTypeRet = new GetJobTypeTask("0");
                    mJobTypeRet.execute();
                }

            }
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.alert_network_disconnected), Toast.LENGTH_LONG).show();
        }


        objusrpro = this;
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

    private void createfragment_firstpage() {

        Fragment frag1 = new FragUsrProfOneCreate();
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack
        transaction.replace(R.id.frag_stageone, frag1,"stageone");
        //transaction.add(R.id.frag_stageone, newFragment);
        transaction.addToBackStack(null);

// Commit the transaction
        transaction.commit();

    }

    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
        if (count == 0 || count == 1) {

            if(count == 1)
                getFragmentManager().popBackStack();

            if(usrprofileinfo != null)
            {
                usrprofileinfo.name = "";
                usrprofileinfo.age = "";
                usrprofileinfo.sex = "";
                usrprofileinfo.caddr = "";
                usrprofileinfo.caddrpinc = "";
                usrprofileinfo.paddr = "";
                usrprofileinfo.paddrpinc = "";
                usrprofileinfo.id = "";
                usrprofileinfo.idtypenum = "";
                usrprofileinfo.csalary = "";
                usrprofileinfo.email = "";
                usrprofileinfo.altmobile = "";
                usrprofileinfo.mobile = "";

                if(usrprofileinfo.userindustype != null )
                    usrprofileinfo.userindustype.clear();

                if(usrprofileinfo.userexperience != null)
                    usrprofileinfo.userexperience.clear();

                if(usrprofileinfo.usereducaiton != null )
                    usrprofileinfo.usereducaiton.clear();

            }
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


    public void showuploadlist(String data)
    {
        try {
            if(usrprofileinfo == null)
                usrprofileinfo = new UserProfile();

            JSONObject jsonObj = new JSONObject(data);
            JSONArray jsonArraymain = jsonObj.getJSONArray("data");
            JSONArray jsonArray = jsonArraymain.getJSONArray(0);
            for(int i = 0;i< jsonArray.length();i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");

                String cat = jsonObject.getString("category");
                String category[] = cat.split(",");

                String name = jsonObject.getString("name");
                String age = jsonObject.getString("age");
                String sex = jsonObject.getString("sex");
                String mobile = jsonObject.getString("mobile");
                String almobile = jsonObject.getString("altmobile");
                String caddr = jsonObject.getString("caddr");
                String caddrpin = jsonObject.getString("caddrpin");
                String paddr = jsonObject.getString("paddr");
                String paddrpin = jsonObject.getString("paddrpin");
                String idtype = jsonObject.getString("idtype");
                String idnumber = jsonObject.getString("idnumber");
                String email = jsonObject.getString("email");
                String csalary = jsonObject.getString("csalary");

                String tempdt = jsonObject.getString("updatetmstp");
                String updatetmstp = tempdt.substring(0, 19).replace('T', ' ');

                String tempjdt =jsonObject.getString("jdate");
                String jdate = tempjdt.substring(0, 19).replace('T', ' ');

                usrprofileinfo.id = id;
                usrprofileinfo.name = name;
                usrprofileinfo.age = age;
                usrprofileinfo.sex = sex;
                usrprofileinfo.mobile = mobile;
                usrprofileinfo.altmobile = almobile;
                usrprofileinfo.caddr = caddr;
                usrprofileinfo.caddrpinc = caddrpin;
                usrprofileinfo.paddr = paddr;
                usrprofileinfo.paddrpinc = paddrpin;
                usrprofileinfo.idtype = idtype;
                usrprofileinfo.idtypenum = idnumber;
                usrprofileinfo.email = email;
                usrprofileinfo.csalary = csalary;
                usrprofileinfo.uptmp = updatetmstp;
                usrprofileinfo.jdate = jdate;

                if(usrprofileinfo.idtype == null || usrprofileinfo.idtype.isEmpty())
                    usrprofileinfo.idtype = "1";

                if(usrprofileinfo.userindustype == null || usrprofileinfo.userindustype.isEmpty())
                    usrprofileinfo.userindustype = new ArrayList<>();


                for(int j=0;j<category.length;j++)
                {
                    AllUsrProvInfo.JobCategory dataitem = new AllUsrProvInfo.JobCategory();
                    dataitem.catname = jtlist.get(Integer.parseInt(category[j])-1);
                    dataitem.catnum = category[j];
                    usrprofileinfo.userindustype.add(dataitem);
                }


            }

            if(usrprofileinfo.usereducaiton == null || usrprofileinfo.usereducaiton.isEmpty())
                usrprofileinfo.usereducaiton = new ArrayList<>();
            //get education
            JSONArray jsonArray2 = jsonArraymain.getJSONArray(1);
            for(int k=0;k<jsonArray2.length();k++) {
                JSONObject jsonObject = jsonArray2.getJSONObject(k);
                String eduid= jsonObject.getString("id");
                String eduname= jsonObject.getString("eduname");
                String edudetail= jsonObject.getString("edudetail");
                String edudate= jsonObject.getString("edudate");
                String edulocation= jsonObject.getString("edulocation");
                String temptp =  jsonObject.getString("updatetmstp");
                String eduuptmsp= temptp.substring(0, 19).replace('T', ' ');

                usr_edu itemdt = new usr_edu();
                itemdt.id = eduid;
                itemdt.eduname = eduname;
                itemdt.edudetail = edudetail;
                itemdt.edudate = edudate;
                itemdt.location = edulocation;
                itemdt.timestamp = eduuptmsp;

                usrprofileinfo.usereducaiton.add(itemdt);
            }


            if(usrprofileinfo.userexperience == null || usrprofileinfo.userexperience.isEmpty())
                usrprofileinfo.userexperience = new ArrayList<>();

            //get exp
            JSONArray jsonArray3 = jsonArraymain.getJSONArray(2);
            for(int l=0;l<jsonArray3.length();l++) {
                JSONObject jsonObject = jsonArray3.getJSONObject(l);
                String expid= jsonObject.getString("id");
                String expname= jsonObject.getString("cname");
                String expcatid = jsonObject.getString("jcatid");
                String exptitle = jsonObject.getString("jtitle");
                String expdetail = jsonObject.getString("jdetail");
                String expsdate = jsonObject.getString("jsdate");
                String expedate = jsonObject.getString("jedate");
                String exploc = jsonObject.getString("jlocation");

                String temptp =  jsonObject.getString("updatetmstp");
                String expuptmsp= temptp.substring(0, 19).replace('T', ' ');

                usr_exp itemdt = new usr_exp();
                itemdt.id = expid;
                itemdt.cname = expname;
                itemdt.jobtitle = exptitle;
                itemdt.jobdetail = expdetail;
                itemdt.jstartdate = expsdate;
                itemdt.jenddate = expedate;
                itemdt.location = exploc;
                itemdt.timestamp = expuptmsp;
                itemdt.catid = expcatid;

                usrprofileinfo.userexperience.add(itemdt);
            }


            //launch fragment
            createfragment_firstpage();
        }
        catch(JSONException ex)
        {
            Log.d("Error",ex.toString());
        }

        return;

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

            createfragment_firstpage();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * Represents an asynchronous
     */
    public class UserProReteriveTask extends AsyncTask<Void, Void, String> {

        private String id;

        UserProReteriveTask(String data) {
            id = data;
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String r = null;
            try
            {
                // Simulate network access.
                r = RestAPI.getUserProfile(id);
                Thread.sleep(2000);
                hideprogrssbar();
                if(r != null)
                {
                    showuploadlist(r);
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
            mUsrProRet = null;

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
            mUsrProRet = null;

        }
    }


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




}
