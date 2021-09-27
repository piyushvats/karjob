package com.karigarjobs.utils;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;

import com.karigarjobs.R;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
            isOnline(context);
    }

    public boolean isOnline(Context context) {

        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final android.net.NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifi.isConnected() || mobile.isConnected()) {
            // Do something
            Toast.makeText(context, context.getResources().getString(R.string.alert_network_connected), Toast.LENGTH_LONG).show();
            Log.d("Network Connected ", "Network Connected");
            return true;
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.alert_network_disconnected), Toast.LENGTH_LONG).show();
            Log.d("Network Not Connected ", "Network Connection Failed");
            return false;
        }
    }


    public static boolean isNetworkConnected(Context cxt)
    {
        final ConnectivityManager connMgr = (ConnectivityManager) cxt
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final android.net.NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifi.isConnected() || mobile.isConnected())
            return true;
        else
            return false;

    }
}