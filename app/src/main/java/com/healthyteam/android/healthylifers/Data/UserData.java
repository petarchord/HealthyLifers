package com.healthyteam.android.healthylifers.Data;

import android.content.res.Configuration;
import android.renderscript.RenderScript;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.healthyteam.android.healthylifers.Domain.EventLocation;
import com.healthyteam.android.healthylifers.Domain.Location;
import com.healthyteam.android.healthylifers.Domain.User;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

@IgnoreExtraProperties
public class UserData {
    public String ImageUrl;
    public String Email;
    public String Name;
    public String Surname;
    public String Username;
    public Integer Points;
    public Double Latitude;
    public Double Longitude;
    public String City;
    public List<String> FriendsIds;
    public List<String> PostsIds;

    @Exclude
    public String UID;
    @Exclude
    private static DatabaseReference mDatabase;
    @Exclude
    private static UserData UserInstance;
    @Exclude
    private static List<Location> LocationListInstance;
    @Exclude
    private static List<User> FriendListInstance;
    @Exclude
    private static List<User> WorldUsersInstance;

    public UserData (){
    }

    //TODO: testiraj funkcije za citanje iz baze. Mozda se rezultat koji vracaju menja sa referencom koju vracaju
    private static DatabaseReference getDatabase(){
        if(mDatabase==null)
            mDatabase=FirebaseDatabase.getInstance().getReference();
        return mDatabase;
    }
    public static UserData getUser(String UID){
        final String uid=UID;
        getDatabase().child(Constants.UsersNode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserInstance = dataSnapshot.child(uid).getValue(UserData.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        UserInstance.UID=uid;
        return UserInstance;
    }
    public List<User> getFriends(){
        FriendListInstance=new ArrayList<>();
        final List<String> friendsIds= this.FriendsIds;
        getDatabase().child(Constants.UsersNode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(String FriendId: friendsIds){
                    UserData data= dataSnapshot.child(Constants.UsersNode).child(FriendId).getValue(UserData.class);
                    User friend = new User();
                    friend.setData(data);
                    FriendListInstance.add(friend);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return FriendListInstance;
    }
    public void Update(){
        FirebaseDatabase.getInstance().getReference().child(Constants.UsersNode).child(UID).setValue(this).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.println(Log.WARN,"Database:",e.getMessage());
            }
        });
    }
    public static List<User> getWorldUser(){
        if(WorldUsersInstance ==null){
            WorldUsersInstance=new ArrayList<>();
            Query query =getDatabase().child(Constants.UsersNode).orderByChild(Constants.UserPointsAtt).limitToFirst(Constants.showUserCount);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot userSnapshot:dataSnapshot.getChildren()){
                        User u = new User();
                        u.setData(userSnapshot.getValue(UserData.class));
                        WorldUsersInstance.add(0,u);
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        return WorldUsersInstance;
    }
    public static List<User> getMoreWorldUsers(){
            Query query =getDatabase().child(Constants.UsersNode).orderByChild(Constants.UserPointsAtt)
                    .limitToLast(WorldUsersInstance.size()+Constants.showUserCount)
                    .limitToFirst(Constants.showUserCount);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot userSnapshot:dataSnapshot.getChildren()){
                        User u = new User();
                        u.setData(userSnapshot.getValue(UserData.class));
                        if(u.getUID().equals(WorldUsersInstance.get(WorldUsersInstance.size()-1).getUID()))
                            return;
                        WorldUsersInstance.add(0,u);
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        return WorldUsersInstance;
    }
  /*  public List<Location> getUserPosts(){
        LocationListInstance=new ArrayList<>();
        final List<String> postIds= this.PostsIds;
        getDatabase().child(Constants.UsersNode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(String postId: postIds){
                    Location location = new Location();
                    LocationModel data= LocationModel.getLocation(postId);
                    //location.setData(data)
                    LocationListInstance.add(location);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return LocationListInstance;
    }*/


}
