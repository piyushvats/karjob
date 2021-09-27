package com.karigarjobs;


import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by root on 3/12/18.
 */

public class Session {

    static SharedPreferences sharedPreferences;
    static SharedPreferences.Editor editor;
    Context context;
    int PRIVATE_MODE = 0;
    public static  Boolean IsCreated=false;
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String ISUSRLOGIN = "IsUsrLoggedIn";
    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";
    public static final String KEY_PHONE="phone";
    public static final String KEY_EMAIL="email";
    public static final String KEY_ID="id";
    public static final String KEY_PROID="profid";
    public static final String Key_Img_url="img_url";
    public static final String KEY_LNGC="langcode";
    public static final String KEY_USRCV="usrcvid";


    private static int usr_id;
    public static int getUsr_id() {
        return usr_id;
    }
    public static void setUsr_id(int usr_id) {
        Session.usr_id = usr_id;
    }


    private static String user_com_proid;
    public static String getupid() { return user_com_proid;}
    public static void setupid(String id) {
        Session.user_com_proid= id;
        editor.putString(KEY_PROID,id);
        editor.commit();
    }

    private static String usrcvid;
    public static String getusrcvid() {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_USRCV, sharedPreferences.getString(KEY_USRCV, null));
        usrcvid = user.get(Session.KEY_USRCV);
        return usrcvid;
    }
    public static void setusrcvid(String id) {
        Session.usrcvid= id;
        editor.putString(KEY_USRCV,id);
        editor.commit();
    }


    private static String mob;
    public static String getmob() {

        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_PHONE, sharedPreferences.getString(KEY_PHONE, null));
        mob = user.get(Session.KEY_PHONE);
        return mob;
    }
    public static void setmob(String mob) {
        Session.mob = mob;
        editor.putString(KEY_PHONE,mob);
        editor.commit();
    }

    private static String name;
    public static String getname() {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_NAME, sharedPreferences.getString(KEY_NAME, null));
        name = user.get(Session.KEY_NAME);
        return name;
    }
    public static void setname(String name) {
        Session.name = name;
        editor.putString(KEY_NAME,name);
        editor.commit();
    }

    private static String email;
    public static String getemail() { return email;}
    public static void setemail(String email) { Session.email = email;}

    private static String isuserlogin;
    public static String getlogintype() { return email;}
    public static void setlogintype(String type) { Session.isuserlogin = type;}


    public static String lan_code = "en";
    public static String getlancode() {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_LNGC, sharedPreferences.getString(KEY_LNGC, null));
        lan_code = user.get(Session.KEY_LNGC);
        return lan_code;
    }
    public static void setlancode(String code) {
        Session.lan_code = code;
        editor.putString(KEY_LNGC,code);
        editor.commit();
    }


    public Session(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences("LoginPref", PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }


    public void createSession(String name, String number, String email,String isusrlogin,int usrid,String comid,String lan){

        setname(name);
        mob = number;//setmob(number);
        setemail(email);
        setlogintype(isusrlogin);
        setUsr_id(usrid);
        setupid(comid);
        lan_code =  lan;//setlancode(lan);

        editor.putBoolean(IS_LOGIN, true);
        editor.putString(ISUSRLOGIN,isusrlogin);
        editor.putString(KEY_NAME,name);
        editor.putString(KEY_PHONE,number);
        editor.putString(KEY_EMAIL,email);
        editor.putString(KEY_ID,String.valueOf(usr_id));
        editor.putString(KEY_PROID,comid);
        editor.putString(KEY_LNGC,lan);
        editor.commit();
        Session.IsCreated = true;

    }
    public void clearSession(){
        editor.remove(IS_LOGIN);
        editor.remove(ISUSRLOGIN);
        editor.remove(KEY_NAME);
        editor.remove(KEY_PHONE);
        editor.remove(KEY_EMAIL);
        editor.remove(KEY_ID);
        editor.remove(KEY_PROID);
        editor.remove(KEY_LNGC);

        editor.apply();
        Session.IsCreated = false;
    }



    private HashMap<String, String> getsession()
    {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_NAME, sharedPreferences.getString(KEY_NAME, null));
        user.put(KEY_PHONE, sharedPreferences.getString(KEY_PHONE, null));
        user.put(KEY_EMAIL, sharedPreferences.getString(KEY_EMAIL, null));
        user.put(KEY_ID,sharedPreferences.getString(KEY_ID,null));
        user.put(ISUSRLOGIN,sharedPreferences.getString(ISUSRLOGIN,null));
        user.put(KEY_PROID, sharedPreferences.getString(KEY_PROID, null));
        user.put(KEY_LNGC, sharedPreferences.getString(KEY_LNGC, null));
        return user;
    }

    public void refreshSession()
    {
        HashMap<String, String> user = getsession();

        Session.setmob(user.get(Session.KEY_PHONE));
        Session.setname(user.get(Session.KEY_NAME));
        Session.setemail(user.get(Session.KEY_EMAIL));
        Session.setUsr_id(Integer.parseInt(user.get(Session.KEY_ID)));
        Session.setlogintype(user.get(Session.ISUSRLOGIN));
        Session.setupid(user.get(Session.KEY_PROID));
        Session.setlancode(user.get(Session.KEY_LNGC));
        Session.IsCreated = true;

    }


    public boolean checkLogin() throws Exception {
        // Check login status
        if(isLoggedIn()){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean checkLogintype() throws Exception {
        // Check login status
        String val = sharedPreferences.getString(ISUSRLOGIN,null);
        if(val.equals("USER")){
            return true;
        }
        else{
            return false;
        }
    }

    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
    }


    public boolean isLoggedIn(){
        return sharedPreferences.getBoolean(IS_LOGIN,false);
    }



}

