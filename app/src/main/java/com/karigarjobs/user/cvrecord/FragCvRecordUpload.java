package com.karigarjobs.user.cvrecord;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.karigarjobs.R;
import com.karigarjobs.Session;
import com.karigarjobs.user.HomeActivityU;
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
import java.io.FileFilter;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

import static com.karigarjobs.user.cvrecord.UserProfileCreateVoice.mediasuffix;
import static com.karigarjobs.user.cvrecord.UserProfileCreateVoice.objcvvoice;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragCvRecordUpload#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragCvRecordUpload extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static String outdirpath = "";
    static Context cxt;

    ProgressDialog progress;

    private static Button submitbtn ;
    UserCvUploadTask mUsrCvUpload;
    static boolean isImgSend = false , isVoiSend = false;

    private OnFragmentInteractionListener mListener;

    public FragCvRecordUpload() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragCvRecordUpload.
     */
    // TODO: Rename and change types and number of parameters
    public static FragCvRecordUpload newInstance(String param1, String param2) {
        FragCvRecordUpload fragment = new FragCvRecordUpload();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static TextView filename1,filename2,filename3;
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
        final View view = inflater.inflate(R.layout.fragment_frag_cv_record_upload, container, false);
        filename1 = view.findViewById(R.id.textView_edu);
        Button btnseleduc = (Button)view.findViewById(R.id.q_button1);
        btnseleduc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opendir("edu");

            }
        });

        filename2 = view.findViewById(R.id.textView_id);
        Button btnselid = (Button)view.findViewById(R.id.q_button2);
        btnselid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opendir("iddoc");
            }
        });

        filename3 = view.findViewById(R.id.textView_self);
        Button btnselself = (Button)view.findViewById(R.id.q_button3);
        btnselself.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opendir("spic");
            }
        });


        submitbtn = (Button)view.findViewById(R.id.imageButton2);
        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CheckBox chekbx = view.findViewById(R.id.checkBox_term);
                if(chekbx.isChecked()) {
                    //prepare json data
                    //makedataforsend();
                    if(NetworkChangeReceiver.isNetworkConnected(cxt)) {
                        mUsrCvUpload = new UserCvUploadTask(String.valueOf(Session.getUsr_id()));
                        mUsrCvUpload.execute();
                    } else {
                        Toast.makeText(cxt, getResources().getString(R.string.alert_network_disconnected), Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(getActivity(), R.string.info_click_term_condition, Toast.LENGTH_SHORT).show();
                }

            }
        });

         cxt = objcvvoice.getApplicationContext();
        return view;
    }

    void showprogressbar()
    {
        if(progress == null)
            progress = new ProgressDialog(objcvvoice);

        progress.setMessage(objcvvoice.getResources().getString(R.string.text_status_processing));
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();
    }

    void hideprogrssbar()
    {
        if(progress !=null)
            progress.dismiss();
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

    public void makedataforsend()
    {
        //send image data
        submitbtn.setEnabled(false);
        sendimagedata();
    }

    public void sendimagedata()
    {
        String filen[] = new String[3];
        //Bitmap bitm[] = new Bitmap[3];
        int k =0 ;

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
            //sumbitimgprofile(filen,"/upusrrecfiles");
            sendvoicedata(filen);
        } else {
            //clearall();
            sendvoicedata(null);
            isImgSend = true;
        }
    }

    private long MAX_AUDIO_FILE_SZ = 1024 * 1024 * 1; //1MB bytes
    private long MAX_AUDIO_CNT = 16; //total audio profile to upload
    private long MIN_AUDIO_CNT = 8; //min total audio profile to upload


    public boolean sendvoicedata( String imgfile[])
    {
        File f = new File(objcvvoice.recfilePath);
        List<File> result = new ArrayList<File>();
        if( !f.exists() || !f.isDirectory() ) {
            Toast.makeText(cxt, getResources().getString(R.string.error_file_create_falied), Toast.LENGTH_SHORT).show();
            return false;
        }


        FileFilter filt = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                String suffix = mediasuffix;
                if( pathname.isFile()  &&  pathname.length() < (MAX_AUDIO_FILE_SZ) && (pathname.length() != 0) && pathname.getName().toLowerCase().endsWith(suffix) ) {
                    return true;
                }
                return false;
            }
        };

        //Add all files that comply with the given filter
        File[] files = f.listFiles(filt);
        for( File fi : files) {
            if( !result.contains(fi) )
                result.add(fi);
        }

        if(result.size() > MAX_AUDIO_CNT )
        {
            Toast.makeText(cxt, getResources().getString(R.string.alert_aud_filecnt_max), Toast.LENGTH_SHORT).show();
            return false;
        }
        if(result.size() < MIN_AUDIO_CNT )
        {
            Toast.makeText(cxt, getResources().getString(R.string.alert_aud_filecnt_min), Toast.LENGTH_SHORT).show();
            return false;
        }

        JSONObject jsonObj = prepaudioflist(result);
        sumbitallprofile(result,imgfile,jsonObj);
        return true;
    }

    public JSONObject prepaudioflist(List<File> filen)
    {
        JSONObject objlist = new JSONObject();
        try {
            objlist.put("id",String.valueOf(Session.getUsr_id()));

            JSONArray objArr1 = new JSONArray();
            JSONObject objtemp0 = new JSONObject();
            for(int j=0;j<filen.size();j++)
            {
                objArr1.put(filen.get(j).getName().split("[.]")[0]);
            }
            objtemp0.put("myrows",objArr1);

            objlist.put("vclist",objtemp0);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return objlist;
    }

    public byte[] makevoicefilestream(File filen)
    {
        //String outputFile = recfilePath + "/" + filen;
        //byte[] soundBytes;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            InputStream inputStream = cxt.getContentResolver().openInputStream(Uri.fromFile(filen));

           //soundBytes = new byte[inputStream.available()];

            byte[] buff = new byte[10240];
            int i = Integer.MAX_VALUE;
            while ((i = inputStream.read(buff, 0, buff.length)) > 0) {
                baos.write(buff, 0, i);
            }

            //Toast.makeText(this, "Recordin Finished"+ " " + soundBytes, Toast.LENGTH_LONG).show();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
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
    public  byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        //bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        bitmap.compress(Bitmap.CompressFormat.JPEG,80,byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    Handler handle_finish = new Handler();
    final Runnable changeView_finish = new Runnable()
    {
        public void run()
        {
            Toast.makeText(objcvvoice,objcvvoice.getResources().getString(R.string.err_allcvprofileupd),Toast.LENGTH_LONG).show();
            return;
        }
    };


    private static final String URL_SUBMIT_ALL = "/upusrrecfiles";
    public  void sumbitallprofile(final List<File> filen, final String imgfile[], final JSONObject jsonObj)
    {
        //our custom volley request
        String TAG_VOLLEY = "VolleyMultiAud";
        String TAG = "PreDataHandler";

        showprogressbar();

        VolleyMultiPart volleyMultipartRequest = new VolleyMultiPart(Request.Method.POST, RestAPI.REST_BASE_URL + RestAPI.REST_USER+ URL_SUBMIT_ALL ,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        hideprogrssbar();
                        if(response !=null)
                        {
                            try {
                                JSONObject obj = new JSONObject(new String(response.data));
                                if(obj.has("data")) {
                                    String usrcvid = obj.getString("data");
                                    Session.setusrcvid(usrcvid);
                                    HomeActivityU.reqcvid = usrcvid;
                                    isImgSend = true;
                                    isVoiSend = true;
                                    Toast.makeText(objcvvoice,objcvvoice.getResources().getString(R.string.alert_audiocv_submit_succ),Toast.LENGTH_LONG).show();
                                    clearall();
                                } else {
                                    handle_finish.postDelayed(changeView_finish,10);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();

                            }

                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideprogrssbar();
                        handle_finish.postDelayed(changeView_finish,10);
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
                params.put("id", String.valueOf(Session.getUsr_id()));
                //params.put("jdata", obj.toString());
                params.put("fc", String.valueOf(filen.size()));
                params.put("jsonval",jsonObj.toString());
                return params;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() throws UnsupportedEncodingException {
                Map<String, DataPart> params = new HashMap<>();
                //DataPart part = new DataPart(null,jsonObj.toString().getBytes("UTF-8"),"application/json");
                //params.put("jsonval",part);
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
                for(int l=0;(l<filen.size()) && (filen.get(l) != null) ;l++) {
                    DataPart item = new DataPart(filen.get(l).getName(),makevoicefilestream(filen.get(l)), "application/x-www-form-urlencoded");
                    data.add(item);
                }
                for(int l=0;(imgfile != null) && (l<imgfile.length) && (imgfile[l] != null) ;l++) {
                    byte bdata[] = new byte[imgfile[l].length()];
                    Bitmap bm = ImageCache.decodeSampledBitmap(imgfile[l] , null, 400, 600);
                    DataPart item = new DataPart(imgfile[l], getFileDataFromDrawable(bm) , "application/x-www-form-urlencoded");
                    data.add(item);
                }

                params.put("imgvoi", data);
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
        outdirpath = objcvvoice.recfilePath + id + ".jpg";

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

    public static void clearall()
    {
        //go back to home activity
        if(isImgSend && isVoiSend) {
            Intent intent = new Intent(objcvvoice, HomeActivityU.class);
            objcvvoice.startActivity(intent);
            objcvvoice.finish();

            clearStack();
        }
    }


    public static void clearStack() {
        //Here we are clearing back stack fragment entries
        FragmentManager fragmentManager = objcvvoice.getFragmentManager();

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


    Handler handle_start = new Handler();
    final Runnable changeView = new Runnable()
    {
        public void run()
        {
            makedataforsend();
        }
    };


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserCvUploadTask extends AsyncTask<Void, Void, String> {

        private String uid;

        UserCvUploadTask(String data) {
            uid  = data;
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String r = null;
            try
            {
                //Do upload task in async
                handle_start.postDelayed(changeView, 10);

            } catch ( Exception ex)
            {
                Log.e("", ex.toString());
                return r;
            }

            // TODO: register the new account here.
            return r;
        }

        @Override
        protected void onPostExecute(final String success) {
            mUsrCvUpload = null;

            if (success!=null) {
                //finish();
            } else {
                //mPasswordView.setError(getString(R.string.error_incorrect_password));
                //mPasswordView.requestFocus();
                //Toast.makeText(getActivity(), R.string.error_no_compost, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mUsrCvUpload = null;

        }
    }
}
