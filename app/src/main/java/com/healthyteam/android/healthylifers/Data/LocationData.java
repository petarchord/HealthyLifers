package com.healthyteam.android.healthylifers.Data;


import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import java.util.LinkedList;
import java.util.List;

@IgnoreExtraProperties
public class LocationData {
    public String imageURL;
    public String Name;
    public String Description;
    public String DateAdded;
    public Integer Category;
    public List<String> Tags;
    public List<String> likedBy;
    public List<String> DislikedBy;
    public Double Longitude;
    public Double Latitude;
    public String City;
    public List<String> CommentsIds;
    public String AuthorUID;

    @Exclude
    public String UID;

    public LocationData(){
        Tags = new LinkedList<>();
        likedBy = new LinkedList<>();
        DislikedBy = new LinkedList<>();
        CommentsIds = new LinkedList<>();
    }





}
