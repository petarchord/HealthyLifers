package com.healthyteam.android.healthylifers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.database.DatabaseError;
import com.healthyteam.android.healthylifers.Data.UserLocationData;
import com.healthyteam.android.healthylifers.Domain.DomainController;
import com.healthyteam.android.healthylifers.Domain.OnGetListListener;

import com.healthyteam.android.healthylifers.Domain.Comment;

import com.healthyteam.android.healthylifers.Domain.User;
import com.karumi.dexter.BuildConfig;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import java.util.ArrayList;



public class MapFragment extends Fragment {
    private  static final String TAG = "Location Service: ";
    private static final int REQUEST_CHECK_SETTINGS = 100;
    private static final String PROVIDER_STRING ="provider";

    private View layout_fragment;
    private FloatingActionButton fabAddLocation;
    private FloatingActionButton fabCenterLocation;
    private Context context;
    private MapView map=null;
    private IMapController mapController=null;
    private LocationManager locationManager;
    MyLocationNewOverlay myLocationOverlay;
    private Dialog addItemDialog;
    private Dialog locationViewDialog;
    private Dialog addCommentDialog;
    private ImageButton closeCommentDialog;
    private Fragment tabInfoFragment;
    private Fragment tabCommentsFragment;
    private Button infoButton;
    private Button commentsButton;
    private Button addCommentButton;
    private ImageButton closeLocationView;
    private ListView commentListView;
    private ArrayList<Comment> commentsArray;

    // bunch of location related apis
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;

    private Location currLocation;
    private Double currZoom=null;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationRequest mLocationRequest;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ImageButton closeAddItem;
    private SectionsPageAdapter mSectionPageAdapter;

    private OnGetListListener getNeighborListener;

    private LinearLayout layoutInfo;
    private LinearLayout layoutComments;


    private boolean addPlace = false;
    private static MapFragment instance;
    private List<Marker> UserMarkerList;
    //TODO:
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Initialize(inflater,container);


        return layout_fragment;

    }
    private void Initialize(@NonNull LayoutInflater inflater, @Nullable ViewGroup container){
        context=getContext();
        layout_fragment =  inflater.inflate(R.layout.fragment_map,container,false);
        fabAddLocation = layout_fragment.findViewById(R.id.floatingActionButton_addLocation);
        fabCenterLocation = layout_fragment.findViewById(R.id.floatingActionButton_centerLocation);
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        map = layout_fragment.findViewById(R.id.MapView);
        initMap();
        setLocationSettings();
        setAddLocationListener();

       // mSectionPageAdapter = new SectionsPageAdapter(((MainActivity) getContext()).getSupportFragmentManager());

        addItemDialog = new Dialog(getContext());
        addItemDialog.setContentView(R.layout.dialog_add_item);

        addCommentDialog = new Dialog(getContext());
        addCommentDialog.setContentView(R.layout.dialog_add_comment);

        closeCommentDialog = addCommentDialog.findViewById(R.id.closeCommentDialog);

        closeAddItem = (ImageButton) addItemDialog.findViewById(R.id.closeAddItem);

        locationViewDialog = new Dialog(context);
        locationViewDialog.setContentView(R.layout.dialog_location_view);
        closeLocationView = locationViewDialog.findViewById(R.id.closeLocationDialog);
        addCommentButton = locationViewDialog.findViewById(R.id.addCommentButton);
        layoutInfo = locationViewDialog.findViewById(R.id.layout_info);
        layoutComments = locationViewDialog.findViewById(R.id.layout_comments);

        commentListView = locationViewDialog.findViewById(R.id.comment_listView);
        commentsArray = new ArrayList<>();


        infoButton = locationViewDialog.findViewById(R.id.info_button);
        commentsButton = locationViewDialog.findViewById(R.id.comments_button);

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                commentsButton.setBackgroundResource(R.color.common_google_signin_btn_text_dark_disabled);
                v.setBackgroundResource(R.color.common_google_signin_btn_text_dark_pressed);
                layoutComments.setVisibility(View.GONE);
                layoutComments.invalidate();
                layoutInfo.setVisibility(View.VISIBLE);
                layoutInfo.invalidate();

            }
        });

        commentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoButton.setBackgroundResource(R.color.common_google_signin_btn_text_dark_disabled);
                v.setBackgroundResource(R.color.common_google_signin_btn_text_dark_pressed);
                layoutComments.setVisibility(View.VISIBLE);
                layoutComments.invalidate();
                layoutInfo.setVisibility(View.GONE);
                layoutInfo.invalidate();

            }
        });

        addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCommentDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                addCommentDialog.show();
            }
        });

        closeLocationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationViewDialog.dismiss();
            }
        });

        closeCommentDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCommentDialog.dismiss();
            }
        });

        closeAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemDialog.dismiss();
            }
        });

        fabAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                addItemDialog.show();

              //locationViewDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
             // locationViewDialog.show();


            }
        });
        fabCenterLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                centerUserLocation();
            }
        });
        DomainController.getUser().addGetFriendListener(new OnGetListListener() {
            @Override
            public void onChildAdded(List<?> list, int index) {

            }

            @Override
            public void onChildChange(List<?> list, int index) {

            }

            @Override
            public void onChildRemove(List<?> list, int index, Object removedObject) {

            }

            @Override
            public void onChildMoved(List<?> list, int index) {

            }
            //TODO: fix memory leak
            @Override
            public void onListLoaded(List<?> list) {
                    DomainController.addGetNeigborsListener(new NeighbourEventHandler());
            }

            @Override
            public void onCanclled(DatabaseError error) {

            }
        });

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


    public class CommentsAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            final LayoutInflater inflater = getLayoutInflater();
            view = inflater.inflate(R.layout.row_comment,null);
            TextView user = view.findViewById(R.id.user_comment);
            TextView comment = view.findViewById(R.id.text_comment);

            return null;
        }
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

    private void updateLocationUI() {
        if (currLocation == null){
            User user = DomainController.getUser();
            mapController.setCenter(new GeoPoint(user.getLatitude(),user.getLongitude()));
            }
        if (currLocation != null) {
            mapController.setCenter(new GeoPoint(currLocation.getLatitude(), currLocation.getLongitude()));
            if (currZoom != null)
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
        myLocationOverlay.enableFollowLocation();
        updateLocationUI();
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
        likeMarker.setIcon(getResources().getDrawable(R.drawable.baseline_thumb_up_24_green));


        likeMarker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                Toast.makeText(context,marker.getPosition().getLatitude() + " - "+marker.getPosition().getLongitude(),Toast.LENGTH_LONG).show();
                Log.println(Log.INFO,"Map", "latitude: " + marker.getPosition().getLatitude()+ ", "
                        + "longitude: " + marker.getPosition().getLongitude());
                return true;
            }
        });
        map.getOverlays().add(likeMarker);
        Marker textMarker = new Marker(map);
        textMarker.setPosition(p);
        textMarker.setTextIcon("TEXT");
        textMarker.setAnchor(0,(float)0);
        map.getOverlays().add(textMarker);


    }

    class NeighbourEventHandler implements OnGetListListener {
        private Drawable drawableFromUrl(String urlStr) throws IOException {
            Bitmap x;
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL url = new URL(urlStr);
            x = BitmapFactory.decodeStream((InputStream)url.getContent());
            return new BitmapDrawable(Resources.getSystem(), x);
        }
        private Drawable resize(Drawable image) {
            Bitmap b = ((BitmapDrawable)image).getBitmap();
            Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 50, 50, false);
            return new BitmapDrawable(getResources(), bitmapResized);
        }
        private void setUserMarker(UserLocationData user){
            if(UserMarkerList == null)
                UserMarkerList=new LinkedList<Marker>();
            Marker userMarker = new Marker(map);
            Marker textMarker = new Marker(map);
            GeoPoint p = new GeoPoint(user.getLatitude(), user.getLongitude());
            userMarker.setPosition(p);
            final User friend = DomainController.getUser().getFriendByUid(user.getUID());
            if (friend != null) {
                try {
                    userMarker.setIcon(drawableFromUrl(friend.getImageUrl()));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else
                userMarker.setIcon(getResources().getDrawable(R.drawable.profile_picture));
            userMarker.setIcon(resize(userMarker.getIcon()));
            if(friend!=null) {
                userMarker.setAnchor(0, 1);
                userMarker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker, MapView mapView) {
                        FriendProfileFragment FriendProfile = new FriendProfileFragment();
                        FriendProfile.setFriend(friend);
                        FriendProfile.setPerent(MapFragment.getInstance());
                        ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,FriendProfile).commit();
                        return true;
                    }
                });
            }
            else
                userMarker.setAnchor(0,(float)0.4);
            userMarker.setInfoWindow(null);
            userMarker.setPanToView(false);

            textMarker.setPosition(p);
            textMarker.setTextIcon(user.getUsername());
            textMarker.setInfoWindow(null);
            textMarker.setPanToView(false);
            textMarker.setAnchor(0,0);
            map.getOverlays().add(userMarker);
            map.getOverlays().add(textMarker);

        }
        @Override
        public void onChildAdded(List<?> list, int index) {
            UserLocationData user = (UserLocationData) list.get(index);
            setUserMarker(user);

        }

        @Override
        public void onChildChange(List<?> list, int index) {

        }

        @Override
        public void onChildRemove(List<?> list, int index, Object removedObject) {

        }

        @Override
        public void onChildMoved(List<?> list, int index) {

        }

        @Override
        public void onListLoaded(List<?> list) {
            for(UserLocationData user: (List<UserLocationData>) list)
                setUserMarker(user);
        }

        @Override
        public void onCanclled(DatabaseError error) {

        }
    }
    //function should recive Location parametar in the future







}
