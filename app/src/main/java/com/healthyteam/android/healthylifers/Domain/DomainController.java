package com.healthyteam.android.healthylifers.Domain;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.healthyteam.android.healthylifers.Data.OnGetDataListener;
import com.healthyteam.android.healthylifers.Data.UserData;

import java.util.ArrayList;
import java.util.List;

public class DomainController {
    static private User UserInstance;
    static private List<User> WorldScoreUsers;

    public static User getUser(){
        return UserInstance;
    }
    public static void setUser(String username, String password){
        //get user from base
        UserInstance=TestFunctions.createUser();
    }
    public static List<User> getWorldScoreUsers(){
        if(WorldScoreUsers==null)
            DomainController.WorldScoreUsers= PersistenceController.getWorldUsers();
        return WorldScoreUsers;
    }
    public static void getWorldScoreUsersDB(final OnGetListListener listener){
        WorldScoreUsers= new ArrayList<>();
        UserData.getWorldUsers(new OnGetDataListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(DataSnapshot data) {
                for(DataSnapshot userDS: data.getChildren()) {
                    User u = new User();
                    u.setData(userDS.getValue(UserData.class));
                    WorldScoreUsers.add(0,u);
                }
                listener.onSucces(WorldScoreUsers);
            }

            @Override
            public void onFailed(DatabaseError databaseError) {

            }
        });
    }
    public static List<User> getMoreWorldUsers(){
        return PersistenceController.getMoreWorldUsers(WorldScoreUsers);
    }

    public static List<Location> getLocationsFor(User u){
        return PersistenceController.getLocationFor(u);
    }

}
