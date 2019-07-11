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

import java.util.List;

@IgnoreExtraProperties
public class LocationModel {
    public String imageURL;
    public String Name;
    public String Description;
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

    @Exclude
    public static LocationModel LocationInstance;
    public LocationModel(){}
    public static LocationModel getLocation(String id) {
        final String LocationId=id;
        FirebaseDatabase.getInstance().getReference().child(Constants.LocationsNode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int category=dataSnapshot.child(LocationId).child(Constants.LocationCategoryAtt).getValue(Integer.class);
                switch(Location.Category.values()[category]){
                    case EVENT:
                        LocationInstance= dataSnapshot.child(LocationId).getValue(EventLocationData.class);
                        break;
                    case HEALTHYFOOD:
                        LocationInstance= dataSnapshot.child(LocationId).getValue(HealthyFoodLocationData.class);
                        break;
                    case COURT:
                        LocationInstance= dataSnapshot.child(LocationId).getValue(CourtLocationData.class);
                        break;
                    case FITNESSCENTER:
                        LocationInstance= dataSnapshot.child(LocationId).getValue(FitnessCenterLocationData.class);
                        break;


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return LocationInstance;
    }
    //proveriti da li pamti u bazi kao EventLocation ili kao LocationModel
    public void Update(){
        FirebaseDatabase.getInstance().getReference().child(Constants.LocationsNode).child(Id).setValue(this);
    }



}
