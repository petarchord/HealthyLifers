package com.healthyteam.android.healthylifers.Data;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;


import java.util.ArrayList;
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

    public UserData (){
        FriendsIds=new ArrayList<String>();
        PostsIds = new ArrayList<String>();
    }
    @Exclude
    public String UID;


}
