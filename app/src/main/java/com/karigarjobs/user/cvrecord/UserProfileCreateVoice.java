package com.karigarjobs.user.cvrecord;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.karigarjobs.R;
import com.karigarjobs.Session;
import com.karigarjobs.user.HomeActivityU;
import com.karigarjobs.utils.LocaleManager;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class UserProfileCreateVoice extends AppCompatActivity
        implements FragCvRecordSelfInfo.OnFragmentInteractionListener ,
        FragCvRecordExpInfo.OnFragmentInteractionListener ,
        FragCvRecordEduInfo.OnFragmentInteractionListener,
        FragCvRecordUpload.OnFragmentInteractionListener {
    private static final String LOG_TAG = "UserProfileCreateVoice";
    public static String usrloginid;
    public static UserProfileCreateVoice objcvvoice;
    //public static String recfilePath = Environment.getExternalStorageDirectory().getPath() + "/karijob/rsm/rec_ans/";
    public String recfilePath = "";
    public static String mediasuffix = ".mp3";
    public int REQUEST_READ_EXSTORAGE = 502;
    private String [] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_create_voice);
        if(Session.IsCreated) {
            usrloginid = String.valueOf(Session.getUsr_id());
        }

        recfilePath = getExternalFilesDir(null).getPath() + "/profile/rec/";
        File fd = new File(recfilePath);
        if(!fd.exists()) {
            if (!fd.mkdirs()) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_file_create_falied), Toast.LENGTH_LONG).show();
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    return;
                }

                if(checkSelfPermission(READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(permissions,REQUEST_READ_EXSTORAGE);
                    return ;
                }
            }
        }
        createfragment_firstpage();
        objcvvoice = this;

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
        int count = getFragmentManager().getBackStackEntryCount();
        if(count == 0 || count == 1)
        {
            if(count == 1)
                getFragmentManager().popBackStack();

            Intent intent = new Intent(getApplicationContext(), HomeActivityU.class);
            startActivity(intent);
            finish();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_EXSTORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                createfragment_firstpage();
                objcvvoice = this;
            }
        }

    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //Log.d("Info","onFragmentInteraction called");
    }

    private void createfragment_firstpage() {

        Fragment frag1 = new FragCvRecordSelfInfo();
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

}
