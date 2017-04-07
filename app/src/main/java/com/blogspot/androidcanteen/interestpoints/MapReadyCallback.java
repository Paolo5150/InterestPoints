package com.blogspot.androidcanteen.interestpoints;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.MainThread;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

/**
 * Created by Paolo on 13/02/2017.
 */

public class MapReadyCallback implements OnMapReadyCallback,IDatabaseListener {

    public static boolean useLocationManager = false; //eDIT IN CONSTRUCTOR

    public static Location currentLocation;
    public static int PLACE_PICKER_REQUEST = 1;

    public boolean requestingUpdates = false;



    public GoogleMap map;
    float mapPadding;
    SupportMapFragment mapFrag;
    View mapView;
    Activity act;
    GoogleApiClient googleApiClient;
   public LocationRequest locationRequest;
    LocationListener locationListener;

    LocationManager locationManager;
    LocationSettingsRequest.Builder builder;
    static android.location.LocationListener gpsListener;
    static android.location.LocationListener netListener;


    TrackingService trackingService;
    Intent toTracking;

    Circle userCircle;
    Circle range;


    public MapReadyCallback(final Activity act, SupportMapFragment mapFrag, GoogleApiClient googleApiClient)  {


        this.mapFrag = mapFrag;
        this.act = act;
        this.googleApiClient = googleApiClient;

        useLocationManager = false;


        IPDatabase.getInstance().AddListener(this);
        locationRequest = LocationRequest.create();

        locationRequest.setInterval(5);
        locationRequest.setSmallestDisplacement(0);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        builder  = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);






        locationManager = (LocationManager) act.getSystemService(Context.LOCATION_SERVICE);

      SetUpListeners();


        mapView = mapFrag.getView();
    }

    void RemoveLocationNotification()
    {
        NotificationManager notManager = (NotificationManager) act.getSystemService(Context.NOTIFICATION_SERVICE);

        notManager.cancel(TrackingService.GPS_NOTIFICATION_ID);
    }
    private void NotifyOfProviderDisabled() {

        Intent gpsOptionsIntent = new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);

        gpsOptionsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(act);

        stackBuilder.addParentStack(MainActivity.class);

        stackBuilder.addNextIntent(gpsOptionsIntent);
        PendingIntent pi =stackBuilder.getPendingIntent(TrackingService.GPS_NOTIFICATION_ID,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager notManager = (NotificationManager) act.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification gpsOffNotification = new Notification.Builder(act)
                .setContentTitle("Location is off!")
                .setContentText("Click to turn the GPS on")
                .setContentIntent(pi)
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .setVibrate(new long[]{0,150,100,150,100,500})
                .setOnlyAlertOnce(true)
                .setAutoCancel(true).build();

        notManager.notify(TrackingService.GPS_NOTIFICATION_ID,gpsOffNotification);
    }

    private void UpdateLocation(Location location) {




        if(currentLocation==null)
        {
           MoveGentlyToPosition(new LatLng(location.getLatitude(),location.getLongitude()),18);

        }

        currentLocation = location;



        if(userCircle!=null)
            userCircle.remove();

        if(range!=null)
            range.remove();



        userCircle = map.addCircle(new CircleOptions().zIndex(1000).strokeWidth(2).strokeColor(Color.YELLOW).radius(3).fillColor(Color.BLUE).center(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())));
        range = map.addCircle(new CircleOptions().radius(MyOptions.meterRange).fillColor(Color.argb(80,160,160,160)).center(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())));

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {


    //    GlobalVariables.LogWithTag("Map ready");

        mapPadding = GlobalVariables.DpToPx(60);

        map = googleMap;

        if (ActivityCompat.checkSelfPermission(act, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(act, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(false);
        map.getUiSettings().setCompassEnabled(true);
        map.setPadding(0, (int) mapPadding, 0, 0);



        UpdateMarkers();

        map.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
            @Override
            public void onInfoWindowLongClick(Marker marker) {
                IPDatabase.getInstance().DeleteInterestPointByTitle(marker.getTitle());
                UpdateMarkers();

            }
        });

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng)
            {
               // stopLocationRequest();
            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                marker.showInfoWindow();
                MoveGentlyToPosition(marker.getPosition(), 18);
                return true;
            }
        });




        View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));

       locationButton.setVisibility(View.VISIBLE);

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                {
                    GlobalVariables.ToastShort("Please turn location on");
                    return;
                }

                if (currentLocation != null) {

                    MoveGentlyToPosition(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 18);
                }

            }
        });



        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                builder.setLatLngBounds(new LatLngBounds(latLng, latLng));
                try {
                    act.startActivityForResult(builder.build(act), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

            }
        });

    }



    public void startLocationRequest() {

    requestingUpdates = true;

        if (ActivityCompat.checkSelfPermission(act, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(act, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (useLocationManager) {
        //    GlobalVariables.LogWithTag("Update requested to LOCATION MANAGER");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, gpsListener);
           locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, netListener);


        } else {
         //   GlobalVariables.LogWithTag("Update requested to FUSEDLOCATIONAPI");
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient, locationRequest, locationListener);
        }

        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            GlobalVariables.ToastShort("Plase turn your location on");
    }

    public void stopLocationRequest() {

     //   GlobalVariables.LogWithTag("Local manager in activity update stopped");
       // currentLocation = null;
        requestingUpdates = false;

        if (useLocationManager) {
            if (ActivityCompat.checkSelfPermission(act, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(act, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
           locationManager.removeUpdates(netListener);
            locationManager.removeUpdates(gpsListener);

         //   GlobalVariables.LogWithTag("Local manager in activity update stopped");
        }
        else
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, locationListener);
    }

    public void MoveGentlyToPosition(LatLng pos,float zoom)
    {

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(pos,zoom));
    }

    public void UpdateMarkers()
    {
        List<InterestPoint> points = IPDatabase.getInstance().GetAllPoints();
        map.clear();
        for(InterestPoint p : points)
        {
            if(p.notifyWhenClose)
            map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).title(p.title).position(p.getLatLng()));
            else
                map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).title(p.title).position(p.getLatLng()));
        }

        if(currentLocation!=null)
            UpdateLocation(currentLocation);
    }

    public void SetUpListeners()
    {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
       //         GlobalVariables.LogWithTag("Location changed by location request");
                UpdateLocation(location);

            }
        };



        netListener = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

             //   GlobalVariables.LogWithTag("Location changed in NET by location manager");
                UpdateLocation(location);

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

           //     GlobalVariables.LogWithTag("Location changed in GPS by location manager");
                UpdateLocation(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
           //     GlobalVariables.LogWithTag("Provider enabled again");
                RemoveLocationNotification();
            }

            @Override
            public void onProviderDisabled(String provider) {
            //    GlobalVariables.LogWithTag("Provider disabled");

                NotifyOfProviderDisabled();



            }
        };
    }

    @Override
    public void OnDatabaseChange() {
        UpdateMarkers();
    }
}
