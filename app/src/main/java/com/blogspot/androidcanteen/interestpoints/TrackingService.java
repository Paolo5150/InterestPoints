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
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Paolo on 13/02/2017.
 */

public class TrackingService extends Service {

    LocationManager locationManager;
    android.location.LocationListener gpsListener;

    public final int TRACKING_NOTIFICATION_ID = 1986;
    Notification trackingNotification;
    NotificationManager notManager;

    @Override
    public int onStartCommand(Intent intent2, int flags, int startId) {

        GlobalVariables.ToastShort("Tracking started");

        locationManager = (LocationManager) MainActivity.appCont.getSystemService(Context.LOCATION_SERVICE);
        notManager = (NotificationManager) MainActivity.appCont.getSystemService(Context.NOTIFICATION_SERVICE);

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
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .setContentIntent(pIntent)
                .setAutoCancel(true).build();

        trackingNotification.flags = Notification.FLAG_ONGOING_EVENT;

        notManager.notify(TRACKING_NOTIFICATION_ID,trackingNotification);


        gpsListener = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                GlobalVariables.LogWithTag("Location changed in service");

                List<InterestPoint> points = IPDatabase.getInstance().GetAllPoints();

                for(InterestPoint p : points)
                {
                    double distance = GlobalVariables.CalculationByDistance(p.getLatLng(),new LatLng(location.getLatitude(),location.getLongitude()));
                    GlobalVariables.LogWithTag("Distance from " + p.title + ": " + distance);

                    if(distance < 0.350 && p.notifyWhenClose)
                    {

                     NotifyOfBeingClose(p);

                    }
                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                GlobalVariables.LogWithTag("GPS enabled");

                //Will resume request automatically
            }

            @Override
            public void onProviderDisabled(String provider) {
                GlobalVariables.LogWithTag("GPS disabled");

            }
        };


        startLocationRequest();

        return START_STICKY;
    }

    private void NotifyOfBeingClose(InterestPoint p) {

        Notification not = new Notification.Builder(this)
                .setContentTitle(p.title + " is close to you!")
                .setContentText("Click to get directions")
                .setSmallIcon(R.drawable.noticon)
                .setVibrate(new long[]{0,150,100,150,100,500})
                .setOnlyAlertOnce(true)
                .setAutoCancel(true).build();

        notManager.notify(p.id,not);
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, gpsListener);

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
    }

    @Override
    public void onDestroy() {

        GlobalVariables.LogWithTag("Service stop");
        GlobalVariables.ToastShort("Tracking stopped");
        stopLocationRequest();
        notManager.cancel(TRACKING_NOTIFICATION_ID);
        super.onDestroy();
    }
}
