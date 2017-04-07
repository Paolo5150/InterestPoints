package com.blogspot.androidcanteen.interestpoints;

import android.Manifest;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.GnssStatus;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.logging.Handler;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1111;
    final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1268;


    public static Context appCont;

    SupportMapFragment mapFrag;
    private GoogleApiClient mGoogleApiClientStatic;
    public static GoogleApiClient mGoogleApiClient;
    MapReadyCallback mapCall;
    PlaceAutocompleteFragment autocompleteFragment;
    Toolbar toolbar;
    FloatingActionButton fab;
    WarningPanel panel;

    Infodialog delayedMessage;

    View bottomSheetView;
    SavedPlacesBottomSheet bottomSheet;
     RelativeLayout warning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        appCont = MainActivity.this;

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)

                .addApi(LocationServices.API)
                .enableAutoManage(this, this)
                .build();


        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFrag);
        mapCall = new MapReadyCallback(MainActivity.this, mapFrag, mGoogleApiClient);

       warning = (RelativeLayout) findViewById(R.id.warningPanel);

        panel = new WarningPanel(warning);

        bottomSheetView = findViewById(R.id.bottom_sheet);
        bottomSheet = new SavedPlacesBottomSheet(this,bottomSheetView);

        if(IPDatabase.getInstance().GetAllPoints().size()==0)
        bottomSheet.Hide();
       else
            bottomSheet.Show();

        delayedMessage = new Infodialog(this);

      /*  fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (IPDatabase.getInstance().GetAllPoints().size() != 0) {
                    Intent toList = new Intent(MainActivity.this, PointListActivity.class);
                    startActivityForResult(toList, 1000);
                } else
                    GlobalVariables.ToastShort("No places saved!");

            }
        });*/

        CheckPermission();
    }

    public void onResume() {
        super.onResume();
        stopService(new Intent(MainActivity.this, TrackingService.class));

        CheckcurrentGPSstatus((LocationManager) getSystemService(LOCATION_SERVICE));

        if (mapCall != null && mapCall.map != null)
            mapCall.UpdateMarkers();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void Initialize() {

        GlobalVariables.LogWithTag("Initialization");

        CheckForLocationService();

        SetUpToolbar();






        mGoogleApiClient.registerConnectionCallbacks(this); //Will connect

        mapFrag.getMapAsync(mapCall);


        if (MapReadyCallback.useLocationManager)
            mapCall.startLocationRequest();

        delayedMessage.create(getString(R.string.infoDialogSavePlace), null, 5000);


    }


    private void CheckForLocationService() {


        final LocationManager man = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        //Set up listener
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            man.registerGnssStatusCallback(new GnssStatus.Callback() {
                @Override
                public void onStarted() {
                    super.onStarted();
                    CheckcurrentGPSstatus(man);

                }
            });


            man.registerGnssStatusCallback(new GnssStatus.Callback() {
                @Override
                public void onStopped() {
                    CheckcurrentGPSstatus(man);
                }
            });
        }
        else
        {
            man.addGpsStatusListener(new GpsStatus.Listener() {
                @Override
                public void onGpsStatusChanged(int event) {

                    CheckcurrentGPSstatus(man);

                }
            });
        }



    }


    void CheckcurrentGPSstatus(LocationManager man)
    {

        if(!man.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
          //  fab.setClickable(false);
         //   fab.setVisibility(View.INVISIBLE);
            toolbar.setClickable(false);
            panel.setMessage("Location service is disabled.");
            panel.Show();

        }
        else
        {
         //   fab.setClickable(true);
        //    fab.setVisibility(View.VISIBLE);
            toolbar.setClickable(true);
            panel.Hide();
        }
    }
    private void SetUpToolbar() {

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(getLayoutInflater().inflate(R.layout.toolbar_items,null));

        View v = getSupportActionBar().getCustomView();
        ViewGroup.LayoutParams lp = v.getLayoutParams();
        lp.width = Toolbar.LayoutParams.MATCH_PARENT; //Important
        v.setLayoutParams(lp);


        //Track button
        TextView track = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.toolbarTrack);

        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                final Intent toTracking = new Intent(MainActivity.this, TrackingService.class);

                Infodialog dialog = new Infodialog(MainActivity.this);

                if(dialog.create(getString(R.string.infoDialogTracking), new IDialogListener() {
                    @Override
                    public void OnOKButtonPressed(String description) {

                        if (!TrackingService.isTracking)
                            startService(toTracking);
                        else
                            stopService(toTracking);

                        finish();

                    }
                }))
                {

                }
                else
                {
                    if (!TrackingService.isTracking)
                        startService(toTracking);
                    else
                        stopService(toTracking);

                    finish();
                }








            }
        });

        //Search button

        TextView search = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.toolbarSearch);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(MainActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        //Place picker result
        if (requestCode == MapReadyCallback.PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);

                SavePlace(place);
            }
        }


        //Autocomplete places result
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                SavePlace(place);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }


        //Data returned fomr PointListActivity with the title of the place selected which will be shown on the map
        if(resultCode == RecyclerAdapter.VIEW_ON_MAP_REQUEST)
        {
           // GlobalVariables.LogWithTag("Main activity got: " + data.getStringExtra("place_title"));
          InterestPoint point = IPDatabase.getInstance().getPointByTitle(data.getStringExtra("place_title"));

            mapCall.MoveGentlyToPosition(point.getLatLng(),18);
        }
    }

    private void SavePlace(final Place place)
    {

        NewPlaceDialog d = new NewPlaceDialog(MainActivity.this, place.getName().toString(),new IDialogListener() {
            @Override
            public void OnOKButtonPressed(String description) {

                GlobalVariables.LogWithTag("Description returned: " + description);

                InterestPoint point = new InterestPoint(place.getId(),place.getName().toString(),place.getAddress().toString(),description,String.valueOf(place.getLatLng().latitude),String.valueOf(place.getLatLng().longitude),true);

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
        },0);
        

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

        delayedMessage.StopShowDelay();
        mapCall.stopLocationRequest();
        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {

       mapCall.currentLocation = null;
        super.onDestroy();
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

  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
      //  getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }*/

 /*   @Override
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
    }*/


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
