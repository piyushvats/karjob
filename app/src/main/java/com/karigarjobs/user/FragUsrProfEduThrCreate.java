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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.karigarjobs.R;

import java.util.ArrayList;
import java.util.Calendar;

import static android.view.View.TEXT_ALIGNMENT_CENTER;
import static com.karigarjobs.user.UserProfileCreateActivity.objusrpro;
import static com.karigarjobs.user.UserProfileCreateActivity.usrprofileinfo;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragUsrProfEduThrCreate#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragUsrProfEduThrCreate extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    static EditText edcname,eddetail,edyear,edlocation;
    static int offsetmargin =0;
    public class EduListRes
    {
        Integer count;
        Integer name;
        Integer del;
        Integer edt;
    };
    public ArrayList<EduListRes> listresid;
    RelativeLayout relLayoutexp;
    int editpos = -1;

    private OnFragmentInteractionListener mListener;

    public FragUsrProfEduThrCreate() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragUsrProfEduThrCreate.
     */
    // TODO: Rename and change types and number of parameters
    public static FragUsrProfEduThrCreate newInstance(String param1, String param2) {
        FragUsrProfEduThrCreate fragment = new FragUsrProfEduThrCreate();
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
        View view = inflater.inflate(R.layout.fragment_frag_usr_prof_edu_create, container, false);

        edcname = view.findViewById(R.id.editText1);
        eddetail = view.findViewById(R.id.editText2);
        edyear = view.findViewById(R.id.editText3);
        edyear.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });

        edlocation = view.findViewById(R.id.editText4);
        relLayoutexp = view.findViewById(R.id.itemlist_layout);

        Button addbtn = (Button)view.findViewById(R.id.imageButton1);
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do something
                //usrprofileinfo.userexperience = new ArrayList<UserProfileCreateActivity.usr_exp>();
                UserProfileCreateActivity.usr_edu itemedu = new UserProfileCreateActivity.usr_edu();
                itemedu.eduname = edcname.getText().toString();
                itemedu.edudetail = eddetail.getText().toString();
                itemedu.edudate = edyear.getText().toString();
                itemedu.location = edlocation.getText().toString();

                add_itemin_view(itemedu);

            }
        });


        Button submitbtn = (Button)view.findViewById(R.id.imageButton2);
        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment newFragment = new FragUsrProfDocUpload();
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

        listresid = new ArrayList<EduListRes>();

        //if(usrprofileinfo.usereducaiton != null && !usrprofileinfo.usereducaiton.isEmpty())
        //    usrprofileinfo.usereducaiton.clear();

        if(usrprofileinfo.usereducaiton != null && !usrprofileinfo.usereducaiton.isEmpty())
            update_view();

        return view;
    }


    void update_view()
    {
        udpateaddlist_view();
    }

    void add_itemin_view(UserProfileCreateActivity.usr_edu item)
    {

        if(!check_values(item))
        {
            Toast.makeText(objusrpro.getApplicationContext(),objusrpro.getResources().getString(R.string.err_fill_emptyfield),Toast.LENGTH_LONG).show();
            return;
        }

        if(usrprofileinfo.usereducaiton == null || usrprofileinfo.usereducaiton.isEmpty())
            usrprofileinfo.usereducaiton = new ArrayList<UserProfileCreateActivity.usr_edu>();

        //add in list
        if(editpos == -1) {
            usrprofileinfo.usereducaiton.add(item);
        } else {
            usrprofileinfo.usereducaiton.remove(editpos);
            usrprofileinfo.usereducaiton.add(item);
            editpos = -1;
        }

        udpateaddlist_view();
        //update in view

        clear_view();
    }

    void clear_view()
    {
        edcname.setText("");
        eddetail.setText("");
        edyear.setText("");
        edlocation.setText("");
    }

    void udpateaddlist_view()
    {
        int countid = 200,nameid=300,delid=400;
        int listlen = usrprofileinfo.usereducaiton.size();

        removeallitem_view();

        for(int count=0;count< listlen;count++)
        {
            offsetmargin = 100*count;
            UserProfileCreateActivity.usr_edu item =  usrprofileinfo.usereducaiton.get(count);

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
            jtitle.setText(item.eduname);
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

            EduListRes itemres = new EduListRes();
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
                    usrprofileinfo.usereducaiton.remove(j);
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
                    UserProfileCreateActivity.usr_edu item = usrprofileinfo.usereducaiton.get(j);
                    edcname.setText(item.eduname);
                    eddetail.setText(item.edudetail);
                    edyear.setText(item.edudate);
                    edlocation.setText(item.location);
                    editpos = j;
                    break;
                }
            }
        }
    }



    boolean check_values(UserProfileCreateActivity.usr_edu item)
    {
        boolean ret = true;
        if(item.eduname == null || item.eduname.isEmpty())
            ret = false;

        if(item.location == null || item.location.isEmpty())
            ret = false;

        if(item.edudetail == null || item.edudetail.isEmpty())
            ret = false;

        if(item.edudate == null || item.edudate.isEmpty() )
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
            edyear.setText(String.valueOf(year));
        }
    }


    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }


}
