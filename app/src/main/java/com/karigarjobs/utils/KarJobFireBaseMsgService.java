package com.karigarjobs.utils;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.karigarjobs.MainActivity;
import com.karigarjobs.user.HomeActivityU;

import org.json.JSONObject;

public class KarJobFireBaseMsgService extends FirebaseMessagingService {
    private static final String TAG = KarJobFireBaseMsgService.class.getSimpleName();
    //private NotificationUtils notificationUtils;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("NEW_TOKEN",s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            //handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                //handleDataMessage(json);
                String isuser = json.getString("isusr");
                String uid = json.getString("usrid");
                String upid = json.getString("usrpid");

                if(isuser!= null && isuser.equals("USER")) {
                    Intent mIntent = new Intent(getApplicationContext(), MainActivity.class);
                    mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Bundle b = new Bundle();
                    b.putString("uid", uid); //Your id
                    b.putString("upid", upid); //Your id
                    b.putString("isuser", isuser); //Your id
                    mIntent.putExtras(b);
                    startActivity(mIntent);
                }

            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }
}

