package com.karigarjobs.utils;

/**
 * Created by root on 24/1/19.
 */

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import androidx.annotation.NonNull;


//import static com.karigarjobs.user.FragUsrProfDocUpload.outdirpath;


/**
 * Requires: android:minSdkVersion="9"
 *
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 * <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
 * <uses-permission android:name="android.permission.INTERNET" />
 * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
 *
 * <activity android:name=".CopyFileActivity"
 * android:screenOrientation="portrait" />
 *
 * USAGE:
 *
 * public void selectFile(View w) { startActivityForResult(new Intent(this,
 * CopyFileActivity.class), RESULT_CODE); }
 *
 * @Override protected void onActivityResult(int requestCode, int resultCode,
 *           Intent data) { if (requestCode == RESULT_CODE && resultCode ==
 *           RESULT_OK && data != null) { String fileName =
 *           data.getStringExtra(CopyFileActivity.RESULT); } }
 */

public class CopyFiles extends Activity {
    private static final String TAG = "CopyFileActivity";
    private static final int GALLERY_CODE = 0x321;
    private static final int CAMERA_CODE = 0x322;
    private static final int DEFAULT_BUFFER_SIZE = 1024/* bytes */* 1024/* kilobytes */* 10/* megabytes */;
    //private static final String DEFAULT_MIME_TYPE = "*/*";
    public static final String RESULT = "FILE_NAME";
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int MY_EXT_STORAGE_READ_PERMISSION_CODE = 200;
    private static final int MY_EXT_STORAGE_WRITE_PERMISSION_CODE = 202;

    public static int numberOfImagesToSelect = 10;
    private ProgressDialog progressDialog;

    //int PICK_IMAGE_MULTIPLE = 1;
    //String imageEncoded;
    //List<String> imagesEncodedList;
    public String outdirpath = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        if(b != null)
            outdirpath = b.getString("outdirpath");


        showPictureDialog();
        this.progressDialog = new ProgressDialog(CopyFiles.this);
        progressDialog.setMessage("Copying files..");
        progressDialog.getWindow().setGravity(Gravity.CENTER);
    }


    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action(कृपया चुने)");
        String[] pictureDialogItems = {
                "Select photo from gallery(गैलरी से फोटो का चयन करें)",
                "Capture photo from camera(कैमरे से फोटो खींचे)" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });


        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        //Intent galleryIntent = new Intent(Intent.ACTION_PICK,
        //        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        //startActivityForResult(galleryIntent, GALLERY);
        showFileChooser();
    }

    private void takePhotoFromCamera() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
            }  else {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_CODE);
                //finish();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_CODE);
                //finish();
            }
            else
            {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showFileChooser() {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_EXT_STORAGE_READ_PERMISSION_CODE);
                }
                else if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_EXT_STORAGE_WRITE_PERMISSION_CODE);
                }
                else {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(intent,GALLERY_CODE);
                    //finish();
                }
            }

        }
        catch (ActivityNotFoundException e) {
            cancelAndFinish(e);
        }
    }

    private void cancelAndFinish(ActivityNotFoundException e) {
        Log.e(TAG, e.getMessage());
        setResult(RESULT_CANCELED);
        finish();
    }

/*    private Intent getIntentForChoosingFiles() {
        //Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        //intent.addCategory(Intent.CATEGORY_OPENABLE);
        //intent.setType(DEFAULT_MIME_TYPE);
        //intent.setType("image/*");

        Intent intent = new Intent(this, AlbumSelectActivity.class);
        //set limit on number of images that can be selected, default is 10
        intent.putExtra(Constants.INTENT_EXTRA_LIMIT, numberOfImagesToSelect);
        //startActivityForResult(intent, Constants.REQUEST_CODE);

        return intent;
    }*/

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
/*        File wallpaperDirectory = new File(outdirpath);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }*/

        try {
            File f = new File(outdirpath);
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::---&gt;" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //boolean  iscreate = false;
        //SimpleDateFormat sdfprint = new SimpleDateFormat("yyyyMMdd");
        if (resultCode == RESULT_OK ) {
            if(requestCode == GALLERY_CODE && data != null) {

                //SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                //String currentDate = sdf.format(new Date());
                //String outdir = "/storage/emulated/0/fab_medbox/app_upload/";

                File file = new File(outdirpath);
                File dirp = new File(file.getParent());

                //Chnage date format for print
                //String datename = null;
                if(!dirp.exists()) {
                    if(dirp.mkdirs()) //directory is created;
                    {
/*                        iscreate = true;
                        try {
                            Date d = sdfprint.parse(currentDate);
                            datename = DateFormat.getDateInstance().format(d);
                        } catch (ParseException ex) {
                            Log.v("Exception", ex.getLocalizedMessage());
                        }*/
                    }
                }

                //ArrayList<Image> images = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
                ClipData images = data.getClipData();

                if (progressDialog.isShowing() == false)
                    progressDialog.show();

                if (images != null || data.getData()!=null) {
                    if(images!=null) {
                        int fileselected = images.getItemCount();
                        for (int i = 0; i < fileselected; i++)
                            copyFile(images.getItemAt(i).getUri().toString());
                    } else {
                        copyFile(data.getData().normalizeScheme().toString());
                    }
                    //setfilename(outdirpath);

/*                    if(iscreate) {
                        int len = dataSet.size();
                        DataModel item = new DataModel("Record updated: " + String.valueOf(len),datename, R.mipmap.list_folder);
                        ListViewAdaptor.addRecord(item);
                        ListViewAdaptor.adaptor.notifyDataSetChanged();
                    }*/
                }

            }
            else if(requestCode == CAMERA_CODE)
            {
                //SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                //String currentDate = sdf.format(new Date());
                //String outdir = "/storage/emulated/0/fab_medbox/app_upload/";

                File file = new File(outdirpath);
                File dirp = new File(file.getParent());

                //String datename = null;
                if(!dirp.exists()) {
                    if(dirp.mkdirs()) //directory is created;
                    {
/*                        iscreate = true;
                        try {
                            Date d = sdfprint.parse(currentDate);
                            datename = DateFormat.getDateInstance().format(d);
                        } catch (ParseException ex) {
                            Log.v("Exception", ex.getLocalizedMessage());
                        }*/
                    }
                }

                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                if (progressDialog.isShowing() == false)
                    progressDialog.show();

                saveImage(thumbnail);
                //setfilename(outdirpath);
                Intent retdata = new Intent();
                retdata.putExtra("outdirpath",outdirpath);
                setResult(RESULT_OK,retdata);
                finish();

/*                if(iscreate) {
                    int len = dataSet.size();
                    DataModel item = new DataModel("Record updated: " + String.valueOf(len),datename, R.mipmap.list_folder);
                    ListViewAdaptor.addRecord(item);
                    ListViewAdaptor.adaptor.notifyDataSetChanged();
                }*/
            }
            if(progressDialog.isShowing())
                progressDialog.dismiss();



        }
        else
        {
            onBackPressed();
        }
    }

    private void addItemsToList() {

    }

        @Override
    public void onBackPressed() {

        Intent retdata = new Intent();
        retdata.putExtra("outdirpath",outdirpath);
        setResult(RESULT_CANCELED,retdata);
        finish();

        //super.onBackPressed();
    }

    private void copyFile(final String uri) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new CopyFileAsyncTask().execute(uri,outdirpath);
            }
        }, 100);
    }

    private class CopyFileAsyncTask extends AsyncTask<String, Void, String> {
        private static final String TYPE_CONTENT = "content";
        private static final String TYPE_FILE = "file";

        private DownloadManager downloadManager;

        private boolean isDownloading = false;
        private long enqueue;
        private String srcfile;
        private String desfile;
        private final BroadcastReceiver downloadCompleteReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    isDownloading = false;
                    String oldFileName = getFileFromDownloadManager(enqueue);
                    String fileName = copyFile(oldFileName,desfile);
                    setResultAndFinish(fileName);
                    unregisterReceiver(this);
                }
            }
        };

        public CopyFileAsyncTask() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... uris) {
            srcfile = uris[0];
            desfile = uris[1];
            return copyFile(uris[0],uris[1]);
        }

        @Override
        protected void onPostExecute(String fileName) {
            super.onPostExecute(fileName);
            if (!isDownloading) {
                setResultAndFinish(fileName);
            }
        }

        private void setResultAndFinish(String fileName) {
            setResult(RESULT_OK, getResultIntent(fileName));
            Log.d(TAG, "If no errors before, file should be now in /" + fileName);
            finish();
        }

        private Intent getResultIntent(String fileName) {
            Intent intent = new Intent();
            intent.putExtra(RESULT, fileName);
            intent.putExtra("outdirpath",desfile);
            return intent;
        }

        private String copyFile(String uri,String destpath) {
            try {
                String str_path = URLDecoder.decode(uri.toString(), "UTF-8");
                Log.d(TAG,str_path);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String path = RealPathUtil.getRealPath(getApplicationContext(),Uri.parse(uri));//getPath(Uri.parse(uri));

            if (TextUtils.isEmpty(path)) {
                Log.e(TAG, "Can't read path.");
                return null;
            }

            Log.d(TAG, "Copying from " + path);
            String fileName = Uri.parse(path).getLastPathSegment(); // in case of TYPE_CONTENT
            copyFileCore(path, destpath);

            return fileName;
        }

        private void copyFileCore(String oldFileName, String newFileName) {
            if (Patterns.WEB_URL.matcher(oldFileName).matches()) {
                if (isNetworkAvailable()) {
                    downloadFileFromUrl(oldFileName);
                }
                else {
                    Log.e(TAG, "No internet connection. File neither downloaded nor copied.");
                }
            }
            else {
                //File oldFile = new File(oldFileName);
                //File newFile = new File(getFilesDir(), newFileName);
                File oldFile = new File(oldFileName);
                File newFile = new File(newFileName);
                Log.d(TAG,oldFileName);
                Log.d(TAG,newFileName);
                copyFile(oldFile, newFile);
            }
        }

        public boolean isNetworkAvailable() {
            return ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
        }

        private void downloadFileFromUrl(String fileUrl) {
            registerDownloadReceiver();
            downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            Request request = new Request(Uri.parse(fileUrl));
            enqueue = downloadManager.enqueue(request);
            isDownloading = true;
        }

        private void registerDownloadReceiver() {
            registerReceiver(downloadCompleteReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }

        private String getFileFromDownloadManager(long enqueue) {
            Query query = new Query();
            query.setFilterById(enqueue);
            Cursor c = downloadManager.query(query);
            if (c.moveToFirst()) {
                int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                    String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    c.close();
                    return uriString;
                }
            }
            Log.e(TAG, "Downloading file not successful. Malformed Url or no internet connection");
            return null;
        }

        private void copyFile(File oldFile, File newFile) {
            InputStream in = null;
            OutputStream out = null;
            int copiedBytesSize = 0;

            try {
                try {
                    in = new BufferedInputStream(new FileInputStream(oldFile));
                    out = new BufferedOutputStream(new FileOutputStream(newFile));
                    copiedBytesSize = copyStreams(in, out);

                    Log.d(TAG, "Copied bytes: " + copiedBytesSize);
                }
                finally {
                    if (in != null) {
                        in.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                }
            }
            catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }

        private int copyStreams(InputStream in, OutputStream out) throws IOException {
            byte[] bytes = new byte[DEFAULT_BUFFER_SIZE];
            int copiedBytesSize = 0;
            int currentByte = 0;

            while (-1 != (currentByte = in.read(bytes))) {
                out.write(bytes, 0, currentByte);
                copiedBytesSize += currentByte;
            }

            return copiedBytesSize;
        }

    }

    public static void clearCache(final Context context) {
        new Thread() {
            @Override
            public void run() {
                File dataDirectory = context.getFilesDir();
                Log.d(TAG, "Deleting: " + dataDirectory.getPath());
                deleteRecursive(dataDirectory);
            }

            private void deleteRecursive(File rootPath) {
                if (rootPath.isDirectory()) {
                    for (File fileOrFolder : rootPath.listFiles()) {
                        deleteRecursive(fileOrFolder);
                    }
                }

                rootPath.delete();
            }
        }.start();
    }
}