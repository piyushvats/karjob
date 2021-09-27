package com.karigarjobs.utils;


import android.content.Context;
import android.util.Log;

import com.karigarjobs.Session;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 1/12/18.
 */

public class RestAPI {

    //NOTE & IMP : Update this version if new server code is update
    public static final String REST_APIVER = "4.6/";
    public static final String REST_USER = REST_APIVER + "user/dev";
    public static final String REST_PROV = REST_APIVER + "provider/dev";
    //public static final String REST_BASE_URL = "http://10.0.2.2:3000/portal/admin/server/";//for emulator
    //public static final String REST_BASE_URL = "http://192.168.0.107:3000/portal/admin/server/";//"http://172.20.10.3:3000"
    public static final String REST_BASE_URL = "http://www.karigarjobs.com/portal/admin/server/";
    private static final String TAG = RestAPI.class.getName();
    private static final boolean isLoggingEnabled = false;

    private static String sendRequestToServer(String urlString, Map<String, String> params) throws Exception {
        URL url = new URL(urlString);
        StringBuilder responseData = new StringBuilder();
        StringBuilder requestData = new StringBuilder();
        OutputStreamWriter writer = null;

        for (Map.Entry<String, String> e : params.entrySet()) {
            String name = e.getKey();
            String value = e.getValue();

            requestData.append(name).append("=").append(URLEncoder.encode(value, "UTF-8")).append("&");
        }

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setChunkedStreamingMode(0);
            urlConnection.setRequestProperty( "Content-type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty( "Accept", "*/*" );


            writer = new OutputStreamWriter(urlConnection.getOutputStream());

            writer.write(requestData.toString());
            writer.flush();

            String line;
            BufferedReader reader = new BufferedReader(new
                    InputStreamReader(urlConnection.getInputStream()));
            while ((line = reader.readLine()) != null) {
                responseData.append(line);
                //Log.d(TAG, line);
            }

            writer.close();
            reader.close();
        } finally {
            try {
                writer.flush();
            }catch (Exception e){}

            urlConnection.disconnect();
        }

        return responseData.toString();
    }



    private static String sendJSONRequesttoserver(String path,JSONObject jsonParam) {
        StringBuilder responseData = new StringBuilder();
        String ret = null;
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept","application/json");

            conn.setDoOutput(true);
            conn.setDoInput(true);


            //Log.i("JSON", jsonParam.toString());
            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
            //os.writeBytes(jsonParam.toString());
            os.write(jsonParam.toString().getBytes(("UTF-8")));

            os.flush();
            os.close();

            //Log.i("STATUS", String.valueOf(conn.getResponseCode()));
            //Log.i("MSG" , conn.getResponseMessage());
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = reader.readLine()) != null) {
                responseData.append(line);
            }

            reader.close();

            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseData.toString();
    }




    public static String getLoginMob(boolean isUserlogin,String mobile,String uid, Context context) throws Exception
    {

        Map<String,String> params=new HashMap<>();
        params.put("mob",mobile);
        params.put("uid",uid);
        String response = null;

        if(isUserlogin)
            response = sendRequestToServer(REST_BASE_URL+REST_USER+"/loginmob",params);
        else
            response = sendRequestToServer(REST_BASE_URL+REST_PROV+"/loginmob",params);

        return response;
    }


    public static String getLoginCred(boolean isUserlogin,String mobile,String uid, String val) throws Exception
    {

        Map<String,String> params=new HashMap<>();
        params.put("mob",mobile);
        params.put("uid",uid);
        params.put("cred",val);
        String response = null;

        if(isUserlogin)
            response = sendRequestToServer(REST_BASE_URL+REST_USER+"/loginotmob",params);
        else
            response = sendRequestToServer(REST_BASE_URL+REST_PROV+"/loginotmob",params);

        return response;
    }

    public static String getComJobPost(String usrid,String com,String pgnum,Context context)
    {
        Map<String,String> params=new HashMap<>();
        params.put("pgnum", pgnum);
        params.put("cid", com);
        params.put("uid", usrid);
        String ret = null;
        try {
            String response=sendRequestToServer(REST_BASE_URL+REST_PROV+"/getcomjp",params);
            ret= response;
        }
        catch(Exception e){
            ret = null;

        }

        if(isLoggingEnabled)
            Log.i("RestAPI",ret);

        return ret;

    }

    public static String setDeactivateJobPost(String jpid , Context context)
    {
        Map<String,String> params=new HashMap<>();
        params.put("jpid", jpid);
        String ret = null;
        try {
            String response=sendRequestToServer(REST_BASE_URL+REST_PROV+"/getdeactjp",params);
            ret= response;
        }
        catch(Exception e){
            ret = null;

        }
        if(isLoggingEnabled)
            Log.i("RestAPI",ret);

        return ret;


    }

    public static String getUserJobReqList(String jpid, String pgnum,Context context)
    {
        Map<String,String> params=new HashMap<>();
        params.put("pgnum", pgnum);
        params.put("jpid", jpid);
        String ret = null;
        try {
            String response=sendRequestToServer(REST_BASE_URL+REST_PROV+"/getusrjpreq",params);
            ret= response;
        }
        catch(Exception e){
            ret = null;

        }
        if(isLoggingEnabled)
            Log.i("RestAPI",ret);

        return ret;

    }


    public static String getComProfile(String comid,Context context)
    {
        Map<String,String> params=new HashMap<>();
        params.put("comid", comid);
        String ret = null;
        try {
            String response=sendRequestToServer(REST_BASE_URL+REST_PROV+"/getcomprofile",params);
            ret= response;
        }
        catch(Exception e){
            ret = null;

        }
        if(isLoggingEnabled)
            Log.i("RestAPI",ret);

        return ret;

    }

    public static String uploadComProfile(JSONObject jobj,Context context)
    {
        String ret = null;
        try {
            ret= sendJSONRequesttoserver(REST_BASE_URL+REST_PROV+"/getcomprofileup",jobj);
        }
        catch(Exception e){
            Log.d("Error",e.toString());
        }

        if(isLoggingEnabled)
            Log.i("RestAPI",ret);

        return ret;
    }


    public static String getJPView(String comid,Context context)
    {
        Map<String,String> params=new HashMap<>();
        params.put("comid", comid);
        String ret = null;
        try {
            String response=sendRequestToServer(REST_BASE_URL+REST_PROV+"/getitviewdt",params);
            ret= response;
        }
        catch(Exception e){
            ret = null;

        }
        if(isLoggingEnabled)
            Log.i("RestAPI",ret);

        return ret;

    }

    public static String uploadJobPost(JSONObject jobj,Context context)
    {
        String ret = null;
        try {
            ret= sendJSONRequesttoserver(REST_BASE_URL+REST_PROV+"/updtjobpost",jobj);
        }
        catch(Exception e){
            Log.d("Error",e.toString());
        }

        if(isLoggingEnabled)
            Log.i("RestAPI",ret);

        return ret;

    }

    public static String getCheckComLogin(String mob,String mail,String uid,Context context)
    {
        Map<String,String> params=new HashMap<>();
        params.put("mobile", mob);
        params.put("email", mail);
        params.put("uid", uid);
        String ret = null;
        try {
            String response=sendRequestToServer(REST_BASE_URL+REST_PROV+"/getcomlogincheck",params);
            ret= response;
        }
        catch(Exception e){
            ret = null;

        }
        if(isLoggingEnabled)
            Log.i("RestAPI",ret);

        return ret;


    }


    public static String addProvInfoRegister(JSONObject jobj,Context context)
    {
        String ret = null;
        try {
            ret= sendJSONRequesttoserver(REST_BASE_URL+REST_PROV+"/addprovregis",jobj);
        }
        catch(Exception e){
            Log.d("Error",e.toString());
        }

        if(isLoggingEnabled)
            Log.i("RestAPI",ret);

        return ret;

    }

    public static String updateProvcallrec(String jsid,Context context)
    {
        Map<String,String> params=new HashMap<>();
        params.put("jsid", jsid);
        String ret = null;
        try {
            String response;
            response = sendRequestToServer(REST_BASE_URL+REST_PROV+"/provusrcallrec",params);
            ret= response;
        }
        catch(Exception e){
            ret = null;

        }
        if(isLoggingEnabled)
            Log.i("RestAPI",ret);

        return ret;
    }


    public static String updateprvtoken(String tkn,String loginid)
    {
        Map<String,String> params=new HashMap<>();
        params.put("cred", tkn);
        params.put("logid", loginid);
        String ret = null;
        try {
            String response=sendRequestToServer(REST_BASE_URL+REST_PROV+"/updatetoken",params);
            ret= response;
        }
        catch(Exception e){
            ret = null;

        }
        if(isLoggingEnabled)
            Log.i("RestAPI",ret);

        return ret;

    }

    /********User Rest API*************/

    public static String getComJobPostUser(String pgnum)
    {
        Map<String,String> params=new HashMap<>();
        params.put("pgnum", pgnum);
        String ret = null;
        try {
            String response=sendRequestToServer(REST_BASE_URL+REST_USER+"/getcomjp",params);
            ret= response;
        }
        catch(Exception e){
            ret = null;

        }
        if(isLoggingEnabled)
            Log.i("RestAPI",ret);

        return ret;

    }


    public static String setUsrApyJobPost(String jid,String cid,String uid)
    {
        Map<String,String> params=new HashMap<>();
        params.put("jpid", jid);
        params.put("comid", cid);
        params.put("usrid", uid);
        String ret = null;
        try {
            String response=sendRequestToServer(REST_BASE_URL+REST_USER+"/usrjpaplreq",params);
            ret= response;
        }
        catch(Exception e){
            ret = null;

        }
        if(isLoggingEnabled)
            Log.i("RestAPI",ret);

        return ret;

    }

    public static String setUsrApyJobPostWithCv(String jid,String cid,String uid)
    {
        Map<String,String> params=new HashMap<>();
        params.put("jpid", jid);
        params.put("comid", cid);
        params.put("usrid", uid);
        String ret = null;
        try {
            String response=sendRequestToServer(REST_BASE_URL+REST_USER+"/usrcvjpaplreq",params);
            ret= response;
        }
        catch(Exception e){
            ret = null;

        }
        if(isLoggingEnabled)
            Log.i("RestAPI",ret);

        return ret;

    }

    public static String getUsrJPHist(String uid,String pgnum)
    {
        Map<String,String> params=new HashMap<>();
        params.put("usrid", uid);
        params.put("pgnum", pgnum);
        String ret = null;
        try {
            String response=sendRequestToServer(REST_BASE_URL+REST_USER+"/usraplhist",params);
            ret= response;
        }
        catch(Exception e){
            ret = null;

        }
        if(isLoggingEnabled)
            Log.i("RestAPI",ret);

        return ret;

    }

    public static String setUserProfile(JSONObject dt)
    {
        String ret = null;
        try {
            ret= sendJSONRequesttoserver(REST_BASE_URL+REST_USER+"/usrprofreg",dt);
        }
        catch(Exception e){
            Log.d("Error",e.toString());
        }

        if(isLoggingEnabled)
            Log.i("RestAPI",ret);

        return ret;
    }

    public static String getUserProfile(String id)
    {
        Map<String,String> params=new HashMap<>();
        params.put("usrid", id);
        String ret = null;
        try {
            String response=sendRequestToServer(REST_BASE_URL+REST_USER+"/usrallproinfo",params);
            ret= response;
        }
        catch(Exception e){
            ret = null;

        }
        if(isLoggingEnabled)
            Log.i("RestAPI",ret);

        return ret;
    }


    public static String updateUserProfile(JSONObject dt)
    {
        String ret = null;
        try {
            ret= sendJSONRequesttoserver(REST_BASE_URL+REST_USER+"/usrprofupdt",dt);
        }
        catch(Exception e){
            Log.d("Error",e.toString());
        }

        if(isLoggingEnabled)
            Log.i("RestAPI",ret);

        return ret;
    }


    public static String getJobType(String id)
    {
        Map<String,String> params=new HashMap<>();
        params.put("usrid", id);
        String ret = null;
        try {
            String response=sendRequestToServer(REST_BASE_URL+REST_USER+"/getusrjobtype",params);
            ret= response;
        }
        catch(Exception e){
            ret = null;

        }
        if(isLoggingEnabled)
            Log.i("RestAPI",ret);

        return ret;
    }



    public static String setUsrJpAplFeed(String jsid,String urate,String ufeedb,String gfeedb)
    {
        Map<String,String> params=new HashMap<>();
        params.put("jsid", jsid);
        params.put("usrrate", urate);
        params.put("usrfeedb", ufeedb);
        params.put("genfeedb", gfeedb);

        String ret = null;
        try {
            String response=sendRequestToServer(REST_BASE_URL+REST_USER+"/usrjpaplyfeed",params);
            ret= response;
        }
        catch(Exception e){
            ret = null;

        }
        if(isLoggingEnabled)
            Log.i("RestAPI",ret);

        return ret;

    }


    public static String updateusrtoken(String tkn,String loginid)
    {
        Map<String,String> params=new HashMap<>();
        params.put("cred", tkn);
        params.put("logid", loginid);
        String ret = null;
        try {
            String response=sendRequestToServer(REST_BASE_URL+REST_USER+"/updatetoken",params);
            ret= response;
        }
        catch(Exception e){
            ret = null;

        }
        if(isLoggingEnabled)
            Log.i("RestAPI",ret);

        return ret;

    }

    public static String getAppVer(String ver)
    {
        Map<String,String> params=new HashMap<>();
        params.put("ver", ver);
        String ret = null;
        try {
            String response=sendRequestToServer(REST_BASE_URL+"getversion",params);
            ret= response;
        }
        catch(Exception e){
            ret = null;

        }
        if(isLoggingEnabled)
            Log.i("RestAPI",ret);

        return ret;
    }



    public static String submitusrcvsimple(JSONObject dt)
    {
        String ret = null;
        try {
            ret= sendJSONRequesttoserver(REST_BASE_URL+REST_USER+"/submitusrcvsimple",dt);
        }
        catch(Exception e){
            Log.d("Error",e.toString());
        }

        if(isLoggingEnabled)
            Log.i("RestAPI",ret);

        return ret;
    }

    /*Date : 26 Jan 2021
      Update: Add call record of user to prov from job post option. On each user call to prov from job post.
    */

    public static String updateUsercallrec(String jsid,Context context)
    {
        Map<String,String> params=new HashMap<>();
        params.put("jsid", jsid);
        String ret = null;
        try {
            String response;
            response = sendRequestToServer(REST_BASE_URL+REST_USER+"/usrcallrec",params);
            ret= response;
        }
        catch(Exception e){
            ret = null;

        }
        if(isLoggingEnabled)
            Log.i("RestAPI",ret);

        return ret;
    }



}
