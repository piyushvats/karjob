package com.karigarjobs.user;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.Toast;

import com.karigarjobs.R;

import java.util.ArrayList;
import java.util.Calendar;

import static android.view.View.TEXT_ALIGNMENT_CENTER;
import static com.karigarjobs.user.UserProfileCreateActivity.objusrpro;
import static com.karigarjobs.user.UserProfileCreateActivity.usrjclist;
import static com.karigarjobs.user.UserProfileCreateActivity.usrprofileinfo;

//import static com.karigarjobs.user.HomeActivityU.jclist;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragUsrProfExpSecCreate#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragUsrProfExpSecCreate extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters

    static TextView edstart,edend;
    EditText edcname,edjtitle,edjdetail,edlocation;
    Spinner edjcategory;
    RelativeLayout relLayoutexp;

    Button addbtn;
    int editpos = -1;
    ArrayList<String> jtlist = null;

    int offsetmargin =0;
    public class ExpListRes
    {
        Integer count;
        Integer name;
        Integer del;
        Integer edt;
    };

    public ArrayList<ExpListRes> listresid;

    private OnFragmentInteractionListener mListener;

    public FragUsrProfExpSecCreate() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragUsrProfExpSecCreate.
     */
    // TODO: Rename and change types and number of parameters
    public static FragUsrProfExpSecCreate newInstance(String param1, String param2) {
        FragUsrProfExpSecCreate fragment = new FragUsrProfExpSecCreate();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_frag_usr_prof_exp_create, container, false);

        edcname  = view.findViewById(R.id.editText1);
        edjtitle = view.findViewById(R.id.editText3);
        edjdetail = view.findViewById(R.id.editText4);
        edlocation = view.findViewById(R.id.editText6);
        edstart = view.findViewById(R.id.editText2);

        edstart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v,0);
            }
        });

        edend = view.findViewById(R.id.editText5);
        edend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v,1);
            }
        });




        relLayoutexp = view.findViewById(R.id.itemlist_layout);
        jtlist = new ArrayList<>(usrjclist.size());
        for (int i = 0; i < usrjclist.size(); i++) {
            jtlist.add(usrjclist.get(i).catname);
        }

        edjcategory = (Spinner) view.findViewById(R.id.editspinner1);
        ArrayAdapter<String> spinadap1 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, jtlist);
        spinadap1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        edjcategory.setAdapter(spinadap1);
        edjcategory.setOnItemSelectedListener(new CustomOnItemSelectedListener());

        addbtn = (Button)view.findViewById(R.id.imageButton1);
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do something
                //usrprofileinfo.userexperience = new ArrayList<UserProfileCreateActivity.usr_exp>();
                UserProfileCreateActivity.usr_exp itemexp = new UserProfileCreateActivity.usr_exp();
                itemexp.cname = edcname.getText().toString();
                itemexp.jobtitle = edjtitle.getText().toString();
                itemexp.jobdetail = edjdetail.getText().toString();
                itemexp.catid = usrjclist.get(edjcategory.getSelectedItemPosition()).catnum;
                itemexp.jstartdate = edstart.getText().toString();
                itemexp.jenddate = edend.getText().toString();
                itemexp.location = edlocation.getText().toString();

                add_itemin_view(itemexp);

            }
        });


        Button submitbtn = (Button)view.findViewById(R.id.imageButton2);
        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do something

                Fragment newFragment = new FragUsrProfEduThrCreate();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack
                //transaction.add(R.id.frag_stageone, newFragment);
                transaction.replace(R.id.frag_stageone, newFragment,"stagethree");
                transaction.addToBackStack(null);

// Commit the transaction
                transaction.commit();


            }
        });

        listresid = new ArrayList<ExpListRes>();


        //if(usrprofileinfo.userexperience != null && !usrprofileinfo.userexperience.isEmpty())
        //    usrprofileinfo.userexperience.clear();
        if(usrprofileinfo.userexperience != null && !usrprofileinfo.userexperience.isEmpty())
            udpateaddlist_view();

        return view;


    }

    void add_itemin_view(UserProfileCreateActivity.usr_exp item)
    {

        if(!check_values(item))
        {
            Toast.makeText(objusrpro.getApplicationContext(),objusrpro.getResources().getString(R.string.err_fill_emptyfield),Toast.LENGTH_LONG).show();
            return;
        }

        if(usrprofileinfo.userexperience == null || usrprofileinfo.userexperience.isEmpty())
            usrprofileinfo.userexperience = new ArrayList<UserProfileCreateActivity.usr_exp>();

        //add in list
        if(editpos == -1) {
            usrprofileinfo.userexperience.add(item);
        } else {
            usrprofileinfo.userexperience.remove(editpos);
            usrprofileinfo.userexperience.add(item);
            editpos = -1;
        }
        udpateaddlist_view();
        //update in view

        clear_view();
    }

    void clear_view()
    {
        edcname.setText("");
        edjtitle.setText("");
        edjdetail.setText("");
        edjcategory.setSelection(0);
        edstart.setText("");
        edend.setText("");
        edlocation.setText("");
    }

    void udpateaddlist_view()
    {
        int countid = 200,nameid=300,delid=400;
        int listlen = usrprofileinfo.userexperience.size();

        removeallitem_view();

        for(int count=0;count< listlen;count++)
        {
            offsetmargin =  100*count;
            UserProfileCreateActivity.usr_exp item =  usrprofileinfo.userexperience.get(count);

            //RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(20,50);
            TextView srlno = new TextView(getActivity().getApplicationContext());
            TextView jtitle = new TextView(getActivity().getApplicationContext());
            ImageButton expdel = new ImageButton(getActivity().getApplicationContext());
            ImageButton expedit = new ImageButton(getActivity().getApplicationContext());

            RelativeLayout.LayoutParams par = new RelativeLayout.LayoutParams(20,50);
            par.width = 20;
            par.height = 50;
            par.setMarginStart(5);
            par.topMargin = offsetmargin;


            srlno.setId(countid+offsetmargin);
            srlno.setLayoutParams(par);
            srlno.setText(String.valueOf(count+1)+". ");
            srlno.setTextAlignment(TEXT_ALIGNMENT_CENTER);
            srlno.setTextColor(Color.BLACK);

            RelativeLayout.LayoutParams par1 = new RelativeLayout.LayoutParams(600,100);

            par1.width = 600;
            par1.height = 100;
            par1.setMarginStart(10);
            par1.topMargin = offsetmargin;
            par1.leftMargin = 20;


            jtitle.setId(nameid+offsetmargin);
            jtitle.setLayoutParams(par1);
            jtitle.setText(item.cname);
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

            RelativeLayout.LayoutParams par3 = new RelativeLayout.LayoutParams(100,100);

            par3.width = 100;
            par3.height = 100;
            par3.setMarginStart(500);
            par3.topMargin = offsetmargin;

            expedit.setLayoutParams(par3);
            expedit.setId(delid+offsetmargin);
            expedit.setAdjustViewBounds(true);
            expedit.setMaxWidth(50);
            expedit.setMaxHeight(50);
            expedit.setScaleType(ImageView.ScaleType.FIT_CENTER);
            expedit.setImageResource(R.mipmap.edit_icon);
            expedit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = v.getId();
                    edititem_view(id);

                }
            });

            relLayoutexp.addView(srlno);
            relLayoutexp.addView(jtitle);
            relLayoutexp.addView(expdel);
            relLayoutexp.addView(expedit);

            ExpListRes itemres = new ExpListRes();
            itemres.count = srlno.getId();
            itemres.name = jtitle.getId();
            itemres.del = expdel.getId();
            itemres.edt = expdel.getId();

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
            relLayoutexp.removeView(relLayoutexp.findViewById(listresid.get(0).edt));

            listresid.remove(0);
        }

    }

    void deleteitem_view(int position)
    {
        if(position > -1)
        {
            for(int j=0;j<listresid.size();j++) {
                if(listresid.get(j).del.equals(position)) {
                    usrprofileinfo.userexperience.remove(j);
                    udpateaddlist_view();
                    break;
                }
            }
        }
    }

    void edititem_view(int position)
    {
        if(position > -1)
        {
            for(int j=0;j<listresid.size();j++) {
                if(listresid.get(j).edt.equals(position)) {
                    UserProfileCreateActivity.usr_exp item = usrprofileinfo.userexperience.get(j);
                    edcname.setText(item.cname);
                    edjtitle.setText(item.jobtitle);
                    edjdetail.setText(item.jobdetail);
                    edlocation.setText(item.location);
                    edstart.setText(item.jstartdate);
                    edend.setText(item.jenddate);
                    edjcategory.setSelection(Integer.parseInt(item.catid));
                    editpos = j;
                    break;
                }
            }
        }
    }


    boolean check_values(UserProfileCreateActivity.usr_exp item)
    {
        boolean ret = true;
        if(item.cname == null || item.cname.isEmpty())
            ret = false;

        if(item.location == null || item.location.isEmpty())
            ret = false;

        if(item.jstartdate == null || item.jstartdate.isEmpty() || !item.jstartdate.matches("^[0-3]?[0-9]/[0-1]?[0-9]/(?:[0-9]{2})?[0-9]{2}$"))
            ret = false;

        if(item.jenddate == null || item.jenddate.isEmpty() || !item.jenddate.matches("^[0-3]?[0-9]/[0-1]?[0-9]/(?:[0-9]{2})?[0-9]{2}$"))
            ret = false;

        if(item.catid == null || item.catid.isEmpty())
            ret = false;

        if(item.jobtitle == null || item.jobtitle.isEmpty())
            ret = false;

        if(item.jobdetail == null || item.jobdetail.isEmpty())
            ret = false;

        return ret;
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


    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        int value = 0;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog obj = new DatePickerDialog(getActivity(), this, year, month, day);

            int totalminday = 365 * 40;  // 80 year maximum for work
            c.add(Calendar.DATE, -totalminday);
            obj.getDatePicker().setMinDate(c.getTimeInMillis());
            c.add(Calendar.DATE, totalminday);
            obj.getDatePicker().setMaxDate(c.getTimeInMillis());

            // Create a new instance of DatePickerDialog and return it
            return obj;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            String strday = (day < 10) ? ("0"+String.valueOf(day)):String.valueOf(day);
            String strmon = ((month+1) < 10) ? ("0"+String.valueOf(month+1)):String.valueOf(month+1);
            String dtstr= strday + "/" + strmon + "/" + String.valueOf(year);

            if(value==0)
            {
                edstart.setText(dtstr);
            }
            else if(value==1)
            {
                edend.setText(dtstr);
            }

        }
    }


    public void showDatePickerDialog(View v,int val) {
        DialogFragment newFragment = new DatePickerFragment();
        ((DatePickerFragment) newFragment).value = val;
        newFragment.show(getFragmentManager(), "datePicker");
    }


}
