package com.healthyteam.android.healthylifers.Data;

import android.content.res.Configuration;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.healthyteam.android.healthylifers.Domain.User;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
    private static List<LocationModel> LocationListInstace;
    @Exclude
    private static List<User> FriendListInstance;
    @Exclude
    private static List<User> WorldUsersInstance;

    public UserData (){
    }

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
        getDatabase().child(Constants.UsersNode).child(UID).setValue(this);
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


}
