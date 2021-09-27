package com.karigarjobs.user.cvrecord;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.karigarjobs.R;
import com.karigarjobs.utils.PlayRecordAct;

import java.io.File;

import static com.karigarjobs.user.cvrecord.UserProfileCreateVoice.mediasuffix;
import static com.karigarjobs.user.cvrecord.UserProfileCreateVoice.objcvvoice;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragCvRecordSelfInfo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragCvRecordSelfInfo extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private OnFragmentInteractionListener mListener;
    private View curview;

    public FragCvRecordSelfInfo() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragCvRecordSelfInfo.
     */
    // TODO: Rename and change types and number of parameters
    public static FragCvRecordSelfInfo newInstance(String param1, String param2) {
        FragCvRecordSelfInfo fragment = new FragCvRecordSelfInfo();
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
        View view = inflater.inflate(R.layout.fragment_frag_cv_record_self_info, container, false);
        curview = view;
        Button btn1 = (Button)view.findViewById(R.id.q_button1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startplayrecordactvity(R.raw.q1,101,getResources().getString(R.string.title_whatisname));
            }
        });
        if(checkfileavial(String.valueOf(101) + mediasuffix)) {
            btn1.setBackgroundResource(R.drawable.mybuttongrn);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                btn1.setBackgroundTintList(objcvvoice.getResources().getColorStateList(R.color.colorAccept));
            }
        }

        Button btn2 = (Button)view.findViewById(R.id.q_button2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startplayrecordactvity(R.raw.q2,102,getResources().getString(R.string.title_whatisdob));
            }
        });
        if(checkfileavial(String.valueOf(102) + mediasuffix)) {
            btn2.setBackgroundResource(R.drawable.mybuttongrn);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                btn2.setBackgroundTintList(objcvvoice.getResources().getColorStateList(R.color.colorAccept));
            }
        }

        Button btn3 = (Button)view.findViewById(R.id.q_button3);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startplayrecordactvity(R.raw.q3,103,getResources().getString(R.string.title_whatisaddr));
            }
        });
        if(checkfileavial(String.valueOf(103) + mediasuffix)) {
            btn3.setBackgroundResource(R.drawable.mybuttongrn);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                btn3.setBackgroundTintList(objcvvoice.getResources().getColorStateList(R.color.colorAccept));
            }
        }

        Button btn4 = (Button)view.findViewById(R.id.q_button4);
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startplayrecordactvity(R.raw.q4,104,getResources().getString(R.string.title_whatismob));
            }
        });
        if(checkfileavial(String.valueOf(104) + mediasuffix)) {
            btn4.setBackgroundResource(R.drawable.mybuttongrn);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                btn4.setBackgroundTintList(objcvvoice.getResources().getColorStateList(R.color.colorAccept));
            }
        }

        Button btn5 = (Button)view.findViewById(R.id.q_button5);
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startplayrecordactvity(R.raw.q5,105,getResources().getString(R.string.title_doyouhaveid));
            }
        });
        if(checkfileavial(String.valueOf(105) + mediasuffix)) {
            btn5.setBackgroundResource(R.drawable.mybuttongrn);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                btn5.setBackgroundTintList(objcvvoice.getResources().getColorStateList(R.color.colorAccept));
            }
        }

        Button btn6 = (Button)view.findViewById(R.id.q_button6);
        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startplayrecordactvity(R.raw.q6,106,getResources().getString(R.string.title_typeofworksrch));
            }
        });
        if(checkfileavial(String.valueOf(106) + mediasuffix)) {
            btn6.setBackgroundResource(R.drawable.mybuttongrn);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                btn6.setBackgroundTintList(objcvvoice.getResources().getColorStateList(R.color.colorAccept));
            }
        }

        Button btn7 = (Button)view.findViewById(R.id.q_button7);
        btn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startplayrecordactvity(R.raw.q16,107,getResources().getString(R.string.title_canenglish));
            }
        });
        if(checkfileavial(String.valueOf(107) + mediasuffix)) {
            btn7.setBackgroundResource(R.drawable.mybuttongrn);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                btn7.setBackgroundTintList(objcvvoice.getResources().getColorStateList(R.color.colorAccept));
            }
        }


        Button btn8 = (Button)view.findViewById(R.id.q_button8);
        btn8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startplayrecordactvity(R.raw.q17,108,getResources().getString(R.string.title_eduinfo));
            }
        });
        if(checkfileavial(String.valueOf(108) + mediasuffix)) {
            btn8.setBackgroundResource(R.drawable.mybuttongrn);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                btn8.setBackgroundTintList(objcvvoice.getResources().getColorStateList(R.color.colorAccept));
            }
        }

        Button btn9 = (Button)view.findViewById(R.id.q_button9);
        btn9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startplayrecordactvity(R.raw.q18,109,getResources().getString(R.string.title_expinfo));
            }
        });
        if(checkfileavial(String.valueOf(109) + mediasuffix)) {
            btn9.setBackgroundResource(R.drawable.mybuttongrn);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                btn9.setBackgroundTintList(objcvvoice.getResources().getColorStateList(R.color.colorAccept));
            }
        }



        Button nextfrag = (Button)view.findViewById(R.id.next_button);
        nextfrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Fragment newFragment = new FragCvRecordExpInfo();
                Fragment newFragment = new FragCvRecordUpload();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack
                //transaction.add(R.id.frag_stageone, newFragment);
                //transaction.replace(R.id.frag_stageone, newFragment,"stagetwo");
                transaction.replace(R.id.frag_stageone, newFragment,"stagefour");
                transaction.addToBackStack(null);

// Commit the transaction
                transaction.commit();
            }
        });


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Button btn1 = (Button)curview.findViewById(R.id.q_button1);
        if(checkfileavial(String.valueOf(101) + mediasuffix)) {
            btn1.setBackgroundResource(R.drawable.mybuttongrn);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                btn1.setBackgroundTintList(objcvvoice.getResources().getColorStateList(R.color.colorAccept));
            }
        }

        Button btn2 = (Button)curview.findViewById(R.id.q_button2);
        if(checkfileavial(String.valueOf(102) + mediasuffix)) {
            btn2.setBackgroundResource(R.drawable.mybuttongrn);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                btn2.setBackgroundTintList(objcvvoice.getResources().getColorStateList(R.color.colorAccept));
            }
        }

        Button btn3 = (Button)curview.findViewById(R.id.q_button3);
        if(checkfileavial(String.valueOf(103) + mediasuffix)) {
            btn3.setBackgroundResource(R.drawable.mybuttongrn);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                btn3.setBackgroundTintList(objcvvoice.getResources().getColorStateList(R.color.colorAccept));
            }
        }

        Button btn4 = (Button)curview.findViewById(R.id.q_button4);
        if(checkfileavial(String.valueOf(104) + mediasuffix)) {
            btn4.setBackgroundResource(R.drawable.mybuttongrn);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                btn4.setBackgroundTintList(objcvvoice.getResources().getColorStateList(R.color.colorAccept));
            }
        }

        Button btn5 = (Button)curview.findViewById(R.id.q_button5);
        if(checkfileavial(String.valueOf(105) + mediasuffix)) {
            btn5.setBackgroundResource(R.drawable.mybuttongrn);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                btn5.setBackgroundTintList(objcvvoice.getResources().getColorStateList(R.color.colorAccept));
            }
        }

        Button btn6 = (Button)curview.findViewById(R.id.q_button6);
        if(checkfileavial(String.valueOf(106) + mediasuffix)) {
            btn6.setBackgroundResource(R.drawable.mybuttongrn);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                btn6.setBackgroundTintList(objcvvoice.getResources().getColorStateList(R.color.colorAccept));
            }
        }

        Button btn7 = (Button)curview.findViewById(R.id.q_button7);
        if(checkfileavial(String.valueOf(107) + mediasuffix)) {
            btn7.setBackgroundResource(R.drawable.mybuttongrn);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                btn7.setBackgroundTintList(objcvvoice.getResources().getColorStateList(R.color.colorAccept));
            }
        }


        Button btn8 = (Button)curview.findViewById(R.id.q_button8);
        if(checkfileavial(String.valueOf(108) + mediasuffix)) {
            btn8.setBackgroundResource(R.drawable.mybuttongrn);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                btn8.setBackgroundTintList(objcvvoice.getResources().getColorStateList(R.color.colorAccept));
            }
        }

        Button btn9 = (Button)curview.findViewById(R.id.q_button9);
        if(checkfileavial(String.valueOf(109) + mediasuffix)) {
            btn9.setBackgroundResource(R.drawable.mybuttongrn);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                btn9.setBackgroundTintList(objcvvoice.getResources().getColorStateList(R.color.colorAccept));
            }
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

    public void startplayrecordactvity(int idx,int num,String val)
    {
        Intent mIntent = new Intent(objcvvoice.getApplicationContext(), PlayRecordAct.class);
        Bundle b = new Bundle();
        String filep = String.valueOf(idx);
        String destfile = objcvvoice.recfilePath;
        b.putString("str",val); //text string for question
        b.putString("file",filep); //audio file path for play question
        b.putString("filenum",String.valueOf(num)); //audio file path for play question
        b.putString("desfile",destfile); //audio file path for save record
        b.putString("uid", UserProfileCreateVoice.usrloginid); //Your id
        mIntent.putExtras(b);
        startActivity(mIntent);

    }

    boolean checkfileavial(String filename)
    {

        File fl = new File(objcvvoice.recfilePath+filename);
        if(fl.exists()) {
            int file_size = Integer.parseInt(String.valueOf(fl.length() / 1024));
            if (file_size > 0)
                return true;
            else
                return false;
        }
        else
            return false;
    }
}
