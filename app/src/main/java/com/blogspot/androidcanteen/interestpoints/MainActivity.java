package com.blogspot.androidcanteen.interestpoints;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1111;

    public static Context appCont;

    SupportMapFragment mapFrag;
    private GoogleApiClient mGoogleApiClientStatic;
    public static GoogleApiClient mGoogleApiClient;
    MapReadyCallback mapCall;
    PlaceAutocompleteFragment autocompleteFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      //  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);


        appCont = MainActivity.this;

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)

                .addApi(LocationServices.API)
                .enableAutoManage(this, this)
                .build();


        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFrag);
        mapCall = new MapReadyCallback(MainActivity.this,mapFrag,mGoogleApiClient);

        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                SavePlace(place);

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.

            }
        });


        CheckPermission();



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent toList = new Intent(MainActivity.this, PointListActivity.class);
                startActivity(toList);

            }
        });
    }

    public void onResume()
    {
        super.onResume();
        stopService(new Intent(MainActivity.this,TrackingService.class));

        if(mapCall!=null && mapCall.map!=null)
            mapCall.UpdateMarkers();
    }



    public void Initialize() {

GlobalVariables.LogWithTag("Initialization");

        mGoogleApiClient.registerConnectionCallbacks(this); //Will connect

        mapFrag.getMapAsync(mapCall);



        if(MapReadyCallback.useLocationManager)
            mapCall.startLocationRequest();


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MapReadyCallback.PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);

                SavePlace(place);
            }
        }
    }

    private void SavePlace(Place place)
    {
        
        InterestPoint point = new InterestPoint(place.getId(),place.getName().toString(),"Description",String.valueOf(place.getLatLng().latitude),String.valueOf(place.getLatLng().longitude),true);

        List<InterestPoint> allPoints = IPDatabase.getInstance().GetAllPoints();

        boolean foundIt = false;
        for(InterestPoint p : allPoints)
        {
           if(p.title.equalsIgnoreCase(point.title)) {
               foundIt = true;
              // GlobalVariables.LogWithTag("Found a copy");
               break;
           }
        }

        if(!foundIt)
        {
        IPDatabase.getInstance().AddInterestPoint(point);
        mapCall.UpdateMarkers();}

        mapCall.MoveGentlyToPosition(place.getLatLng(),18);
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0)
                {
                    if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                       Initialize();

                    }
                    else
                        finish();
                }else {

                    //Do nothing, wait for user to chose
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onStop() {

        mapCall.stopLocationRequest();
        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    protected void onStart() {
        GlobalVariables.LogWithTag("Start");
        if(mGoogleApiClient!=null && !mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();


        super.onStart();
    }

    private void CheckPermission() {

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        else
            Initialize();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        GlobalVariables.LogWithTag("Connection failed");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        GlobalVariables.LogWithTag("Connection was OKKK, boolean is " + MapReadyCallback.useLocationManager);

        mGoogleApiClientStatic = mGoogleApiClient;



        if(!MapReadyCallback.useLocationManager) {

          if(!mapCall.requestingUpdates)
            mapCall.startLocationRequest();
            GlobalVariables.LogWithTag("Request made in connection");
        }




    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
