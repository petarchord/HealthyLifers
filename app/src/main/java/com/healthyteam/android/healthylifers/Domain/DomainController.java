package com.healthyteam.android.healthylifers.Domain;

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
    public static List<User> getMoreWorldUsers(){
        return PersistenceController.getMoreWorldUsers(WorldScoreUsers);
    }

    public static List<Location> getLocationsFor(User u){
        return PersistenceController.getLocationFor(u);
    }

}
