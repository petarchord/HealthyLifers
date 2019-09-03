package com.healthyteam.android.healthylifers.Domain;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.healthyteam.android.healthylifers.Data.OnGetDataListener;
import com.healthyteam.android.healthylifers.Data.UserData;
import com.healthyteam.android.healthylifers.Data.UserLocationData;

import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class DomainController {
    static private User UserInstance;
    private static DatabaseReference mDatabase;
    private static DatabaseReference getDatabase() {
        if (mDatabase == null)
            mDatabase = FirebaseDatabase.getInstance().getReference();
        return mDatabase;
    }
    static private List<User> WorldScoreUsers;
    static private List<UserLocationData> Neighbors;
    public static User getUser(){
        return UserInstance;
    }
    public static void setUser(User user){
        UserInstance=user;
    }
    private static List<OnGetListListener> worldScoreListeners;
    private static List<OnGetListListener> neighborListeners;


    public static void addGetNeigborsListener(OnGetListListener listener){
        if(neighborListeners==null)
            neighborListeners=new ArrayList<>();
        neighborListeners.add(listener);
        getNeighbors(listener);
    }
    public static boolean removeGetNeighborsListener(OnGetListListener listener){
        return neighborListeners.remove(listener);
    }
    public static void reinitalizeNeighbors(){
        if(neighborListeners==null)
            neighborListeners=new ArrayList<>();
        Neighbors=null;
        getNeighbors(null);
    }
    private static void getNeighbors(OnGetListListener listener){
        if(Neighbors ==null) {
            Neighbors = new ArrayList<>();
            Query query = getDatabase().child(Constants.UsersNode)
                    .orderByChild(Constants.UserCityAtt).equalTo(getUser().getCity());
            query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    UserLocationData u=new UserLocationData();
                    User user= new User();
                    user.setData(dataSnapshot.getValue(UserData.class));
                    if(getUser().getUID().equals(dataSnapshot.getKey()))
                        return;
                    u.setUID(dataSnapshot.getKey());
                    u.setLongitude(user.getLongitude());
                    u.setLatitude(user.getLatitude());
                    u.setUsername(user.getUsername());
                    Neighbors.add(u);
                    for (OnGetListListener listener : neighborListeners)
                        listener.onChildAdded(Neighbors, Neighbors.size() - 1);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    User u = new User();
                    u.setData(dataSnapshot.getValue(UserData.class));
                    String uid= dataSnapshot.getKey();
                    int uIndex = getUserIndex(uid, Neighbors);
                    if (uIndex != -1) {
                        Neighbors.get(uIndex).setLatitude(u.getLatitude());
                        Neighbors.get(uIndex).setLongitude(u.getLongitude());
                        for (OnGetListListener listener : neighborListeners)
                            listener.onChildChange(Neighbors, uIndex);
                    }


                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    String uid = dataSnapshot.getKey();
                    int uIndex = getUserIndex(uid,Neighbors);
                    if (uIndex != -1) {
                        UserLocationData u = Neighbors.get(uIndex);
                        Neighbors.remove(uIndex);
                        for (OnGetListListener listener : neighborListeners)
                            listener.onChildRemove(Neighbors, uIndex,u);


                    }

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.i("getNeighbors: ", databaseError.getMessage());
                    for (OnGetListListener listener : neighborListeners)
                        listener.onCanclled(databaseError);
                }
            });
        }
        else
            listener.onListLoaded(Neighbors);
    }
    public static void addGetWorldScoreListeners(OnGetListListener listener){
        if(worldScoreListeners==null)
            worldScoreListeners=new ArrayList<>();
        worldScoreListeners.add(listener);
        getWorldScoreUsersDB();

    }
    private static List<User> getWorldScoreUsers(){
        if(WorldScoreUsers==null)
            DomainController.WorldScoreUsers= PersistenceController.getWorldUsers();
        return WorldScoreUsers;

    }
    private static void getWorldScoreUsersDB(){
        if(WorldScoreUsers ==null) {

            WorldScoreUsers = new ArrayList<>();
            Query query = getDatabase().child(Constants.UsersNode).orderByChild(Constants.UserPointsAtt).limitToLast(Constants.showUserCount);
            query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    User u = new User();
                    u.setData(dataSnapshot.getValue(UserData.class));
                    u.setUID(dataSnapshot.getKey());
                    WorldScoreUsers.add(0, u);
                    for (OnGetListListener listener : worldScoreListeners)
                        listener.onChildAdded(WorldScoreUsers, 0);

                    //TODO: fix - ne aktivirta se kada u bazi ima manje user-a od showUserCount
                    if (WorldScoreUsers.size() == Constants.showUserCount)
                        for (OnGetListListener listener : worldScoreListeners)
                            listener.onListLoaded(WorldScoreUsers);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    User u = new User();
                    u.setData(dataSnapshot.getValue(UserData.class));
                    int uIndex = getUserIndex(u.getUID(), WorldScoreUsers);
                    if (uIndex != -1) {
                        WorldScoreUsers.set(uIndex, u);
                        for (OnGetListListener listener : worldScoreListeners)
                            listener.onChildChange(WorldScoreUsers, uIndex);
                    }


                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    User u = new User();
                    u.setData(dataSnapshot.getValue(UserData.class));
                    int uIndex = getUserIndex(u.getUID(), WorldScoreUsers);
                    if (uIndex != -1) {
                        WorldScoreUsers.remove(uIndex);
                        for (OnGetListListener listener : worldScoreListeners)
                            listener.onChildRemove(WorldScoreUsers, uIndex, u);

                    }

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.i("getWorldScore: ", databaseError.getMessage());
                    for (OnGetListListener listener : worldScoreListeners)
                        listener.onCanclled(databaseError);
                }
            });
        }
        else
            for (OnGetListListener listener : worldScoreListeners)
                listener.onListLoaded(WorldScoreUsers);

    }
    //TODO: bonus - update worldScore info
    public static List<User> getMoreWorldUsers(){
        return PersistenceController.getMoreWorldUsers(WorldScoreUsers);
    }

    public static int getUserIndex(String uid,List<? extends DBReference> list){
        if(list==null)
            return -1;
        for(int i=0; i<list.size();i++)
            if(list.get(i).getUID().equals(uid))
                return i;
        return -1;
    }
    public static List<UserLocation> getLocationsFor(User u){
        return PersistenceController.getLocationFor(u);
    }

    //TODO:create getNeihgborPlaces func (listener, func...)
    @Exclude
    public static void getMoreWorldUsersData(List<User> worldList, final OnGetDataListener listener){
        listener.onStart();
        Query query =getDatabase().child(Constants.UsersNode).orderByChild(Constants.UserPointsAtt)
                .limitToLast(worldList.size()+Constants.showUserCount)
                .limitToFirst(Constants.showUserCount);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //send in ascending order. Listener need to check last elements
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static String getCityFromCoo(Context context, Double latitude, Double longitude){
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses= geocoder.getFromLocation(latitude, longitude, 1);
            return addresses.get(0).getLocality();
        }
        catch (Exception e){
            Log.println(Log.ERROR,"Geocoder", e.getMessage());
        }
        return "Error";
    }
    public static Drawable drawableFromUrl(String urlStr) throws IOException {
        Bitmap x;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        URL url = new URL(urlStr);
        x = BitmapFactory.decodeStream((InputStream)url.getContent());
        return new BitmapDrawable(Resources.getSystem(), x);
    }
    public static Drawable resize(Context context, Drawable image) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 50, 50, false);
        return new BitmapDrawable(context.getResources(), bitmapResized);
    }

}
