package com.blogspot.androidcanteen.interestpoints;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

/**
 * Created by Paolo on 13/02/2017.
 */

public class MapReadyCallback implements OnMapReadyCallback {

    public static Location currentLocation;
    public static int PLACE_PICKER_REQUEST = 1;

    public GoogleMap map;
    float mapPadding;
    SupportMapFragment mapFrag;
    View mapView;
    Activity act;
    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;
    LocationListener locationListener;

    TrackingService trackingService;
    Intent toTracking;


    public MapReadyCallback(Activity act, SupportMapFragment mapFrag, GoogleApiClient googleApiClient) {
        this.mapFrag = mapFrag;
        this.act = act;
        this.googleApiClient = googleApiClient;



        locationRequest = LocationRequest.create();

        locationRequest.setInterval(3);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
              currentLocation = location;
            }
        };


        mapView = mapFrag.getView();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {



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
        map.setMyLocationEnabled(true);
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
            public void onMapClick(LatLng latLng) {
                stopLocationRequest();
            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                marker.showInfoWindow();
                MoveGentlyToPosition(marker.getPosition(),18);
                return true;
            }
        });


        View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (currentLocation != null) {
                    MoveGentlyToPosition(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 18);
                }

            }
        });

        locationButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                GlobalVariables.LogWithTag("Long click " + googleApiClient.isConnected());


                toTracking = new Intent(act,TrackingService.class);
                act.startService(toTracking);


                return true;
            }
        });


        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

               builder.setLatLngBounds(new LatLngBounds(latLng,latLng));
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
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest,locationListener);
    }

    public void stopLocationRequest()
    {
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
            map.addMarker(new MarkerOptions().title(p.title).position(p.getLatLng()));
        }
    }
}
