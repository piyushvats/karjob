package com.karigarjobs.user;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.karigarjobs.AllUsrProvInfo;
import com.karigarjobs.R;

import java.util.ArrayList;
import java.util.Calendar;

import static android.view.View.TEXT_ALIGNMENT_CENTER;
import static com.karigarjobs.user.UserProfileCreateActivity.usrjclist;
import static com.karigarjobs.user.UserProfileCreateActivity.usrprofileinfo;

//import static com.karigarjobs.user.HomeActivityU.jclist;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragUsrProfOneCreate#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragUsrProfOneCreate extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;



    static TextView textviewtime;
    static TextView dobdate;
    EditText edname,edcuaddr,edcupinc,edperaddr,edperpinc,edmob,edalmob,edidnum,edemail,edcusal;
    Spinner spinner1,spinner2,spinner3,spinner5;

    ArrayAdapter<CharSequence> spinadap,spinadap1;
    RelativeLayout relLayoutexp;

    ArrayList<String> jtlist = null;
    boolean isInitSpinnerListener  = false;
    int offsetmargin =0;
    public class IndusListRes
    {
        Integer count;
        Integer name;
        Integer del;
    };

    public ArrayList<IndusListRes> listresid;

    public FragUsrProfOneCreate() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragUsrProfOneCreate.
     */
    // TODO: Rename and change types and number of parameters
    public static FragUsrProfOneCreate newInstance(String param1, String param2) {
        FragUsrProfOneCreate fragment = new FragUsrProfOneCreate();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_frag_usr_prof_one_create, container, false);

        relLayoutexp = view.findViewById(R.id.itemlist_layout);

        edname = view.findViewById(R.id.editText1);
        dobdate = view.findViewById(R.id.editText2);
        dobdate.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v) {
               showDateDialog(v);
           }
        });



        spinner1 = (Spinner)view.findViewById(R.id.editspinner1);
        spinadap = ArrayAdapter.createFromResource(getActivity(),R.array.sex_type, android.R.layout.simple_spinner_item);
        spinadap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(spinadap);
        spinner1.setOnItemSelectedListener(new CustomOnItemSelectedListener());

        edcuaddr = view.findViewById(R.id.editText3);
        edcupinc = view.findViewById(R.id.editText4);

        //edperaddr = view.findViewById(R.id.editText5);
        //edperpinc = view.findViewById(R.id.editText6);

        edmob = view.findViewById(R.id.editText7);
        //edalmob = view.findViewById(R.id.editText8);

        spinner2 = (Spinner)view.findViewById(R.id.editspinner2);
        spinadap1 = ArrayAdapter.createFromResource(getActivity(),R.array.id_type, android.R.layout.simple_spinner_item);
        spinadap1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(spinadap1);
        spinner2.setOnItemSelectedListener(new CustomOnItemSelectedListener());

        edidnum = view.findViewById(R.id.editText9);
        //edemail = view.findViewById(R.id.editText10);
        edcusal = view.findViewById(R.id.editText11);

        jtlist = new ArrayList<>(usrjclist.size());
        for (int i = 0; i < usrjclist.size(); i++) {
            jtlist.add(usrjclist.get(i).catname);
        }
        isInitSpinnerListener  = false;

        final Spinner spinner5 = (Spinner) view.findViewById(R.id.editspinner3);
        ArrayAdapter<String> spinadap3 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, jtlist);
        spinadap3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner5.setAdapter(spinadap3);
        spinner5.setOnItemSelectedListener(new CustomOnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if(isInitSpinnerListener) {
                    String val = parent.getItemAtPosition(pos).toString();
                    AllUsrProvInfo.JobCategory it = new AllUsrProvInfo.JobCategory();
                    it.catname = val;
                    it.catnum = usrjclist.get(pos).catnum;
                    add_itemin_view(it);
                }
                else
                    isInitSpinnerListener = true;
            }
        });


        Button submitbtn = (Button)view.findViewById(R.id.imageButton2);
        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do something
                if(usrprofileinfo == null)
                    usrprofileinfo = new UserProfileCreateActivity.UserProfile();

                usrprofileinfo.name = edname.getText().toString();
                usrprofileinfo.age =  dobdate.getText().toString();
                usrprofileinfo.sex =  String.valueOf(spinadap.getPosition(spinner1.getSelectedItem().toString()) + 1);
                usrprofileinfo.caddr = edcuaddr.getText().toString();
                usrprofileinfo.caddrpinc = edcupinc.getText().toString();
                usrprofileinfo.paddr = edcuaddr.getText().toString();//edperaddr.getText().toString();
                usrprofileinfo.paddrpinc = edcupinc.getText().toString();//edperpinc.getText().toString();
                usrprofileinfo.mobile = edmob.getText().toString();
                usrprofileinfo.altmobile = "";//edalmob.getText().toString();
                usrprofileinfo.idtype = String.valueOf(spinadap1.getPosition(spinner2.getSelectedItem().toString()) + 1);
                usrprofileinfo.idtypenum = edidnum.getText().toString();
                usrprofileinfo.email = "";//edemail.getText().toString();
                usrprofileinfo.csalary = edcusal.getText().toString();
                //usrprofileinfo.industype = usrjclist.get(spinner5.getSelectedItemPosition()).catnum;



                if(check_all())
                {
                    //launch next fragment
                    createfragment_secondpage();
                }
                else
                {
                    Toast.makeText(getActivity().getApplicationContext(),getResources().getString(R.string.err_req_field_empty),Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        listresid = new ArrayList<>();

        //update all items if  data available in usr profile
        update_view();

        //if(usrprofileinfo != null && usrprofileinfo.userindustype != null && !usrprofileinfo.userindustype.isEmpty())
        //    usrprofileinfo.userindustype.clear();


        return view;
    }

    void update_view()
    {
        if(usrprofileinfo != null) {

            edname.setText(usrprofileinfo.name);
            dobdate.setText(usrprofileinfo.age);

            //ArrayAdapter<CharSequence> spinadap = ArrayAdapter.createFromResource(getActivity(),R.array.sex_type, android.R.layout.simple_spinner_item);
            spinner1.setSelection(Integer.parseInt(usrprofileinfo.sex)-1);

            edcuaddr.setText(usrprofileinfo.caddr);
            edcupinc.setText(usrprofileinfo.caddrpinc);
            //edperaddr.setText(usrprofileinfo.paddr);
            //edperpinc.setText(usrprofileinfo.paddrpinc);
            edmob.setText(usrprofileinfo.mobile);
            //edalmob.setText(usrprofileinfo.altmobile);

            //ArrayAdapter<CharSequence> spinadap1 = ArrayAdapter.createFromResource(getActivity(),R.array.id_type, android.R.layout.simple_spinner_item);
            spinner2.setSelection(Integer.parseInt(usrprofileinfo.idtype)-1);

            edidnum.setText(usrprofileinfo.idtypenum);
            //edemail.setText(usrprofileinfo.email);
            edcusal.setText(usrprofileinfo.csalary);

            //update industry type list
            if(usrprofileinfo.userindustype != null && !usrprofileinfo.userindustype.isEmpty())
                udpateaddlist_view();
        }

    }

    void add_itemin_view(AllUsrProvInfo.JobCategory item)
    {
        if(usrprofileinfo == null)
            usrprofileinfo = new UserProfileCreateActivity.UserProfile();

        if(usrprofileinfo.userindustype == null || usrprofileinfo.userindustype.isEmpty())
            usrprofileinfo.userindustype = new ArrayList<>();

        //add in list
        if(usrprofileinfo.userindustype.size() < 7)   // only 3 type domain allowed for apply jobs
        {
            if(isalreadyadd(item)) {
                usrprofileinfo.userindustype.add(item);
                udpateaddlist_view();
            }
            else
                Toast.makeText(getActivity(),R.string.alert_dup_jobcat,Toast.LENGTH_LONG).show();

        }
        else
            Toast.makeText(getActivity(),R.string.alert_max3_comp,Toast.LENGTH_LONG).show();

    }

    boolean isalreadyadd(AllUsrProvInfo.JobCategory item)
    {
        boolean ret = true;
        for(int i=0;i<usrprofileinfo.userindustype.size();i++ )
        {
            if(usrprofileinfo.userindustype.get(i).catnum == item.catnum)
            {
                ret = false;
                break;
            }
        }
        return ret;
    }

    void udpateaddlist_view()
    {
        int countid = 200,nameid=300,delid=400;
        int listlen = usrprofileinfo.userindustype.size();

        removeallitem_view();

        for(int count=0;count< listlen;count++)
        {
            offsetmargin = 100*count;
            AllUsrProvInfo.JobCategory item =  usrprofileinfo.userindustype.get(count);

            TextView srlno = new TextView(getActivity().getApplicationContext());
            TextView jtitle = new TextView(getActivity().getApplicationContext());
            ImageButton expdel = new ImageButton(getActivity().getApplicationContext());


            RelativeLayout.LayoutParams par = new RelativeLayout.LayoutParams(20,50);
            par.width = 20;
            par.height = 50;
            par.setMarginStart(30);
            par.topMargin = offsetmargin;


            srlno.setId(countid+offsetmargin);
            srlno.setLayoutParams(par);
            String strv = String.valueOf(count+1)+". ";
            srlno.setText(strv);
            srlno.setTextAlignment(TEXT_ALIGNMENT_CENTER);
            srlno.setTextColor(Color.BLACK);

            RelativeLayout.LayoutParams par1 = new RelativeLayout.LayoutParams(600,100);

            par1.width = 600;
            par1.height = 100;
            par1.setMarginStart(35);
            par1.topMargin = offsetmargin;
            par1.leftMargin = 20;


            jtitle.setId(nameid+offsetmargin);
            jtitle.setLayoutParams(par1);
            jtitle.setText(item.catname);
            jtitle.setTextAlignment(TEXT_ALIGNMENT_CENTER);
            jtitle.setTextColor(Color.BLACK);

            RelativeLayout.LayoutParams par2 = new RelativeLayout.LayoutParams(100,100);


            par2.width = 100;
            par2.height = 100;
            par2.setMarginStart(600);
            par2.topMargin = offsetmargin;

            expdel.setLayoutParams(par2);
            expdel.setId(delid+offsetmargin);
            expdel.setAdjustViewBounds(true);
            expdel.setMaxWidth(50);
            expdel.setMaxHeight(50);
            expdel.setScaleType(ImageView.ScaleType.FIT_CENTER);
            expdel.setImageResource(R.mipmap.del_icon);
            expdel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = v.getId();
                    deleteitem_view(id);

                }
            });

            relLayoutexp.addView(srlno);
            relLayoutexp.addView(jtitle);
            relLayoutexp.addView(expdel);

            IndusListRes itemres = new IndusListRes();
            itemres.count = srlno.getId();
            itemres.name = jtitle.getId();
            itemres.del = expdel.getId();

            listresid.add(itemres);

        }

    }

    void removeallitem_view()
    {
        int len = listresid.size();
        for(int i=0;i<len;i++)
        {
            relLayoutexp.removeView(relLayoutexp.findViewById(listresid.get(0).count));
            relLayoutexp.removeView(relLayoutexp.findViewById(listresid.get(0).name));
            relLayoutexp.removeView(relLayoutexp.findViewById(listresid.get(0).del));

            listresid.remove(0);
        }

    }

    void deleteitem_view(int position)
    {
        if(position > -1)
        {
            for(int j=0;j<listresid.size();j++) {
                if(listresid.get(j).del.equals(position)) {
                    usrprofileinfo.userindustype.remove(j);
                    udpateaddlist_view();
                    break;
                }
            }
        }
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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



    public boolean check_all()
    {
        boolean ret = true;

        if(usrprofileinfo.name == null || usrprofileinfo.name.isEmpty())
            ret = false;

        if(usrprofileinfo.age == null || usrprofileinfo.age.isEmpty() || !usrprofileinfo.age.matches("^[0-3]?[0-9]/[0-1]?[0-9]/(?:[0-9]{2})?[0-9]{2}$"))
            ret = false;

        if(usrprofileinfo.sex == null || usrprofileinfo.sex.isEmpty())
            ret = false;

        if(usrprofileinfo.caddr == null || usrprofileinfo.caddr.isEmpty())
            ret = false;

        if(usrprofileinfo.caddrpinc == null || usrprofileinfo.caddrpinc.isEmpty() || !usrprofileinfo.caddrpinc.matches("\\d{6}"))
            ret = false;

        if(usrprofileinfo.paddr == null || usrprofileinfo.paddr.isEmpty())
            ret = false;

        if(usrprofileinfo.paddrpinc == null || usrprofileinfo.paddrpinc.isEmpty() || !usrprofileinfo.paddrpinc.matches("\\d{6}"))
            ret = false;

        if(usrprofileinfo.userindustype == null || usrprofileinfo.userindustype.isEmpty())
            ret = false;

        return ret ;

    }


    private void createfragment_secondpage() {


        Fragment newFragment = new FragUsrProfExpSecCreate();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack

        //transaction.add(R.id.frag_stageone, newFragment);
        transaction.replace(R.id.frag_stageone, newFragment,"stagetwo");
        transaction.addToBackStack(null);

// Commit the transaction
        transaction.commit();


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
            // Do something with the time chosen by the user
            textviewtime.setText(String.valueOf(hourOfDay)+":"+String.valueOf(minute));
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
            DatePickerDialog obj = new DatePickerDialog(getActivity(), this, year, month, day);

            int totalminday = 365 * 80;  // 80 year maximum for work
            c.add(Calendar.DATE, -totalminday);
            obj.getDatePicker().setMinDate(c.getTimeInMillis());
            c.add(Calendar.DATE, totalminday);
            obj.getDatePicker().setMaxDate(c.getTimeInMillis());


            return obj;
        }


        public void onDateSet(DatePicker view, int year, int month, int day) {

            // Do something with the date chosen by the user
            Calendar c = Calendar.getInstance();
            int y = c.get(Calendar.YEAR);
            if(y > 0 && year > 0 && ((y - year) >= 18) ) {
                String strday = (day < 10) ? ("0"+String.valueOf(day)):String.valueOf(day);
                String strmon = ((month+1) < 10) ? ("0"+String.valueOf(month+1)):String.valueOf(month+1);
                dobdate.setText(strday + "/" + strmon + "/" + String.valueOf(year));
            }
            else
                Toast.makeText(getActivity(),R.string.alert_add_valid_dob,Toast.LENGTH_LONG).show();


        }
    }


    public void showDateDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }
}
