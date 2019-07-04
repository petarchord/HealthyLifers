package com.healthyteam.android.healthylifers;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.healthyteam.android.healthylifers.Domain.Builder;
import com.healthyteam.android.healthylifers.Domain.LocationBuilder;
import com.healthyteam.android.healthylifers.Domain.User;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;


public class MapFragment extends Fragment {
    private  static final String TAG = "Location Service: ";
    private static final int REQUEST_CHECK_SETTINGS = 100;
    private static final String PROVIDER_STRING ="provider";

    private View layour_fragment;
    private FloatingActionButton fabAddLocation;
    private Context context;
    private MapView map=null;
    private IMapController mapController=null;
    private LocationManager locationManager;
    MyLocationNewOverlay myLocationOverlay;
    private Dialog addItemDialog;

    // bunch of location related apis
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private Location currLocation;
    private Double currZoom=null;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationRequest mLocationRequest;

    private boolean addPlace = false;

    private static MapFragment instance;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context=getContext();
        layour_fragment =  inflater.inflate(R.layout.fragment_map,container,false);
        fabAddLocation = layour_fragment.findViewById(R.id.floatingActionButton_addLocation);
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        map = layour_fragment.findViewById(R.id.MapView);
        initMap();
        setLocationSettings();
        setAddLocationListener();

        addItemDialog = new Dialog(getContext());
        addItemDialog.setContentView(R.layout.dialog_add_item);

        fabAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             /*   if(addPlace){
                    pickLocationOptionOff();
                    return;
                }
                pickLocationOptionOn(); */
                addItemDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                addItemDialog.show();

            }
        });
        return layour_fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        centerUserLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.e(TAG, "User chose not to make required location settings changes.");
                        break;
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        IGeoPoint PointCenter = map.getMapCenter();
        currLocation=new Location(PROVIDER_STRING);
        currLocation.setLatitude(PointCenter.getLatitude());
        currLocation.setLongitude(PointCenter.getLongitude());
        currZoom=map.getZoomLevelDouble();
        map.onPause();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void pickLocationOptionOff(){
        int color = getResources().getColor(R.color.colorPrimary);
        fabAddLocation.setBackgroundTintList(ColorStateList.valueOf(color));
        fabAddLocation.setImageResource(R.drawable.baseline_add_location_24_red_dark);
        addPlace=false;

    }
    private void pickLocationOptionOn(){
        int color = getResources().getColor(R.color.colorPrimaryLighter);
        fabAddLocation.setBackgroundTintList(ColorStateList.valueOf(color));
        fabAddLocation.setImageResource(R.drawable.baseline_add_location_24_red_light);
        addPlace=true;
    }

    public static MapFragment getInstance(){
        if(instance==null)
            instance=new MapFragment();
        return instance;
    }

    private void initMap(){
        Context ctx = context.getApplicationContext();
        mapController = map.getController();

        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        map.setUseDataConnection(true);
        map.setMultiTouchControls(true);
        setMyLocationOverlay();
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        if(currZoom==null) {
            mapController.setZoom(5.0);
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                mapController.setZoom(15.0);
        }
    }
    private void setLocationSettings() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);//trenutno ne koristim, a moze da se koristi za lokaciju
        mSettingsClient = LocationServices.getSettingsClient(context);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();

        setLocationSettingsListeners();


    }
    private void setLocationSettingsListeners() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    }
                })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    startIntentSenderForResult(rae.getResolution().getIntentSender(),REQUEST_CHECK_SETTINGS,null,0,0,0,null);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                        }
                    }
                });
    }
    //permission will be checked in SignIn activity
    private Location getLocationFromDevice(){
        Location gpsLocation=null;
        Location networkLocation=null;
        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean netEnabled=locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(gpsEnabled)
            gpsLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(netEnabled)
            networkLocation= locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        try{
            if(gpsLocation.getAccuracy()>networkLocation.getAccuracy())
                return networkLocation;
            else
                return gpsLocation;
        }
        catch (NullPointerException e){
            if(networkLocation!=null)
                return networkLocation;
            return gpsLocation;
        }
    }
    private void updateLocationUI() {
        if (currLocation == null)
            currLocation = getLocationFromDevice();//ovo verovatno treba da se izbrise. Za sad nema funkciju
        if (currLocation != null) {
            mapController.setCenter(new GeoPoint(currLocation.getLatitude(), currLocation.getLongitude()));
            if(currZoom!=null)
                mapController.setZoom(currZoom);
        }
    }
    private void centerUserLocation(){
        map.getOverlays().remove(myLocationOverlay);
        myLocationOverlay.enableFollowLocation();
        map.getOverlays().add(myLocationOverlay);
    }
    private void setMyLocationOverlay(){
        myLocationOverlay= new MyLocationNewOverlay(new GpsMyLocationProvider(getContext()),map);
        myLocationOverlay.enableMyLocation();

        if(currLocation!=null)
            updateLocationUI();
        else
            myLocationOverlay.enableFollowLocation();

        map.getOverlays().add(this.myLocationOverlay);
    }
    private void setAddLocationListener(){
        MapEventsReceiver mRecive=new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                if(addPlace)
                {
                    //open dialog and get Location object from it
                    //then send to setMarker function and initialize new marker
                    setTestMarker(p);


                }
                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        };
        MapEventsOverlay OverlayEvents = new MapEventsOverlay(context,mRecive);
        map.getOverlays().add(OverlayEvents);
    }
    private void setTestMarker( GeoPoint p){
        Marker likeMarker = new Marker(map);
        likeMarker.setPosition(p);
        //marker depend of location category
        likeMarker.setIcon(getResources().getDrawable(R.drawable.baseline_thumb_up_24_green));
        //onClick initialize showLocation dialog with Location object then show dialog
        likeMarker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                Toast.makeText(context,marker.getPosition().getLatitude() + " - "+marker.getPosition().getLongitude(),Toast.LENGTH_LONG).show();
                return true;
            }
        });
        map.getOverlays().add(likeMarker);

    }

     //function should recive Location parametar in the future







}
