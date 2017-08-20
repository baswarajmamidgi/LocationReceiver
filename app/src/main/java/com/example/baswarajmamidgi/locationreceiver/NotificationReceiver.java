package com.example.baswarajmamidgi.locationreceiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

/**
 * Created by baswarajmamidgi on 05/10/16.
 */

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String busroute=intent.getStringExtra("alarmid");
        Log.i("log","Notification receiver called");
        NotificationManager notificationManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent i=new Intent(context,MapsActivity.class);
        i.putExtra("alarmbus",busroute);
        i.putExtra("route",intent.getStringExtra("route"));
        PendingIntent pendingIntent=PendingIntent.getActivity(context,1000,i,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notification=new NotificationCompat.Builder(context);
        notification.setContentIntent(pendingIntent);
        notification.setContentTitle("Alert");
        notification.setContentText(busroute +" Bus is approaching near to you shortly");
        notification.setSmallIcon(R.drawable.ic_directions_bus);
        notification.setAutoCancel(true);
        notification.setDefaults(Notification.DEFAULT_SOUND);
        notificationManager.notify(100,notification.build());


    }
}
