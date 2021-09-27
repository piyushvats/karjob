package com.karigarjobs.provider;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.slider.RangeSlider;
import com.google.android.material.slider.Slider;
import com.karigarjobs.R;
import com.karigarjobs.utils.LocaleManager;
import com.karigarjobs.utils.NetworkChangeReceiver;
import com.karigarjobs.utils.RestAPI;
import com.karigarjobs.utils.SpinnerAdaptor;
import com.edmodo.rangebar.RangeBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import static com.karigarjobs.provider.HomeActivity.jbenlist;
import static com.karigarjobs.provider.HomeActivity.jclist;

public class JobPostCreateActivity extends AppCompatActivity {

    private JobProfileUploadTask mJobPostUpload;
    private String usrid,compid;
    ArrayList<String> jtlist = null;
    ArrayList<HomeActivity.BenefitCategory> blist = null;
    Spinner spinner1,spinner2,spinner3,spinner4,spinner5,spinner6;
    Button btnupload;
    ArrayAdapter<CharSequence> spinadap,spinadap1,spinadap2,spinadap3;
    static TextView textviewdate;
    static TextView textviewtime;
    private Handler mHandler;
    ProgressDialog progress;

    ArrayList<SpinnerAdaptor.SpinnerItem> splist =null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_post_create);

        if(HomeActivity.objSing.compid == null || HomeActivity.objSing.compid.equals("null") || HomeActivity.objSing.compid.isEmpty())
        {
            Toast.makeText(JobPostCreateActivity.this, getResources().getString(R.string.err_no_comp_profile), Toast.LENGTH_SHORT).show();
            return ;
        }

        usrid = String.valueOf(HomeActivity.objSing.user_id);
        compid = String.valueOf(HomeActivity.objSing.compid);


        spinner1 = (Spinner) findViewById(R.id.editspinner2);
        spinadap = ArrayAdapter.createFromResource(this,R.array.work_type, android.R.layout.simple_spinner_item);
        spinadap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(spinadap);
        spinner1.setOnItemSelectedListener(new CustomOnItemSelectedListener());


        spinner2 = (Spinner) findViewById(R.id.editspinner3);
        spinadap1 = ArrayAdapter.createFromResource(this,R.array.work_shift, android.R.layout.simple_spinner_item);
        spinadap1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(spinadap1);
        spinner2.setOnItemSelectedListener(new CustomOnItemSelectedListener());


        spinner3 = (Spinner) findViewById(R.id.editspinner4);
        spinadap2 = ArrayAdapter.createFromResource(this,R.array.over_time, android.R.layout.simple_spinner_item);
        spinadap2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3.setAdapter(spinadap2);
        spinner3.setOnItemSelectedListener(new CustomOnItemSelectedListener());


        spinner4 = (Spinner) findViewById(R.id.editspinner5);
        spinadap3 = ArrayAdapter.createFromResource(this,R.array.interview_mode, android.R.layout.simple_spinner_item);
        spinadap3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner4.setAdapter(spinadap3);
        spinner4.setOnItemSelectedListener(new CustomOnItemSelectedListener());


        textviewdate = findViewById(R.id.editText5);
        textviewtime = findViewById(R.id.editText6);

        updateviewdetail();


        final int mul = 100;
        final int oldleftThumbIndex = 4000;
        final int oldrightThumbIndex = 10000;
        //final RangeBar rangebar = findViewById(R.id.seekBar);
        //rangebar.setTickCount(1000);

        RangeSlider rangeSlider = findViewById(R.id.rangeSlider);

        //rangeSlider.setThumbIndices(oldleftThumbIndex , oldrightThumbIndex);
        TextView tvmin = (TextView)findViewById(R.id.textView13);
        TextView tvmax = (TextView)findViewById(R.id.textView14);
        tvmin.setText("Min:"+String.valueOf(oldleftThumbIndex));
        tvmax.setText("Max:"+String.valueOf(oldrightThumbIndex));

        rangeSlider.addOnSliderTouchListener( new RangeSlider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(RangeSlider slider) {
                // Responds to when slider's touch event is being started
            }
            @Override
            public void onStopTrackingTouch(RangeSlider slider) {
                // Responds to when slider's touch event is being stopped
            }
        });

        rangeSlider.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                TextView tvmin = (TextView)findViewById(R.id.textView13);
                TextView tvmax = (TextView)findViewById(R.id.textView14);
                int val = slider.getValues().get(0).intValue();
                tvmin.setText("Min:"+String.valueOf(val));
                val = slider.getValues().get(1).intValue();
                tvmax.setText("Max:"+String.valueOf(val));

            }
        });
            // Responds to when slider's value is changed




        btnupload = (Button)(findViewById(R.id.imageButton2));
        btnupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnupload.setEnabled(false);
                uploadjobpostreq();

            }
        });


        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {

                String str = message.obj.toString();
                Toast.makeText(JobPostCreateActivity.this, str+":"+getResources().getString(R.string.alertnewjobpost), Toast.LENGTH_SHORT).show();
                Intent mIntent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(mIntent);
                finish();

            }
        };


    }

    private void updateviewdetail()
    {

        jtlist = new ArrayList<>(jclist.size());
        for(int i = 0;i< jclist.size();i++) {
            jtlist.add(jclist.get(i).catname);
        }

        spinner5 = (Spinner) findViewById(R.id.editspinner1);
        ArrayAdapter<String> spinadap4 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, jtlist);
        spinadap4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner5.setAdapter(spinadap4);
        spinner5.setOnItemSelectedListener(new CustomOnItemSelectedListener());



        //fill benefits
        blist = new ArrayList<>(jbenlist.size());
        for(int i = 0;i< jbenlist.size();i++) {
            HomeActivity.BenefitCategory item = new HomeActivity.BenefitCategory();
            item.catname = jbenlist.get(i).catname;
            item.catnum = jbenlist.get(i).catnum;
            blist.add(item);
        }

        spinner6 = (Spinner) findViewById(R.id.benefit_spinner);
        splist = new ArrayList<>();
        SpinnerAdaptor.SpinnerItem headeritem = new SpinnerAdaptor.SpinnerItem();
        headeritem.setTitle(getResources().getString(R.string.title_sel_benefits));
        headeritem.setIndex("0");
        splist.add(headeritem);
        for (int i = 0; i < blist.size(); i++) {
            SpinnerAdaptor.SpinnerItem item = new SpinnerAdaptor.SpinnerItem();
            item.setTitle(blist.get(i).catname);
            item.setIndex(blist.get(i).catnum);
            item.setSelected(false);
            splist.add(item);
        }
        SpinnerAdaptor myAdapter = new SpinnerAdaptor(this, 0,splist);
        spinner6.setAdapter(myAdapter);
        spinner6.setOnItemSelectedListener(new CustomOnItemSelectedListener());

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
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
        finish();
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

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);


            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String am_pm = "";

            Calendar datetime = Calendar.getInstance();
            datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            datetime.set(Calendar.MINUTE, minute);

            if (datetime.get(Calendar.AM_PM) == Calendar.AM)
                am_pm = "AM";
            else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
                am_pm = "PM";

            // Do something with the time chosen by the user
            int inthr = datetime.get(Calendar.HOUR);
            int intmin = datetime.get(Calendar.MINUTE);

            String strhr = (inthr < 10) ? ("0"+String.valueOf(inthr)):String.valueOf(inthr);
            String strmin = (intmin < 10) ? ("0"+String.valueOf(intmin)):String.valueOf(intmin);


            textviewtime.setText(strhr+":"+strmin+" "+am_pm);
        }
    }



    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            String strday = (day < 10) ? ("0"+String.valueOf(day)):String.valueOf(day);
            String strmon = ((month+1) < 10) ? ("0"+String.valueOf(month+1)):String.valueOf(month+1);
            textviewdate.setText(strday+"/"+strmon+"/"+String.valueOf(year));

        }
    }


    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
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


    public void uploadjobpostreq()
    {

        EditText edittitle = findViewById(R.id.editText1);
        String title = edittitle.getText().toString();

        EditText edidetail = findViewById(R.id.editText2);
        String detail = edidetail.getText().toString();

        Spinner editjobtype = findViewById(R.id.editspinner1);
        String  jobtype = editjobtype.getSelectedItem().toString();
        int listindx = jtlist.indexOf(jobtype);
        String jtidx = jclist.get(listindx).catnum;

        ArrayList<String> benval = new ArrayList<>();

        for(int c = 0;c < splist.size();c++) {
            if(splist.get(c).isSelected())
            {
                HomeActivity.BenefitCategory item = new HomeActivity.BenefitCategory();
                item.catname = splist.get(c).getTitle();
                item.catnum = splist.get(c).getIndex();
                for (int i=0;i< blist.size();i++ ) {
                    if(blist.get(i).catname.equals(item.catname))
                    {
                        benval.add(blist.get(i).catnum);
                        break;
                    }
                }
            }
        }

        EditText editcount = findViewById(R.id.editText3);
        String numreq = editcount.getText().toString();

        EditText editlocation = findViewById(R.id.editText4);
        String location = editlocation.getText().toString();

        TextView editexpire = findViewById(R.id.editText5);
        String expiredon = editexpire.getText().toString();

        //Spinner editworktype = findViewById(R.id.editspinner2);
        String  worktype = String.valueOf(spinadap.getPosition(spinner1.getSelectedItem().toString()) + 1);

        //Spinner editshift = findViewById(R.id.editspinner3);
        String  jobshift = String.valueOf(spinadap1.getPosition(spinner2.getSelectedItem().toString()) + 1);

        //Spinner editOT = findViewById(R.id.editspinner4);
        String  overtime = String.valueOf(spinadap2.getPosition(spinner3.getSelectedItem().toString()) +1);

        TextView editintertime = findViewById(R.id.editText6);
        String intertime = editintertime.getText().toString();


        //Spinner editintermode = findViewById(R.id.editspinner5);
        String  intermode =  String.valueOf(spinadap3.getPosition(spinner4.getSelectedItem().toString()) +1);

        String temp = ((TextView)findViewById(R.id.textView13)).getText().toString();
        String salmin = temp.substring(4);
        temp = ((TextView)findViewById(R.id.textView14)).getText().toString();
        String salmax = temp.substring(4);


        if(title.isEmpty()  || detail.isEmpty() || numreq.isEmpty() || location.isEmpty() || worktype.isEmpty()|| expiredon.isEmpty() || intertime.isEmpty())
        {
            Toast.makeText(this, "Please enter required information!", Toast.LENGTH_SHORT).show();
            btnupload.setEnabled(true);
            return ;
        }

        JSONObject jsonObj = new JSONObject();
        try {

            jsonObj.put("uid",usrid);
            jsonObj.put("cid",compid);
            jsonObj.put("jtitle",title);
            jsonObj.put("detail",detail);
            jsonObj.put("jobtype",jtidx);
            jsonObj.put("numreq",numreq);
            jsonObj.put("jlocation",location);
            jsonObj.put("expiredon",expiredon);
            jsonObj.put("worktype",worktype);
            jsonObj.put("jobshift",jobshift);
            jsonObj.put("overtime",overtime);
            jsonObj.put("intertime",intertime);
            jsonObj.put("intermode",intermode);
            jsonObj.put("salmin",salmin);
            jsonObj.put("salmax",salmax);

            if(benval.size()> 0) {
                JSONArray objArr2 = new JSONArray();
                JSONObject objtemp1 = new JSONObject();
                for (int j = 0; j < benval.size(); j++) {
                    objArr2.put(benval.get(j));
                }

                objtemp1.put("myrows", objArr2);
                jsonObj.put("blist", objtemp1);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }



        if(NetworkChangeReceiver.isNetworkConnected(getApplicationContext())) {
            showprogressbar();
            mJobPostUpload = new JobProfileUploadTask(jsonObj);
            mJobPostUpload.execute();
        } else {
            Toast.makeText(this, getResources().getString(R.string.alert_network_disconnected), Toast.LENGTH_LONG).show();
        }

    }


    public class JobProfileUploadTask extends AsyncTask<Void, Void, String> {

        private String comid;
        private JSONObject jsonObject;
        JobProfileUploadTask(JSONObject obj) {
            jsonObject = obj;
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String r = null;
            try
            {
                // Simulate network access.
                r = RestAPI.uploadJobPost(jsonObject,getApplicationContext());
                Thread.sleep(2000);
                hideprogrssbar();
                if(r !=null)
                {
                    //uploadjobpostreq(r);

                    // And this is how you call it from the worker thread:
                    Message message = mHandler.obtainMessage(1, r.toString());
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
            mJobPostUpload = null;

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
            mJobPostUpload = null;

        }
    }

}
