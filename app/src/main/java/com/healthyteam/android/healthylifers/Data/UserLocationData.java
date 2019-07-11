package com.healthyteam.android.healthylifers.Data;

import com.google.firebase.database.Exclude;
import com.healthyteam.android.healthylifers.Domain.DBReference;

public class UserLocationData implements DBReference {
    public Double Latitude;
    public Double Longitude;
    @Exclude
    public String UID;

    public UserLocationData(){}

    public void setUID(String uid){
        UID=uid;
    }
    public void setLatitude(Double lat){
        Latitude=lat;
    }
    public void setLongitude(Double lon){
        Longitude=lon;
    }

    @Override
    public String getUID() {
        return UID;
    }

    public Double getLongitude(){
        return Longitude;
    }
    public Double getLatitude(){
        return Latitude;
    }
}
