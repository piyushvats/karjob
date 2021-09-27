package com.karigarjobs.user;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.karigarjobs.R;
import com.karigarjobs.Session;
import com.karigarjobs.utils.CopyFiles;
import com.karigarjobs.utils.ImageCache;
import com.karigarjobs.utils.NetworkChangeReceiver;
import com.karigarjobs.utils.RestAPI;
import com.karigarjobs.utils.VolleyMultiPart;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

import static com.karigarjobs.user.UserProfileCreateActivity.objusrpro;
import static com.karigarjobs.user.UserProfileCreateActivity.usrprofid;
import static com.karigarjobs.user.cvrecord.UserProfileCreateVoice.objcvvoice;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragUsrProfDocUpload#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragUsrProfDocUpload extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    UserProStoreTask mUsrProfile = null;
    UserProUpdateTask mUsrProfileUpdate = null;

    ProgressDialog progress;
    public static String outdirpath = "";
    static Context cxt;

    public FragUsrProfDocUpload() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragUsrProfDocUpload.
     */
    // TODO: Rename and change types and number of parameters
    public static FragUsrProfDocUpload newInstance(String param1, String param2) {
        FragUsrProfDocUpload fragment = new FragUsrProfDocUpload();
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

    public static TextView filename1,filename2,filename3;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_frag_usr_prof_doc_upload, container, false);


        filename1 = view.findViewById(R.id.textView_edu);
        Button btnseleduc = (Button)view.findViewById(R.id.btnedu1);
        btnseleduc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opendir("edu");

            }
        });

        filename2 = view.findViewById(R.id.textView_id);
        Button btnselid = (Button)view.findViewById(R.id.btnaddr);
        btnselid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opendir("iddoc");
            }
        });

        filename3 = view.findViewById(R.id.textView_self);
        Button btnselself = (Button)view.findViewById(R.id.btnself);
        btnselself.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opendir("spic");
            }
        });


        final Button submitbtn = (Button)view.findViewById(R.id.imageButton2);
        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CheckBox chekbx = view.findViewById(R.id.checkBox_term);
                if(chekbx.isChecked()) {
                    submitbtn.setEnabled(false);
                    //prepare json data
                    JSONObject jsonobj = getUsrInfoJSONFormat();

                    //send all info to server
                    if(NetworkChangeReceiver.isNetworkConnected(cxt)) {
                        showprogressbar();
                        if (usrprofid == null  || usrprofid.equals("null")) {
                            mUsrProfile = new UserProStoreTask(jsonobj);
                            mUsrProfile.execute();
                        } else {
                            mUsrProfileUpdate = new UserProUpdateTask(jsonobj);
                            mUsrProfileUpdate.execute();
                        }
                    } else {
                        Toast.makeText(cxt, getResources().getString(R.string.alert_network_disconnected), Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(getActivity(), R.string.info_click_term_condition, Toast.LENGTH_SHORT).show();
                }

            }
        });

        cxt = objusrpro.getApplicationContext();
        return view;
    }

    void showprogressbar()
    {
        if(progress == null)
            progress = new ProgressDialog(objusrpro);

        progress.setMessage(objusrpro.getResources().getString(R.string.text_status_processing));
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();
    }

    void hideprogrssbar()
    {
        if(progress !=null)
            progress.dismiss();
    }

    /*
     * The method is taking Bitmap as an argument
     * then it will return the byte[] array for the given bitmap
     * and we will send this array to the server
     * here we are using PNG Compression with 80% quality
     * you can give quality between 0 to 100
     * 0 means worse quality
     * 100 means best quality
     * */
    public static byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        //bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        bitmap.compress(Bitmap.CompressFormat.JPEG,80,byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public void sumbitallprofile(final String filen[], String urlpath)
    {
        //our custom volley request
        final String TAG_VOLLEY = "VolleyMulti";
        final String TAG = "PreDataHandler";

/*        final ProgressDialog progress;
        progress = new ProgressDialog(cxt);
        progress.setMessage(cxt.getResources().getString(R.string.text_status_processing));
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();*/

        VolleyMultiPart volleyMultipartRequest = new VolleyMultiPart(Request.Method.POST, RestAPI.REST_BASE_URL + RestAPI.REST_USER+ urlpath ,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        hideprogrssbar();
                        clearall();
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideprogrssbar();
                        //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG_VOLLEY,error.getMessage());
                        //PhotoAdapter.isprocessed = 0;
                        //progress.dismiss();
                    }
                }) {

                /*
                 * If you want to add more parameters with the image
                 * you can do it here
                 * here we have only one parameter with the image
                 * which is tags
                 * */
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("id", Session.getupid());
                    //params.put("jdata", obj.toString());
                    params.put("fc", String.valueOf(filen.length));
                    return params;
                }

                /*
                 * Here we are passing image by renaming it with a unique name
                 * */
                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    //long imagename = System.currentTimeMillis();
/*                    for(int l=0;(l<filen.length) && (filen[l] != null) && (bitmap[l] != null);l++) {
                        params.put("pic"+String.valueOf(l), new DataPart(filen[l], getFileDataFromDrawable(bitmap[l]), "application/x-www-form-urlencoded"));
                    }*/
                    return params;
                }


                /*
                 * Here we are passing image by renaming it with a unique name
                 * */
                @Override
                protected Map<String, ArrayList<DataPart>> getArrayByteData() {
                    Map<String, ArrayList<DataPart>> params = new HashMap<>();
                    //long imagename = System.currentTimeMillis();
                    ArrayList<DataPart> data = new ArrayList<>();
                    //for(int l=0;(l<filen.length) && (filen[l] != null) && (bitmap[l] != null);l++) {
                    for(int l=0;(filen != null) && (l<filen.length) && (filen[l] != null) ;l++) {
                        //DataPart item = new DataPart(filen[l], getFileDataFromDrawable(bitmap[l]), "application/x-www-form-urlencoded");
                        byte bdata[] = new byte[filen[l].length()];
                        Bitmap bm = ImageCache.decodeSampledBitmap(filen[l] , null, 400, 600);
                        DataPart item = new DataPart(filen[l], getFileDataFromDrawable(bm) , "application/x-www-form-urlencoded");
                        data.add(item);
                    }
                    params.put("pic", data);
                    return params;
                }

        };

        //stop all
        Volley.newRequestQueue(cxt).cancelAll(TAG_VOLLEY);
        //adding the request to volley
        volleyMultipartRequest.setTag(TAG_VOLLEY);
        int socketTimeout = 30000;//30 seconds server wait
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        volleyMultipartRequest.setRetryPolicy(policy);
        Volley.newRequestQueue(cxt).add(volleyMultipartRequest);

    }

    private static final int RESULT_CODE = 0x321;
    private static final int READ_REQUEST_CODE = 42;

    public String opendir(String id) {
        outdirpath = objusrpro.fileSelectPath + id + ".jpg";

        Intent data = new Intent(getActivity(), CopyFiles.class);
        data.putExtra("outdirpath",outdirpath);
        //setResult(RESULT_OK,data);

        startActivityForResult(data, RESULT_CODE);
        return null;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                String flpath = data.getStringExtra("outdirpath");
                setfilename(flpath);

            }
        }
    }

    public static void setfilename(String tag)
    {
        File f = new File(tag);
        String name = f.getName();
        switch(name)
        {
            case "edu.jpg":
                filename1.setText(tag);
                break;
            case "iddoc.jpg":
                filename2.setText(tag);
                break;
            case "spic.jpg":
                filename3.setText(tag);
                break;
            default:
                break;
        }
    }

    public JSONObject getUsrInfoJSONFormat()
    {
        JSONObject jsonObj = new JSONObject();
        try {


            jsonObj.put("adminid", UserProfileCreateActivity.usrloginid);
            jsonObj.put("uid",UserProfileCreateActivity.usrprofid);
            jsonObj.put("uname",UserProfileCreateActivity.usrprofileinfo.name);
            jsonObj.put("dob",UserProfileCreateActivity.usrprofileinfo.age);
            jsonObj.put("sex",UserProfileCreateActivity.usrprofileinfo.sex);
            jsonObj.put("caddr",UserProfileCreateActivity.usrprofileinfo.caddr);
            jsonObj.put("caddrpin",UserProfileCreateActivity.usrprofileinfo.caddrpinc);
            jsonObj.put("paddr",UserProfileCreateActivity.usrprofileinfo.paddr);
            jsonObj.put("paddrpin",UserProfileCreateActivity.usrprofileinfo.paddrpinc);
            jsonObj.put("umob",UserProfileCreateActivity.usrprofileinfo.mobile);
            jsonObj.put("uamob",UserProfileCreateActivity.usrprofileinfo.altmobile);
            jsonObj.put("idtype",UserProfileCreateActivity.usrprofileinfo.idtype);
            jsonObj.put("idtypeval",UserProfileCreateActivity.usrprofileinfo.idtypenum);
            jsonObj.put("uemail",UserProfileCreateActivity.usrprofileinfo.email);
            jsonObj.put("csal",UserProfileCreateActivity.usrprofileinfo.csalary);


            JSONArray objArr1 = new JSONArray();
            JSONObject objtemp0 = new JSONObject();
            for(int j=0;j<UserProfileCreateActivity.usrprofileinfo.userindustype.size();j++)
            {
                objArr1.put(UserProfileCreateActivity.usrprofileinfo.userindustype.get(j).catnum.toString());
            }
            objtemp0.put("myrows",objArr1);
            jsonObj.put("jtype",objtemp0);



            JSONArray objArr2 = new JSONArray();
            JSONObject objtemp1 = new JSONObject();
            if(UserProfileCreateActivity.usrprofileinfo.userexperience != null && !UserProfileCreateActivity.usrprofileinfo.userexperience.isEmpty()) {
                for (int j = 0; j < UserProfileCreateActivity.usrprofileinfo.userexperience.size(); j++) {
                    JSONArray objtemp = new JSONArray();
                    objtemp.put(UserProfileCreateActivity.usrprofileinfo.userexperience.get(j).cname);
                    objtemp.put(UserProfileCreateActivity.usrprofileinfo.userexperience.get(j).catid);
                    objtemp.put(UserProfileCreateActivity.usrprofileinfo.userexperience.get(j).jobtitle);
                    objtemp.put(UserProfileCreateActivity.usrprofileinfo.userexperience.get(j).jobdetail);
                    objtemp.put(UserProfileCreateActivity.usrprofileinfo.userexperience.get(j).jstartdate);
                    objtemp.put(UserProfileCreateActivity.usrprofileinfo.userexperience.get(j).jenddate);
                    objtemp.put(UserProfileCreateActivity.usrprofileinfo.userexperience.get(j).location);
                    objArr2.put(objtemp);
                }
            }
            objtemp1.put("myrows", objArr2);
            jsonObj.put("expdt", objtemp1);


            JSONArray objArr3 = new JSONArray();
            JSONObject objtemp2 = new JSONObject();
            if(UserProfileCreateActivity.usrprofileinfo.usereducaiton != null && !UserProfileCreateActivity.usrprofileinfo.usereducaiton.isEmpty()) {
                for (int j = 0; j < UserProfileCreateActivity.usrprofileinfo.usereducaiton.size(); j++) {
                    JSONArray objtemp = new JSONArray();
                    objtemp.put(UserProfileCreateActivity.usrprofileinfo.usereducaiton.get(j).eduname);
                    objtemp.put(UserProfileCreateActivity.usrprofileinfo.usereducaiton.get(j).edudetail);
                    objtemp.put(UserProfileCreateActivity.usrprofileinfo.usereducaiton.get(j).edudate);
                    objtemp.put(UserProfileCreateActivity.usrprofileinfo.usereducaiton.get(j).location);
                    objArr3.put(objtemp);
                }
            }
            objtemp2.put("myrows", objArr3);
            jsonObj.put("edudt", objtemp2);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObj;
    }



    Handler handle_finish = new Handler();
    final Runnable changeView = new Runnable()
    {
        public void run()
        {

            String filen[] = new String[3];
            //Bitmap bitm[] = new Bitmap[3];
            int k =0 ;
            showprogressbar();
            try {
                if(!filename1.getText().toString().equals("")) {
                    filen[k] = filename1.getText().toString();
                    //bitm[k] = MediaStore.Images.Media.getBitmap(cxt.getContentResolver(), Uri.fromFile(new File(filen[k])));
                    k++;
                }
                if(!filename2.getText().toString().equals("")) {
                    filen[k] = filename2.getText().toString();
                    //bitm[k] = MediaStore.Images.Media.getBitmap(cxt.getContentResolver(), Uri.fromFile(new File(filen[k])));
                    k++;
                }
                if(!filename3.getText().toString().equals("")) {
                    filen[k] = filename3.getText().toString();
                    //bitm[k] = MediaStore.Images.Media.getBitmap(cxt.getContentResolver(), Uri.fromFile(new File(filen[k])));
                    k++;
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            if(k > 0) {
                sumbitallprofile(filen,"/upusrfiles");
            } else {
                clearall();
            }
        }
    };



    Handler handle_error = new Handler();
    final Runnable showmsg = new Runnable() {
        public void run() {
            Toast.makeText(getActivity(), R.string.error_no_usrprofupt, Toast.LENGTH_SHORT).show();
        }
    };

    public static void clearall()
    {
        //handler.postDelayed(this, 200);
        UserProfileCreateActivity.usrprofileinfo.userindustype.clear();
        if (UserProfileCreateActivity.usrprofileinfo.userexperience != null && !UserProfileCreateActivity.usrprofileinfo.userexperience.isEmpty())
            UserProfileCreateActivity.usrprofileinfo.userexperience.clear();
        if (UserProfileCreateActivity.usrprofileinfo.usereducaiton != null && !UserProfileCreateActivity.usrprofileinfo.usereducaiton.isEmpty())
            UserProfileCreateActivity.usrprofileinfo.usereducaiton.clear();

        Toast.makeText(objusrpro, R.string.info_now_can_apply_job, Toast.LENGTH_SHORT).show();

        //go back to home activity
        Intent intent = new Intent(objusrpro, HomeActivityU.class);
        objusrpro.startActivity(intent);
        objusrpro.finish();

        clearStack();
    }


    public static void clearStack() {
        //Here we are clearing back stack fragment entries
        FragmentManager fragmentManager = objusrpro.getFragmentManager();

        if(fragmentManager == null)
            return ;

        int backStackEntry = fragmentManager.getBackStackEntryCount();
        if (backStackEntry > 0) {
            for (int i = 0; i < backStackEntry; i++) {
                fragmentManager.popBackStackImmediate();
            }
        }

        //Here we are removing all the fragment that are shown here
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (fragmentManager.getFragments() != null && fragmentManager.getFragments().size() > 0) {
                for (int i = 0; i < fragmentManager.getFragments().size(); i++) {
                    Fragment mFragment = fragmentManager.getFragments().get(i);
                    if (mFragment != null) {
                        fragmentManager.beginTransaction().remove(mFragment).commit();
                    }
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


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserProStoreTask extends AsyncTask<Void, Void, String> {

        private JSONObject proinfo;

        UserProStoreTask(JSONObject data) {
            proinfo = data;
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String r = null;
            try
            {
                // Simulate network access.
                r = RestAPI.setUserProfile(proinfo);
                Thread.sleep(2000);
                hideprogrssbar();
                Thread.sleep(1000);
                if(r != null && !r.equals("Something went wrong. Try Again!"))
                {
                    //showuploadlist(r);
                    Session.setupid(r);
                    handle_finish.postDelayed(changeView, 10);

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
            mUsrProfile = null;

            if (success!=null) {
                //finish();
            } else {
                //mPasswordView.setError(getString(R.string.error_incorrect_password));
                //mPasswordView.requestFocus();
                Toast.makeText(getActivity(), R.string.error_no_compost, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mUsrProfile = null;

        }
    }




    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserProUpdateTask extends AsyncTask<Void, Void, String> {

        private JSONObject proinfo;

        UserProUpdateTask(JSONObject data) {
            proinfo = data;
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String r = null;
            try
            {
                // Simulate network access.
                r = RestAPI.updateUserProfile(proinfo);
                Thread.sleep(2000);
                hideprogrssbar();
                if(!r.equals(""))
                {
                    JSONObject res = new JSONObject(r);
                    if (!res.has("error") ) {
                        //showuploadlist(r);
                        //Session.setupid(r);
                        handle_finish.postDelayed(changeView, 0);

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
            mUsrProfileUpdate = null;

            if (success!=null && !success.equals("")) {
                //finish();
            } else {
                //mPasswordView.setError(getString(R.string.error_incorrect_password));
                //mPasswordView.requestFocus();
                //Toast.makeText(getActivity(), R.string.error_no_compost, Toast.LENGTH_SHORT).show();
                handle_error.postDelayed(showmsg,10);
            }
        }

        @Override
        protected void onCancelled() {
            mUsrProfileUpdate = null;

        }
    }




}
