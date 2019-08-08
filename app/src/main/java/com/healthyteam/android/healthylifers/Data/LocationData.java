package com.healthyteam.android.healthylifers.Data;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.healthyteam.android.healthylifers.Domain.Constants;
import com.healthyteam.android.healthylifers.Domain.Location;

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
    public Integer UserRate;
    public String City;
    public List<String> CommentsIds;
    public String AuthorID;

    @Exclude
    public String Id;

    public LocationData(){
        Tags = new LinkedList<>();
        likedBy = new LinkedList<>();
        DislikedBy = new LinkedList<>();
        CommentsIds = new LinkedList<>();
    }





}
