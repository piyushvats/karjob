package com.karigarjobs.user;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karigarjobs.AllUsrProvInfo;
import com.karigarjobs.AllUsrProvInfo.JobCategory;
import com.karigarjobs.BuildConfig;
import com.karigarjobs.R;
import com.karigarjobs.Session;
import com.karigarjobs.provider.HomeActivity;
import com.karigarjobs.provider.JobPostCreateActivity;
import com.karigarjobs.user.cvbutton.UserCvCreateSimple;
import com.karigarjobs.user.cvrecord.UserProfileCreateVoice;
import com.karigarjobs.utils.ImageCache;
import com.karigarjobs.utils.LocaleManager;
import com.karigarjobs.utils.NetworkChangeReceiver;
import com.karigarjobs.utils.RestAPI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.karigarjobs.utils.SpinnerAdaptor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeActivityU extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static HomeActivityU objSing = null;
    public static String parent = "MAIN";
    public static boolean InitListJobPost = false;
    public Integer userloginid=0;
    public String userid = null;
    public static String reqcvid = null;
    private ComLoadJobPostTask  mCJPost = null;
    private UsrLoadJobPostHistTask mUsrJPost = null;
    //private UserCvStatusTask mUsrCv =null;
    private CheckVersionTask mCheckAppVersionTask=null;

    private RecyclerView recyclerView,recyclerView_pre;
    private ListPostActivityUser adapter;
    private ListJobPostUserReq adapter_pre;
    public ArrayList<JobPostDataModelUser> dataModels;
    public ArrayList<JobPostDataModelUsrApl> usrapldataModels;
    public TextView tvUserName, tvUserMob;
    public ImageView tvUserProfImg;

    ProgressDialog progress;

    public static ArrayList<JobCategory> jclist=null;
    public static ArrayList<AllUsrProvInfo.BenefitCategory> jbenlist=null;
    ArrayList<SpinnerAdaptor.SpinnerItem> jc_spinnerlist = null;
    public static ArrayList<String> jcfilterval = null;
    //public static String fileSelectPath = Environment.getExternalStorageDirectory().getPath() + "/karijob/profile/";
    public String fileSelectPath = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_u);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_home);
        setSupportActionBar(toolbar);


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

        fileSelectPath = getExternalFilesDir(null).getPath() + "/profile/";;

        Session sess = new Session(getApplicationContext());
        sess.refreshSession();
        if (Session.IsCreated) {
            tvUserName.setText(Session.getname());
            tvUserMob.setText(Session.getmob());
            userloginid = Session.getUsr_id();
            userid = Session.getupid();
            reqcvid = Session.getusrcvid();

            //if( userid != null && userid.equals("null") && reqcvid != null && !reqcvid.equals("null"))
/*            if(reqcvid != null && !reqcvid.equals("null"))
            {
                if(NetworkChangeReceiver.isNetworkConnected(getApplicationContext())) {
                    mUsrCv = new UserCvStatusTask(reqcvid);
                    mUsrCv.execute();
                } else {
                    Toast.makeText(this, getResources().getString(R.string.alert_network_disconnected), Toast.LENGTH_LONG).show();
                }
            }*/

            FloatingActionButton floatbtn = findViewById(R.id.cv_alert_button);
            if(( userid != null && !userid.equals("null")) || (reqcvid != null && !reqcvid.equals("null")))
            {
                floatbtn.setVisibility(View.INVISIBLE);
            } else {
                floatbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent mIntent = new Intent(getApplicationContext(), UserCvCreateSimple.class);
                        startActivity(mIntent);
                        finish();
                    }
                });
            }

            if(NetworkChangeReceiver.isNetworkConnected(getApplicationContext())) {
                //update token to server
                //FirebaseApp.initializeApp(this);
                manageFirebasetoken(String.valueOf(userloginid));
            } else {
                Toast.makeText(this, getResources().getString(R.string.alert_network_disconnected), Toast.LENGTH_LONG).show();
            }

            String strprofl = fileSelectPath+ "profpic.jpg";
            File fl = new File(strprofl);
            if(fl.exists())
            {
                Bitmap btmp ;
                try {
                     btmp = ImageCache.decodeSampledBitmap(strprofl , null, 200, 200);
                    //btmp = getResizedBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(fl)),200,200);
                    tvUserProfImg.setImageBitmap(btmp);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }



        }

        initrecycleview();
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
        if(progress.isShowing())
            return;
        progress.setMessage(getResources().getString(R.string.text_status_processing));
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();

    }

    void hideprogrssbar()
    {
        if(progress !=null && progress.isShowing())
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
        isLoading = false;
        isLastPage = false;
        currentPage = 0;


        //View itemview = inflater.inflate(R.layout.activity_home, null);
        recyclerView = findViewById(R.id.recycler_userview);
        adapter = new ListPostActivityUser(HomeActivityU.this);

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
        //isFirstLast = 1;
        //if(!InitListJobPost) {
        //    InitListJobPost = true;
            getpostlist();
        //} else {
        //    adapter.refereshListData();
        //}
    }

    private void loadNextPage() {
        //isFirstLast = 2;
        getpostlist();
    }

    private void getpostlist()
    {
        //page num 0
        if(NetworkChangeReceiver.isNetworkConnected(getApplicationContext())) {
            showprogressbar();
            mCJPost = new ComLoadJobPostTask("LOAD_JOB",String.valueOf(userloginid), String.valueOf(currentPage));
            mCJPost.execute();
        } else {
            Toast.makeText(this, getResources().getString(R.string.alert_network_disconnected), Toast.LENGTH_LONG).show();
        }

    }


    void startpreviousjpapplication()
    {
        isLoading = false;
        isLastPage = false;
        currentPage = 0;
        //getMenuInflater().inflate(0,null);
        //View itemview = inflater.inflate(R.layout.activity_home, null);
        recyclerView_pre = findViewById(R.id.recycler_userview);
        adapter_pre = new ListJobPostUserReq(HomeActivityU.this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);//new GridLayoutManager(this.getContext(), 1);
        recyclerView_pre.setLayoutManager(mLayoutManager);
        //recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView_pre.setItemAnimator(new DefaultItemAnimator());
        recyclerView_pre.setAdapter(adapter_pre);
        recyclerView_pre.addOnScrollListener(new PaginationScrollListener((LinearLayoutManager) mLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                //Increment page index to load the next one
                currentPage += 10;
                loadNextPagePre();
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
        loadFirstPagePre();

    }


    private void loadFirstPagePre() {
        //showprogress(true);
        //isFirstLast = 1;
        getpostlisthis();
    }

    private void loadNextPagePre() {
        //isFirstLast = 2;
        getpostlisthis();
    }

    private void getpostlisthis()
    {
        //page num 0
        if(NetworkChangeReceiver.isNetworkConnected(getApplicationContext())) {
            showprogressbar();
            mUsrJPost = new UsrLoadJobPostHistTask(String.valueOf(userid), String.valueOf(currentPage));
            mUsrJPost.execute();
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
                if(recyclerView != null && recyclerView_pre != null)
                {
                    finish();
                    startActivity(getIntent());
                }
                else
                    this.moveTaskToBack(true);
                //super.onBackPressed();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_activity_u, menu);
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
            if(recyclerView != null ) {
                if (!adapter.isEmpty())
                    adapter.clear();
                InitListJobPost = false;
                initrecycleview();
            }
            return true;
        }
        if (id == R.id.jobfilter) {
            enablefilterjobcat();
            showfilteroption();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_editcv) {
            // Handle the camera action
            startnewuserprofiletext();
        } else if (id == R.id.nav_prvapp) {
            if(userid != null)
            {
                if(dataModels != null)
                    dataModels.clear();
                startpreviousjpapplication();

            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.alert_create_cv_historyjob), Toast.LENGTH_LONG).show();
            }

        } else if (id == R.id.nav_editcv_rec) {
            startcvbyvoice();
        } else if (id == R.id.nav_accsettings) {
            startsettingschg();
        } else if (id == R.id.nav_share) {
            startsharelink();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
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
        Intent mIntent = new Intent(getApplicationContext(), SettingsUser.class);
        startActivity(mIntent);
        finish();

    }

    void enablefilterjobcat()
    {
        jc_spinnerlist = new ArrayList<>();
        SpinnerAdaptor.SpinnerItem headeritem = new SpinnerAdaptor.SpinnerItem();
        headeritem.setTitle(getResources().getString(R.string.title_sel_jobcat));
        headeritem.setIndex("0");
        jc_spinnerlist.add(headeritem);
        for (int i = 0; i < jclist.size(); i++) {
            SpinnerAdaptor.SpinnerItem item = new SpinnerAdaptor.SpinnerItem();
            item.setTitle(jclist.get(i).catname);
            item.setIndex(jclist.get(i).catnum);
            item.setSelected(false);
            jc_spinnerlist.add(item);
        }


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

    public void showfilteroption()
    {
        final LayoutInflater inflater = LayoutInflater.from(HomeActivityU.this);
        final View alertLayout = inflater.inflate(R.layout.alert_job_filter, null);
        final AlertDialog.Builder alert = new AlertDialog.Builder(HomeActivityU.this);

        //final EditText feedinput = alertLayout.findViewById(R.id.editText3);
        Spinner spinner6 = (Spinner)alertLayout.findViewById(R.id.jobcat_spinner);
        SpinnerAdaptor myAdapter = new SpinnerAdaptor(alert.getContext(), 0,jc_spinnerlist);
        spinner6.setAdapter(myAdapter);
        spinner6.setOnItemSelectedListener(new CustomOnItemSelectedListener());


        alert.setTitle(getResources().getString(R.string.title_sel_jobcat));
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);

        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        alert.setNegativeButton(getResources().getString(R.string.alert_cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(getBaseContext(), "Cancel clicked", Toast.LENGTH_SHORT).show();
            }
        });

        alert.setPositiveButton(getResources().getString(R.string.action_submitpwd), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
/*                String userfb = feedinput.getText().toString();
                String feedrateres = ((TextView) alertLayout.findViewById(R.id.textView5)).getText().toString();
                if(!feedrateres.isEmpty())
                {
                    int pos = Integer.parseInt(idx);
                    JobPostDataModelUser data = new JobPostDataModelUser();
                    String jsid = dataSet.get(pos).jsid;
                    //send feedback
                    objSing.sendusrfeedback(jsid,feedrateres,userfb,"");
                }*/
                if(jcfilterval != null) {
                    jcfilterval.clear();
                    jcfilterval = null;
                }
                jcfilterval = new ArrayList<>();
                for(int c = 0;c < jc_spinnerlist.size();c++) {
                    if(jc_spinnerlist.get(c).isSelected())
                    {
                        //JobCategory item = new JobCategory();
                        //item.catname = jc_spinnerlist.get(c).getTitle();
                        //item.catnum = jc_spinnerlist.get(c).getIndex();
                        //for (int i=0;i< jclist.size();i++ ) {
                        //    if(jclist.get(i).catname.equals(item.catname))
                        //    {
                        jcfilterval.add(jc_spinnerlist.get(c).getIndex());
                        //        break;
                        //    }
                        //}
                    }
                }
                if(recyclerView != null ) {
                    if (adapter != null ) { //!adapter.isEmpty()) {
                        if(jcfilterval.size() > 0)
                            adapter.getFilter().filter("showfilter");
                        else
                            adapter.getFilter().filter("");
                    }
                }


                //String pass = etPassword.getText().toString();
                //Toast.makeText(getBaseContext(), "Username: " + user + " Password: " + pass, Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
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
                String comid = jsonObject.getString("cid");
                String jpid = jsonObject.getString("jid");
                String cname = jsonObject.getString("cname");

                String catname = "";
                if(ishi_lan)
                    catname = jsonObject.getString("catname_hi");
                else
                    catname = jsonObject.getString("catname");

                String catnum = jsonObject.getString("catnum");
                String title = jsonObject.getString("jtitle");
                String detail = jsonObject.getString("jdetail");
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

                String jpblist = jsonObject.getString("bid");

                JobPostDataModelUser item = new JobPostDataModelUser();
                item.set(jpid,comid,cname,catname,catnum,title,detail,location,expdate,iscontract,dnshift,salmin, salmax,jpdate ,otlimit,novacency,intermode,intertime,jpblist);
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
                    AllUsrProvInfo.BenefitCategory item = new AllUsrProvInfo.BenefitCategory();
                    JSONObject jsonObject = jsonArray3.getJSONObject(j);
                    item.catname = jsonObject.getString("catname_hi");
                    item.catnum = jsonObject.getString("catnum");
                    jbenlist.add(item);
                }
            } else {
                for (int j = 0; j < jsonArray3.length(); j++) {
                    AllUsrProvInfo.BenefitCategory item = new AllUsrProvInfo.BenefitCategory();
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


    public static void startJopPostInform(JobPostDataModelUser data) throws JSONException {

        try {
            Intent mIntent = new Intent(objSing.getApplicationContext(), JobPostInformUser.class);

            JSONObject jobj = new JSONObject();
            jobj.put("uid",objSing.userid);
            jobj.put("jid",data.jid);
            jobj.put("cid",data.comid);
            jobj.put("cname",data.cname);
            jobj.put("catname",data.catname);
            jobj.put("title",data.title);
            jobj.put("detail",data.detail);
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




    public void showJpApluploadlist(String response)
    {

        try {
            JSONObject jsonObj = new JSONObject(response);
            if(jsonObj.has("data")) {
                JSONArray jsonArraymain = jsonObj.getJSONArray("data");
                JSONArray jsonArray = jsonArraymain.getJSONArray(0);
                usrapldataModels = new ArrayList<>(jsonArray.length());
                boolean ishi_lan = LocaleManager.getlanguage().equals(LocaleManager.LOCALE_HI);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String comid = jsonObject.getString("cid");
                    String jpid = jsonObject.getString("jid");
                    String cname = jsonObject.getString("cname");

                    String catname = "";
                    if (ishi_lan)
                        catname = jsonObject.getString("catname_hi");
                    else
                        catname = jsonObject.getString("catname");

                    String catnum = jsonObject.getString("catnum");
                    String title = jsonObject.getString("jtitle");
                    String detail = jsonObject.getString("jdetail");
                    String location = jsonObject.getString("jlocation");
                    String expdate = jsonObject.getString("jexpiredate");
                    String iscontract = jsonObject.getString("jiscontract");
                    String dnshift = jsonObject.getString("jdnshift");
                    String salmin = jsonObject.getString("jsalmin");
                    String salmax = jsonObject.getString("jsalmax");

                    String tm = jsonObject.getString("postdate");
                    String jpdate = tm.substring(0, 19).replace('T', ' ');


                    String apdt = jsonObject.getString("jpusrapldate");
                    String apjpdate = apdt.substring(0, 19).replace('T', ' ');


                    String offadr = jsonObject.getString("offaddr");
                    String offaddrp = jsonObject.getString("offaddrpin");
                    String comp_name = jsonObject.getString("conper1_name");
                    String comp_desg = jsonObject.getString("conper1_desg");
                    String comp_phone = jsonObject.getString("conper1_phone");
                    String cemail = jsonObject.getString("email");

                    String novacency = jsonObject.getString("jttlvacency");
                    String otlimit = jsonObject.getString("jotlimithour");
                    String intermode = jsonObject.getString("jinterviewmode");
                    String intertime = jsonObject.getString("jinterviewtm");
                    String jpblist = jsonObject.getString("bid");
                    String jsid = jsonObject.getString("jsid");


                    JobPostDataModelUsrApl item = new JobPostDataModelUsrApl();
                    item.set(jpid, comid, cname, catname, catnum, title, detail,location, expdate, iscontract, dnshift, salmin, salmax, jpdate, apjpdate, offadr, offaddrp, comp_name, comp_desg, comp_phone, cemail, otlimit, novacency, intermode, intertime, jpblist, jsid);
                    usrapldataModels.add(item);

                }
            }

            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    int count = (usrapldataModels!=null) ? usrapldataModels.size():0;
                    if(count > 0)
                    {
                        adapter_pre.addAll(usrapldataModels);
                        if((count < PER_PAGE_COUNT) || (currentPage >= (TOTAL_PAGES*PER_PAGE_COUNT)))
                            isLastPage = true;
                        else
                            isLastPage = false;
                    }
                    isLoading = false;
                    if(count == 0 )
                    {
                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.alert_no_data_available),Toast.LENGTH_LONG).show();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }




    void startnewuserprofiletext()
    {

        if (Session.IsCreated) {
            tvUserName.setText(Session.getname());
            tvUserMob.setText(Session.getmob());
            userloginid = Session.getUsr_id();
            userid = Session.getupid();

            if(userid!=null && !userid.isEmpty()) {
                Intent mIntent = new Intent(getApplicationContext(), UserProfileCreateActivity.class);
                Bundle b = new Bundle();


                b.putString("uid", String.valueOf(Session.getUsr_id())); //Your id
                b.putString("mob", Session.getmob()); //Your id
                b.putString("mail", Session.getemail()); //Your id
                b.putString("usrproid", Session.getupid()); //Your id
                mIntent.putExtras(b);
                startActivity(mIntent);
                finish();
            } else {
                Intent mIntent = new Intent(getApplicationContext(), UserCvCreateSimple.class);
                startActivity(mIntent);
                finish();
            }

        }

    }


    void startcvbyvoice()
    {
        Intent mIntent = new Intent(getApplicationContext(), UserProfileCreateVoice.class);
        startActivity(mIntent);
        finish();
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
            try
            {
                // Simulate network access.
                r = RestAPI.getComJobPostUser(spgnum);
                Thread.sleep(2000);
                hideprogrssbar();
                if(r !=null)
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
            mCJPost = null;

            if (success!=null) {
                //finish();
            } else {
                //mPasswordView.setError(getString(R.string.error_incorrect_password));
                //mPasswordView.requestFocus();
                Toast.makeText(HomeActivityU.this, R.string.error_no_compost, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mCJPost = null;

        }
    }


    public class UsrLoadJobPostHistTask extends AsyncTask<Void, Void, String> {

        private String usrid;
        private String spgnum;

        UsrLoadJobPostHistTask(String uid, String pgnum) {
            usrid = uid;
            spgnum = pgnum;
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String r = null;
            try
            {
                // Simulate network access.
                r = RestAPI.getUsrJPHist(usrid,spgnum);
                Thread.sleep(2000);
                hideprogrssbar();
                if(r !=null)
                {
                    showJpApluploadlist(r);
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
            mUsrJPost = null;

            if (success!=null) {
                //finish();
            } else {
                //mPasswordView.setError(getString(R.string.error_incorrect_password));
                //mPasswordView.requestFocus();
                Toast.makeText(HomeActivityU.this, R.string.error_no_compost, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mUsrJPost = null;

        }
    }


/*

    public class UserCvStatusTask extends AsyncTask<Void, Void, String> {

        private String reqid;

        UserCvStatusTask(String uid) {
            reqid = uid;
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String r = null;
            try
            {
                // Simulate network access.
                r = RestAPI.getUsrCvSt(reqid);
                Thread.sleep(2000);
                if(r !=null)
                {
                    JSONObject obj = new JSONObject(r);
                    String val = obj.getString("data");
                    if(val.equals("ACCEPTED")) {
                        if(userid == null ) {
                            userid = obj.getString("field");
                            Session.setupid(userid);
                        }
                        HomeActivityU.reqcvid = null;
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
            mUsrCv = null;

            if (success!=null) {
                //finish();
            } else {
                //mPasswordView.setError(getString(R.string.error_incorrect_password));
                //mPasswordView.requestFocus();
                Toast.makeText(HomeActivityU.this, R.string.error_no_compost, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mUsrCv = null;

        }
    }
*/

    UserJpFeedBackTask  mUsrfeed = null;
    public void sendusrfeedback(String id,String rate,String feed,String genfeed)
    {
        if(NetworkChangeReceiver.isNetworkConnected(getApplicationContext())) {
            showprogressbar();
            mUsrfeed = new UserJpFeedBackTask(id,rate,feed,genfeed);
            mUsrfeed.execute();
        } else {
            Toast.makeText(this, getResources().getString(R.string.alert_network_disconnected), Toast.LENGTH_LONG).show();
        }
    }

    public class UserJpFeedBackTask extends AsyncTask<Void, Void, String> {

        private String jsid;
        private String urate;
        private String ufeed;
        private String gfeed;

        UserJpFeedBackTask(String id,String rate,String feed,String genfeed) {
            jsid = id;
            urate = rate;
            ufeed = feed;
            gfeed = genfeed;
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String r = null;
            try
            {
                // Simulate network access.
                r = RestAPI.setUsrJpAplFeed(jsid,urate,ufeed,gfeed);
                Thread.sleep(2000);
                hideprogrssbar();
                if(r !=null)
                {
                    JSONObject obj = new JSONObject(r);
                    String val = obj.getString("data");
                    if(val.equals("ACCEPTED")) {
                        userid = obj.getString("field");
                        Session.setupid(userid);
                    }
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
            mUsrfeed = null;

            if (success!=null) {
                //finish();
            } else {
                //mPasswordView.setError(getString(R.string.error_incorrect_password));
                //mPasswordView.requestFocus();
                Toast.makeText(HomeActivityU.this, R.string.error_no_compost, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mUsrfeed = null;

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
                r = RestAPI.updateusrtoken(tkn,id);
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
