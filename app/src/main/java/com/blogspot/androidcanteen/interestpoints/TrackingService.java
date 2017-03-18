package com.blogspot.androidcanteen.interestpoints;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paolo on 13/02/2017.
 */

public class TrackingService extends Service {

    public static boolean isTracking = false;

    LocationManager locationManager;

    android.location.LocationListener gpsListener;
    android.location.LocationListener netListener;


    public final int TRACKING_NOTIFICATION_ID = 1986;
    public static final int GPS_NOTIFICATION_ID = 3011;
    Notification trackingNotification;
    Notification gpsOffNotification;
    NotificationManager notManager;
    Uri notificationsound;
    List<InterestPoint> points;
    List<InterestPoint> closePoints;

    @Override
    public int onStartCommand(Intent intent2, int flags, int startId) {

     //   GlobalVariables.ToastShort("Tracking started");
        isTracking = true;

        locationManager = (LocationManager) MainActivity.appCont.getSystemService(Context.LOCATION_SERVICE);
        notManager = (NotificationManager) MainActivity.appCont.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        points = IPDatabase.getInstance().GetAllPoints();
        closePoints = new ArrayList<>();

        /*
        Intent
         */
        Intent toMain = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        stackBuilder.addParentStack(MainActivity.class);

        stackBuilder.addNextIntent(toMain);
        PendingIntent pIntent =stackBuilder.getPendingIntent(TRACKING_NOTIFICATION_ID,PendingIntent.FLAG_UPDATE_CURRENT);


        trackingNotification = new Notification.Builder(this)
                .setContentTitle("InterestPoints")
                .setContentText("Tracking is on")
                .setSound(notificationsound)
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .setContentIntent(pIntent)
                .setAutoCancel(true).build();

        trackingNotification.flags = Notification.FLAG_ONGOING_EVENT;

       // notManager.notify(TRACKING_NOTIFICATION_ID,trackingNotification);

        startForeground(TRACKING_NOTIFICATION_ID,trackingNotification);

    SetUpListeners();


        startLocationRequest();

        return START_STICKY;
    }

    private void CalculateDistance(Location location) {





        for(InterestPoint p : points)
        {
            float distance = GlobalVariables.distFrom(p.getLatLng(),new LatLng(location.getLatitude(),location.getLongitude()));
       //     GlobalVariables.LogWithTag("Distance from " + p.title + ": " + distance);

            if(distance < MyOptions.meterRange && p.notifyWhenClose)
            {
                closePoints.add(p);

            }
        }

        for(InterestPoint p : closePoints)
        NotifyOfBeingClose(p);

        closePoints.clear();
    }

    private void NotifyOfBeingClose(InterestPoint p) {

       /* Intent direction = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?daddr=" + p.lat +"," + p.lng));

        PendingIntent pDir =PendingIntent.getActivity(getApplicationContext(),p.id.hashCode(),direction,0);*/

        int NotificationID = p.id.hashCode();

        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?&daddr="+p.lat+","+p.lng));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_LAUNCHER );
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");


        Intent buttonIntent = new Intent(getApplicationContext(), NotificationDismissReceiver.class);
        buttonIntent.putExtra("notificationId",NotificationID);

//Create the PendingIntent
        PendingIntent btPendingIntent = PendingIntent.getBroadcast(getBaseContext(),NotificationID,buttonIntent,0);



        PendingIntent pDir =PendingIntent.getActivity(getApplicationContext(),p.id.hashCode(),intent,0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        Notification not = mBuilder
                .setContentTitle(p.title + " is close to you!")
                .setContentText("Click to get directions")
                .setContentIntent(pDir)
                .setSmallIcon(R.drawable.noticon)
                .addAction(0,"Dismiss",btPendingIntent)
                .setSound(notificationsound)
                .setAutoCancel(false)
                .setVibrate(new long[]{0,150,100,150,100,500})
                .setOnlyAlertOnce(true)
                .build();




        not.flags = Notification.FLAG_NO_CLEAR ;
        notManager.notify(NotificationID,not);

        //Stop notification
        points.remove(p);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void startLocationRequest() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, gpsListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, netListener);

    }

    public void stopLocationRequest() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(gpsListener);
        locationManager.removeUpdates(netListener);
    }

    void RemoveLocationNotification()
    {
               notManager.cancel(TrackingService.GPS_NOTIFICATION_ID);
    }

    public void SetUpListeners()
    {



        netListener = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

              //  GlobalVariables.LogWithTag("Location changed in NET in SERVICE");
                CalculateDistance(location);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                RemoveLocationNotification();
            }

            @Override
            public void onProviderDisabled(String provider) {



            }
        };



        gpsListener = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

           //     GlobalVariables.LogWithTag("Location changed in GPS by in SERVICE");
                CalculateDistance(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
            //    GlobalVariables.LogWithTag("Provider enabled again in service");
                RemoveLocationNotification();
            }

            @Override
            public void onProviderDisabled(String provider) {
          //      GlobalVariables.LogWithTag("Provider disabled");

                NotifyOfProviderDisabled();



            }
        };
    }

    private void NotifyOfProviderDisabled() {

        Intent gpsOptionsIntent = new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);

        gpsOptionsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(TrackingService.this);

        // stackBuilder.addParentStack(MainActivity.class);

        stackBuilder.addNextIntent(gpsOptionsIntent);
        PendingIntent pi =stackBuilder.getPendingIntent(GPS_NOTIFICATION_ID,PendingIntent.FLAG_UPDATE_CURRENT);

        gpsOffNotification = new Notification.Builder(TrackingService.this)
                .setContentTitle("Location is off!")
                .setContentText("Click to turn the GPS on")
                .setContentIntent(pi)

                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .setVibrate(new long[]{0,150,100,150,100,500})
                .setOnlyAlertOnce(true)
                .setAutoCancel(true).build();


        notManager.notify(GPS_NOTIFICATION_ID,gpsOffNotification);

   

    }

    @Override
    public void onDestroy() {

    //    GlobalVariables.LogWithTag("Service stop");
   //     GlobalVariables.ToastShort("Tracking stopped");
        stopLocationRequest();
        isTracking = false;
        notManager.cancel(TRACKING_NOTIFICATION_ID);

        Notification not = new Notification.Builder(this)
                .setContentTitle("Tracking was interrupted")

                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                           .setVibrate(new long[]{0,150,100,150,100,500})
                .setOnlyAlertOnce(true)
                .setSound(notificationsound)
                .setAutoCancel(true).build();

        notManager.notify(1234,not);

        super.onDestroy();
    }
}
