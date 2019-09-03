package com.healthyteam.android.healthylifers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.healthyteam.android.healthylifers.Data.OnUploadDataListener;
import com.healthyteam.android.healthylifers.Data.UserLocationData;
import com.healthyteam.android.healthylifers.Domain.DomainController;
import com.healthyteam.android.healthylifers.Domain.OnGetListListener;
import com.healthyteam.android.healthylifers.Domain.Comment;
import com.healthyteam.android.healthylifers.Domain.User;
import com.healthyteam.android.healthylifers.Domain.UserLocation;
import com.karumi.dexter.BuildConfig;
import com.squareup.picasso.Picasso;

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
import java.lang.reflect.Array;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import java.util.ArrayList;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


public class MapFragment extends Fragment {
    private  static final String TAG = "Location Service: ";
    private static final int REQUEST_CHECK_SETTINGS = 100;
    private static final String PROVIDER_STRING ="provider";

    private View layout_fragment;
    private FloatingActionButton fabAddLocation;
    private FloatingActionButton fabCenterLocation;
    private Context context;

    //map related
    private MapView map=null;
    private IMapController mapController=null;
    private LocationManager locationManager;
    MyLocationNewOverlay myLocationOverlay;
    private Location currLocation;
    private Double currZoom=null;
    private Map<String,Marker[]> addedMarkers;
    // bunch of location related apis
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationRequest mLocationRequest;




    private Dialog locationViewDialog;
    //locationView element
    private LinearLayout layoutInfo;
    private LinearLayout layoutComments;
    private ImageButton closeLocationView;
    private Button infoButton;
    private Button commentsButton;
    private Button addCommentButton;
    private ImageView imgViewLocationPic;
    private ImageView imgViewAuthorPic;
    private ImageView imgViewCategory;
    private TextView txtLocationName;
    private TextView txtAuthorName;
    private TextView txtLikeNum;
    private TextView txtDislikeNum;
    private TextView txtLocationDesc;
    private TextView txtLocationTags;




    private Dialog addCommentDialog;
    private ImageButton closeCommentDialog;



    private ListView commentListView;
    private ArrayList<Comment> commentsArray;



    private SectionsPageAdapter mSectionPageAdapter;

    private OnGetListListener getNeighborListener;




    private boolean addPlace = false;
    private static MapFragment instance;
    private List<Marker> UserMarkerList;
    private NeighbourEventHandler neighBourHandler;


    private Dialog addItemDialog;
    //dialog_add_item views
    private CheckBox cbEvent;
    private CheckBox cbCourt;
    private CheckBox cbFitnessCenter;
    private CheckBox cbHealthyFood;
    private Button btnPost;
    private Button btnAddImage;
    private ImageButton closeAddItem;
    private ImageView imgViewLocation;
    private EditText etLocationName;
    private EditText etLocationDesc;
    private EditText etLocationTags;
    private Uri LocationPicUri;

    private static final int PICK_IMAGE_REQUEST = 1;
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
        setCurrentUserLocation();
        initMap();
        setLocationSettings();

        addItemDialog = new Dialog(getContext());
        addItemDialog.setContentView(R.layout.dialog_add_item);

        locationViewDialog = new Dialog(context);
        locationViewDialog.setContentView(R.layout.dialog_location_view);

        //TODO: initialize comment list
        commentsArray = new ArrayList<>();

        fabAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                initAddLocatonDialogElement(addItemDialog);
                configAddLocationDialogElements();

                addItemDialog.show();

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
            @Override
            public void onListLoaded(List<?> list) {

                if(neighBourHandler!=null)
                    DomainController.removeGetNeighborsListener(neighBourHandler);
                neighBourHandler=new NeighbourEventHandler();
                DomainController.addGetNeigborsListener(neighBourHandler);
            }

            @Override
            public void onCanclled(DatabaseError error) {

            }
        });

    }
    //region dialog_view_item

    //end region
    void initLocationViewDialogElement(){
        locationViewDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        closeLocationView = locationViewDialog.findViewById(R.id.closeLocationDialog);

        layoutInfo = locationViewDialog.findViewById(R.id.layout_info);
        infoButton = locationViewDialog.findViewById(R.id.info_button);
        imgViewLocationPic =locationViewDialog.findViewById(R.id.ImageView_LocationPicDLV);
        imgViewAuthorPic = locationViewDialog.findViewById(R.id.ImageView_AuthorDLV);
        imgViewCategory = locationViewDialog.findViewById(R.id.ImageView_CategoryDLV);
        txtLocationName =locationViewDialog.findViewById(R.id.TextView_LocationNameDLV);
        txtAuthorName = locationViewDialog.findViewById(R.id.TextView_AuthorDLV);
        txtLikeNum=locationViewDialog.findViewById(R.id.TextView_likeNumDLV);
        txtDislikeNum = locationViewDialog.findViewById(R.id.TextView_dislikenumDLV);
        txtLocationDesc = locationViewDialog.findViewById(R.id.TextView_LocationDescDLV);
        txtLocationTags = locationViewDialog.findViewById(R.id.TextView_TagsDLV);


        layoutComments = locationViewDialog.findViewById(R.id.layout_comments);
        commentsButton = locationViewDialog.findViewById(R.id.comments_button);
        addCommentButton = locationViewDialog.findViewById(R.id.addCommentButton);

        commentListView = locationViewDialog.findViewById(R.id.ListView_CommentsDLV);
        addCommentDialog = new Dialog(getContext());
        addCommentDialog.setContentView(R.layout.dialog_add_comment);
        closeCommentDialog = addCommentDialog.findViewById(R.id.closeCommentDialog);
    }
    void setLocationVIewElementContent(UserLocation location){
        if(location.getImageUrl()!=null)
            Picasso.get().load(location.getImageUrl()).into(imgViewLocationPic);
        imgViewAuthorPic.setImageResource(R.drawable.profile_picture);
        imgViewCategory.setImageResource(getLocationIconResurce(location.getCategory()));
        //TODO: getAuthor name
        txtLocationName.setText(location.getName());
        txtLocationDesc.setText(location.getDescripition());
        txtLocationTags.setText(location.getTagsString());
        txtLikeNum.setText(location.getLikeCountString());
        txtDislikeNum.setText(location.getDislikeCountString());
    }
    void setLocationViewDialogElementListener(){

        closeLocationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationViewDialog.dismiss();
            }
        });
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


        closeCommentDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCommentDialog.dismiss();
            }
        });



    }
    //region dialog_add_item
    void initAddLocatonDialogElement(Dialog addLocationDialog){
        cbEvent = addLocationDialog.findViewById(R.id.cbEvent);
        cbCourt = addLocationDialog.findViewById(R.id.cbCourt);
        cbFitnessCenter = addLocationDialog.findViewById(R.id.cbFitnessCenter);
        cbHealthyFood = addLocationDialog.findViewById(R.id.cbHealthyFood);
        btnPost = addLocationDialog.findViewById(R.id.Button_PostDAI);
        btnAddImage = addLocationDialog.findViewById(R.id.Button_AddImageDAI);
        etLocationDesc = addLocationDialog.findViewById(R.id.EditText_LocationDescDAI);
        etLocationName= addLocationDialog.findViewById(R.id.EditText_LocationNameDAI);
        etLocationTags = addLocationDialog.findViewById(R.id.EditText_TagsDAI);
        closeAddItem = (ImageButton) addItemDialog.findViewById(R.id.ImageButton_closeAddItem);

        addLocationDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                clearAddLocationView();
            }
        });
        imgViewLocation = addLocationDialog.findViewById(R.id.ImageView_LocationPic);


    }
    void clearAddLocationView(){
        etLocationDesc.getText().clear();
        etLocationName.getText().clear();
        etLocationTags.getText().clear();
        cbCourt.setChecked(false);
        cbEvent.setChecked(false);
        cbFitnessCenter.setChecked(false);
        cbHealthyFood.setChecked(false);
    }
    void configAddLocationDialogElements(){
        final UserLocation newLocation = new UserLocation();
        cbEvent.setOnCheckedChangeListener(createAddLocationCbListener(newLocation));
        cbCourt.setOnCheckedChangeListener(createAddLocationCbListener(newLocation));
        cbFitnessCenter.setOnCheckedChangeListener(createAddLocationCbListener((newLocation)));
        cbHealthyFood.setOnCheckedChangeListener(createAddLocationCbListener(newLocation));
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewLocation(newLocation);
            }
        });
        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddLocationImage(newLocation);
            }
        });
        closeAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemDialog.dismiss();
            }
        });
    }
    CompoundButton.OnCheckedChangeListener createAddLocationCbListener(final UserLocation location){
        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switch (buttonView.getId()) {
                        case R.id.cbEvent:
                            location.setCategory(UserLocation.Category.EVENT);
                            break;
                        case R.id.cbCourt:
                            location.setCategory(UserLocation.Category.COURT);
                            break;
                        case R.id.cbFitnessCenter:
                            location.setCategory(UserLocation.Category.FITNESSCENTER);
                            break;
                        case R.id.cbHealthyFood:
                            location.setCategory(UserLocation.Category.HEALTHYFOOD);
                            break;

                    }

                    //TODO: disable/enable rest ckBoxes
                }
            }
        };
        return listener;
    }
    void AddNewLocation(UserLocation location){
        location.setAuthor(DomainController.getUser());
        location.setDescripition(etLocationDesc.getText().toString());
        location.setName(etLocationName.getText().toString());
        location.setLon(currLocation.getLongitude());
        location.setLat(currLocation.getLatitude());
        location.setDateAdded(DomainController.getCurrentDate());
        location.setCity(DomainController.getCityFromCoo(context,currLocation.getLatitude(),currLocation.getLongitude()));

        location.setTagListFromString(etLocationTags.getText().toString());
        SaveNewLocation(location);
    }
    void SaveNewLocation(final UserLocation location){
        if(LocationPicUri!=null){
            location.UpdatePicture(LocationPicUri, new OnUploadDataListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onSuccess() {
                    setUsertLocationMarker(location);
                    DomainController.getUser().addPost(location);
                    addItemDialog.dismiss();
                    LocationPicUri=null;
                }

                @Override
                public void onFailed(Exception e) {

                }
            });
        }
        else{
            location.Save(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    addItemDialog.dismiss();
                    setUsertLocationMarker(location);
                    DomainController.getUser().addPost(location);
                }
            });
        }
    }
    void AddLocationImage(UserLocation location){
        openFileChooser();
    }
    void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    //endregion

    //region Event Handlers
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
            case PICK_IMAGE_REQUEST:
                if(resultCode == RESULT_OK
                        && data != null && data.getData() != null) {
                    LocationPicUri = data.getData();
                    Log.i("onChoosePic:", LocationPicUri.getPath());
                    imgViewLocation.setImageURI(LocationPicUri);}

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

    //endregion

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
        configChangeLocationListener();
    }
    private void setCurrentUserLocation(){
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            return;
        //TODO:EXCEPTION HADNLE: getLastKnownLocation return null when device dont have baffered location
        //it doesn't depend on permission
        currLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        String City = DomainController.getCityFromCoo(context, currLocation.getLatitude(),currLocation.getLongitude());
        DomainController.getUser().updateLocation();
        if(DomainController.getUser().updateCity(City))
            DomainController.reinitalizeNeighbors();
    }
    private void configChangeLocationListener(){
        //TODO: try-catch block. For now exception couse application break which is fine
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //TODO: without FakeGPS requestLocationUpdates Event doesn't called on beggining


            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, new OsmLocationHandler());
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
    //check if gps is enabled
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
        mapController.setCenter(new GeoPoint(currLocation.getLatitude(), currLocation.getLongitude()));
        if (currZoom != null)
            mapController.setZoom(currZoom);
        else {
            mapController.setZoom(15.0);
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


    private void cleanPreviusMarkers(UserLocationData user){
        Marker array[] = addedMarkers.get(user.getUID());
        if(array!=null)
        {
            map.getOverlays().remove(array[0]);
            map.getOverlays().remove(array[1]);
            addedMarkers.remove(user.getUID());
        }
    }
    private void setUserMarker(UserLocationData user){
        if(addedMarkers == null)
            addedMarkers=new HashMap<>();
        Marker userMarker = new Marker(map);
        Marker textMarker = new Marker(map);
        GeoPoint p = new GeoPoint(user.getLatitude(), user.getLongitude());
        userMarker.setPosition(p);
        final User friend = DomainController.getUser().getFriendByUid(user.getUID());
        if (friend != null) {
            try {
                userMarker.setIcon(DomainController.drawableFromUrl(friend.getImageUrl()));
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        if(friend ==null || friend.getImageUrl()==null)
            userMarker.setIcon(getResources().getDrawable(R.drawable.profile_picture));

        userMarker.setIcon(DomainController.resize(getContext(), userMarker.getIcon()));
        if(friend!=null)
            userMarker.setAnchor(0, 1);
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
        Marker array[]={userMarker,textMarker};
        addedMarkers.put(user.getUID(),array);


    }
    private int getLocationIconResurce(UserLocation.Category category){
        switch (category){
            case EVENT: return R.drawable.flag_triangle;
            case COURT: return R.drawable.soccer_field;
            case HEALTHYFOOD: return R.drawable.store;
            case FITNESSCENTER: return R.drawable.dumbbell;
            default: return R.drawable.ic_dashboard_black_24dp;
        }
    }
    private void setUsertLocationMarker(final UserLocation uLocation){
        if(addedMarkers == null)
            addedMarkers=new HashMap<>();
        Marker locationMarker = new Marker(map);
        Marker textMarker = new Marker(map);
        GeoPoint p = new GeoPoint(uLocation.getLan(), uLocation.getLon());
        locationMarker.setPosition(p);

        locationMarker.setIcon(getResources().getDrawable(getLocationIconResurce(uLocation.getCategory())));
        locationMarker.setAnchor(0,(float)1);

        locationMarker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                initLocationViewDialogElement();
                setLocationVIewElementContent(uLocation);
                setLocationViewDialogElementListener();
                locationViewDialog.show();
                return true;
            }
        });
        locationMarker.setInfoWindow(null);
        locationMarker.setPanToView(false);

        textMarker.setPosition(p);
        textMarker.setTextIcon(uLocation.getName());
        textMarker.setInfoWindow(null);
        textMarker.setPanToView(false);
        textMarker.setAnchor(0,0);
        map.getOverlays().add(locationMarker);
        map.getOverlays().add(textMarker);
        Marker array[]={locationMarker,textMarker};
        addedMarkers.put(uLocation.getUID(),array);


    }

    //region inner handler classes
    class NeighbourEventHandler implements OnGetListListener {

        @Override
        public void onChildAdded(List<?> list, int index) {
            UserLocationData user = (UserLocationData) list.get(index);
            setUserMarker(user);

        }

        @Override
        public void onChildChange(List<?> list, int index) {
            UserLocationData user = (UserLocationData) list.get(index);
            cleanPreviusMarkers(user);
            setUserMarker(user);
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
    class OsmLocationHandler implements LocationListener
    {
        @Override
        public void onLocationChanged(Location location) {
            currLocation=location;
            DomainController.getUser().setLocation(location);
            String City = DomainController.getCityFromCoo(context, currLocation.getLatitude(),currLocation.getLongitude());
            //TODO: maybe need option for eneble/disable option for updateLocation
            DomainController.getUser().updateLocation();

            //TODO: doesn't remove previous marker which is good for now
            if(DomainController.getUser().updateCity(City))
                DomainController.reinitalizeNeighbors();
            Log.i("OSMLocationHandler: ", "Lat:" + location.getLatitude() +" Lon: " +location.getLongitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
    //endregion





}
