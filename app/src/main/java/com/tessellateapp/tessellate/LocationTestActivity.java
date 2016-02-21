package com.tessellateapp.tessellate;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.util.Date;

public class LocationTestActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMapReadyCallback {

    //GoogleMap map;
    private static final String TAG = "LocationTestActivity";
    GoogleApiClient mGoogleApiClient;
    TextView mLatitudeText;
    TextView mLongitudeText;
    TextView mLastUpdateTimeTextView;
    Location mLastLocation, mCurrentLocation;
    Context ctx = this;
    Boolean mRequestingLocationUpdates = false;
    LocationRequest locationRequest;
    String mLastUpdateTime;
    GoogleMap gMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //updateValuesFromBundle(savedInstanceState);
        setContentView(R.layout.activity_location_test);
        Log.i(TAG,"onCreate");
        mLatitudeText = (TextView) findViewById(R.id.latitudeText);
        mLongitudeText = (TextView) findViewById(R.id.longitudeText);
        mLastUpdateTimeTextView = (TextView) findViewById(R.id.lastUpdateTimeTextView);
        Log.i(TAG, mLatitudeText.toString() + "," + mLongitudeText.toString() + "," + mLastUpdateTimeTextView.toString());


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });


        // Create an instance of GoogleAPIClient.

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(this, "onConnected_Googleapiclient",
                Toast.LENGTH_LONG).show();
        Log.i(TAG, "onConnected_Googleapiclient");
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);


        //location settings api
        //**************************
        builder.setAlwaysShow(true);
        //**************************

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        startLocationUpdates();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    (Activity) ctx, 1000);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });

        Boolean checkPermissionResult = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            //DO CHECK PERMISSION
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                checkPermissionResult = false;
            }
        }
        if (checkPermissionResult == true) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null && mLatitudeText !=null && mLongitudeText != null ) {
                mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
                mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
            }
        }

        //if (mRequestingLocationUpdates) {

        //}


    }

    private void startLocationUpdates() {

        Boolean checkPermissionResult = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            //DO CHECK PERMISSION
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                checkPermissionResult = false;
            }
        }
        if (checkPermissionResult == true) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, locationRequest, this);
        }
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    protected void onStart() {
        Log.i(TAG, "onStart");
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        Log.i(TAG, "onStop");
        if ( mGoogleApiClient.isConnected() ){
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "onlocationchanged" + String.valueOf(location.getLatitude()) + "," + location.getLongitude());
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
    }

    private void updateUI() {
        if (mLastUpdateTimeTextView != null && mLatitudeText !=null && mLongitudeText != null ) {
            mLatitudeText.setText(String.valueOf(mCurrentLocation.getLatitude()));
            mLongitudeText.setText(String.valueOf(mCurrentLocation.getLongitude()));
            mLastUpdateTimeTextView.setText(mLastUpdateTime);
            updateMarkerandCamera();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    protected void stopLocationUpdates() {
        if ( mGoogleApiClient.isConnected() ){
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }

    @Override
    public void onMapReady(GoogleMap mMap) {
        // Add a marker in Sydney, Australia, and move the camera.
        Log.i(TAG, "onMapReady");
        gMap = mMap;
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void updateMarkerandCamera() {
        Log.i(TAG, "updateMarkerandCamera");
        LatLng mLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        gMap.addMarker(new MarkerOptions().position(mLatLng));
        gMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLng));
    }

//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
//                mRequestingLocationUpdates);
//        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
//        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
//        super.onSaveInstanceState(savedInstanceState);
//    }

//    private void updateValuesFromBundle(Bundle savedInstanceState) {
//        if (savedInstanceState != null) {
//            // Update the value of mRequestingLocationUpdates from the Bundle, and
//            // make sure that the Start Updates and Stop Updates buttons are
//            // correctly enabled or disabled.
//            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
//                mRequestingLocationUpdates = savedInstanceState.getBoolean(
//                        REQUESTING_LOCATION_UPDATES_KEY);
//                setButtonsEnabledState();
//            }
//
//            // Update the value of mCurrentLocation from the Bundle and update the
//            // UI to show the correct latitude and longitude.
//            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
//                // Since LOCATION_KEY was found in the Bundle, we can be sure that
//                // mCurrentLocationis not null.
//                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
//            }
//
//            // Update the value of mLastUpdateTime from the Bundle and update the UI.
//            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
//                mLastUpdateTime = savedInstanceState.getString(
//                        LAST_UPDATED_TIME_STRING_KEY);
//            }
//            updateUI();
//        }
//    }

}
