package com.karigarjobs.provider;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karigarjobs.BuildConfig;
import com.karigarjobs.R;
import com.karigarjobs.Session;
import com.karigarjobs.user.HomeActivityU;
import com.karigarjobs.user.JobPostInformUser;
import com.karigarjobs.user.cvrecord.UserProfileCreateVoice;
import com.karigarjobs.utils.LocaleManager;
import com.karigarjobs.utils.NetworkChangeReceiver;
import com.karigarjobs.utils.RestAPI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static HomeActivity objSing = null;
    public static String parent = "MAIN";
    private ComLoadJobPostTask  mCJPost = null;
    private ComJobPostTask  mDelCJPost = null;
    private CheckVersionTask mCheckAppVersionTask=null;

    public TextView tvUserName, tvUserMob;
    public ImageView tvUserProfImg;
    public Integer user_id=0;
    public String compid = null;
    public String pemail=null,pmobile = null;
    private RecyclerView recyclerView,recyclerView_usr;
    private ListPostActivity adapter;
    private ListUserReqActivity adapter_usr;
    public ArrayList<JobPostDataModel> dataModels;
    public ArrayList<UserJobReqDataModel> dataModels_usr;

    public static class JobCategory
    {
        String catnum;
        String catname;
    };

    public static class BenefitCategory
    {
        String catnum;
        String catname;
    };
    ProgressDialog progress;

    public static ArrayList<JobCategory> jclist=null;
    public static ArrayList<BenefitCategory> jbenlist=null;
    //public static String provfileSelectPath = Environment.getExternalStorageDirectory().getPath() + "/karijob/prprofile/";
    public String provfileSelectPath = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.app_name);

/*        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View header = navigationView.getHeaderView(0);
        tvUserName = (TextView) header.findViewById(R.id.textView_username);
        tvUserMob = (TextView) header.findViewById(R.id.textView_mobile);
        tvUserProfImg = (ImageView) header.findViewById(R.id.imageView_user);

        Menu ver = navigationView.getMenu();
        MenuItem it = ver.findItem(R.id.nav_appversion);
        it.setTitle(BuildConfig.VERSION_NAME);

        FloatingActionButton floatbtn = findViewById(R.id.jobpost_button);
        floatbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(compid != null && !compid.equals("null")) {
                    Intent i = new Intent(getApplicationContext(), JobPostCreateActivity.class);
                    startActivity(i);
                    //finish();
                } else {
                    //Toast.makeText(getApplicationContext(),getResources().getString(R.string.err_create_comp_profile),Toast.LENGTH_LONG).show();
                    new AlertDialog.Builder(HomeActivity.this)
                            .setTitle(getResources().getString(R.string.cv_info))
                            .setMessage(getResources().getString(R.string.alert_create_profile_jobpost))
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent mIntent = new Intent(getApplicationContext(), RegisterProvProfile.class);
                                    Bundle b = new Bundle();
                                    b.putString("parent","HOME");
                                    b.putString("uid", String.valueOf(user_id)); //Your id
                                    b.putString("mob", pmobile); //Your id
                                    b.putString("mail", pemail); //Your id
                                    b.putString("pswd", ""); //Your id
                                    mIntent.putExtras(b);
                                    startActivity(mIntent);

                                }
                            })
                            .setNegativeButton(android.R.string.no,null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
        });

        provfileSelectPath = getExternalFilesDir(null).getPath() + "/prprofile/";
        Session sess = new Session(getApplicationContext());
        sess.refreshSession();
        if (Session.IsCreated) {
            tvUserName.setText(Session.getname());
            tvUserMob.setText(Session.getmob());
            pemail = Session.getemail();
            pmobile = Session.getmob();
            user_id = Session.getUsr_id();
            compid = Session.getupid();

            String strprofl = provfileSelectPath+ "profpic.jpg";
            File fl = new File(strprofl);
            if(fl.exists())
            {
                Bitmap btmp ;
                try {
                    btmp = getResizedBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(fl)),200,200);
                    tvUserProfImg.setImageBitmap(btmp);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }

            if(NetworkChangeReceiver.isNetworkConnected(getApplicationContext())) {
                manageFirebasetoken(String.valueOf(user_id));
            } else {
                Toast.makeText(this, getResources().getString(R.string.alert_network_disconnected), Toast.LENGTH_LONG).show();
            }




        }

        if(parent == "VIEWUSR")
        {

            startjobapplications();
        }
        else
        {
            initrecycleview();
        }
        parent = "MAIN";
        objSing = this;

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


    private FireBaseStoreTask mFBStore = null;
    public static String appToken = null;
    private void manageFirebasetoken(final String loginid)
    {
        final String TAG = "FirebaseInstanceId";
        if(appToken != null)
            return;
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        if(appToken == null || !appToken.equals(token)) {
                            Log.d("Info","Updating server token");
                            mFBStore = new FireBaseStoreTask(token, loginid);
                            mFBStore.execute();
                            appToken = token;
                        }

                        Log.d(TAG, appToken);

                    }
                });

    }

    @Override
    protected void onResume()
    {
        if(NetworkChangeReceiver.isNetworkConnected(getApplicationContext())) {
            //update plays store app version
            mCheckAppVersionTask = new CheckVersionTask(getApplicationContext());
            mCheckAppVersionTask.execute();
        } else {
            Toast.makeText(this, getResources().getString(R.string.alert_network_disconnected), Toast.LENGTH_LONG).show();
        }
        super.onResume();
    }

    Handler handle_end = new Handler();
    final Runnable changeView = new Runnable()
    {
        public void run()
        {
            //Toast.makeText(getApplicationContext(), getResources().getString(R.string.alert_req_update_app), Toast.LENGTH_LONG).show();

            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }

        }

    };


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
                        double curvarval = 0.0;
                        double newVarVal = 0.0;
                        curvarval = Float.parseFloat(currversion);

                        if(newVar != null && !newVar.equals("null") && !newVar.isEmpty())
                        {
                            newVarVal = Float.parseFloat(newVar);
                        }
                        Log.d("Current Version:",String.valueOf(curvarval));
                        Log.d("Server Version:",String.valueOf(newVarVal));
                        if(curvarval < newVarVal)
                        {
                            //update APP from play store
                            handle_end.postDelayed(changeView, 10);

                        }
                    }
                    else
                    {
                        //handle_finish.postDelayed(changeView, SPLASH_SCREEN_TIME_OUT);
                        Log.d("Server Error",res.getString("error"));
                    }
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
            mCheckAppVersionTask = null;
        }

        @Override
        protected void onCancelled() {
            mCheckAppVersionTask = null;
        }
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

    private static final int PER_PAGE_COUNT = 10;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 10;
    private int currentPage = 0;

    private void initrecycleview()
    {
        //View itemview = inflater.inflate(R.layout.activity_home, null);
        recyclerView = findViewById(R.id.recycler_view);
        adapter = new ListPostActivity(HomeActivity.this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);//new GridLayoutManager(this.getContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        //recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new PaginationScrollListener((LinearLayoutManager) mLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                //Increment page index to load the next one
                currentPage += 10;
                loadNextPage();
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });


        isLoading = true;
        loadFirstPage();
    }

    private void loadFirstPage() {
        //showprogress(true);
        getpostlist();
    }

    private void loadNextPage() {
        getpostlist();
    }

    private void getpostlist()
    {
        //page num 0
        if(NetworkChangeReceiver.isNetworkConnected(getApplicationContext())) {
            showprogressbar();
            mCJPost = new ComLoadJobPostTask("LOAD_JOB", String.valueOf(user_id), String.valueOf(currentPage));
            mCJPost.execute();
        } else {
            Toast.makeText(this, getResources().getString(R.string.alert_network_disconnected), Toast.LENGTH_LONG).show();
        }

    }




    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if(recyclerView == null )
            {
                finish();
                startActivity(getIntent());
            }
            else
            {
                this.moveTaskToBack(true);
                //super.onBackPressed();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);



        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //finish();
            //startActivity(getIntent());
            if(recyclerView != null ) {
                if (!adapter.isEmpty())
                    adapter.clear();
                initrecycleview();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            // Handle the profile sttings
            if(compid != null && !compid.equals("null")) {
                Intent i = new Intent(this, ProvProfileInfo.class);
                //Intent is used to switch from one activity to another.
                startActivity(i);
            } else {

                Intent mIntent = new Intent(getApplicationContext(), RegisterProvProfile.class);
                Bundle b = new Bundle();
                b.putString("parent","HOME");
                b.putString("uid", String.valueOf(user_id)); //Your id
                b.putString("mob", pmobile); //Your id
                b.putString("mail", pemail); //Your id
                b.putString("pswd", ""); //Your id
                mIntent.putExtras(b);
                startActivity(mIntent);

            }


        } else if (id == R.id.nav_newpost) {
            if(compid != null && !compid.equals("null")) {
                Intent i = new Intent(this, JobPostCreateActivity.class);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.err_create_comp_profile),Toast.LENGTH_LONG).show();
            }


        } else if(id == R.id.nav_share){
            startsharelink();
        } else if(id == R.id.nav_accsettings){
            startsettingschg();
        }

        /*else if (id == R.id.nav_prepost) {

        } else if (id == R.id.nav_appget) {

        }*/


        return true;
    }


    public void startsharelink()
    {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.title_myapp_name));
            String shareMessage= "\n"+getResources().getString(R.string.info_appsuggest_msg)+"\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one(एक चुनो)"));
        } catch(Exception e) {
            //e.toString();
            Log.d("Error",e.toString());
        }

    }

    public void startsettingschg()
    {
        Intent mIntent = new Intent(getApplicationContext(), SettingsProv.class);
        startActivity(mIntent);
        finish();

    }
    public abstract class PaginationScrollListener extends RecyclerView.OnScrollListener {

        LinearLayoutManager layoutManager;

        public PaginationScrollListener(LinearLayoutManager layoutManager) {
            this.layoutManager = layoutManager;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            int visibleItemCount = layoutManager.getChildCount();
            int totalItemCount = layoutManager.getItemCount();
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

            if (!isLoading() && !isLastPage()) {
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= getTotalPageCount() ) {
                    loadMoreItems();
                }
            }
        }

        protected abstract void loadMoreItems();
        public abstract int getTotalPageCount();
        public abstract boolean isLastPage();
        public abstract boolean isLoading();
    }


    public void showuploadlist(String response)
    {
        try {
            JSONObject jsonObj = new JSONObject(response);
            JSONArray jsonArraymain = jsonObj.getJSONArray("data");
            JSONArray jsonArray = jsonArraymain.getJSONArray(0);
            dataModels = new ArrayList<>(jsonArray.length());
            boolean ishi_lan = LocaleManager.getlanguage().equals(LocaleManager.LOCALE_HI);
            for(int i = 0;i< jsonArray.length();i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String jpid = jsonObject.getString("jid");
                String cname = jsonObject.getString("cname");
                String catname = "";

                if(ishi_lan)
                    catname = jsonObject.getString("catname_hi");
                else
                    catname = jsonObject.getString("catname");

                String catnum = jsonObject.getString("catnum");
                String title = jsonObject.getString("jtitle");
                String location = jsonObject.getString("jlocation");
                String expdate = jsonObject.getString("jexpiredate");
                String iscontract = jsonObject.getString("jiscontract");
                String dnshift = jsonObject.getString("jdnshift");
                String salmin = jsonObject.getString("jsalmin");
                String salmax = jsonObject.getString("jsalmax");
                String novacency = jsonObject.getString("jttlvacency");
                String otlimit = jsonObject.getString("jotlimithour");
                String intermode = jsonObject.getString("jinterviewmode");
                String intertime = jsonObject.getString("jinterviewtm");

                String tm = jsonObject.getString("postdate");
                String dt = tm.substring(0, 19).replace('T', ' ');

                String jpdate = dt;//(jsonObject.getString("postdate"));

                String benid = jsonObject.getString("bid");


                JobPostDataModel item = new JobPostDataModel();
                item.set(jpid,cname,catname,catnum,title,location,expdate,iscontract,dnshift,salmin, salmax,jpdate,otlimit,novacency,intermode,intertime,benid);
                dataModels.add(item);

            }

            JSONArray jsonArray2 = jsonArraymain.getJSONArray(1);
            jclist = new ArrayList<>(jsonArray2.length());
            if(LocaleManager.getlanguage().equals(LocaleManager.LOCALE_HI)) {
                for (int j = 0; j < jsonArray2.length(); j++) {
                    JobCategory item = new JobCategory();
                    JSONObject jsonObject = jsonArray2.getJSONObject(j);
                    item.catname = jsonObject.getString("catname_hi");
                    item.catnum = jsonObject.getString("catnum");
                    jclist.add(item);
                }
            } else {
                for (int j = 0; j < jsonArray2.length(); j++) {
                    JobCategory item = new JobCategory();
                    JSONObject jsonObject = jsonArray2.getJSONObject(j);
                    item.catname = jsonObject.getString("catname");
                    item.catnum = jsonObject.getString("catnum");
                    jclist.add(item);
                }
            }

            JSONArray jsonArray3 = jsonArraymain.getJSONArray(2);
            jbenlist = new ArrayList<>(jsonArray3.length());
            if(LocaleManager.getlanguage().equals(LocaleManager.LOCALE_HI)) {
                for (int j = 0; j < jsonArray3.length(); j++) {
                    BenefitCategory item = new BenefitCategory();
                    JSONObject jsonObject = jsonArray3.getJSONObject(j);
                    item.catname = jsonObject.getString("catname_hi");
                    item.catnum = jsonObject.getString("catnum");
                    jbenlist.add(item);
                }
            } else {
                for (int j = 0; j < jsonArray3.length(); j++) {
                    BenefitCategory item = new BenefitCategory();
                    JSONObject jsonObject = jsonArray3.getJSONObject(j);
                    item.catname = jsonObject.getString("catname");
                    item.catnum = jsonObject.getString("catnum");
                    jbenlist.add(item);
                }
            }


            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    int count = dataModels.size();
                    if(count > 0)
                    {
                        adapter.addAll(dataModels);
                        if((count < PER_PAGE_COUNT) || (currentPage >= (TOTAL_PAGES*PER_PAGE_COUNT)))
                            isLastPage = true;
                        else
                            isLastPage = false;
                    }
                    isLoading = false;
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void startdeactivatepost(String jpid)
    {
        if(NetworkChangeReceiver.isNetworkConnected(getApplicationContext())) {
            mDelCJPost = new ComJobPostTask(jpid);
            mDelCJPost.execute();
        } else {
            Toast.makeText(this, getResources().getString(R.string.alert_network_disconnected), Toast.LENGTH_LONG).show();
        }

    }

    public void reloadrecycleview(final String param)
    {
        isLoading = false;
        isLastPage = false;
        currentPage = 0;

        //setTitle(R.string.app_name);
        recyclerView_usr = findViewById(R.id.recycler_view);
        adapter_usr = new ListUserReqActivity(HomeActivity.this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);//new GridLayoutManager(this.getContext(), 1);
        recyclerView_usr.setLayoutManager(mLayoutManager);
        //recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView_usr.setItemAnimator(new DefaultItemAnimator());
        recyclerView_usr.setAdapter(adapter_usr);
        recyclerView_usr.addOnScrollListener(new PaginationScrollListener((LinearLayoutManager) mLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                //Increment page index to load the next one
                currentPage += 10;
                loadNextPageUsr(param);
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
        isLoading = true;
        loadFirstPageUsr(param);

    }

    private void loadFirstPageUsr(String param) {
        //showprogress(true);
        getusrpostlist(param);
    }

    private void loadNextPageUsr(String param) {
        getusrpostlist(param);
    }

    private void getusrpostlist(String str)
    {
        if(NetworkChangeReceiver.isNetworkConnected(getApplicationContext())) {
            showprogressbar();
            mCJPost = new ComLoadJobPostTask("LOAD_USER",str, String.valueOf(0));
            mCJPost.execute();
        } else {
            Toast.makeText(this, getResources().getString(R.string.alert_network_disconnected), Toast.LENGTH_LONG).show();
        }

    }

    public String caltimediff(String oldtm,String curtime)
    {

        String tm = curtime;
        String secondtm = tm.substring(0, 19).replace('T', ' ');

        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
        int diff = 0;

        try {
            Date date1 = simpleDateFormat1.parse(oldtm);
            Date date2 = simpleDateFormat2.parse(secondtm);

            //obj.printDifference(date1, date2);
            diff = date2.getYear() - date1.getYear();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return String.valueOf(diff);

    }

    public void startupdateviewdata(String response)
    {

        try {
            JSONObject jsonObj = new JSONObject(response);
            if(jsonObj.has("data")) {
                JSONArray jsonArray = jsonObj.getJSONArray("data");
                dataModels_usr = new ArrayList<>(jsonArray.length());
                boolean ishi_lan = LocaleManager.getlanguage().equals(LocaleManager.LOCALE_HI);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String uid = jsonObject.getString("id");
                    String catname = jsonObject.getString("category");
                    if (ishi_lan)
                        catname = jsonObject.getString("category_hi");
                    String uname = jsonObject.getString("name");
                    String mobile = jsonObject.getString("mobile");
                    //String updatetm = jsonObject.getString("updatetm");
                    String dt = jsonObject.getString("jpapldate");
                    String updatetm = dt.substring(0, 19).replace('T', ' ');

                    String curtm = jsonObject.getString("curdate");

                    String dob = jsonObject.getString("age");
                    String age = caltimediff(dob, curtm);//(jsonObject.getString("postdate"));

                    String jsid = jsonObject.getString("jsid");

                    UserJobReqDataModel item = new UserJobReqDataModel();
                    item.set(uid, uname, age, null, null, mobile, null, null, updatetm, catname,
                            null, null, null, null, null, null, null, null, null, curtm, jsid);

                    dataModels_usr.add(item);

                }
            }


            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    int count = (dataModels_usr!=null) ? dataModels_usr.size(): 0;
                    if(count > 0)
                    {
                        adapter_usr.addAll(dataModels_usr);
                        if((count < PER_PAGE_COUNT) || (currentPage >= (TOTAL_PAGES*PER_PAGE_COUNT)))
                            isLastPage = true;
                        else
                            isLastPage = false;
                    } else {
                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.alert_no_jobapp_received),Toast.LENGTH_LONG).show();
                    }

                    isLoading = false;


                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void  startjobapplications()
    {
        Bundle b = getIntent().getExtras();
        String str = null;
        if(b != null)
            str = b.getString("jpid");

        reloadrecycleview(str);
    }

    private static final int PHONE_CALL_PERMISSION_CODE = 300;
    private static String callparam = "";
    public static void startGsmCallActivity(String data)
    {
        String phoneno = data.replaceAll("[\\D]", "");
        if(phoneno !="")
        {
            if (ActivityCompat.checkSelfPermission(HomeActivity.objSing, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                callparam = data;
                objSing.checkPhoneCallpermission(data);
            }
            else {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+phoneno));
                objSing.startActivity(callIntent);
            }

        }
    }

    private void checkPhoneCallpermission(String data)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(HomeActivity.objSing, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
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
    public void startUserGsmCall(UserJobReqDataModel data)
    {
        //call REST API for save call rec
        if(NetworkChangeReceiver.isNetworkConnected(getApplicationContext())) {
            mCCRPost = new ComCallRecTask(data);
            mCCRPost.execute();
        } else {
            Toast.makeText(this, getResources().getString(R.string.alert_network_disconnected), Toast.LENGTH_LONG).show();
        }
    }

    public static void startJopPostInform(JobPostDataModel data) throws JSONException {

        try {
            Intent mIntent = new Intent(objSing.getApplicationContext(), JobPostInform.class);

            JSONObject jobj = new JSONObject();
            jobj.put("jid",data.jid);
            jobj.put("cname",data.cname);
            jobj.put("catname",data.catname);
            jobj.put("title",data.title);
            jobj.put("location",data.location);
            jobj.put("expdate",data.expdate);
            jobj.put("iscontract",data.iscontract);
            jobj.put("dnshift",data.dnshift);
            jobj.put("salmin",data.salmin);
            jobj.put("salmax",data.salmax);
            jobj.put("jpdate",data.jpdate);
            jobj.put("otlimit",data.otlimithour);
            jobj.put("vacencyno",data.vacencyno);
            jobj.put("interm",data.intermode);
            jobj.put("intert",data.intertime);
            jobj.put("benlist",data.jobblist);

            Bundle b = new Bundle();
            b.putString("jpdata", jobj.toString()); //Your id
            mIntent.putExtras(b);
            objSing.startActivity(mIntent);
            objSing.finish();
        } catch(JSONException e)
        {
            Log.d("Error",e.toString());
        }

    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class ComLoadJobPostTask extends AsyncTask<Void, Void, String> {

        private String path;
        private String scomid;
        private String spgnum;

        ComLoadJobPostTask(String url,String comid, String pgnum) {
            path = url;
            scomid = comid;
            spgnum = pgnum;
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String r = null;
            String usrid = String.valueOf(user_id);
            try
            {
                // Simulate network access.
                if(path == "LOAD_JOB")
                    r = RestAPI.getComJobPost(usrid,compid,spgnum,getApplicationContext());
                else if (path == "LOAD_USER")
                    r = RestAPI.getUserJobReqList(scomid,spgnum,getApplicationContext());

                Thread.sleep(2000);
                hideprogrssbar();
                if(r !=null)
                {
                    if(path == "LOAD_JOB")
                        showuploadlist(r);
                    else if (path == "LOAD_USER")
                        startupdateviewdata(r);
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
            mCJPost = null;

            if (success!=null) {
                //finish();
            } else {
                //mPasswordView.setError(getString(R.string.error_incorrect_password));
                //mPasswordView.requestFocus();
                Toast.makeText(HomeActivity.this, R.string.error_no_compost, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mCJPost = null;

        }
    }

    public class ComJobPostTask extends AsyncTask<Void, Void, String> {

        private String jobpid;
        ComJobPostTask(String jpid) {
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
            mDelCJPost = null;

            if (success!=null) {
                //finish();
            } else {
                //mPasswordView.setError(getString(R.string.error_incorrect_password));
                //mPasswordView.requestFocus();
                Toast.makeText(HomeActivity.this, R.string.error_no_compost, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mDelCJPost = null;

        }
    }


    public class ComCallRecTask extends AsyncTask<Void, Void, String> {

        private UserJobReqDataModel data;
        ComCallRecTask(UserJobReqDataModel dt) {
            data = dt;
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String r = null;
            try
            {
                // Simulate network access.
                r = RestAPI.updateProvcallrec(data.jsid,getApplicationContext());
                Thread.sleep(2000);
                if(r !=null)
                {
                    startGsmCallActivity(data.getMobile());
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
                Toast.makeText(HomeActivity.this, R.string.error_no_compost, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mCCRPost = null;

        }
    }

    public class FireBaseStoreTask extends AsyncTask<Void, Void, String> {

        private String tkn,id;

        FireBaseStoreTask(String val,String loginid) {
            tkn = val;
            id = loginid;
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String r = null;
            try
            {
                // Simulate network access.
                r = RestAPI.updateprvtoken(tkn,id);
                Thread.sleep(2000);
                if(r !=null)
                {
                    Log.d("Error","Token not updated in server!");
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
            mFBStore = null;

            if (success!=null) {
                //finish();
            } else {
                //mPasswordView.setError(getString(R.string.error_incorrect_password));
                //mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mFBStore = null;

        }
    }



}
