package com.karigarjobs.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;

import java.util.Locale;

public class LocaleManager {

    public static String TAG = "LocaleManager";
    public static String LOCALE_HI = "hi_IN";
    public static String LOCALE_EN = "en_US";
    private static String lan_str;
    private static String country_str;
    public static void setlan(String str)
    {
        String val[] = str.split("_");
        LocaleManager.lan_str = val[0];
        LocaleManager.country_str = val[1];
    }

    public static String getlanguage()
    {
        return LocaleManager.lan_str+"_"+LocaleManager.country_str;
    }


    public static Context updateResources(Context context) {

        if(lan_str == null || country_str == null)
            return context;

        Locale locale = new Locale(lan_str,country_str);
        Locale.setDefault(locale);
        Log.i(TAG,"SetDefaultLocale:"+locale);
        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale);
            config.locale = locale;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Log.i(TAG,"config.setLocale:"+config.toString());
            }
        } else {
            config.locale = locale;
            Log.i(TAG,"config.locale:"+config.toString());
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N){
            context = context.createConfigurationContext(config);
            Log.i(TAG,"context.createConfigurationContext");
        } else {
            res.updateConfiguration(config, res.getDisplayMetrics());
            Log.i(TAG,"res.updateConfiguration");
        }
        return context;
    }

    public static void updateResourcesNew(Configuration config) {

        if(lan_str == null || country_str == null)
            return ;
        Log.i(TAG,"updateResourcesNew");
        Locale locale = new Locale(lan_str,country_str);
        Locale.setDefault(locale);

        //Resources res = context.getResources();
        //Configuration config = new Configuration(res.getConfiguration());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale);
        } else {
            config.locale = locale;
        }
    }

}
