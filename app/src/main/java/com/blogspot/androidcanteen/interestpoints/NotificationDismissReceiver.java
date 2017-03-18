package com.blogspot.androidcanteen.interestpoints;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Paolo on 18/03/2017.
 */

public class NotificationDismissReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        GlobalVariables.LogWithTag("RECEIVED");

        int NotID = intent.getIntExtra("notificationId",0);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        manager.cancel(NotID);
    }
}
