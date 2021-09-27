package com.karigarjobs;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

public class AllUsrProvInfo {

    public static class JobCategory
    {
        public String catnum;
        public String catname;
    }

    public static class BenefitCategory
    {
        public String catnum;
        public String catname;
    };


    public static String getPastDateFromDays(int days)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, (-1*days));

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String newDate = sdf.format(calendar.getTime());;
        return newDate;
    }

    public static Address getAddresFromLocation(Context ctx, double lati, double longi)
    {
        Geocoder geocoder;
        List<Address> addresses = null;
        try {
            geocoder = new Geocoder(ctx, Locale.getDefault());
            addresses = geocoder.getFromLocation(lati,longi,3);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addresses.get(0);
    }

    public static void logdumpfile(String filepath, String tag,String text)
    {
        String fileSelectPath = filepath + "/log.file";
        File logFile = new File(fileSelectPath);
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try
        {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            String line = getCurrentTimeStamp()+":"+tag+":"+text;
            buf.append(line);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void logcatdump(String filepath)
    {
        String fileSelectPath = filepath + "/log.file";
        File logFile = new File(fileSelectPath);
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            Process process = Runtime.getRuntime().exec("logcat -c");
            process = Runtime.getRuntime().exec("logcat -f " + logFile);
        } catch ( IOException e ) {
            e.printStackTrace();
        }


    }

    public static String getCurrentTimeStamp(){
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateTime = dateFormat.format(new Date()); // Find todays date

            return currentDateTime;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

}
